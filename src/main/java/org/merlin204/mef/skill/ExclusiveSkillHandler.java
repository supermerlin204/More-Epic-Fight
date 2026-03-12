package org.merlin204.mef.skill;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.merlin204.mef.capability.AdvanceWeaponCapability;
import org.merlin204.mef.capability.OriginalSkillCapability;
import org.merlin204.mef.main.MoreEpicFightMod;
import yesman.epicfight.api.data.reloader.SkillManager;
import yesman.epicfight.api.forgeevent.InnateSkillChangeEvent;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPChangeSkill;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlot;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

@Mod.EventBusSubscriber(modid = MoreEpicFightMod.MOD_ID)
public class ExclusiveSkillHandler {

    @SubscribeEvent
    public static void onWeaponSkillChange(InnateSkillChangeEvent event) {
        ServerPlayerPatch serverPlayerPatch = event.getPlayerPatch();

        if (serverPlayerPatch.isLogicalClient()) return;
        if (event.getHand() != InteractionHand.MAIN_HAND) return;

        CapabilityItem toCap = event.getToItemCapability();
        ItemStack newItem = event.getTo();

        AdvanceWeaponCapability.ExclusiveSkillData exclusiveDodge = (toCap instanceof AdvanceWeaponCapability advCap) ? advCap.getExclusiveDodge(serverPlayerPatch, newItem) : null;
        handleExclusiveSkill(serverPlayerPatch, SkillSlots.DODGE, exclusiveDodge);

        AdvanceWeaponCapability.ExclusiveSkillData exclusiveGuard = (toCap instanceof AdvanceWeaponCapability advCap) ? advCap.getExclusiveGuard(serverPlayerPatch, newItem) : null;
        handleExclusiveSkill(serverPlayerPatch, SkillSlots.GUARD, exclusiveGuard);

        AdvanceWeaponCapability.ExclusiveSkillData exclusiveIdentity = (toCap instanceof AdvanceWeaponCapability advCap) ? advCap.getExclusiveIdentity(serverPlayerPatch, newItem) : null;
        handleExclusiveSkill(serverPlayerPatch, SkillSlots.IDENTITY, exclusiveIdentity);

        AdvanceWeaponCapability.ExclusiveSkillData exclusiveMover = (toCap instanceof AdvanceWeaponCapability advCap) ? advCap.getExclusiveMover(serverPlayerPatch, newItem) : null;
        handleExclusiveSkill(serverPlayerPatch, SkillSlots.MOVER, exclusiveMover);
    }

    private static void handleExclusiveSkill(ServerPlayerPatch serverPlayerPatch, SkillSlot slot, AdvanceWeaponCapability.ExclusiveSkillData exclusiveSkillData) {
        ServerPlayer player = serverPlayerPatch.getOriginal();
        SkillContainer container = serverPlayerPatch.getSkill(slot);
        Skill currentSkill = container.getSkill();
        int entityId = player.getId();
        String slotName = slot.toString();

        player.getCapability(OriginalSkillCapability.INSTANCE).ifPresent(cap -> {

            if (exclusiveSkillData != null && exclusiveSkillData.skill() != null) {
                Skill targetSkill = exclusiveSkillData.skill();
                boolean forceReplace = exclusiveSkillData.isFocusReplace();

                if (currentSkill != targetSkill) {
                    boolean isSlotEmpty = (currentSkill == null);

                    if (forceReplace || isSlotEmpty) {
                        if (!cap.hasSkill(slotName)) {
                            cap.saveSkill(slotName, currentSkill == null ? null : currentSkill.toString());
                        }

                        container.setSkill(targetSkill);
                        container.setDisabled(false);

                        EpicFightNetworkManager.sendToPlayer(
                                new SPChangeSkill(slot, entityId, targetSkill), player
                        );
                    }
                }
            }
            else {
                if (cap.hasSkill(slotName)) {
                    String savedSkillId = cap.getSkill(slotName);
                    Skill originalSkill = null;

                    if (savedSkillId != null && !"none".equals(savedSkillId)) {
                        originalSkill = SkillManager.getSkill(savedSkillId);
                    }

                    if (currentSkill != originalSkill) {
                        container.setSkill(originalSkill);

                        EpicFightNetworkManager.sendToPlayer(
                                new SPChangeSkill(slot, entityId, originalSkill), player
                        );

                        if (originalSkill == null) {
                            container.setDisabled(true);
                        }
                    }

                    cap.removeSkill(slotName);
                }
            }
        });
    }
}