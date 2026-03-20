package org.merlin204.mef.world.entity.ai.attribute;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

import java.util.HashMap;
import java.util.Map;

public class MEFAttributeSupplier extends AttributeSupplier {
    private static Map<Attribute, AttributeInstance> putEpicFightAttributes(Map<Attribute, AttributeInstance> originalMap) {
        AttributeSupplier supplier = AttributeSupplier.builder()
                .add(EpicFightAttributes.STAMINA_REGEN.get())
                .add(EpicFightAttributes.MAX_STAMINA.get())
                .build();

        Map<Attribute, AttributeInstance> newMap = new HashMap<>(supplier.instances);
        newMap.putAll(originalMap);

        return ImmutableMap.copyOf(newMap);
    }

    public MEFAttributeSupplier(AttributeSupplier copy) {
        super(putEpicFightAttributes(copy.instances));
    }
}
