package com.github.alexthe666.iceandfire.effect;

import com.github.alexthe666.iceandfire.config.FrostShatterConfig;
import com.github.alexthe666.iceandfire.config.VoltageDischargeConfig;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

public class MobEffectChaos extends MobEffect {

    public enum Element {
        FIRE, ICE, LIGHTNING
    }

    public enum ChaosType {
        CHAOS_I,
        CHAOS_II,
        CHAOS_III,
        RELEASE
    }

    private final ChaosType chaosType;

    private static final UUID ARMOR_UUID = UUID.fromString("c1a2b3c4-d5e6-4f70-8192-a3b4c5d6e7f8");
    private static final UUID TOUGHNESS_UUID = UUID.fromString("c2b3c4d5-e6f7-4081-9203-b4c5d6e7f8a9");
    private static final UUID ATTACK_SPEED_UUID = UUID.fromString("c3c4d5e6-f708-4192-a314-c5d6e7f8a9b0");
    private static final UUID MOVE_SPEED_UUID = UUID.fromString("c4d5e6f7-0819-42a3-b425-d6e7f8a9b0c1");

    public static final int DURATION_I_II_III = 300; // 15s
    public static final int DURATION_RELEASE = 600; // 30s

    public MobEffectChaos(ChaosType type) {
        super(MobEffectCategory.HARMFUL, getColorForType(type));
        this.chaosType = type;
        applyModifiers(type);
    }

    public ChaosType getChaosType() {
        return chaosType;
    }

    private static int getColorForType(ChaosType type) {
        return switch (type) {
            case CHAOS_I -> 0xFF6600;
            case CHAOS_II -> 0xCC3366;
            case CHAOS_III -> 0x6633CC;
            case RELEASE -> 0x000000;
        };
    }

    private void applyModifiers(ChaosType type) {
        switch (type) {
            case CHAOS_I -> {
                addAttributeModifier(Attributes.ARMOR, ARMOR_UUID.toString(),
                        -0.25, AttributeModifier.Operation.MULTIPLY_TOTAL);
                addAttributeModifier(Attributes.ARMOR_TOUGHNESS, TOUGHNESS_UUID.toString(),
                        -0.25, AttributeModifier.Operation.MULTIPLY_TOTAL);
                addAttributeModifier(Attributes.ATTACK_SPEED, ATTACK_SPEED_UUID.toString(),
                        -0.25, AttributeModifier.Operation.MULTIPLY_TOTAL);
            }
            case CHAOS_II -> {
                addAttributeModifier(Attributes.MOVEMENT_SPEED, MOVE_SPEED_UUID.toString(),
                        -0.20, AttributeModifier.Operation.MULTIPLY_TOTAL);
            }
            case CHAOS_III -> {
                addAttributeModifier(Attributes.MOVEMENT_SPEED, MOVE_SPEED_UUID.toString(),
                        -0.20, AttributeModifier.Operation.MULTIPLY_TOTAL);
                addAttributeModifier(Attributes.ATTACK_SPEED, ATTACK_SPEED_UUID.toString(),
                        -0.20, AttributeModifier.Operation.MULTIPLY_TOTAL);
            }
            case RELEASE -> {
                addAttributeModifier(Attributes.ARMOR, ARMOR_UUID.toString(),
                        -0.30, AttributeModifier.Operation.MULTIPLY_TOTAL);
                addAttributeModifier(Attributes.ARMOR_TOUGHNESS, TOUGHNESS_UUID.toString(),
                        -0.30, AttributeModifier.Operation.MULTIPLY_TOTAL);
                addAttributeModifier(Attributes.ATTACK_SPEED, ATTACK_SPEED_UUID.toString(),
                        -0.30, AttributeModifier.Operation.MULTIPLY_TOTAL);
                addAttributeModifier(Attributes.MOVEMENT_SPEED, MOVE_SPEED_UUID.toString(),
                        -0.30, AttributeModifier.Operation.MULTIPLY_TOTAL);
            }
        }
    }

    // ══════════════════════════════════════════════════════
    // ══════════════════════════════════════════════════════

