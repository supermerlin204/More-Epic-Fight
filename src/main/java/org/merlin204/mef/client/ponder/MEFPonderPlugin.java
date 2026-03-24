package org.merlin204.mef.client.ponder;

import net.createmod.catnip.platform.ForgeRegisteredObjectsHelper;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import org.merlin204.mef.main.MoreEpicFightMod;
import yesman.epicfight.world.item.EpicFightItems;

public class MEFPonderPlugin implements PonderPlugin {

    @Override
    public @NotNull String getModId() {
        return MoreEpicFightMod.MOD_ID;
    }

    @Override
    public void registerScenes(@NotNull PonderSceneRegistrationHelper<ResourceLocation> helper) {
        ForgeRegisteredObjectsHelper forgeRegisteredObjectsHelper = new ForgeRegisteredObjectsHelper();
        PonderSceneRegistrationHelper<Item> itemHelper = helper.withKeyFunction(forgeRegisteredObjectsHelper::getKeyOrThrow);

        itemHelper.forComponents(EpicFightItems.DIAMOND_TACHI.get())
                .addStoryBoard("tachi_combo_showcase", MEFWeaponScenes::showcaseWeaponCombo);
    }
}