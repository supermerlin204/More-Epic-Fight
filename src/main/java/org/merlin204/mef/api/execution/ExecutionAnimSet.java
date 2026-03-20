package org.merlin204.mef.api.execution;

import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;


public record ExecutionAnimSet(
        AnimationManager.AnimationAccessor<? extends StaticAnimation> attackerAnim,
        AnimationManager.AnimationAccessor<? extends StaticAnimation> victimAnim
) {}