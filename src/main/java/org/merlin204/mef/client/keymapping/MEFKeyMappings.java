package org.merlin204.mef.client.keymapping;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(value = {Dist.CLIENT},bus = Mod.EventBusSubscriber.Bus.MOD)
public class MEFKeyMappings {

    public static final KeyMapping PARRY = new CombatKeyMapping("key.mef.parry", KeyConflictContext.IN_GAME, KeyModifier.CONTROL, InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_LEFT, "key.mef.common");


    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {

        event.register(PARRY);

    }

}
