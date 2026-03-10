package org.merlin204.mef.capability;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.Style;
import yesman.epicfight.world.capabilities.item.WeaponCapability;
import yesman.epicfight.world.capabilities.item.WeaponCategory;
import yesman.epicfight.world.entity.eventlistener.ComboCounterHandleEvent;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 允许武器自带专属的闪避(DODGE)和防御(GUARD)技能。
 * 支持按 Style 独立配置，也支持全 Style 通用的快速配置。
 * 请使用newAdvanceStyleCombo来代替原有的newStyleCombo
 */
public class AdvanceWeaponCapability extends WeaponCapability {

    protected final Map<Style, BiFunction<CapabilityItem, PlayerPatch<?>, Skill>> exclusiveDodges;
    protected final Map<Style, BiFunction<CapabilityItem, PlayerPatch<?>, Skill>> exclusiveGuards;

    protected final BiFunction<CapabilityItem, PlayerPatch<?>, Skill> defaultExclusiveDodge;
    protected final BiFunction<CapabilityItem, PlayerPatch<?>, Skill> defaultExclusiveGuard;

    protected AdvanceWeaponCapability(CapabilityItem.Builder builder) {
        super(builder);
        AdvanceBuilder advanceBuilder = (AdvanceBuilder) builder;

        this.exclusiveDodges = advanceBuilder.exclusiveDodges;
        this.exclusiveGuards = advanceBuilder.exclusiveGuards;

        this.defaultExclusiveDodge = advanceBuilder.defaultExclusiveDodge;
        this.defaultExclusiveGuard = advanceBuilder.defaultExclusiveGuard;
    }

    public Skill getExclusiveDodge(PlayerPatch<?> playerpatch, ItemStack itemstack) {
        Style style = this.getStyle(playerpatch);
        if (this.exclusiveDodges.containsKey(style)) {
            return this.exclusiveDodges.get(style).apply(this, playerpatch);
        }
        return this.defaultExclusiveDodge != null ? this.defaultExclusiveDodge.apply(this, playerpatch) : null;
    }

    public Skill getExclusiveGuard(PlayerPatch<?> playerpatch, ItemStack itemstack) {
        Style style = this.getStyle(playerpatch);
        if (this.exclusiveGuards.containsKey(style)) {
            return this.exclusiveGuards.get(style).apply(this, playerpatch);
        }
        return this.defaultExclusiveGuard != null ? this.defaultExclusiveGuard.apply(this, playerpatch) : null;
    }

    public static AdvanceBuilder builder() {
        return new AdvanceBuilder();
    }

    public static class AdvanceBuilder extends WeaponCapability.Builder {
        protected final Map<Style, BiFunction<CapabilityItem, PlayerPatch<?>, Skill>> exclusiveDodges;
        protected final Map<Style, BiFunction<CapabilityItem, PlayerPatch<?>, Skill>> exclusiveGuards;

        protected BiFunction<CapabilityItem, PlayerPatch<?>, Skill> defaultExclusiveDodge = null;
        protected BiFunction<CapabilityItem, PlayerPatch<?>, Skill> defaultExclusiveGuard = null;

        protected AdvanceBuilder() {
            super();
            this.constructor(AdvanceWeaponCapability::new);
            this.exclusiveDodges = Maps.newHashMap();
            this.exclusiveGuards = Maps.newHashMap();
        }

        public AdvanceBuilder exclusiveDodge(Style style, Skill skill) {
            this.exclusiveDodges.put(style, (cap, patch) -> skill);
            return this;
        }

        public AdvanceBuilder exclusiveGuard(Style style, Skill skill) {
            this.exclusiveGuards.put(style, (cap, patch) -> skill);
            return this;
        }

        public AdvanceBuilder exclusiveDodge(Skill skill) {
            this.defaultExclusiveDodge = (cap, patch) -> skill;
            return this;
        }

        public AdvanceBuilder exclusiveGuard(Skill skill) {
            this.defaultExclusiveGuard = (cap, patch) -> skill;
            return this;
        }

        @SafeVarargs
        public final AdvanceBuilder newAdvanceStyleCombo(Style style, AnimationAccessor<? extends AttackAnimation>... animation) {
            super.newStyleCombo(style, animation);
            return this;
        }

        @Override
        public AdvanceBuilder category(WeaponCategory category) {
            super.category(category);
            return this;
        }

        @Override
        public AdvanceBuilder styleProvider(Function<LivingEntityPatch<?>, Style> styleProvider) {
            super.styleProvider(styleProvider);
            return this;
        }

        @Override
        public AdvanceBuilder passiveSkill(Skill passiveSkill) {
            super.passiveSkill(passiveSkill);
            return this;
        }

        @Override
        public AdvanceBuilder swingSound(SoundEvent swingSound) {
            super.swingSound(swingSound);
            return this;
        }

        @Override
        public AdvanceBuilder hitSound(SoundEvent hitSound) {
            super.hitSound(hitSound);
            return this;
        }

        @Override
        public AdvanceBuilder hitParticle(HitParticleType hitParticle) {
            super.hitParticle(hitParticle);
            return this;
        }

        @Override
        public AdvanceBuilder collider(Collider collider) {
            super.collider(collider);
            return this;
        }

        @Override
        public AdvanceBuilder canBePlacedOffhand(boolean canBePlacedOffhand) {
            super.canBePlacedOffhand(canBePlacedOffhand);
            return this;
        }

        @Override
        public AdvanceBuilder reach(float reach) {
            super.reach(reach);
            return this;
        }

        @Override
        public AdvanceBuilder livingMotionModifier(Style wieldStyle, LivingMotion livingMotion, AnimationAccessor<? extends StaticAnimation> animation) {
            super.livingMotionModifier(wieldStyle, livingMotion, animation);
            return this;
        }

        @Override
        public AdvanceBuilder addStyleAttibutes(Style style, Pair<Attribute, AttributeModifier> attributePair) {
            super.addStyleAttibutes(style, attributePair);
            return this;
        }

        @Override
        public AdvanceBuilder weaponCombinationPredicator(Function<LivingEntityPatch<?>, Boolean> predicator) {
            super.weaponCombinationPredicator(predicator);
            return this;
        }

        @Override
        public AdvanceBuilder innateSkill(Style style, Function<ItemStack, Skill> innateSkill) {
            super.innateSkill(style, innateSkill);
            return this;
        }

        @Override
        @Deprecated
        public AdvanceBuilder comboCancel(Function<Style, Boolean> comboCancel) {
            super.comboCancel(comboCancel);
            return this;
        }

        @Override
        public AdvanceBuilder comboCounterHandler(ComboCounterHandleEvent.ComboCounterHandler comboHandler) {
            super.comboCounterHandler(comboHandler);
            return this;
        }

        @Override
        public AdvanceBuilder zoomInType(ZoomInType zoomInType) {
            super.zoomInType(zoomInType);
            return this;
        }
    }
}