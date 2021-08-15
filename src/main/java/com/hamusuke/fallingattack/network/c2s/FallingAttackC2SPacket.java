package com.hamusuke.fallingattack.network.c2s;

import com.hamusuke.fallingattack.invoker.IPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class FallingAttackC2SPacket {
    private final boolean start;

    public FallingAttackC2SPacket(PacketBuffer buffer) {
        this.start = buffer.readBoolean();
    }

    public FallingAttackC2SPacket(boolean start) {
        this.start = start;
    }

    public void write(PacketBuffer buffer) {
        buffer.writeBoolean(this.start);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        AtomicBoolean atomicBoolean = new AtomicBoolean();
        ctx.get().enqueueWork(() -> {
            IPlayerEntity invoker = (IPlayerEntity) ctx.get().getSender();
            if (this.start) {
                if (invoker.checkFallingAttack()) {
                    invoker.startFallingAttack();
                    invoker.sendFallingAttackPacket(true);
                }
            } else {
                invoker.stopFallingAttack();
                invoker.sendFallingAttackPacket(false);
            }

            atomicBoolean.set(true);
        });
        ctx.get().setPacketHandled(atomicBoolean.get());
    }
}
