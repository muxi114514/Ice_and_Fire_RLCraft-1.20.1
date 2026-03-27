package com.github.alexthe666.iceandfire.item.blooded;

import com.github.alexthe666.iceandfire.client.model.armor.ModelBloodedFireArmor;
import com.github.alexthe666.iceandfire.client.model.armor.ModelBloodedIceArmor;
import com.github.alexthe666.iceandfire.client.model.armor.ModelBloodedLightningArmor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class ItemBloodedArmor extends ArmorItem {

    private final BloodedDragonType dragonType;

    public ItemBloodedArmor(BloodedDragonType dragonType, Type slot) {
        super(BloodedArmorMaterial.fromElement(dragonType.getElement()), slot,
                new Properties());
        this.dragonType = dragonType;
    }

    public BloodedDragonType getDragonType() {
        return dragonType;
    }

    @Override
    public @NotNull String getDescriptionId() {
        return switch (this.type) {
            case HELMET -> "item.iceandfire.blooded_helmet";
            case CHESTPLATE -> "item.iceandfire.blooded_chestplate";
            case LEGGINGS -> "item.iceandfire.blooded_leggings";
            case BOOTS -> "item.iceandfire.blooded_boots";
        };
    }

    public HumanoidModel<?> getArmorModel(LivingEntity entity, EquipmentSlot slot) {
        boolean inner = slot == EquipmentSlot.LEGS || slot == EquipmentSlot.HEAD;
        return switch (dragonType.getElement()) {
            case FIRE -> new ModelBloodedFireArmor(inner);
            case ICE -> new ModelBloodedIceArmor(inner);
            case LIGHTNING -> new ModelBloodedLightningArmor(inner);
        };
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(
                    LivingEntity living, ItemStack stack, EquipmentSlot slot,
                    HumanoidModel<?> defaultModel) {
                boolean inner = slot == EquipmentSlot.LEGS || slot == EquipmentSlot.HEAD;
                return switch (dragonType.getElement()) {
                    case FIRE -> new ModelBloodedFireArmor(inner);
                    case ICE -> new ModelBloodedIceArmor(inner);
                    case LIGHTNING -> new ModelBloodedLightningArmor(inner);
                };
            }
        });
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        String color = dragonType.getColorName();
        String suffix = slot == EquipmentSlot.LEGS ? "_legs.png" : ".png";
        return "iceandfire:textures/models/armor/blooded_" + color + suffix;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
            @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        ChatFormatting elementColor = switch (dragonType.getElement()) {
            case FIRE -> ChatFormatting.DARK_RED;
            case ICE -> ChatFormatting.AQUA;
            case LIGHTNING -> ChatFormatting.LIGHT_PURPLE;
        };

        tooltip.add(Component.translatable("dragon." + dragonType.getColorName())
                .withStyle(dragonType.getColor()));

        tooltip.add(Component.translatable("item.blooded_armor.desc")
                .withStyle(elementColor));

        tooltip.add(Component.empty());
        int wornCount = countWornPieces(level);
        boolean fullSet = wornCount >= 4;

        tooltip.add(Component.translatable("item.iceandfire.blooded_set.title")
                .withStyle(ChatFormatting.GRAY));

        ChatFormatting descColor = fullSet ? elementColor : ChatFormatting.GRAY;
        String effectKey = switch (dragonType.getElement()) {
            case FIRE -> "item.iceandfire.blooded_set.fire";
            case ICE -> "item.iceandfire.blooded_set.ice";
            case LIGHTNING -> "item.iceandfire.blooded_set.lightning";
        };

        tooltip.add(
                Component.literal(wornCount + "/4: ").append(Component.translatable(effectKey)).withStyle(descColor));
    }

    private int countWornPieces(@Nullable Level level) {
        if (level == null)
            return 0;
        try {
            Player player = net.minecraft.client.Minecraft.getInstance().player;
            if (player == null)
                return 0;

            BloodedDragonType.DragonElement myElement = dragonType.getElement();
            int count = 0;
            for (EquipmentSlot slot : new EquipmentSlot[] {
                    EquipmentSlot.HEAD, EquipmentSlot.CHEST,
                    EquipmentSlot.LEGS, EquipmentSlot.FEET }) {
                ItemStack equipped = player.getItemBySlot(slot);
                if (equipped.getItem() instanceof ItemBloodedArmor armor
                        && armor.getDragonType().getElement() == myElement) {
                    count++;
                }
            }
            return count;
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean hasFullArmorSet(LivingEntity entity, BloodedDragonType.DragonElement element) {
        if (entity == null)
            return false;
        int count = 0;
        for (EquipmentSlot slot : new EquipmentSlot[] {
                EquipmentSlot.HEAD, EquipmentSlot.CHEST,
                EquipmentSlot.LEGS, EquipmentSlot.FEET }) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (stack.getItem() instanceof ItemBloodedArmor armor && armor.getDragonType().getElement() == element) {
                count++;
            }
        }
        return count == 4;
    }
}
