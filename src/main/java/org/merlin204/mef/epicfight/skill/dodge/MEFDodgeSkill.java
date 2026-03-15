package org.merlin204.mef.epicfight.skill.dodge;

import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.network.client.CPSkillRequest;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.Style;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

import java.util.List;
import java.util.Map;

/**
 * MEFDodgeSkill:更好的闪避技能类，自带兼容越肩，Leawind的八向闪避功能；可为不同武器的不同style配置不同的闪避动画，新增了无方向输入时的默认闪避动画（五号位动画）
 */
public class MEFDodgeSkill extends Skill {

    public static final int DIRECTION_FORWARD = 0;
    public static final int DIRECTION_BACKWARD = 1;
    public static final int DIRECTION_LEFT = 2;
    public static final int DIRECTION_RIGHT = 3;
    public static final int DIRECTION_UP = 4;

    protected final Map<WeaponCategory, Map<Style, Map<Integer, AnimationAccessor<? extends StaticAnimation>>>> animations;

    protected final AnimationAccessor<? extends StaticAnimation> forwardAnim;
    protected final AnimationAccessor<? extends StaticAnimation> backwardAnim;
    protected final AnimationAccessor<? extends StaticAnimation> leftAnim;
    protected final AnimationAccessor<? extends StaticAnimation> rightAnim;
    protected final AnimationAccessor<? extends StaticAnimation> upAnim;

    public MEFDodgeSkill(Builder builder) {
        super(builder);
        this.animations = builder.animations;
        this.forwardAnim = builder.forwardAnim;
        this.backwardAnim = builder.backwardAnim;
        this.leftAnim = builder.leftAnim;
        this.rightAnim = builder.rightAnim;
        this.upAnim = builder.upAnim;
    }

    public static Builder createEFNDodgeBuilder() {
        return new Builder()
                .setCategory(SkillCategories.DODGE)
                .setActivateType(ActivateType.ONE_SHOT)
                .setResource(Resource.STAMINA);
    }

    private AnimationAccessor<? extends StaticAnimation> getAnimationForWeapon(WeaponCategory weaponCategory,
                                                                               Style style,
                                                                               int direction) {

        if (this.animations.containsKey(weaponCategory)) {
            Map<Style, Map<Integer, AnimationAccessor<? extends StaticAnimation>>> styleMap =
                    this.animations.get(weaponCategory);

            if (styleMap.containsKey(style)) {
                Map<Integer, AnimationAccessor<? extends StaticAnimation>> directionMap = styleMap.get(style);
                if (directionMap.containsKey(direction)) {
                    return directionMap.get(direction);
                }
            }
            if (styleMap.containsKey(CapabilityItem.Styles.ONE_HAND)) {
                Map<Integer, AnimationAccessor<? extends StaticAnimation>> directionMap =
                        styleMap.get(CapabilityItem.Styles.ONE_HAND);
                if (directionMap.containsKey(direction)) {
                    return directionMap.get(direction);
                }
            }
            if (styleMap.containsKey(CapabilityItem.Styles.TWO_HAND)) {
                Map<Integer, AnimationAccessor<? extends StaticAnimation>> directionMap =
                        styleMap.get(CapabilityItem.Styles.TWO_HAND);
                if (directionMap.containsKey(direction)) {
                    return directionMap.get(direction);
                }
            }
        }

        return switch (direction) {
            case DIRECTION_FORWARD -> this.forwardAnim;
            case DIRECTION_BACKWARD -> this.backwardAnim;
            case DIRECTION_LEFT -> this.leftAnim;
            case DIRECTION_RIGHT -> this.rightAnim;
            case DIRECTION_UP -> this.upAnim;
            default -> this.forwardAnim;
        };
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Object getExecutionPacket(SkillContainer skillContainer, FriendlyByteBuf args) {
        LocalPlayerPatch executor = skillContainer.getClientExecutor();
        LocalPlayer localPlayer = executor.getOriginal();
        float pulse = Mth.clamp(0.3F + EnchantmentHelper.getSneakingSpeedBonus(executor.getOriginal()), 0.0F, 1.0F);
        Input input = localPlayer.input;
        input.tick(false, pulse);

        int forward = input.up ? 1 : 0;
        int backward = input.down ? -1 : 0;
        int left = input.left ? 1 : 0;
        int right = input.right ? -1 : 0;
        int vertical = forward + backward;
        int horizon = left + right;
        float yRot = Minecraft.getInstance().gameRenderer.getMainCamera().getYRot();
        float degree;
        int animation;
        boolean isInAir = !executor.getOriginal().onGround();

        if (vertical == 0 && horizon == 0) {
            
            if (!isInAir) {
                animation = DIRECTION_UP; 
                degree = yRot;
            } else {
                animation = DIRECTION_FORWARD;
                degree = yRot;
            }
        } else if (vertical == 0) {
            
            if (this.leftAnim == null || this.rightAnim == null) {
                animation = DIRECTION_FORWARD;
                degree = yRot + (horizon >= 0 ? -90.0f : 90.0f);
            } else {
                animation = horizon >= 0 ? DIRECTION_LEFT : DIRECTION_RIGHT;
                degree = yRot;
            }
        } else {
            
            animation = vertical >= 0 ? DIRECTION_FORWARD : DIRECTION_BACKWARD;
            degree = (float) (-((45 * vertical * horizon))) + yRot;
        }

        CPSkillRequest packet = new CPSkillRequest(skillContainer.getSlot());
        packet.getBuffer().writeInt(animation);
        packet.getBuffer().writeFloat(degree);
        packet.getBuffer().writeBoolean(isInAir); 
        return packet;
    }

    @OnlyIn(Dist.CLIENT)
    public List<Object> getTooltipArgsOfScreen(List<Object> list) {
        list.add(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(this.consumption));
        return list;
    }

    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
        
    }

