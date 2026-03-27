package com.github.alexthe666.iceandfire.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public class MobEffectFrostburn extends MobEffect {

    private static final UUID SPEED_MODIFIER_UUID = UUID.fromString("d2e3f4a5-b6c7-8d9e-0f1a-2b3c4d5e6f7a");

    private static final double SPEED_REDUCTION = -0.30;

    public static final int DURATION_TICKS = 600;

    public MobEffectFrostburn() {
        super(MobEffectCategory.HARMFUL, 0x5B9BD5);
        addAttributeModifier(Attributes.MOVEMENT_SPEED, SPEED_MODIFIER_UUID.toString(),
                SPEED_REDUCTION, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }
}
