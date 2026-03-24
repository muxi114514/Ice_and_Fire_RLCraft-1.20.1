package com.github.alexthe666.iceandfire.client.render;

import com.github.alexthe666.iceandfire.client.particle.LightningBoltData;
import com.github.alexthe666.iceandfire.client.particle.LightningRender;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector4f;

import java.util.List;

/**
 * 链式闪电客户端渲染器：使用 {@link LightningRender} 绘制闪电弧。
 * <p>
 * 复用冰与火现有的 aidancbrady 闪电渲染算法（与闪电龙息相同），
 * 完美复刻 RLC 1.12.2 的闪电链视觉效果。
 */
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = "iceandfire", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ChainLightningRender {

    /** 链式闪电专用的闪电弧颜色（鲜亮紫色，符合闪电的印象色） */
    private static final Vector4f CHAIN_LIGHTNING_COLOR = new Vector4f(0.75F, 0.30F, 1.0F, 0.95F);

    /** 链式闪电的渲染参数 */
    private static final LightningBoltData.BoltRenderInfo CHAIN_BOLT_INFO = new LightningBoltData.BoltRenderInfo(
            0.4F, // parallelNoise
            0.3F, // spreadFactor
            0.2F, // branchInitiationFactor
            0.1F, // branchContinuationFactor
            CHAIN_LIGHTNING_COLOR,
            0.75F // closeness
    );

    /** 天罚引雷的青白色（与紫色链式闪电形成鲜明对比） */
    private static final Vector4f SKY_BOLT_COLOR = new Vector4f(0.6F, 0.85F, 1.0F, 0.95F);

    /** 天罚引雷的渲染参数（粗壮、多分支，力量感十足） */
    private static final LightningBoltData.BoltRenderInfo SKY_BOLT_INFO = new LightningBoltData.BoltRenderInfo(
            0.6F, // parallelNoise — 较高偏移让雷更不规则
            0.15F, // spreadFactor — 垂直方向不需太多横向偏移
            0.35F, // branchInitiationFactor — 更高的分支概率
            0.25F, // branchContinuationFactor — 分支延续
            SKY_BOLT_COLOR,
            0.6F // closeness
    );

    private static final LightningRender RENDER = new LightningRender();

    /**
     * 从网络包收到坐标链后调用，注册闪电弧到渲染器。
     *
     * @param positions 坐标链（至少2个点）
     */
    public static void spawnChainLightning(List<Vec3> positions) {
        if (positions.size() < 2)
            return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null)
            return;

        for (int i = 0; i < positions.size() - 1; i++) {
            Vec3 start = positions.get(i);
            Vec3 end = positions.get(i + 1);
            int segments = Math.max(5, (int) (start.distanceTo(end) * 3));

            LightningBoltData bolt = new LightningBoltData(CHAIN_BOLT_INFO, start, end, segments)
                    .size(0.09F)
                    .lifespan(14)
                    .count(2)
                    .spawn(LightningBoltData.SpawnFunction.NO_DELAY)
                    .fade(LightningBoltData.FadeFunction.fade(0.4F));

            Object ownerKey = "chain_lightning_" + i + "_" + mc.level.getGameTime();
            RENDER.update(ownerKey, bolt, mc.getFrameTime());
        }
    }

    /**
     * 天罚引雷：从实体头顶高空垂直劈下一道青白色巨雷（Voltage 放电时调用）。
     *
     * @param entityId 实体ID
     */
    public static void handleVoltageDischarge(int entityId) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null)
            return;

        Entity entity = mc.level.getEntity(entityId);
        if (entity == null)
            return;

        // 雷电终点：实体中心
        Vec3 target = entity.getBoundingBox().getCenter();
        // 雷电起点：实体正上方 20 格
        Vec3 skyOrigin = new Vec3(target.x, target.y + 20.0, target.z);

        int segments = 25;

        // 主雷：粗壮的青白色闪电
        LightningBoltData mainBolt = new LightningBoltData(SKY_BOLT_INFO, skyOrigin, target, segments)
                .size(0.14F)
                .lifespan(10)
                .count(1)
                .spawn(LightningBoltData.SpawnFunction.NO_DELAY)
                .fade(LightningBoltData.FadeFunction.fade(0.5F));
        Object mainKey = "sky_bolt_main_" + entityId + "_" + mc.level.getGameTime();
        RENDER.update(mainKey, mainBolt, mc.getFrameTime());

        // 副雷：稍细、稍偏移，增加厚重感
        RandomSource random = mc.level.random;
        Vec3 skyOrigin2 = skyOrigin.add(
                (random.nextDouble() - 0.5) * 1.5,
                0,
                (random.nextDouble() - 0.5) * 1.5);
        LightningBoltData subBolt = new LightningBoltData(SKY_BOLT_INFO, skyOrigin2, target, segments)
                .size(0.08F)
                .lifespan(8)
                .count(1)
                .spawn(LightningBoltData.SpawnFunction.NO_DELAY)
                .fade(LightningBoltData.FadeFunction.fade(0.5F));
        Object subKey = "sky_bolt_sub_" + entityId + "_" + mc.level.getGameTime();
        RENDER.update(subKey, subBolt, mc.getFrameTime());
    }

    /**
     * 在世界渲染阶段绘制闪电弧。
     */
    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS)
            return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null)
            return;

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

        // 偏移到世界坐标系（减去摄像机位置）
        Vec3 cam = mc.gameRenderer.getMainCamera().getPosition();
        poseStack.pushPose();
        poseStack.translate(-cam.x, -cam.y, -cam.z);

        RENDER.render(event.getPartialTick(), poseStack, bufferSource);

        // 刷新 buffer 确保立即绘制
        bufferSource.endBatch();
        poseStack.popPose();
    }
}
