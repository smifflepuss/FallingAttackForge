package com.hamusuke.fallingattack.network.packet.c2s;

import com.hamusuke.fallingattack.invoker.IPlayerEntity;
import com.hamusuke.fallingattack.network.packet.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class FallingAttackC2SPacket implements Packet {
    private final boolean start;

    public FallingAttackC2SPacket(PacketBuffer buffer) {
        this.start = buffer.readBoolean();
    }

    public FallingAttackC2SPacket(boolean start) {
        this.start = start;
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeBoolean(this.start);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            IPlayerEntity invoker = (IPlayerEntity) ctx.get().getSender();
            if (invoker != null) {
                if (this.start) {
                    if (invoker.checkFallingAttack()) {
                        invoker.startFallingAttack();
                        invoker.sendFallingAttackPacket(true);
                    }
                } else {
                    invoker.stopFallingAttack();
                    invoker.sendFallingAttackPacket(false);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
