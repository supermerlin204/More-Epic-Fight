package org.merlin204.mef.api.ponder;

import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.Selection;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.PonderSceneBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.LevelUtil;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class EpicFightSceneBuilder extends PonderSceneBuilder {

    public static final String PLAY_SPEED = "play_speed_in_ponder_level";

    private final EffectInstructions effects;
    private final WorldInstructions world;

    public EpicFightSceneBuilder(SceneBuilder baseSceneBuilder) {
        this(baseSceneBuilder.getScene());
    }

    private EpicFightSceneBuilder(PonderScene ponderScene) {
        super(ponderScene);
        effects = new EffectInstructions();
        world = new WorldInstructions();
    }

    @Override
    public @NotNull EffectInstructions effects() {
        return effects;
    }

    @Override
    public @NotNull WorldInstructions world() {
        return world;
    }

    public class EffectInstructions extends PonderEffectInstructions {
        //EF的残影需要实体，还是放world比较好
    }

    public class WorldInstructions extends PonderWorldInstructions {

        //裂地特效
        public void playGroundSlam(Vec3 pos, float radius) {
            addInstruction(ponderScene -> {
                LevelUtil.circleSlamFracture(null, ponderScene.getWorld(), pos, radius);
            });
        }

        public void playGroundSlam(Vec3 pos, float radius, boolean noSound, boolean noParticle) {
            addInstruction(ponderScene -> {
                LevelUtil.circleSlamFracture(null, ponderScene.getWorld(), pos, radius, noSound, noParticle);
            });
        }

        //残影粒子
        public <E extends LivingEntity, T extends LivingEntityPatch<?>> void addEntityAfterImageParticle(Class<E> entityClass) {
            modifyEntities(entityClass, entity -> {
                entity.level().addParticle(EpicFightParticles.WHITE_AFTERIMAGE.get(), entity.getX(), entity.getY(), entity.getZ(), Double.longBitsToDouble(entity.getId()), 0.0, 0.0);
            });
        }

        public <E extends LivingEntity, T extends LivingEntityPatch<?>> void addEntityAfterImageParticle(Class<E> entityClass, Vec3 pos) {
            modifyEntities(entityClass, entity -> {
                entity.level().addParticle(EpicFightParticles.WHITE_AFTERIMAGE.get(), pos.x(), pos.y(), pos.z(), Double.longBitsToDouble(entity.getId()), 0.0, 0.0);
            });
        }

        //基础的操作patch
        public <E extends LivingEntity, T extends LivingEntityPatch<?>> void modifyEntityPatches(Class<E> entityClass, Class<T> type, Consumer<T> entityPatchCallBack) {
            super.modifyEntities(entityClass, e -> EpicFightCapabilities.getUnparameterizedEntityPatch(e, type).ifPresent(entityPatchCallBack));
        }

        public <E extends LivingEntity, T extends LivingEntityPatch<?>> void modifyEntityPatchesInside(Class<E> entityClass, Selection area, Class<T> type, Consumer<T> entityPatchCallBack) {
            super.modifyEntitiesInside(entityClass, area, e -> EpicFightCapabilities.getUnparameterizedEntityPatch(e, type).ifPresent(entityPatchCallBack));
        }

        public <E extends LivingEntity, T extends LivingEntityPatch<?>> void modifyEntityPatch(ElementLink<EntityElement> link, Class<T> type, Consumer<T> entityPatchCallBack) {
            super.modifyEntity(link, e -> EpicFightCapabilities.getUnparameterizedEntityPatch(e, type).ifPresent(entityPatchCallBack));
        }

        //改动画播放速度
        public <E extends LivingEntity> void modifyEntitiesPlaySpeed(Class<E> entityClass, float playSpeed) {
            super.modifyEntities(entityClass, e -> e.getPersistentData().putFloat(PLAY_SPEED, playSpeed));
        }

        public<E extends LivingEntity> void modifyEntitiesInsidePlaySpeed(Class<E> entityClass, Selection area, float playSpeed) {
            super.modifyEntitiesInside(entityClass, area, e -> e.getPersistentData().putFloat(PLAY_SPEED, playSpeed));
        }

        public void modifyEntityPlaySpeed(ElementLink<EntityElement> link, float playSpeed) {
            super.modifyEntity(link, e -> e.getPersistentData().putFloat(PLAY_SPEED, playSpeed));
        }

        //播放动画相关预设
        public <E extends LivingEntity, A extends StaticAnimation> void playEntitiesAnimation(Class<E> entityClass, Selection area, AnimationManager.AnimationAccessor<A> animationAccessor, float transitionTimeModifier) {
            this.modifyEntityPatchesInside(entityClass, area, LivingEntityPatch.class, livingEntityPatch -> {
                livingEntityPatch.playAnimation(animationAccessor, transitionTimeModifier);
            });
        }

        public <E extends LivingEntity, A extends StaticAnimation> void playEntitiesAnimation(Class<E> entityClass, AnimationManager.AnimationAccessor<A> animationAccessor, float transitionTimeModifier) {
            this.modifyEntityPatches(entityClass, LivingEntityPatch.class, livingEntityPatch -> {
                livingEntityPatch.playAnimation(animationAccessor, transitionTimeModifier);
            });
        }

        public <A extends StaticAnimation> void playEntityAnimation(ElementLink<EntityElement> link, AnimationManager.AnimationAccessor<A> animationAccessor, float transitionTimeModifier) {
            this.modifyEntityPatch(link, LivingEntityPatch.class, livingEntityPatch -> {
                livingEntityPatch.playAnimation(animationAccessor, transitionTimeModifier);
            });
        }

        public <E extends LivingEntity, A extends StaticAnimation> void reserveAnimation(Class<E> entityClass, Selection area, AnimationManager.AnimationAccessor<A> animationAccessor) {
            this.modifyEntityPatchesInside(entityClass, area, LivingEntityPatch.class, patch -> patch.reserveAnimation(animationAccessor));
        }

        public <E extends LivingEntity, A extends StaticAnimation> void reserveAnimation(Class<E> entityClass, AnimationManager.AnimationAccessor<A> animationAccessor) {
            this.modifyEntityPatches(entityClass, LivingEntityPatch.class, patch -> patch.reserveAnimation(animationAccessor));
        }

        public <A extends StaticAnimation> void reserveAnimation(ElementLink<EntityElement> link, AnimationManager.AnimationAccessor<A> animationAccessor) {
            this.modifyEntityPatch(link, LivingEntityPatch.class, patch -> patch.reserveAnimation(animationAccessor));
        }

        public <E extends LivingEntity, A extends StaticAnimation> void playAnimationInstantly(Class<E> entityClass, Selection area, AnimationManager.AnimationAccessor<A> animationAccessor) {
            this.modifyEntityPatchesInside(entityClass, area, LivingEntityPatch.class, patch -> patch.playAnimationInstantly(animationAccessor));
        }

        public <E extends LivingEntity, A extends StaticAnimation> void playAnimationInstantly(Class<E> entityClass, AnimationManager.AnimationAccessor<A> animationAccessor) {
            this.modifyEntityPatches(entityClass, LivingEntityPatch.class, patch -> patch.playAnimationInstantly(animationAccessor));
        }

        public <A extends StaticAnimation> void playAnimationInstantly(ElementLink<EntityElement> link, AnimationManager.AnimationAccessor<A> animationAccessor) {
            this.modifyEntityPatch(link, LivingEntityPatch.class, patch -> patch.playAnimationInstantly(animationAccessor));
        }

        public <E extends LivingEntity, A extends StaticAnimation> void playAnimation(Class<E> entityClass, Selection area, AnimationManager.AnimationAccessor<A> animationAccessor, float transitionTimeModifier) {
            this.modifyEntityPatchesInside(entityClass, area, LivingEntityPatch.class, patch -> patch.playAnimation(animationAccessor, transitionTimeModifier));
        }

        public <E extends LivingEntity, A extends StaticAnimation> void playAnimation(Class<E> entityClass, AnimationManager.AnimationAccessor<A> animationAccessor, float transitionTimeModifier) {
            this.modifyEntityPatches(entityClass, LivingEntityPatch.class, patch -> patch.playAnimation(animationAccessor, transitionTimeModifier));
        }

        public <A extends StaticAnimation> void playAnimation(ElementLink<EntityElement> link, AnimationManager.AnimationAccessor<A> animationAccessor, float transitionTimeModifier) {
            this.modifyEntityPatch(link, LivingEntityPatch.class, patch -> patch.playAnimation(animationAccessor, transitionTimeModifier));
        }

        public <E extends LivingEntity, A extends StaticAnimation> void stopPlaying(Class<E> entityClass, Selection area, AnimationManager.AnimationAccessor<A> animationAccessor) {
            this.modifyEntityPatchesInside(entityClass, area, LivingEntityPatch.class, patch -> patch.stopPlaying(animationAccessor));
        }

        public <E extends LivingEntity, A extends StaticAnimation> void stopPlaying(Class<E> entityClass, AnimationManager.AnimationAccessor<A> animationAccessor) {
            this.modifyEntityPatches(entityClass, LivingEntityPatch.class, patch -> patch.stopPlaying(animationAccessor));
        }

        public <A extends StaticAnimation> void stopPlaying(ElementLink<EntityElement> link, AnimationManager.AnimationAccessor<A> animationAccessor) {
            this.modifyEntityPatch(link, LivingEntityPatch.class, patch -> patch.stopPlaying(animationAccessor));
        }

    }

}
