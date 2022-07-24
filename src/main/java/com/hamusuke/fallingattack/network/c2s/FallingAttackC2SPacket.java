package com.hamusuke.fallingattack.network.c2s;

import com.hamusuke.fallingattack.invoker.PlayerInvoker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class FallingAttackC2SPacket {
    private final boolean start;

    public FallingAttackC2SPacket(FriendlyByteBuf buffer) {
        this.start = buffer.readBoolean();
    }

    public FallingAttackC2SPacket(boolean start) {
        this.start = start;
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.start);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PlayerInvoker invoker = (PlayerInvoker) ctx.get().getSender();
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