    public static boolean tryChaosReaction(LivingEntity target, Element triggerElement,
            LivingEntity attacker, float weaponDamage) {
        if (target.level().isClientSide())
            return false;

        if (hasChaos(target, ChaosType.CHAOS_I) && triggerElement == Element.ICE) {
            triggerRelease(target, attacker);
            return true;
        }
        if (hasChaos(target, ChaosType.CHAOS_II) && triggerElement == Element.LIGHTNING) {
            triggerRelease(target, attacker);
            return true;
        }
        if (hasChaos(target, ChaosType.CHAOS_III) && triggerElement == Element.FIRE) {
            triggerRelease(target, attacker);
            return true;
        }

        if (isImmune(target, triggerElement)) {
            return true;
        }

        if (triggerElement == Element.FIRE) {
            if (target.hasEffect(IafMobEffects.VOLTAGE.get())) {
                triggerChaosI(target, attacker, weaponDamage);
                return true;
            }
            if (target.hasEffect(IafMobEffects.FROSTBURN.get())) {
                triggerChaosII(target, attacker);
                return true;
            }
        } else if (triggerElement == Element.LIGHTNING) {
            if (target.hasEffect(IafMobEffects.MELT.get())) {
                triggerChaosI(target, attacker, weaponDamage);
                return true;
            }
            if (target.hasEffect(IafMobEffects.FROSTBURN.get())) {
                triggerChaosIII(target, attacker);
                return true;
            }
        } else if (triggerElement == Element.ICE) {
            if (target.hasEffect(IafMobEffects.MELT.get())) {
                triggerChaosII(target, attacker);
                return true;
            }
            if (target.hasEffect(IafMobEffects.VOLTAGE.get())) {
                triggerChaosIII(target, attacker);
                return true;
            }
        }

        return false;
    }

    // ══════════════════════════════════════════════════════
    // ══════════════════════════════════════════════════════

    private static void triggerChaosI(LivingEntity target, LivingEntity attacker, float weaponDamage) {
        int meltStacks = getMeltStacks(target);
        int dischargeThreshold = VoltageDischargeConfig.getThreshold(target.getType());

        target.removeEffect(IafMobEffects.MELT.get());
        target.removeEffect(IafMobEffects.VOLTAGE.get());
        MobEffectVoltage.cleanupTracking(target.getUUID());

        float damage = (weaponDamage * dischargeThreshold) + (meltStacks * 3.0f);

        hurtWithCredit(target, attacker, damage);

        target.addEffect(new MobEffectInstance(
                IafMobEffects.CHAOS_I.get(), DURATION_I_II_III, 0, false, true, true));

        playChaosSound(target);
    }

    private static void triggerChaosII(LivingEntity target, LivingEntity attacker) {
        int meltStacks = getMeltStacks(target);
        int shatterThreshold = FrostShatterConfig.getThreshold(target.getType());

        target.removeEffect(IafMobEffects.MELT.get());
        target.removeEffect(IafMobEffects.FROSTBURN.get());

        float percent = Math.min((shatterThreshold + meltStacks) / 4.0f, 5.0f);
        float damage = target.getMaxHealth() * percent / 100.0f;

        hurtWithCredit(target, attacker, damage);

        target.addEffect(new MobEffectInstance(
                IafMobEffects.CHAOS_II.get(), DURATION_I_II_III, 0, false, true, true));

        playChaosSound(target);
    }

    private static void triggerChaosIII(LivingEntity target, LivingEntity attacker) {
        int shatterThreshold = FrostShatterConfig.getThreshold(target.getType());
        int dischargeThreshold = VoltageDischargeConfig.getThreshold(target.getType());

        target.removeEffect(IafMobEffects.FROSTBURN.get());
        target.removeEffect(IafMobEffects.VOLTAGE.get());
        MobEffectVoltage.cleanupTracking(target.getUUID());

        float percent = Math.min((shatterThreshold + dischargeThreshold) / 4.0f, 5.0f);
        float damage = target.getMaxHealth() * percent / 100.0f;

        hurtWithCredit(target, attacker, damage);

        target.addEffect(new MobEffectInstance(
                IafMobEffects.CHAOS_III.get(), DURATION_I_II_III, 0, false, true, true));

        playChaosSound(target);
    }

