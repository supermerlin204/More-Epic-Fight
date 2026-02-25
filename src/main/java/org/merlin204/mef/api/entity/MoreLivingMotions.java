package org.merlin204.mef.api.entity;

import yesman.epicfight.api.animation.LivingMotion;

public enum MoreLivingMotions implements LivingMotion {


    WONDER_R,
    WONDER_L;

    final int id;

    private MoreLivingMotions() {
        this.id = LivingMotion.ENUM_MANAGER.assign(this);
    }

    public int universalOrdinal() {
        return this.id;
    }
}
