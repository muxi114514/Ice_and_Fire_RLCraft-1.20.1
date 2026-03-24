package com.github.alexthe666.iceandfire.api;

/**
 * 链式闪电和电压减益系统的配置常量。
 * 后续可迁移到 IafConfig 的 ForgeConfigSpec 中实现运行时可调。
 */
public final class VoltageConfig {

    private VoltageConfig() {
    }

    // ── 链式闪电 ──────────────────────────────────────────────────────
    /** 每跳伤害（index 0 = 首个目标），数组长度 = 最大跳跃次数 */
    public static final float[] CHAIN_DAMAGE_PER_HOP = { 5.0f, 4.0f, 3.0f, 2.0f, 1.0f };

    /** 搜索下一跳目标的方块半径 */
    public static final int CHAIN_RANGE = 8;

    /** 玩家触发后的冷却 tick 数 */
    public static final int CHAIN_COOLDOWN_TICKS = 10;

    /** 链式闪电伤害是否穿甲 */
    public static final boolean CHAIN_BYPASSES_ARMOR = true;

    // ── 电压减益 ──────────────────────────────────────────────────────
    /** 电压状态持续 tick 数 */
    public static final int VOLTAGE_DURATION_TICKS = 200;

    /** 移动距离(格) → 电压累积的倍率 */
    public static final float VOLTAGE_MOVE_MULTIPLIER = 3.0f;

    /** 雨天每 tick 额外累积的电压值 */
    public static final float VOLTAGE_RAIN_BONUS = 1.0f;

    /** 每 tick 自然衰减的电压值 */
    public static final float VOLTAGE_DECAY_PER_TICK = 0.05f;

    /** 累积电压达到此阈值时触发放电 */
    public static final float VOLTAGE_DISCHARGE_THRESHOLD = 5.0f;

    /** 放电伤害 = 累积电压 × 此系数 */
    public static final float VOLTAGE_DAMAGE_FACTOR = 1.5f;
}
