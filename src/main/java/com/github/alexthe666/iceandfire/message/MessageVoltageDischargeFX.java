package com.github.alexthe666.iceandfire.message;

import com.github.alexthe666.iceandfire.client.render.ChainLightningRender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageVoltageDischargeFX {

    public int entityId;

    public MessageVoltageDischargeFX(int entityId) {
        this.entityId = entityId;
    }


    public static void write(MessageVoltageDischargeFX msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityId);
    }

    public static MessageVoltageDischargeFX read(FriendlyByteBuf buf) {
        return new MessageVoltageDischargeFX(buf.readInt());
    }


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
