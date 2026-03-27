package com.github.alexthe666.iceandfire.api;

public final class VoltageConfig {

    private VoltageConfig() {
    }

    public static final float[] CHAIN_DAMAGE_PER_HOP = { 5.0f, 4.0f, 3.0f, 2.0f, 1.0f };

    public static final int CHAIN_RANGE = 8;

    public static final int CHAIN_COOLDOWN_TICKS = 10;

    public static final boolean CHAIN_BYPASSES_ARMOR = true;
}
