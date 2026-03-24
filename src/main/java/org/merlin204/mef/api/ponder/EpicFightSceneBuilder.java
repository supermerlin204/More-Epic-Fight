package org.merlin204.mef.api.ponder;

import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.Selection;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.PonderSceneBuilder;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.LevelUtil;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public class EpicFightSceneBuilder extends PonderSceneBuilder {

    public static final String PLAY_SPEED = "play_speed_in_ponder_level";

    private final EpicFightEffectInstructions effects;
    private final EpicFightWorldInstructions world;

    public EpicFightSceneBuilder(SceneBuilder baseSceneBuilder) {
        this(baseSceneBuilder.getScene());
    }

    private EpicFightSceneBuilder(PonderScene ponderScene) {
        super(ponderScene);
        effects = new EpicFightEffectInstructions();
        world = new EpicFightWorldInstructions();
    }

    @Override
    public @NotNull EpicFightSceneBuilder.EpicFightEffectInstructions effects() {
        return effects;
    }

    @Override
    public @NotNull EpicFightSceneBuilder.EpicFightWorldInstructions world() {
        return world;
    }

    public class EpicFightEffectInstructions extends PonderEffectInstructions {
    }

    public class EpicFightWorldInstructions extends PonderWorldInstructions {
        public void waitForState(ElementLink<EntityElement> link, Predicate<EntityState> statePredicate) {
            addInstruction(new PonderInstruction() {
                private boolean complete = false;

                @Override
                public boolean isComplete() { return complete; }

                @Override
                public boolean isBlocking() { return true; }

                @Override
                public void reset(PonderScene scene) {
                    this.complete = false;
                }

                @Override
                public void tick(PonderScene scene) {
                    EntityElement element = scene.resolve(link);
                    if (element != null) {
                        element.ifPresent(entity -> {
                            if (entity instanceof LivingEntity livingEntity) {
                                EpicFightCapabilities.getUnparameterizedEntityPatch(livingEntity, LivingEntityPatch.class).ifPresent(patch -> {
                                    if (statePredicate.test(patch.getEntityState())) {
                                        complete = true;
                                    }
                                });
                            }
                        });
                    } else {
                        complete = true;
                    }
                }
            });
        }

        /**
         * 实体状态
         */
        public void waitForComboInput(ElementLink<EntityElement> link) {
            waitForState(link, state -> state.canBasicAttack() || !state.inaction());
        }

        public void waitForInaction(ElementLink<EntityElement> link) {
            waitForState(link, state -> !state.inaction());
        }

        public void waitForCanBasicAttack(ElementLink<EntityElement> link) {
            waitForState(link, EntityState::canBasicAttack);
        }

        public void waitForCanUseSkill(ElementLink<EntityElement> link) {
            waitForState(link, EntityState::canUseSkill);
        }

        /**
         * 动画播放的时间进度
         */
        public void waitForAnimationProgress(ElementLink<EntityElement> link, float targetPercentage) {
            addInstruction(new PonderInstruction() {
                private boolean complete = false;

                @Override
                public boolean isComplete() { return complete; }

                @Override
                public boolean isBlocking() { return true; }

                @Override
                public void reset(PonderScene scene) {
                    this.complete = false;
                }

                @Override
                public void tick(PonderScene scene) {
                    EntityElement element = scene.resolve(link);
                    if (element != null) {
                        element.ifPresent(entity -> {
                            if (entity instanceof LivingEntity livingEntity) {
                                EpicFightCapabilities.getUnparameterizedEntityPatch(livingEntity, LivingEntityPatch.class).ifPresent(patch -> {
                                    AnimationPlayer player = patch.getAnimator().getPlayerFor(null);
                                    DynamicAnimation currentAnim = null;
                                    if (player != null) {
                                        currentAnim = player.getAnimation().get();
                                    }

                                    if (currentAnim == null) {
                                        complete = true;
                                        return;
                                    }

                                    float totalTime = currentAnim.getTotalTime();
                                    if (totalTime > 0) {
                                        float progress = player.getElapsedTime() / totalTime;
                                        if (progress >= targetPercentage) {
                                            complete = true;
                                        }
                                    } else {
                                        complete = true;
                                    }
                                });
                            }
                        });
                    } else {
                        complete = true;
                    }
                }
            });
        }

        /**
         * 动画播放的时间
         */
        public void waitForAnimationTime(ElementLink<EntityElement> link, float targetTimeSeconds) {
            addInstruction(new PonderInstruction() {
                private boolean complete = false;

                @Override
                public boolean isComplete() { return complete; }

                @Override
                public boolean isBlocking() { return true; }

                @Override
                public void reset(PonderScene scene) {
                    this.complete = false;
                }

                @Override
                public void tick(PonderScene scene) {
                    EntityElement element = scene.resolve(link);
                    if (element != null) {
                        element.ifPresent(entity -> {
                            if (entity instanceof LivingEntity livingEntity) {
                                EpicFightCapabilities.getUnparameterizedEntityPatch(livingEntity, LivingEntityPatch.class).ifPresent(patch -> {
                                    AnimationPlayer player = patch.getAnimator().getPlayerFor(null);
                                    if (player != null && (player.getAnimation() == null || player.getElapsedTime() >= targetTimeSeconds)) {
                                        complete = true;
                                    }
                                });
                            }
                        });
                    } else {
                        complete = true;
                    }
                }
            });
        }

        public void playGroundSlam(Vec3 pos, float radius) {
            addInstruction(PonderInstruction.simple(ponderScene -> {
                LevelUtil.circleSlamFracture(null, ponderScene.getWorld(), pos, radius);
            }));
        }

        public void playGroundSlam(Vec3 pos, float radius, boolean noSound, boolean noParticle) {
            addInstruction(PonderInstruction.simple(ponderScene -> {
                LevelUtil.circleSlamFracture(null, ponderScene.getWorld(), pos, radius, noSound, noParticle);
            }));
        }

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

        public <E extends LivingEntity, T extends LivingEntityPatch<?>> void modifyEntityPatches(Class<E> entityClass, Class<T> type, Consumer<T> entityPatchCallBack) {
            super.modifyEntities(entityClass, livingEntity -> {
                EpicFightCapabilities.getUnparameterizedEntityPatch(livingEntity, type).ifPresent(entityPatchCallBack);
            });
        }

        public <E extends LivingEntity, T extends LivingEntityPatch<?>> void modifyEntityPatchesInside(Class<E> entityClass, Selection area, Class<T> type, Consumer<T> entityPatchCallBack) {
            super.modifyEntitiesInside(entityClass, area, livingEntity -> {
                EpicFightCapabilities.getUnparameterizedEntityPatch(livingEntity, type).ifPresent(entityPatchCallBack);
            });
        }

        public <E extends LivingEntity, T extends LivingEntityPatch<?>> void modifyEntityPatch(ElementLink<EntityElement> link, Class<T> type, Consumer<T> entityPatchCallBack) {
            super.modifyEntity(link, e -> {
                if (e instanceof LivingEntity livingEntity) {
                    EpicFightCapabilities.getUnparameterizedEntityPatch(livingEntity, type).ifPresent(entityPatchCallBack);
                }
            });
        }

        //调速
        public <E extends LivingEntity> void modifyEntitiesPlaySpeed(Class<E> entityClass, float playSpeed) {
            super.modifyEntities(entityClass, e -> e.getPersistentData().putFloat(PLAY_SPEED, playSpeed));
        }

        public <E extends LivingEntity> void modifyEntitiesInsidePlaySpeed(Class<E> entityClass, Selection area, float playSpeed) {
            super.modifyEntitiesInside(entityClass, area, e -> e.getPersistentData().putFloat(PLAY_SPEED, playSpeed));
        }

        public void modifyEntityPlaySpeed(ElementLink<EntityElement> link, float playSpeed) {
            super.modifyEntity(link, e -> e.getPersistentData().putFloat(PLAY_SPEED, playSpeed));
        }


        //播动画
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