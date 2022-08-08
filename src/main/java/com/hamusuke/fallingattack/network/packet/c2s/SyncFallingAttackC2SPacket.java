package com.hamusuke.fallingattack.network.packet.c2s;

import com.hamusuke.fallingattack.invoker.PlayerInvoker;
import com.hamusuke.fallingattack.network.packet.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncFallingAttackC2SPacket implements Packet {
    public SyncFallingAttackC2SPacket(FriendlyByteBuf ignored) {
    }

    public SyncFallingAttackC2SPacket() {
    }

    @Override
    public void write(FriendlyByteBuf ignored) {
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PlayerInvoker invoker = (PlayerInvoker) ctx.get().getSender();
            if (invoker != null) {
                invoker.sendSynchronizeFallingAttackPacket();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
