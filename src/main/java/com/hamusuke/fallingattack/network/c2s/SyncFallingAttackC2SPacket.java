package com.hamusuke.fallingattack.network.c2s;

import com.hamusuke.fallingattack.invoker.IPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.function.Supplier;

public class SyncFallingAttackC2SPacket {
    public SyncFallingAttackC2SPacket(PacketBuffer ignored) {
    }

    public SyncFallingAttackC2SPacket() {
    }

    public void write(PacketBuffer ignored) {
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        MutableBoolean mutableBoolean = new MutableBoolean();
        ctx.get().enqueueWork(() -> {
            ((IPlayerEntity) ctx.get().getSender()).sendSynchronizeFallingAttackPacket();
            mutableBoolean.setTrue();
        });
        ctx.get().setPacketHandled(mutableBoolean.booleanValue());
    }
}
