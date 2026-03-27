package com.github.alexthe666.iceandfire.client.render;

import com.github.alexthe666.iceandfire.entity.props.EntityDataProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = "iceandfire", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MeltEffectRenderer {

    private static final float FIRE_SCALE = 1.8F;
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("iceandfire", "textures/entity/meltfire.png");
    private static RenderType RENDER_TYPE;
    private static int clientTicks = 0;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !Minecraft.getInstance().isPaused()) {
            clientTicks++;
        }
    }

    public static void renderMeltFire(LivingEntity entity, PoseStack stack, MultiBufferSource bufferSource, int light) {
        boolean isMelting = EntityDataProvider.getCapability(entity)
                .map(data -> data.meltData.isMelting)
                .orElse(false);
        if (!isMelting) return;

        if (RENDER_TYPE == null) {
            RENDER_TYPE = RenderType.entityTranslucent(TEXTURE);
        }

        stack.pushPose();
        float w = entity.getBbWidth() * FIRE_SCALE;
        stack.scale(w, w, w);

        float hwRatio = entity.getBbHeight() / w;
        float xOffset = 0.5F;
        float yOffset = (float) (entity.getY() - entity.getBoundingBox().minY);
        float zOffset = 0.0F;

        Quaternionf cam = Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation();
        stack.mulPose(new Quaternionf(0, cam.y, 0, cam.w));

        stack.translate(0, 0, hwRatio * 0.02f);
        int i = 0;

        VertexConsumer vertexBuilder = bufferSource.getBuffer(RENDER_TYPE);
        Matrix4f matrix4f = stack.last().pose();
        Matrix3f matrix3f = stack.last().normal();

        while (hwRatio > 0.0F) {
            boolean swapU = i % 2 == 0;
            int frame = clientTicks % 32;
            float minU = swapU ? 0.5f : 0.0f;
            float minV = frame / 32f;
            float maxU = swapU ? 1.0f : 0.5f;
            float maxV = (frame + 1) / 32f;

            if (swapU) {
                float swap = maxU;
                maxU = minU;
                minU = swap;
            }

            vertexBuilder.vertex(matrix4f, xOffset, 0.0F - yOffset, zOffset).color(255, 255, 255, 255).uv(maxU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexBuilder.vertex(matrix4f, -xOffset, 0.0F - yOffset, zOffset).color(255, 255, 255, 255).uv(minU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexBuilder.vertex(matrix4f, -xOffset, 1.4F - yOffset, zOffset).color(255, 255, 255, 255).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexBuilder.vertex(matrix4f, xOffset, 1.4F - yOffset, zOffset).color(255, 255, 255, 255).uv(maxU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            hwRatio -= 0.45F;
            yOffset -= 0.45F;
            xOffset *= 0.9F;
            zOffset += 0.03F;
            ++i;
        }

        stack.popPose();
    }
}
