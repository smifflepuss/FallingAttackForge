package com.hamusuke.fallingattack.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public interface Packet {
    void write(FriendlyByteBuf buffer);

    void handle(Supplier<NetworkEvent.Context> ctx);
}
