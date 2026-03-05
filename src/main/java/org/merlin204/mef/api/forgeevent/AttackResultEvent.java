package org.merlin204.mef.api.forgeevent;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.merlin204.mef.api.stamina.StaminaType;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.Map;


/**
 * 修改攻击结果用的事件
 */
public class AttackResultEvent extends Event implements IModBusEvent {
    private AttackResult result;
    private final DamageSource source;
    private final LivingEntity beAttacked;
    private final float damage;

    public AttackResultEvent(AttackResult result, DamageSource source, LivingEntity beAttacked, float damage) {
        this.result = result;
        this.source = source;
        this.beAttacked = beAttacked;
        this.damage = damage;
    }

    public float getDamage() {
        return damage;
    }

    public AttackResult getAttackResult() {
        return result;
    }

    public LivingEntity getBeAttacked() {
        return beAttacked;
    }

    public DamageSource getSource() {
        return source;
    }

    public void setAttackResult(AttackResult result) {
        this.result = result;
    }
}
