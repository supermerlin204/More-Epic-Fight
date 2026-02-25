package org.merlin204.mef.api.forgeevent;

import net.minecraft.world.entity.EntityType;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.merlin204.mef.api.entity.MoreStunType;
import org.merlin204.mef.api.stamina.StaminaType;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;

import java.util.Map;


/**
 * 给实体类型注册对应的耐力类型
 */
public class StaminaTypeRegistryEvent extends Event implements IModBusEvent {
    private final Map<EntityType<?>, StaminaType> map;

    public StaminaTypeRegistryEvent(Map<EntityType<?>, StaminaType> map) {
        this.map = map;
    }


    public Map<EntityType<?>, StaminaType> getMap() {
        return map;
    }
}
