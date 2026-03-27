package com.github.alexthe666.iceandfire.message;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.render.ChainLightningRender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class MessageChainLightningFX {

    private final List<Vec3> positions;

    public MessageChainLightningFX(List<Vec3> positions) {
        this.positions = positions;
    }


    public static void write(MessageChainLightningFX msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.positions.size());
        for (Vec3 pos : msg.positions) {
            buf.writeDouble(pos.x);
            buf.writeDouble(pos.y);
            buf.writeDouble(pos.z);
        }
    }

    public static MessageChainLightningFX read(FriendlyByteBuf buf) {
        int count = buf.readInt();
        List<Vec3> positions = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            positions.add(new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()));
        }
        return new MessageChainLightningFX(positions);
    }


    public static class Handler {
        public static void handle(MessageChainLightningFX msg, Supplier<NetworkEvent.Context> ctx) {
            NetworkEvent.Context context = ctx.get();

            if (context.getDirection().getReceptionSide() != LogicalSide.CLIENT) {
                context.setPacketHandled(true);
                return;
            }

            context.enqueueWork(() -> {
                if (msg.positions.size() < 2)
                    return;
                ChainLightningRender.spawnChainLightning(msg.positions);
            });

            context.setPacketHandled(true);
        }
    }
}
