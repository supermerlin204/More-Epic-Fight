package org.merlin204.mef.api.animation.defense;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

import java.util.ArrayList;
import java.util.List;

public class DefenseTimePair {
    private final float begin;
    private final float end;
    private final List<DefenseCondition> defenseConditions = new ArrayList<>();
    private final List<DefenseSuccessEvent<?>> defenseSuccessEvents = new ArrayList<>();

    private DefenseTimePair(float begin, float end) {
        this.begin = begin;
        this.end = end;
    }

    public static DefenseTimePair create(float begin, float end){
        return new DefenseTimePair(begin,end);
    }

    public float getBegin() {
        return begin;
    }

    public float getEnd() {
        return end;
    }

    public DefenseTimePair addDefenseSuccessEvents(DefenseSuccessEvent<?>... defenseSuccessEvents){
        this.defenseSuccessEvents.addAll(List.of(defenseSuccessEvents));
        return this;
    }

    public DefenseTimePair addDefenseConditions(DefenseCondition... defenseCondition){
        this.defenseConditions.addAll(List.of(defenseCondition));
        return this;
    }

    public boolean canDefense(LivingEntityPatch<?> ownerPatch, Entity attackEntity, DamageSource damageSource){
        for (DefenseCondition defenseCondition : defenseConditions){
            if (!defenseCondition.handle(ownerPatch,attackEntity,damageSource)){
                return false;
            }
        }
        return true;
    }

    public void defenseSuccess(LivingEntityPatch<?> ownerPatch, Entity attackEntity, DamageSource damageSource){
        for (DefenseSuccessEvent<?> defenseSuccessEvent:defenseSuccessEvents){
            defenseSuccessEvent.execute(ownerPatch,attackEntity,damageSource);
        }
    }

    public boolean isTimeIn(float time) {
        return time >= this.begin && time < this.end;
    }

    @FunctionalInterface
    public interface DefenseCondition {
        boolean handle(LivingEntityPatch<?> ownerPatch, Entity attackEntity, DamageSource damageSource);
    }
}
