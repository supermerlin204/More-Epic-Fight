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
import org.merlin204.mef.api.entity.MEFEntityAPI;
import org.merlin204.mef.api.animation.type.MEFActionAnimation;
import org.merlin204.mef.main.MoreEpicFightMod;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import java.util.List;

@Mod.EventBusSubscriber(modid = MoreEpicFightMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MEFAnimations {
    public static AnimationManager.AnimationAccessor<MEFActionAnimation> SHIELD_PARRY;
    public static AnimationManager.AnimationAccessor<MEFActionAnimation> PARRY_ONE_HAND;
    public static AnimationManager.AnimationAccessor<ActionAnimation> BIPED_BE_PARRIED_R;
    public static AnimationManager.AnimationAccessor<ActionAnimation> BIPED_BE_PARRIED_L;
    public static AnimationManager.AnimationAccessor<ActionAnimation> BIPED_BE_PARRIED_M;
    public static AnimationManager.AnimationAccessor<ActionAnimation> BIPED_BE_EXECUTED_START;
    public static AnimationManager.AnimationAccessor<ActionAnimation> BIPED_BE_EXECUTED_END;

    public static AnimationManager.AnimationAccessor<AttackAnimation> FIST_EXECUTE;
    public static AnimationManager.AnimationAccessor<AttackAnimation> ONE_HAND_EXECUTE;
    public static AnimationManager.AnimationAccessor<AttackAnimation> ONE_HAND_EXECUTE_HARD;



    public static void buildAnimations(AnimationManager.AnimationBuilder builder) {

        SHIELD_PARRY = builder.nextAccessor("player/shield_parry", accessor -> new MEFActionAnimation(0.1F, accessor, Armatures.BIPED)
                .addDefenseTimePair(DefenseTimePair.create(30/60F,40/60F).addDefenseConditions(FRONT).addDefenseSuccessEvents(CLASH,PARRY,PERFECT_PARRY)));
        PARRY_ONE_HAND = builder.nextAccessor("player/parry_one_hand", accessor -> new MEFActionAnimation(0.1F, accessor, Armatures.BIPED)
                .addDefenseTimePair(DefenseTimePair.create(10/60F,20/60F).addDefenseConditions(FRONT).addDefenseSuccessEvents(CLASH,PARRY)));
        BIPED_BE_PARRIED_R = builder.nextAccessor("biped/be_parried_r", accessor -> new ActionAnimation(0.15F, accessor, Armatures.BIPED));
        BIPED_BE_PARRIED_L = builder.nextAccessor("biped/be_parried_l", accessor -> new ActionAnimation(0.15F, accessor, Armatures.BIPED));
        BIPED_BE_PARRIED_M = builder.nextAccessor("biped/be_parried_m", accessor -> new ActionAnimation(0.15F, accessor, Armatures.BIPED));
        BIPED_BE_EXECUTED_START = builder.nextAccessor("biped/be_executed_start", accessor -> new ActionAnimation(0.15F, accessor, Armatures.BIPED));
        BIPED_BE_EXECUTED_END = builder.nextAccessor("biped/be_executed_end", accessor -> new ActionAnimation(0.15F, accessor, Armatures.BIPED));

        FIST_EXECUTE = builder.nextAccessor("player/fist_execute", accessor -> new AttackAnimation(0.15F, accessor, Armatures.BIPED,
                new AttackAnimation.Phase(0,35/60F,35/60F,40/60F, 40/60F, 100/60F, InteractionHand.OFF_HAND,Armatures.BIPED.get().toolL, ColliderPreset.FIST)
                        .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND,EpicFightSounds.BLADE_RUSH_FINISHER.get()),
                new AttackAnimation.Phase(0,100/60F,100/60F,110/60F, 140/60F, Float.MAX_VALUE, InteractionHand.OFF_HAND,Armatures.BIPED.get().toolL, ColliderPreset.FIST)
                        .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND,EpicFightSounds.BLADE_RUSH_FINISHER.get())
        ).addProperty(MEFAnimationProperty.IS_EXECUTE_ANIMATION,true).addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER,(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1));

        ONE_HAND_EXECUTE = builder.nextAccessor("player/one_hand_execute", accessor -> new AttackAnimation(0.15F, accessor, Armatures.BIPED,
                new AttackAnimation.Phase(0,55/60F,55/60F,60/60F, 60/60F, 130/60F, InteractionHand.MAIN_HAND,Armatures.BIPED.get().toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND,EpicFightSounds.BLADE_RUSH_FINISHER.get()),
                new AttackAnimation.Phase(0,130/60F,130/60F,140/60F, 180/60F, Float.MAX_VALUE, InteractionHand.MAIN_HAND,Armatures.BIPED.get().toolR,null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND,EpicFightSounds.BLADE_RUSH_FINISHER.get())
        ).addProperty(MEFAnimationProperty.IS_EXECUTE_ANIMATION,true).addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER,(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1));

        ONE_HAND_EXECUTE_HARD = builder.nextAccessor("player/one_hand_execute_hard", accessor -> new AttackAnimation(0.15F, accessor, Armatures.BIPED,
                new AttackAnimation.Phase(0,60/60F,60/60F,70/60F, 70/60F, 150/60F, InteractionHand.MAIN_HAND,Armatures.BIPED.get().toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND,EpicFightSounds.BLADE_RUSH_FINISHER.get()),
                new AttackAnimation.Phase(0,150/60F,150/60F,160/60F, 230/60F, Float.MAX_VALUE, InteractionHand.MAIN_HAND,Armatures.BIPED.get().toolR,null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND,EpicFightSounds.BLADE_RUSH_FINISHER.get())
        ).addProperty(MEFAnimationProperty.IS_EXECUTE_ANIMATION,true).addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER,(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1));

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
    public static DefenseSuccessEvent<?> PARRY = DefenseSuccessEvent.SimpleEvent.create((ownerPatch, attackEntity, damageSource) ->{
        EpicFightCapabilities.getUnparameterizedEntityPatch(attackEntity, LivingEntityPatch.class).ifPresent(MEFEntityAPI::beParried);
    });

    /**
     * 防御成功事件示例：盾完美弹反回满耐力
     */
    public static DefenseSuccessEvent<?> PERFECT_PARRY = DefenseSuccessEvent.TimePairEvent.create(0.5F,0.56F,(ownerPatch, attackEntity, damageSource) ->{
        if (ownerPatch instanceof PlayerPatch<?> patch){
            patch.setStamina(9999999);
        }
    });




    @SubscribeEvent
    public static void registerAnimations(AnimationManager.AnimationRegistryEvent event) {
        event.newBuilder(MoreEpicFightMod.MOD_ID, MEFAnimations::buildAnimations);
    }
}
