package com.hamusuke.fallingattack.network.packet.c2s;

import com.hamusuke.fallingattack.invoker.PlayerInvoker;
import com.hamusuke.fallingattack.network.packet.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class FallingAttackC2SPacket implements Packet {
    private final boolean start;

    public FallingAttackC2SPacket(FriendlyByteBuf buffer) {
        this.start = buffer.readBoolean();
    }

    public FallingAttackC2SPacket(boolean start) {
        this.start = start;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.start);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PlayerInvoker invoker = (PlayerInvoker) ctx.get().getSender();
            if (invoker != null) {
                if (this.start) {
                    if (invoker.fallingattack$checkFallingAttack()) {
                        invoker.fallingattack$startFallingAttack();
                        invoker.fallingattack$sendFallingAttackPacket(true);
                    }
                } else {
                    invoker.fallingattack$stopFallingAttack();
                    invoker.fallingattack$sendFallingAttackPacket(false);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
