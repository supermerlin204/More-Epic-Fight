package org.merlin204.mef.api.execution;

import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import javax.annotation.Nullable;

public record ExecutionAnimSet(
        AnimationManager.AnimationAccessor<? extends StaticAnimation> attackerAnim,
        AnimationManager.AnimationAccessor<? extends StaticAnimation> victimAnim,
        @Nullable Vec3 customOffset
) {
    public ExecutionAnimSet(AnimationManager.AnimationAccessor<? extends StaticAnimation> attackerAnim,
                            AnimationManager.AnimationAccessor<? extends StaticAnimation> victimAnim) {
        this(attackerAnim, victimAnim, null);
    }
}