    @Override
    public void executeOnServer(SkillContainer skillContainer, FriendlyByteBuf args) {
        super.executeOnServer(skillContainer, args);
        ServerPlayerPatch executor = skillContainer.getServerExecutor();

        
        int direction = args.readInt();
        float yRot = args.readFloat();
        boolean isInAir = args.readBoolean(); 

        CapabilityItem holdingItem = executor.getHoldingItemCapability(net.minecraft.world.InteractionHand.MAIN_HAND);
        WeaponCategory weaponCategory = holdingItem.getWeaponCategory();
        Style style = holdingItem.getStyle(executor);

        AnimationAccessor<? extends StaticAnimation> animation = getAnimationForWeapon(weaponCategory, style, direction);
        if (animation != null) {
            executor.playAnimationSynchronized(animation, 0);
            executor.setModelYRot(yRot, true);
        }
    }

    @Override
    public boolean isExecutableState(PlayerPatch<?> executor) {
        EntityState playerState = executor.getEntityState();
        Level level  = executor.getOriginal().level();
        BlockState blockState = level.getBlockState(executor.getOriginal().getOnPos().below());

        return !executor.isInAir() 
                && playerState.canUseSkill()
                && !(executor.getOriginal().isInWater() && (blockState.isAir() || blockState.is(Blocks.WATER) || blockState.is(Blocks.LAVA)))
                && !executor.getOriginal().onClimbable()
                && executor.getOriginal().getVehicle() == null;
    }

    public static class Builder extends SkillBuilder<MEFDodgeSkill> {
        protected final Map<WeaponCategory, Map<Style, Map<Integer, AnimationAccessor<? extends StaticAnimation>>>> animations = Maps.newHashMap();

        protected AnimationAccessor<? extends StaticAnimation> forwardAnim;
        protected AnimationAccessor<? extends StaticAnimation> backwardAnim;
        protected AnimationAccessor<? extends StaticAnimation> leftAnim;
        protected AnimationAccessor<? extends StaticAnimation> rightAnim;
        protected AnimationAccessor<? extends StaticAnimation> upAnim;

        public Builder setDefaultAnimations(AnimationAccessor<? extends StaticAnimation> forwardAnim,
                                            AnimationAccessor<? extends StaticAnimation> backwardAnim) {
            this.forwardAnim = forwardAnim;
            this.backwardAnim = backwardAnim;
            this.leftAnim = forwardAnim;
            this.rightAnim = forwardAnim;
            this.upAnim = forwardAnim;
            return this;
        }

