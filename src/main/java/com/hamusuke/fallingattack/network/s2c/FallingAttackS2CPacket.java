package com.hamusuke.fallingattack.network.s2c;

import com.hamusuke.fallingattack.network.ClientOnlyPacketHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.function.Supplier;

public class FallingAttackS2CPacket {
    private final int playerEntityId;
    private final boolean start;

    public FallingAttackS2CPacket(PacketBuffer buffer) {
        this.playerEntityId = buffer.readVarInt();
        this.start = buffer.readBoolean();
    }

    public FallingAttackS2CPacket(int playerEntityId, boolean start) {
        this.playerEntityId = playerEntityId;
        this.start = start;
    }

    public void write(PacketBuffer buffer) {
        buffer.writeVarInt(this.playerEntityId);
        buffer.writeBoolean(this.start);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        MutableBoolean mutableBoolean = new MutableBoolean();
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> mutableBoolean.setValue(ClientOnlyPacketHandler.handle(this))));
        ctx.get().setPacketHandled(mutableBoolean.booleanValue());
    }

    public int getPlayerEntityId() {
        return this.playerEntityId;
    }

    public boolean isStart() {
        return this.start;
    }
}
