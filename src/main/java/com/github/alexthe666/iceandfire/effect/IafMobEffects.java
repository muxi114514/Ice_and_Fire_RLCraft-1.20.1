package com.github.alexthe666.iceandfire.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import com.github.alexthe666.iceandfire.IceAndFire;

public class IafMobEffects {

        public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(
                        ForgeRegistries.MOB_EFFECTS,
                        IceAndFire.MODID);

        public static final RegistryObject<MobEffect> MELT = MOB_EFFECTS.register("melt", MobEffectMelt::new);

        public static final RegistryObject<MobEffect> FROSTBITE = MOB_EFFECTS.register("frostbite",
                        MobEffectFrostbite::new);

        public static final RegistryObject<MobEffect> FROSTBURN = MOB_EFFECTS.register("frostburn",
                        MobEffectFrostburn::new);

        public static final RegistryObject<MobEffect> VOLTAGE = MOB_EFFECTS.register("voltage",
                        MobEffectVoltage::new);

        public static final RegistryObject<MobEffect> CHAOS_I = MOB_EFFECTS.register("chaos_i",
                        () -> new MobEffectChaos(MobEffectChaos.ChaosType.CHAOS_I));

        public static final RegistryObject<MobEffect> CHAOS_II = MOB_EFFECTS.register("chaos_ii",
                        () -> new MobEffectChaos(MobEffectChaos.ChaosType.CHAOS_II));

        public static final RegistryObject<MobEffect> CHAOS_III = MOB_EFFECTS.register("chaos_iii",
                        () -> new MobEffectChaos(MobEffectChaos.ChaosType.CHAOS_III));

        public static final RegistryObject<MobEffect> CHAOS_RELEASE = MOB_EFFECTS.register("chaos_release",
                        () -> new MobEffectChaos(MobEffectChaos.ChaosType.RELEASE));
}
