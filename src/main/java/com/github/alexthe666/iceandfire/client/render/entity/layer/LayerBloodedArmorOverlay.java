package com.github.alexthe666.iceandfire.client.render.entity.layer;

import com.github.alexthe666.iceandfire.client.model.armor.ModelBloodedFireArmor;
import com.github.alexthe666.iceandfire.client.model.armor.ModelBloodedIceArmor;
import com.github.alexthe666.iceandfire.client.model.armor.ModelBloodedLightningArmor;
import com.github.alexthe666.iceandfire.item.blooded.BloodedDragonType;
import com.github.alexthe666.iceandfire.item.blooded.ItemBloodedArmor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class LayerBloodedArmorOverlay<T extends LivingEntity, M extends HumanoidModel<T>>
        extends RenderLayer<T, M> {

    private static final int FRAME_COUNT = 8;
    private static final int TICKS_PER_FRAME = 4;

    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST,
            EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    public LayerBloodedArmorOverlay(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource,
            int packedLight, @NotNull T entity, float limbSwing, float limbSwingAmount,
            float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {

        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (!(stack.getItem() instanceof ItemBloodedArmor blooded))
                continue;

            HumanoidModel<?> armorModel = blooded.getArmorModel(entity, slot);
            if (armorModel == null)
                continue;

            @SuppressWarnings("unchecked")
            HumanoidModel<T> typedModel = (HumanoidModel<T>) armorModel;
            this.getParentModel().copyPropertiesTo((M) typedModel);
            typedModel.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            setPartVisibility(armorModel, slot);

            ResourceLocation overlayTex = getOverlayTexture(blooded.getDragonType(), slot, entity);
            VertexConsumer consumer = bufferSource.getBuffer(
                    RenderType.armorCutoutNoCull(overlayTex));
            armorModel.renderToBuffer(poseStack, consumer, packedLight,
                    OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);
        }
    }

    private ResourceLocation getOverlayTexture(BloodedDragonType type,
            EquipmentSlot slot, LivingEntity entity) {
        int frame = (int) ((entity.tickCount / TICKS_PER_FRAME) % FRAME_COUNT) + 1;
        String prefix = type.getTexturePrefix();
        String suffix = slot == EquipmentSlot.LEGS
                ? "_armor_legs" + frame + ".png"
                : "_armor" + frame + ".png";
        return new ResourceLocation("iceandfire",
                "textures/models/armor/" + prefix + suffix);
    }

    private static void setPartVisibility(HumanoidModel<?> model, EquipmentSlot slot) {
        model.head.visible = slot == EquipmentSlot.HEAD;
        model.hat.visible = slot == EquipmentSlot.HEAD;
        model.body.visible = slot == EquipmentSlot.CHEST;
        model.leftArm.visible = slot == EquipmentSlot.CHEST;
        model.rightArm.visible = slot == EquipmentSlot.CHEST;
        model.leftLeg.visible = slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET;
        model.rightLeg.visible = slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET;
    }
}
