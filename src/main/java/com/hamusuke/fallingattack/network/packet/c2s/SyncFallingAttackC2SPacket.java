package com.hamusuke.fallingattack.network.packet.c2s;

import com.hamusuke.fallingattack.invoker.IPlayerEntity;
import com.hamusuke.fallingattack.network.packet.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncFallingAttackC2SPacket implements Packet {
    public SyncFallingAttackC2SPacket(PacketBuffer ignored) {
    }

    public SyncFallingAttackC2SPacket() {
    }

    @Override
    public void write(PacketBuffer ignored) {
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            IPlayerEntity invoker = (IPlayerEntity) ctx.get().getSender();
            if (invoker != null) {
                invoker.sendSynchronizeFallingAttackPacket();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
