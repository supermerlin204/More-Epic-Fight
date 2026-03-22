package org.merlin204.mef.event;

import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.merlin204.mef.api.animation.property.MEFAnimationProperty;
import org.merlin204.mef.api.entity.MEFEntityAPI;
import org.merlin204.mef.api.entity.MoreLivingMotions;
import org.merlin204.mef.api.entity.MoreStunType;
import org.merlin204.mef.api.network.PacketHandler;
import org.merlin204.mef.api.network.PacketRelay;
import org.merlin204.mef.api.network.packet.client.SyncBossBarPacket;
import org.merlin204.mef.api.stamina.StaminaType;
import org.merlin204.mef.capability.MEFCapabilities;
import org.merlin204.mef.capability.MEFEntity;
import org.merlin204.mef.client.gui.MEFBossBarManager;
import org.merlin204.mef.main.MoreEpicFightMod;
import yesman.epicfight.api.forgeevent.InitAnimatorEvent;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

import java.util.Objects;

import static org.merlin204.mef.epicfight.MEFAnimations.BIPED_WONDER_L;
import static org.merlin204.mef.epicfight.MEFAnimations.BIPED_WONDER_R;

@Mod.EventBusSubscriber(modid = MoreEpicFightMod.MOD_ID)
public class ForgeEvents {
    /**
     * 给人型实体强上Wonder的Living
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void initAnimator(InitAnimatorEvent event) {
        if(event.getEntityPatch().getArmature() instanceof HumanoidArmature){
            if (!event.getAnimator().getLivingAnimations().containsKey(MoreLivingMotions.WONDER_L)){
                event.getAnimator().addLivingAnimation(MoreLivingMotions.WONDER_L, BIPED_WONDER_L);
            }
            if (!event.getAnimator().getLivingAnimations().containsKey(MoreLivingMotions.WONDER_R)){
                event.getAnimator().addLivingAnimation(MoreLivingMotions.WONDER_R, BIPED_WONDER_R);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if(event.getEntity() instanceof ServerPlayer serverPlayer) {
            MEFBossBarManager.BOSSES.forEach(((uuid, integer) -> PacketRelay.sendToPlayer(PacketHandler.INSTANCE, new SyncBossBarPacket(uuid, integer), serverPlayer)));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void entityJoinLevel(EntityJoinLevelEvent event) {
        if (MEFEntityAPI.getStaminaTypeByEntity(event.getEntity()) != null){
            MEFEntity mefEntity = MEFCapabilities.getMEFEntity(event.getEntity());
            if (mefEntity.staminaIsPresent() && mefEntity.getStaminaType().getBarRenderType() == StaminaType.BarRenderType.BOSS){
                ServerBossEvent bossInfo = new ServerBossEvent(event.getEntity().getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS);
                if (!event.getEntity().level().isClientSide) {
                    MEFBossBarManager.BOSSES.put(bossInfo.getId(), event.getEntity().getId());
                    MEFBossBarManager.BOSS_EVENT_MAP.put(event.getEntity().getId(),bossInfo);
                    PacketRelay.sendToAll(PacketHandler.INSTANCE, new SyncBossBarPacket(bossInfo.getId(), event.getEntity().getId()));
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity deadEntity = event.getEntity();
        Entity attacker = event.getSource().getDirectEntity();
        if (deadEntity.level().isClientSide) return;

        if (attacker instanceof LivingEntity livingAttacker) {
            LivingEntityPatch<?> attackerPatch = EpicFightCapabilities.getEntityPatch(livingAttacker, LivingEntityPatch.class);
            LivingEntityPatch<?> deadEntityPatch = EpicFightCapabilities.getEntityPatch(deadEntity, LivingEntityPatch.class);

            if (attackerPatch != null && deadEntityPatch != null) {
                var animatorPlayer = deadEntityPatch.getAnimator().getPlayerFor(null);
                if (animatorPlayer == null) return;

                var deadEntityAnim = animatorPlayer.getRealAnimation().get();

                var startAnimAccessor = MEFEntityAPI.getMoreStunAnimation(deadEntityPatch, MoreStunType.BE_EXECUTED_START);

                boolean isVictim = deadEntityAnim.getProperty(MEFAnimationProperty.IS_VICTIM_ANIMATION).orElse(false);

                if (startAnimAccessor != null && deadEntityAnim.equals(startAnimAccessor.get()) && !isVictim) {
                    event.setCanceled(true);
                    deadEntity.setHealth(1.0F);
                    return;
                }

                if (isVictim) {
                    MEFEntity mefEntity = MEFCapabilities.getMEFEntity(deadEntity);
                    if (!mefEntity.isDoomed()) {
                        System.out.println("Victim");
                        event.setCanceled(true);
                        deadEntity.setHealth(1.0F);
                        mefEntity.markDoomed(event.getSource());
                        System.out.println(event.getSource());
                    }
                }
            }
        }
    }
}
