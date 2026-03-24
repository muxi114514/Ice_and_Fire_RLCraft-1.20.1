package com.github.alexthe666.iceandfire.message;

import com.github.alexthe666.iceandfire.client.render.ChainLightningRender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * S2C 网络包：通知客户端目标实体触发了 Voltage 放电效果。
 */
public class MessageVoltageDischargeFX {

    public int entityId;

    public MessageVoltageDischargeFX(int entityId) {
        this.entityId = entityId;
    }

    // ── 序列化 ───────────────────────────────────────────────────────

    public static void write(MessageVoltageDischargeFX msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityId);
    }

    public static MessageVoltageDischargeFX read(FriendlyByteBuf buf) {
        return new MessageVoltageDischargeFX(buf.readInt());
    }

    // ── 客户端处理 ───────────────────────────────────────────────────

    public static class Handler {
        public static void handle(MessageVoltageDischargeFX msg, Supplier<NetworkEvent.Context> ctx) {
            NetworkEvent.Context context = ctx.get();

            if (context.getDirection().getReceptionSide() != LogicalSide.CLIENT) {
                context.setPacketHandled(true);
                return;
            }

            context.enqueueWork(() -> {
                ChainLightningRender.handleVoltageDischarge(msg.entityId);
            });

            context.setPacketHandled(true);
        }
    }
}
