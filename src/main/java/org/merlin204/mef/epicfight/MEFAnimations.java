package org.merlin204.mef.epicfight;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.merlin204.mef.api.animation.defense.DefenseSuccessEvent;
import org.merlin204.mef.api.animation.defense.DefenseTimePair;
import org.merlin204.mef.api.animation.property.MEFAnimationProperty;
import org.merlin204.mef.api.animation.type.*;
import org.merlin204.mef.api.entity.MEFEntityAPI;
import org.merlin204.mef.main.MoreEpicFightMod;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@Mod.EventBusSubscriber(modid = MoreEpicFightMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MEFAnimations {
    public static AnimationManager.AnimationAccessor<ModifierMovementAnimation> BIPED_WONDER_L;
    public static AnimationManager.AnimationAccessor<ModifierMovementAnimation> BIPED_WONDER_R;

    public static AnimationManager.AnimationAccessor<MEFActionAnimation> SHIELD_PARRY;
    public static AnimationManager.AnimationAccessor<MEFActionAnimation> PARRY_ONE_HAND;
    public static AnimationManager.AnimationAccessor<ActionAnimation> BIPED_BE_PARRIED_R;
    public static AnimationManager.AnimationAccessor<ActionAnimation> BIPED_BE_PARRIED_L;
    public static AnimationManager.AnimationAccessor<ActionAnimation> BIPED_BE_PARRIED_M;
    public static AnimationManager.AnimationAccessor<MEFBeExecutedAnimation> BIPED_BE_EXECUTED_START;
    public static AnimationManager.AnimationAccessor<MEFBeExecutedAnimation> BIPED_BE_EXECUTED_END;
    public static AnimationManager.AnimationAccessor<MEFVictimAnimation> ARES_BIPED_BE_EXECUTED;

    public static AnimationManager.AnimationAccessor<MEFExecuteAttackAnimation> FIST_EXECUTE;
    public static AnimationManager.AnimationAccessor<MEFExecuteAttackAnimation> ONE_HAND_EXECUTE;
    public static AnimationManager.AnimationAccessor<MEFExecuteAttackAnimation> ONE_HAND_EXECUTE_HARD;
    public static AnimationManager.AnimationAccessor<MEFExecuteAttackAnimation> ARES_BIPED_COMMON_EXECUTE;


    public static void buildAnimations(AnimationManager.AnimationBuilder builder) {
        BIPED_WONDER_L = builder.nextAccessor("biped/wonder_l", accessor -> new ModifierMovementAnimation(0.15F,true, accessor, Armatures.BIPED,3.1F));
        BIPED_WONDER_R = builder.nextAccessor("biped/wonder_r", accessor -> new ModifierMovementAnimation(0.15F,true, accessor, Armatures.BIPED,3.1F));

        SHIELD_PARRY = builder.nextAccessor("player/shield_parry", accessor -> new MEFActionAnimation(0.1F, accessor, Armatures.BIPED)
                .addDefenseTimePair(DefenseTimePair.create(30/60F,40/60F).addDefenseConditions(FRONT).addDefenseSuccessEvents(CLASH,PARRY,PERFECT_PARRY)));
        PARRY_ONE_HAND = builder.nextAccessor("player/parry_one_hand", accessor -> new MEFActionAnimation(0.1F, accessor, Armatures.BIPED)
                .addDefenseTimePair(DefenseTimePair.create(10/60F,20/60F).addDefenseConditions(FRONT).addDefenseSuccessEvents(CLASH,PARRY)));
        BIPED_BE_PARRIED_R = builder.nextAccessor("biped/be_parried_r", accessor -> new ActionAnimation(0.15F, accessor, Armatures.BIPED));
        BIPED_BE_PARRIED_L = builder.nextAccessor("biped/be_parried_l", accessor -> new ActionAnimation(0.15F, accessor, Armatures.BIPED));
        BIPED_BE_PARRIED_M = builder.nextAccessor("biped/be_parried_m", accessor -> new ActionAnimation(0.15F, accessor, Armatures.BIPED));
        BIPED_BE_EXECUTED_START = builder.nextAccessor("biped/be_executed_start", accessor -> new MEFBeExecutedAnimation(0.15F, accessor, Armatures.BIPED));
        BIPED_BE_EXECUTED_END = builder.nextAccessor("biped/be_executed_end", accessor -> new MEFBeExecutedAnimation(0.15F, accessor, Armatures.BIPED));
        ARES_BIPED_BE_EXECUTED = builder.nextAccessor("biped/ares_biped_be_executed", accessor -> new MEFVictimAnimation(0.15F, accessor, Armatures.BIPED));

        FIST_EXECUTE = builder.nextAccessor("player/fist_execute", accessor -> new MEFExecuteAttackAnimation(0.15F, 0, 35/60F, accessor, Armatures.BIPED,
                new AttackAnimation.Phase(0,35/60F,35/60F,40/60F, 40/60F, 100/60F, InteractionHand.OFF_HAND,Armatures.BIPED.get().toolL, ColliderPreset.FIST)
                        .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND,EpicFightSounds.BLADE_RUSH_FINISHER.get()),
                new AttackAnimation.Phase(0,100/60F,100/60F,110/60F, 140/60F, Float.MAX_VALUE, InteractionHand.OFF_HAND,Armatures.BIPED.get().toolL, ColliderPreset.FIST)
                        .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND,EpicFightSounds.BLADE_RUSH_FINISHER.get())
        ));

        ONE_HAND_EXECUTE = builder.nextAccessor("player/one_hand_execute", accessor -> new MEFExecuteAttackAnimation(0.15F, 0, 55/60F, accessor, Armatures.BIPED,
                new AttackAnimation.Phase(0,55/60F,55/60F,60/60F, 60/60F, 130/60F, InteractionHand.MAIN_HAND,Armatures.BIPED.get().toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND,EpicFightSounds.BLADE_RUSH_FINISHER.get()),
                new AttackAnimation.Phase(0,130/60F,130/60F,140/60F, 180/60F, Float.MAX_VALUE, InteractionHand.MAIN_HAND,Armatures.BIPED.get().toolR,null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND,EpicFightSounds.BLADE_RUSH_FINISHER.get())
        ));

        ONE_HAND_EXECUTE_HARD = builder.nextAccessor("player/one_hand_execute_hard", accessor -> new MEFExecuteAttackAnimation(0.15F, 0, 60/60F, accessor, Armatures.BIPED,
                new AttackAnimation.Phase(0,60/60F,60/60F,70/60F, 70/60F, 150/60F, InteractionHand.MAIN_HAND,Armatures.BIPED.get().toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND,EpicFightSounds.BLADE_RUSH_FINISHER.get()),
                new AttackAnimation.Phase(0,150/60F,150/60F,160/60F, 230/60F, Float.MAX_VALUE, InteractionHand.MAIN_HAND,Armatures.BIPED.get().toolR,null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND,EpicFightSounds.BLADE_RUSH_FINISHER.get())
        ));

        ARES_BIPED_COMMON_EXECUTE = builder.nextAccessor("biped/ares_biped_common_execute", accessor -> new MEFExecuteAttackAnimation(0.15F, 0, 45/60F, accessor, Armatures.BIPED,
                new AttackAnimation.Phase(0,45/60F,45/60F,55/60F, 190/60F, 190/60F, InteractionHand.MAIN_HAND,Armatures.BIPED.get().toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND,EpicFightSounds.BLADE_RUSH_FINISHER.get()),
                new AttackAnimation.Phase(0,195/60F,195/60F,205/60F, 250/60F, Float.MAX_VALUE, InteractionHand.MAIN_HAND,Armatures.BIPED.get().toolR,null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND,EpicFightSounds.BLADE_RUSH_FINISHER.get())
        ));

    }

    /**
     * 防御条件示例：伤害源在前方
     */
    public static DefenseTimePair.DefenseCondition FRONT = (ownerPatch, attackEntity, damageSource) ->{
        boolean isFront = false;
        Vec3 sourceLocation = damageSource.getSourcePosition();
        if (sourceLocation != null) {
            Vec3 viewVector = ownerPatch.getOriginal().getViewVector(1.0F);
            viewVector = viewVector.subtract(0, viewVector.y, 0).normalize();
            Vec3 toSourceLocation = sourceLocation.subtract(ownerPatch.getOriginal().position()).normalize();
            if (toSourceLocation.dot(viewVector) > 0.0D) {
                isFront = true;
            }
        }
        return isFront;
    };

    /**
     * 防御成功事件示例：火花
     */
    public static DefenseSuccessEvent<?> CLASH = DefenseSuccessEvent.SimpleEvent.create((ownerPatch, attackEntity, damageSource) ->{
        ownerPatch.playSound(EpicFightSounds.CLASH.get(), -0.05F, 0.1F);
        LivingEntity livingEntity = ownerPatch.getOriginal();
        if (livingEntity.level() instanceof ServerLevel serverLevel){
            EpicFightParticles.HIT_BLUNT.get().spawnParticleWithArgument(serverLevel, HitParticleType.FRONT_OF_EYES, HitParticleType.ZERO, livingEntity, attackEntity);
        }
    });

    /**
     * 防御成功事件示例：弹反
     */
    public static DefenseSuccessEvent<?> PARRY = DefenseSuccessEvent.SimpleEvent.create((defenderPatch, attackEntity, damageSource) ->{
        if (attackEntity instanceof LivingEntity livingAttacker){
            MEFEntityAPI.beParried(livingAttacker, defenderPatch);
        }
    });

    /**
     * 防御成功事件示例：盾完美弹反回满耐力
     */
    public static DefenseSuccessEvent<?> PERFECT_PARRY = DefenseSuccessEvent.TimePairEvent.create(0.5F,0.56F,(ownerPatch, attackEntity, damageSource) ->{
        if (ownerPatch instanceof PlayerPatch<?> patch){
            patch.setStamina(patch.getMaxStamina());
        }
    });




    @SubscribeEvent
    public static void registerAnimations(AnimationManager.AnimationRegistryEvent event) {
        event.newBuilder(MoreEpicFightMod.MOD_ID, MEFAnimations::buildAnimations);
    }
}
