package org.merlin204.mef.api.forgeevent;

import net.minecraft.world.entity.EntityType;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.merlin204.mef.api.entity.MoreStunType;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;

import java.util.Map;


/**
 * 给实体类型添加更多硬直动画
 */
public class MoreStunTypeRegistryEvent extends Event implements IModBusEvent {
    private final Map<EntityType<?>, Map<MoreStunType, AnimationManager.AnimationAccessor<? extends StaticAnimation>>> map;

    public MoreStunTypeRegistryEvent(Map<EntityType<?>, Map<MoreStunType, AnimationManager.AnimationAccessor<? extends StaticAnimation>>> map) {
        this.map = map;
    }


    public Map<EntityType<?>, Map<MoreStunType, AnimationManager.AnimationAccessor< ? extends StaticAnimation>>> getMap() {
        return map;
    }
}
