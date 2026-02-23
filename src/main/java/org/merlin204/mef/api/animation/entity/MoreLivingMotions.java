package org.merlin204.mef.api.animation.entity;

import yesman.epicfight.api.animation.LivingMotion;

public enum MoreLivingMotions implements LivingMotion {


    IDLE_KNOCK,
    WONDER_R,
    WONDER_L,
    BLOCK;

    final int id;

    private MoreLivingMotions() {
        this.id = LivingMotion.ENUM_MANAGER.assign(this);
    }

    public int universalOrdinal() {
        return this.id;
    }
}
