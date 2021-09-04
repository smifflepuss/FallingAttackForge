package com.hamusuke.fallingattack.network.c2s;

import com.hamusuke.fallingattack.invoker.IPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class SyncFallingAttackC2SPacket {
    public SyncFallingAttackC2SPacket(PacketBuffer buffer) {
    }

    public SyncFallingAttackC2SPacket() {
    }

    public void write(PacketBuffer buffer) {
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        AtomicBoolean atomicBoolean = new AtomicBoolean();
        ctx.get().enqueueWork(() -> {
            ((IPlayerEntity) ctx.get().getSender()).sendSynchronizeFallingAttackPacket();
            atomicBoolean.set(true);
        });
        ctx.get().setPacketHandled(atomicBoolean.get());
    }
}