        public Builder setDefaultAnimations(AnimationAccessor<? extends StaticAnimation> forwardAnim,
                                            AnimationAccessor<? extends StaticAnimation> backwardAnim,
                                            AnimationAccessor<? extends StaticAnimation> leftAnim,
                                            AnimationAccessor<? extends StaticAnimation> rightAnim) {
            this.forwardAnim = forwardAnim;
            this.backwardAnim = backwardAnim;
            this.leftAnim = leftAnim;
            this.rightAnim = rightAnim;
            this.upAnim = forwardAnim;
            return this;
        }

        public Builder setUpAnimation(AnimationAccessor<? extends StaticAnimation> upAnim) {
            this.upAnim = upAnim;
            return this;
        }

        public Builder setDefaultAnimations(AnimationAccessor<? extends StaticAnimation> forwardAnim,
                                            AnimationAccessor<? extends StaticAnimation> backwardAnim,
                                            AnimationAccessor<? extends StaticAnimation> leftAnim,
                                            AnimationAccessor<? extends StaticAnimation> rightAnim,
                                            AnimationAccessor<? extends StaticAnimation> upAnim) {
            this.forwardAnim = forwardAnim;
            this.backwardAnim = backwardAnim;
            this.leftAnim = leftAnim;
            this.rightAnim = rightAnim;
            this.upAnim = upAnim;
            return this;
        }

        public Builder addAnimation(WeaponCategory weaponCategory,
                                    Style style,
                                    int direction,
                                    AnimationAccessor<? extends StaticAnimation> animation) {
            this.animations.computeIfAbsent(weaponCategory, k -> Maps.newHashMap());
            this.animations.get(weaponCategory).computeIfAbsent(style, k -> Maps.newHashMap());
            this.animations.get(weaponCategory).get(style).put(direction, animation);
            return this;
        }

        public Builder addAnimationsForWeapon(WeaponCategory weaponCategory,
                                              Style style,
                                              AnimationAccessor<? extends StaticAnimation> forwardAnim,
                                              AnimationAccessor<? extends StaticAnimation> backwardAnim) {
            this.addAnimation(weaponCategory, style, DIRECTION_FORWARD, forwardAnim);
            this.addAnimation(weaponCategory, style, DIRECTION_BACKWARD, backwardAnim);
            this.addAnimation(weaponCategory, style, DIRECTION_LEFT, forwardAnim);
            this.addAnimation(weaponCategory, style, DIRECTION_RIGHT, forwardAnim);
            this.addAnimation(weaponCategory, style, DIRECTION_UP, forwardAnim);
            return this;
        }

        public Builder addAnimationsForWeapon(WeaponCategory weaponCategory,
                                              Style style,
                                              AnimationAccessor<? extends StaticAnimation> forwardAnim,
                                              AnimationAccessor<? extends StaticAnimation> backwardAnim,
                                              AnimationAccessor<? extends StaticAnimation> leftAnim,
                                              AnimationAccessor<? extends StaticAnimation> rightAnim) {
            this.addAnimation(weaponCategory, style, DIRECTION_FORWARD, forwardAnim);
            this.addAnimation(weaponCategory, style, DIRECTION_BACKWARD, backwardAnim);
            this.addAnimation(weaponCategory, style, DIRECTION_LEFT, leftAnim);
            this.addAnimation(weaponCategory, style, DIRECTION_RIGHT, rightAnim);
            this.addAnimation(weaponCategory, style, DIRECTION_UP, forwardAnim);
            return this;
        }

        public Builder addAnimationsForWeapon(WeaponCategory weaponCategory,
                                              Style style,
                                              AnimationAccessor<? extends StaticAnimation> forwardAnim,
                                              AnimationAccessor<? extends StaticAnimation> backwardAnim,
                                              AnimationAccessor<? extends StaticAnimation> leftAnim,
                                              AnimationAccessor<? extends StaticAnimation> rightAnim,
                                              AnimationAccessor<? extends StaticAnimation> upAnim) {
            this.addAnimation(weaponCategory, style, DIRECTION_FORWARD, forwardAnim);
            this.addAnimation(weaponCategory, style, DIRECTION_BACKWARD, backwardAnim);
            this.addAnimation(weaponCategory, style, DIRECTION_LEFT, leftAnim);
            this.addAnimation(weaponCategory, style, DIRECTION_RIGHT, rightAnim);
            this.addAnimation(weaponCategory, style, DIRECTION_UP, upAnim);
            return this;
        }