    private static void triggerRelease(LivingEntity target, LivingEntity attacker) {
        target.removeEffect(IafMobEffects.CHAOS_I.get());
        target.removeEffect(IafMobEffects.CHAOS_II.get());
        target.removeEffect(IafMobEffects.CHAOS_III.get());
        target.removeEffect(IafMobEffects.MELT.get());
        target.removeEffect(IafMobEffects.FROSTBURN.get());
        target.removeEffect(IafMobEffects.FROSTBITE.get());
        target.removeEffect(IafMobEffects.VOLTAGE.get());
        MobEffectVoltage.cleanupTracking(target.getUUID());

        float damage = target.getMaxHealth() * 0.10f;
        hurtWithCredit(target, attacker, damage);

        target.addEffect(new MobEffectInstance(
                IafMobEffects.CHAOS_RELEASE.get(), DURATION_RELEASE, 0, false, true, true));

        playChaosSound(target);
    }

    // ══════════════════════════════════════════════════════
    // ══════════════════════════════════════════════════════

    private static boolean hasChaos(LivingEntity target, ChaosType type) {
        return switch (type) {
            case CHAOS_I -> target.hasEffect(IafMobEffects.CHAOS_I.get());
            case CHAOS_II -> target.hasEffect(IafMobEffects.CHAOS_II.get());
            case CHAOS_III -> target.hasEffect(IafMobEffects.CHAOS_III.get());
            case RELEASE -> target.hasEffect(IafMobEffects.CHAOS_RELEASE.get());
        };
    }

    public static boolean isImmune(LivingEntity target, Element element) {
        if (target.hasEffect(IafMobEffects.CHAOS_RELEASE.get()))
            return true;

        switch (element) {
            case FIRE:
                if (target.hasEffect(IafMobEffects.CHAOS_I.get()))
                    return true;
                if (target.hasEffect(IafMobEffects.CHAOS_II.get()))
                    return true;
                break;
            case ICE:
                if (target.hasEffect(IafMobEffects.CHAOS_II.get()))
                    return true;
                if (target.hasEffect(IafMobEffects.CHAOS_III.get()))
                    return true;
                break;
            case LIGHTNING:
                if (target.hasEffect(IafMobEffects.CHAOS_I.get()))
                    return true;
                if (target.hasEffect(IafMobEffects.CHAOS_III.get()))
                    return true;
                break;
        }
        return false;
    }

    private static int getMeltStacks(LivingEntity target) {
        MobEffectInstance melt = target.getEffect(IafMobEffects.MELT.get());
        return melt != null ? melt.getAmplifier() + 1 : 0;
    }

    private static void hurtWithCredit(LivingEntity target, LivingEntity attacker, float damage) {
        if (damage <= 0)
            return;
        net.minecraft.world.damagesource.DamageSource source = attacker != null
                ? target.level().damageSources().indirectMagic(attacker, attacker)
                : target.level().damageSources().magic();
        target.hurt(source, damage);
    }

    private static void playChaosSound(LivingEntity target) {
        target.playSound(net.minecraft.sounds.SoundEvents.WITHER_SPAWN, 0.5F, 1.8F);
    }

    public static float getDamageAmplification(LivingEntity target) {
        if (target.hasEffect(IafMobEffects.CHAOS_RELEASE.get()))
            return 0.30f;
        if (target.hasEffect(IafMobEffects.CHAOS_II.get()))
            return 0.15f;
        if (target.hasEffect(IafMobEffects.CHAOS_III.get()))
            return 0.15f;
        return 0f;
    }

    // ══════════════════════════════════════════════════════
    // ══════════════════════════════════════════════════════

    @Mod.EventBusSubscriber(modid = "iceandfire", bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ChaosImmunityHandler {

        @SubscribeEvent
        public static void onEffectApplicable(MobEffectEvent.Applicable event) {
            LivingEntity target = event.getEntity();
            MobEffect incomingEffect = event.getEffectInstance().getEffect();

            Element element = getElementForEffect(incomingEffect);
            if (element == null)
                return;

            if (isImmune(target, element)) {
                event.setResult(Event.Result.DENY);
            }
        }

        private static Element getElementForEffect(MobEffect effect) {
            if (effect == IafMobEffects.MELT.get())
                return Element.FIRE;
            if (effect == IafMobEffects.FROSTBITE.get())
                return Element.ICE;
            if (effect == IafMobEffects.FROSTBURN.get())
                return Element.ICE;
            if (effect == IafMobEffects.VOLTAGE.get())
                return Element.LIGHTNING;
            return null;
        }
    }
}
