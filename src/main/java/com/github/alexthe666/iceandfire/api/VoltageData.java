package com.github.alexthe666.iceandfire.api;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * 电压减益系统：被链式闪电击中后，移动会累积电压并最终放电。
 * <p>
 * 使用 {@link WeakHashMap} 管理每个实体的电压状态，
 * 实体被 GC 后自动清理，不会造成内存泄漏。
 */
@Mod.EventBusSubscriber(modid = "iceandfire", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VoltageData {

    // ── 全局注册表 ───────────────────────────────────────────────────
    private static final Map<LivingEntity, VoltageData> REGISTRY = new WeakHashMap<>();

    /** 放电后的冷却 tick 数，防止高频连续放电 */
    private static final int DISCHARGE_COOLDOWN_TICKS = 40;

    // ── 实例状态 ─────────────────────────────────────────────────────
    private int voltageTicks;
    private float voltageAmount;
    private boolean charged;
    private int dischargeCooldown;
    private double prevX, prevY, prevZ;
    private boolean posInitialised;

    // ── 公共 API ─────────────────────────────────────────────────────

    /** 对目标施加电压减益 */
    public static void applyVoltage(LivingEntity target, int durationTicks) {
        VoltageData data = REGISTRY.computeIfAbsent(target, e -> new VoltageData());
        if (!data.charged) {
            target.playSound(SoundEvents.LIGHTNING_BOLT_IMPACT, 0.8F, 1.2F);
        }
        data.voltageTicks = durationTicks;
        data.voltageAmount = 0;
        data.charged = true;
        data.posInitialised = false;
        // 不重置 dischargeCooldown，防止连续攻击绕过冷却
    }

    // ── Forge 事件驱动 tick ─────────────────────────────────────────

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide())
            return;

        VoltageData data = REGISTRY.get(entity);
        if (data == null || !data.charged)
            return;

        data.tickVoltage(entity);
    }

    // ── 内部 tick 逻辑 ───────────────────────────────────────────────

    private void tickVoltage(LivingEntity entity) {
        if (entity.isDeadOrDying()) {
            clearVoltage();
            return;
        }

        // 首次记录位置
        if (!posInitialised) {
            prevX = entity.getX();
            prevY = entity.getY();
            prevZ = entity.getZ();
            posInitialised = true;
        }

        // 放电冷却递减
        if (dischargeCooldown > 0) {
            dischargeCooldown--;
        }

        // 根据移动距离累积电压
        double dx = entity.getX() - prevX;
        double dy = entity.getY() - prevY;
        double dz = entity.getZ() - prevZ;
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        prevX = entity.getX();
        prevY = entity.getY();
        prevZ = entity.getZ();

        voltageAmount += (float) dist * VoltageConfig.VOLTAGE_MOVE_MULTIPLIER;

        // 雨天加速
        if (entity.level().isRainingAt(entity.blockPosition())) {
            voltageAmount += VoltageConfig.VOLTAGE_RAIN_BONUS;
        }

        // 自然衰减
        voltageAmount = Math.max(0, voltageAmount - VoltageConfig.VOLTAGE_DECAY_PER_TICK);

        // 放电检查（需要冷却结束后才能放电）
        if (voltageAmount >= VoltageConfig.VOLTAGE_DISCHARGE_THRESHOLD && dischargeCooldown <= 0) {
            float damage = voltageAmount * VoltageConfig.VOLTAGE_DAMAGE_FACTOR;
            entity.hurt(entity.level().damageSources().lightningBolt(), damage);
            entity.playSound(SoundEvents.LIGHTNING_BOLT_THUNDER, 0.6F, 1.5F);
            sendDischargeEffect(entity);
            voltageAmount = 0;
            dischargeCooldown = DISCHARGE_COOLDOWN_TICKS;
        }

        // 移动减速（创造模式玩家豁免）
        if (!(entity instanceof Player p && p.isCreative())) {
            Vec3 vel = entity.getDeltaMovement();
            entity.setDeltaMovement(vel.multiply(0.7, 1, 0.7));
        }

        // 递减持续时间
        voltageTicks--;
        if (voltageTicks <= 0) {
            clearVoltage();
        }
    }

    private void clearVoltage() {
        charged = false;
        voltageTicks = 0;
        voltageAmount = 0;
        dischargeCooldown = 0;
        posInitialised = false;
    }

    /** 通过网络包通知客户端渲染放电特效 */
    private static void sendDischargeEffect(LivingEntity entity) {
        if (entity.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            com.github.alexthe666.iceandfire.message.MessageVoltageDischargeFX msg = new com.github.alexthe666.iceandfire.message.MessageVoltageDischargeFX(
                    entity.getId());
            for (net.minecraft.server.level.ServerPlayer player : serverLevel
                    .getPlayers(p -> p.distanceTo(entity) < 64)) {
                com.github.alexthe666.iceandfire.IceAndFire.sendMSGToPlayer(msg, player);
            }
        }
    }
}
