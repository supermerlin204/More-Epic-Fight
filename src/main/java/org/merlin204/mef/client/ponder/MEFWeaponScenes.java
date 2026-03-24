package org.merlin204.mef.client.ponder;

import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.merlin204.mef.api.ponder.EpicFightSceneBuilder;
import org.merlin204.mef.entity.DummyPlayerEntity;
import org.merlin204.mef.registry.MEFEntities;
import reascer.wom.gameasset.animations.weapons.AnimsMoonless;
import reascer.wom.gameasset.animations.weapons.AnimsSolar;
import reascer.wom.world.item.WOMItems;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.item.WeaponCapability;

import java.util.List;

public class MEFWeaponScenes {

    public static void showcaseWeaponCombo(SceneBuilder baseScene, SceneBuildingUtil util) {
        EpicFightSceneBuilder scene = new EpicFightSceneBuilder(baseScene);

        baseScene.title("weapon_basic_combo", "武器基础连段演示");
        baseScene.configureBasePlate(0, 0, 5);
        baseScene.showBasePlate();
        baseScene.scaleSceneView(1.5F);

        baseScene.idle(5);

        ItemStack displayItem = new ItemStack(WOMItems.SOLAR.get());

        List<AnimationManager.AnimationAccessor<? extends AttackAnimation>> comboMotions = null;
        var itemCap = EpicFightCapabilities.getItemStackCapability(displayItem);
        if (itemCap instanceof WeaponCapability weaponCap) {
            comboMotions = weaponCap.getAutoAttackMotion(null);
        }

        ElementLink<EntityElement> attackerLink = baseScene.world().createEntity(level -> {
            LivingEntity attacker = MEFEntities.DUMMY_PLAYER.get().create(level);
            if (attacker != null) {
                attacker.setPos(2.5, 1, 2.5);
                attacker.setYRot(180);
                attacker.yBodyRot = 180;
                attacker.yHeadRot = 180;
                attacker.setItemInHand(InteractionHand.MAIN_HAND, displayItem);

                EpicFightCapabilities.getUnparameterizedEntityPatch(attacker, LivingEntityPatch.class).ifPresent(patch -> {
                    patch.setYRot(180);
                    patch.setYRotO(180);
                    patch.getClientAnimator().resetLivingAnimations();
                    patch.playAnimationInClientSide(AnimsSolar.SOLAR_IDLE, 0.0F);
                });
            }
            return attacker;
        });

        baseScene.idle(10);

        baseScene.overlay().showText(30)
                .text("基础连招展示")
                .pointAt(util.vector().topOf(2, 2, 2))
                .placeNearTarget();

        baseScene.idle(5);

        if (comboMotions != null) {
            for (AnimationManager.AnimationAccessor<? extends AttackAnimation> currentMotion : comboMotions) {
                scene.world().playEntitiesAnimation(DummyPlayerEntity.class, currentMotion, 0.0F);
                scene.world().waitForComboInput(attackerLink);
            }
        }

        baseScene.idle(20);
        baseScene.markAsFinished();
    }
}