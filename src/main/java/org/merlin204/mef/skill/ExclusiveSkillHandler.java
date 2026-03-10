package org.merlin204.mef.skill;


import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.merlin204.mef.capability.AdvanceWeaponCapability;
import org.merlin204.mef.main.MoreEpicFightMod;
import yesman.epicfight.api.forgeevent.InnateSkillChangeEvent;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPChangeSkill;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlot;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = MoreEpicFightMod.MOD_ID)
public class ExclusiveSkillHandler {

    private static final Map<UUID, Skill> originalDodges = new HashMap<>();
    private static final Map<UUID, Skill> originalGuards = new HashMap<>();
    private static final Map<UUID, Skill> originalIdentitys = new HashMap<>();
    private static final Map<UUID, Skill> originalMovers = new HashMap<>();

    @SubscribeEvent
    public static void onWeaponSkillChange(InnateSkillChangeEvent event) {
        ServerPlayerPatch serverPlayerPatch = event.getPlayerPatch();

        if (serverPlayerPatch.isLogicalClient()) return;
        if (event.getHand() != InteractionHand.MAIN_HAND) return;

        CapabilityItem toCap = event.getToItemCapability();
        ItemStack newItem = event.getTo();
        UUID playerId = serverPlayerPatch.getOriginal().getUUID();

        Skill exclusiveDodge = (toCap instanceof AdvanceWeaponCapability advCap) ? advCap.getExclusiveDodge(serverPlayerPatch, newItem) : null;
        handleExclusiveSkill(serverPlayerPatch, playerId, SkillSlots.DODGE, originalDodges, exclusiveDodge);

        Skill exclusiveGuard = (toCap instanceof AdvanceWeaponCapability advCap) ? advCap.getExclusiveGuard(serverPlayerPatch, newItem) : null;
        handleExclusiveSkill(serverPlayerPatch, playerId, SkillSlots.GUARD, originalGuards, exclusiveGuard);

        Skill exclusiveIdentity = (toCap instanceof AdvanceWeaponCapability advCap) ? advCap.getExclusiveIdentity(serverPlayerPatch, newItem) : null;
        handleExclusiveSkill(serverPlayerPatch, playerId, SkillSlots.IDENTITY, originalIdentitys, exclusiveIdentity);

        Skill exclusiveMover = (toCap instanceof AdvanceWeaponCapability advCap) ? advCap.getExclusiveMover(serverPlayerPatch, newItem) : null;
        handleExclusiveSkill(serverPlayerPatch, playerId, SkillSlots.MOVER, originalMovers, exclusiveMover);
    }

    private static void handleExclusiveSkill(ServerPlayerPatch serverPlayerPatch, UUID playerId,
                                             SkillSlot slot, Map<UUID, Skill> memoryMap, Skill exclusiveSkill) {
        SkillContainer container = serverPlayerPatch.getSkill(slot);
        Skill currentSkill = container.getSkill();
        int entityId = serverPlayerPatch.getOriginal().getId();

        if (exclusiveSkill != null) {
            if (currentSkill != exclusiveSkill) {
                if (!memoryMap.containsKey(playerId)) {
                    memoryMap.put(playerId, currentSkill);
                }

                container.setSkill(exclusiveSkill);

                EpicFightNetworkManager.sendToPlayer(new SPChangeSkill(slot, entityId, exclusiveSkill), serverPlayerPatch.getOriginal()
                );
            }
        } else {
            if (memoryMap.containsKey(playerId)) {
                Skill originalSkill = memoryMap.get(playerId);

                if (currentSkill != originalSkill) {
                    container.setSkill(originalSkill);

                    EpicFightNetworkManager.sendToPlayer(new SPChangeSkill(slot, entityId, originalSkill), serverPlayerPatch.getOriginal()
                    );

                    if (originalSkill == null) {
                        container.setDisabled(true);
                    }
                }
                memoryMap.remove(playerId);
            }
        }
    }
}