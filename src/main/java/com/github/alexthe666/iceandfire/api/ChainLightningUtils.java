package com.github.alexthe666.iceandfire.api;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.util.IDeadMob;
import com.github.alexthe666.iceandfire.message.MessageChainLightningFX;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;

/**
 * 链式闪电核心逻辑：从目标实体开始，逐跳搜索最近的敌对实体造成递减伤害。
 * <p>
 * 本类自包含，参考 RLC 1.12.2 的 {@code ChainLightningUtils} 和
 * JMixin CE 版的实现，使用 1.20 API 重写。
 */
public final class ChainLightningUtils {

    private ChainLightningUtils() {
    }

    /**
     * 触发链式闪电，从 {@code target} 开始跳跃伤害周围敌人。
     *
     * @param level    服务端世界
     * @param target   刚被击中的实体
     * @param attacker 发起攻击的实体（可能是 Player）
     */
    public static void createChainLightning(Level level, LivingEntity target, Entity attacker) {
        if (level.isClientSide)
            return;
        if (!target.isAttackable())
            return;

        // 玩家冷却检查
        if (attacker instanceof Player player) {
            if (player.getCooldowns().isOnCooldown(player.getMainHandItem().getItem())) {
                return;
            }
            player.getCooldowns().addCooldown(
                    player.getMainHandItem().getItem(),
                    VoltageConfig.CHAIN_COOLDOWN_TICKS);
        }

        float[] damage = VoltageConfig.CHAIN_DAMAGE_PER_HOP;
        int range = VoltageConfig.CHAIN_RANGE;

        // ── 首跳：伤害初始目标 ───────────────────────────────────────
        int hop = 0;
        attackWithLightning(level, attacker, target, damage[hop]);
        VoltageData.applyVoltage(target, VoltageConfig.VOLTAGE_DURATION_TICKS);
        target.playSound(SoundEvents.LIGHTNING_BOLT_IMPACT, 0.5F, 1.0F);

        // 收集坐标链用于客户端粒子渲染
        List<Vec3> chainPositions = new ArrayList<>();
        chainPositions.add(target.getBoundingBox().getCenter());

        // ── 后续跳跃 ────────────────────────────────────────────────
        LivingEntity source = target;
        Set<Integer> visited = new HashSet<>();
        visited.add(target.getId());

        for (hop = 1; hop < damage.length; hop++) {
            final LivingEntity currentSource = source;
            AABB searchBox = currentSource.getBoundingBox().inflate(range);

            List<LivingEntity> candidates = level.getEntitiesOfClass(
                    LivingEntity.class, searchBox,
                    e -> !visited.contains(e.getId())
                            && canChainTo(e, attacker)
                            && currentSource.hasLineOfSight(e));

            if (candidates.isEmpty())
                break;

            // 选最近的目标
            candidates.sort(Comparator.comparingDouble(
                    e -> e.distanceToSqr(currentSource)));
            LivingEntity next = candidates.get(0);

            attackWithLightning(level, attacker, next, damage[hop]);
            VoltageData.applyVoltage(next, VoltageConfig.VOLTAGE_DURATION_TICKS);

            visited.add(next.getId());
            chainPositions.add(next.getBoundingBox().getCenter());
            source = next;
        }

        // ── 发送粒子网络包 ───────────────────────────────────────────
        if (level instanceof ServerLevel serverLevel && chainPositions.size() >= 2) {
            sendLightningFxPacket(serverLevel, target, chainPositions);
        }
    }

    // ── 伤害处理 ─────────────────────────────────────────────────────

    private static void attackWithLightning(Level level, Entity attacker,
            LivingEntity target, float dmg) {
        DamageSource src;
        if (VoltageConfig.CHAIN_BYPASSES_ARMOR) {
            src = level.damageSources().magic();
        } else {
            src = level.damageSources().lightningBolt();
        }
        target.hurt(src, dmg);

        // 苦力怕 → 充能苦力怕
        if (target instanceof Creeper creeper && !creeper.isPowered()) {
            CompoundTag tag = new CompoundTag();
            creeper.addAdditionalSaveData(tag);
            tag.putBoolean("powered", true);
            creeper.readAdditionalSaveData(tag);
        }

        // 击退
        if (attacker != null) {
            target.knockback(0.4F,
                    attacker.getX() - target.getX(),
                    attacker.getZ() - target.getZ());
        }
    }

    // ── 目标验证 ─────────────────────────────────────────────────────

    private static boolean canHurt(LivingEntity target, Entity attacker) {
        if (!target.isAlive())
            return false;
        if (!target.isAttackable())
            return false;
        if (target instanceof Mob mob && mob.isNoAi())
            return false;
        if (target instanceof IDeadMob deadMob && deadMob.isMobDead())
            return false;
        return target instanceof Mob || target instanceof Player;
    }

    private static boolean canChainTo(LivingEntity target, Entity attacker) {
        if (target instanceof Player)
            return false;
        if (!canHurt(target, attacker))
            return false;

        // 不链到玩家驯服的宠物（除非宠物正在攻击攻击者）
        if (target instanceof TamableAnimal tamable) {
            LivingEntity owner = tamable.getOwner();
            if (owner instanceof Player) {
                if (target instanceof Mob mob) {
                    LivingEntity attackTarget = mob.getTarget();
                    LivingEntity revengeTarget = target.getLastHurtByMob();
                    if (!Objects.equals(attacker, attackTarget) &&
                            !Objects.equals(attacker, revengeTarget)) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    // ── 网络包发送 ───────────────────────────────────────────────────

    private static void sendLightningFxPacket(ServerLevel level,
            LivingEntity origin,
            List<Vec3> positions) {
        MessageChainLightningFX msg = new MessageChainLightningFX(positions);
        for (ServerPlayer player : level.getPlayers(p -> p.distanceTo(origin) < 64)) {
            IceAndFire.sendMSGToPlayer(msg, player);
        }
    }
}
