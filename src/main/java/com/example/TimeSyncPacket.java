package com.example;

import com.example.examplemod.ExampleMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;

import java.sql.Time;
import java.util.function.Supplier;
@Mod.EventBusSubscriber(modid = "untitled7", value = Dist.CLIENT)

public class TimeSyncPacket {
    private final int remainingTime;

    public TimeSyncPacket(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public static void encode(TimeSyncPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.remainingTime);
    }

    public static TimeSyncPacket decode(FriendlyByteBuf buffer) {
        return new TimeSyncPacket(buffer.readInt());
    }

    public static void handle(TimeSyncPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ExampleMod.CLIENT_TIME = packet.remainingTime;
           // ExampleMod.updateClientSidebar();
            System.out.println("Received remainingTime: " + packet.remainingTime); // ログ出力
        });
        context.get().setPacketHandled(true);
    }
}