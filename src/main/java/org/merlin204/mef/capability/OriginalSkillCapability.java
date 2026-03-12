package org.merlin204.mef.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class OriginalSkillCapability {

    public static final Capability<IOriginalSkillMemory> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});

    public interface IOriginalSkillMemory {
        boolean hasSkill(String slotName);
        void saveSkill(String slotName, String skillId);
        String getSkill(String slotName);
        void removeSkill(String slotName);
        CompoundTag serializeNBT();
        void deserializeNBT(CompoundTag nbt);
    }

    public static class OriginalSkillMemory implements IOriginalSkillMemory {
        private final Map<String, String> memory = new HashMap<>();

        @Override
        public boolean hasSkill(String slotName) {
            return memory.containsKey(slotName);
        }

        @Override
        public void saveSkill(String slotName, String skillId) {
            memory.put(slotName, skillId == null ? "none" : skillId);
        }

        @Override
        public String getSkill(String slotName) {
            return memory.get(slotName);
        }

        @Override
        public void removeSkill(String slotName) {
            memory.remove(slotName);
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            memory.forEach(tag::putString);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            memory.clear();
            for (String key : nbt.getAllKeys()) {
                memory.put(key, nbt.getString(key));
            }
        }
    }

    public static class Provider implements ICapabilitySerializable<CompoundTag> {
        private final IOriginalSkillMemory memory = new OriginalSkillMemory();
        private final LazyOptional<IOriginalSkillMemory> optional = LazyOptional.of(() -> memory);

        @NotNull
        @Override
        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return cap == INSTANCE ? optional.cast() : LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            return memory.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            memory.deserializeNBT(nbt);
        }
    }
}