        public Builder addAnimationsForWeaponAllStyles(WeaponCategory weaponCategory,
                                                       AnimationAccessor<? extends StaticAnimation> forwardAnim,
                                                       AnimationAccessor<? extends StaticAnimation> backwardAnim) {
            this.addAnimationsForWeapon(weaponCategory, CapabilityItem.Styles.ONE_HAND, forwardAnim, backwardAnim);
            this.addAnimationsForWeapon(weaponCategory, CapabilityItem.Styles.TWO_HAND, forwardAnim, backwardAnim);
            this.addAnimationsForWeapon(weaponCategory, CapabilityItem.Styles.COMMON, forwardAnim, backwardAnim);
            this.addAnimationsForWeapon(weaponCategory, CapabilityItem.Styles.SHEATH, forwardAnim, backwardAnim);
            this.addAnimationsForWeapon(weaponCategory, CapabilityItem.Styles.OCHS, forwardAnim, backwardAnim);
            return this;
        }

        public Builder addAnimationsForWeaponAllStyles(WeaponCategory weaponCategory,
                                                       AnimationAccessor<? extends StaticAnimation> forwardAnim,
                                                       AnimationAccessor<? extends StaticAnimation> backwardAnim,
                                                       AnimationAccessor<? extends StaticAnimation> leftAnim,
                                                       AnimationAccessor<? extends StaticAnimation> rightAnim) {
            this.addAnimationsForWeapon(weaponCategory, CapabilityItem.Styles.ONE_HAND,
                    forwardAnim, backwardAnim, leftAnim, rightAnim);
            this.addAnimationsForWeapon(weaponCategory, CapabilityItem.Styles.TWO_HAND,
                    forwardAnim, backwardAnim, leftAnim, rightAnim);
            this.addAnimationsForWeapon(weaponCategory, CapabilityItem.Styles.COMMON,
                    forwardAnim, backwardAnim, leftAnim, rightAnim);
            this.addAnimationsForWeapon(weaponCategory, CapabilityItem.Styles.SHEATH,
                    forwardAnim, backwardAnim, leftAnim, rightAnim);
            this.addAnimationsForWeapon(weaponCategory, CapabilityItem.Styles.OCHS,
                    forwardAnim, backwardAnim, leftAnim, rightAnim);
            return this;
        }

        public Builder addAnimationsForWeaponAllStyles(WeaponCategory weaponCategory,
                                                       AnimationAccessor<? extends StaticAnimation> forwardAnim,
                                                       AnimationAccessor<? extends StaticAnimation> backwardAnim,
                                                       AnimationAccessor<? extends StaticAnimation> leftAnim,
                                                       AnimationAccessor<? extends StaticAnimation> rightAnim,
                                                       AnimationAccessor<? extends StaticAnimation> upAnim) {
            this.addAnimationsForWeapon(weaponCategory, CapabilityItem.Styles.ONE_HAND,
                    forwardAnim, backwardAnim, leftAnim, rightAnim, upAnim);
            this.addAnimationsForWeapon(weaponCategory, CapabilityItem.Styles.TWO_HAND,
                    forwardAnim, backwardAnim, leftAnim, rightAnim, upAnim);
            this.addAnimationsForWeapon(weaponCategory, CapabilityItem.Styles.COMMON,
                    forwardAnim, backwardAnim, leftAnim, rightAnim, upAnim);
            this.addAnimationsForWeapon(weaponCategory, CapabilityItem.Styles.SHEATH,
                    forwardAnim, backwardAnim, leftAnim, rightAnim, upAnim);
            this.addAnimationsForWeapon(weaponCategory, CapabilityItem.Styles.OCHS,
                    forwardAnim, backwardAnim, leftAnim, rightAnim, upAnim);
            return this;
        }
    }
}