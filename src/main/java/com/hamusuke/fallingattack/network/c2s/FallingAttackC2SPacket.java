package com.hamusuke.fallingattack.network.c2s;

import com.hamusuke.fallingattack.invoker.IPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.commons.lang3.mutable.MutableBoolean;

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
        MutableBoolean mutableBoolean = new MutableBoolean();
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

            mutableBoolean.setTrue();
        });
        ctx.get().setPacketHandled(mutableBoolean.booleanValue());
    }
}
