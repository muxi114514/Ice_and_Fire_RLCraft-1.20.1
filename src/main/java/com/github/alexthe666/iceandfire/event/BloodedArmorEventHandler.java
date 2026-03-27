package com.github.alexthe666.iceandfire.event;

import com.github.alexthe666.iceandfire.item.blooded.BloodedSetEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "iceandfire", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BloodedArmorEventHandler {

    @SubscribeEvent
    public static void onPlayerAttack(LivingDamageEvent event) {
        if (event.getEntity().level().isClientSide)
            return;

        LivingEntity target = event.getEntity();
        if (!(event.getSource().getEntity() instanceof Player player))
            return;

        BloodedSetEffect.tryApplySetEffect(player, target, event.getAmount());
    }
}
