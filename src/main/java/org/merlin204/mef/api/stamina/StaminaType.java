package org.merlin204.mef.api.stamina;

public class StaminaType {
    private final float defaultMax;
    private final float defaultRegen;


    public StaminaType(float defaultMax, float defaultRegen) {
        this.defaultMax = defaultMax;
        this.defaultRegen = defaultRegen;
    }

    public float getDefaultMax() {
        return defaultMax;
    }

    public float getDefaultRegen() {
        return defaultRegen;
    }
}
