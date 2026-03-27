package com.github.alexthe666.iceandfire.entity.props;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

public class MeltData {
    public boolean isMelting;
    private boolean triggerClientUpdate;

    public void tickMelt(final LivingEntity entity) {
        if (entity.level().isClientSide()) return;

        boolean hasMelt = entity.hasEffect(
                com.github.alexthe666.iceandfire.effect.IafMobEffects.MELT.get());

        if (hasMelt != isMelting) {
            isMelting = hasMelt;
            triggerClientUpdate = true;
        }
    }

    public void serialize(final CompoundTag tag) {
        CompoundTag meltData = new CompoundTag();
        meltData.putBoolean("isMelting", isMelting);
        tag.put("meltData", meltData);
    }

    public void deserialize(final CompoundTag tag) {
        CompoundTag meltData = tag.getCompound("meltData");
        isMelting = meltData.getBoolean("isMelting");
    }

    public boolean doesClientNeedUpdate() {
        if (triggerClientUpdate) {
            triggerClientUpdate = false;
            return true;
        }
        return false;
    }
}
