package org.merlin204.mef.api.animation.defense;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

public abstract class DefenseSuccessEvent<T extends DefenseSuccessEvent<T>> {

    protected DefenseSuccessEvent() {}

    protected abstract boolean checkCondition(LivingEntityPatch<?> ownerPatch, Entity attackEntity, DamageSource damageSource);

    public void execute(LivingEntityPatch<?> ownerPatch, Entity attackEntity, DamageSource damageSource) {
        if (this.checkCondition(ownerPatch,attackEntity, damageSource)) {
            this.fire(ownerPatch,attackEntity, damageSource);
        }
    }

    protected abstract void fire(LivingEntityPatch<?> ownerPatch, Entity attackEntity, DamageSource damageSource);

    public static class TimePairEvent extends DefenseSuccessEvent<TimePairEvent> {
        private final defenseHandler handler;
        private final float start;
        private final float end;

        private TimePairEvent(float start, float end,defenseHandler handler) {
            this.handler = handler;
            this.start = start;
            this.end = end;
        }

        @Override
        protected boolean checkCondition(LivingEntityPatch<?> ownerPatch, Entity attackEntity, DamageSource damageSource) {
            if (ownerPatch.getAnimator().getPlayerFor(null).getElapsedTime() >=start &&ownerPatch.getAnimator().getPlayerFor(null).getElapsedTime() <=end){
                return true;
            }
            return false;
        }

        @Override
        protected void fire(LivingEntityPatch<?> ownerPatch, Entity attackEntity, DamageSource damageSource) {
            handler.handle(ownerPatch, attackEntity, damageSource);
        }

        public static TimePairEvent create( float start, float end,defenseHandler handler) {
            return new TimePairEvent(start,end,handler);
        }


        @FunctionalInterface
        public interface defenseHandler {
            void handle(LivingEntityPatch<?> ownerPatch, Entity attackEntity, DamageSource damageSource);
        }
    }

    public static class SimpleEvent extends DefenseSuccessEvent<TimePairEvent> {
        private final defenseHandler handler;

        private SimpleEvent(defenseHandler handler) {
            this.handler = handler;
        }

        @Override
        protected boolean checkCondition(LivingEntityPatch<?> ownerPatch, Entity attackEntity, DamageSource damageSource) {
            return true;
        }

        @Override
        protected void fire(LivingEntityPatch<?> ownerPatch, Entity attackEntity, DamageSource damageSource) {
            handler.handle(ownerPatch, attackEntity, damageSource);
        }

        public static SimpleEvent create(defenseHandler handler) {
            return new SimpleEvent(handler);
        }


        @FunctionalInterface
        public interface defenseHandler {
            void handle(LivingEntityPatch<?> ownerPatch, Entity attackEntity, DamageSource damageSource);
        }
    }
}
