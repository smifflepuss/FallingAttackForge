package com.hamusuke.fallingattack.network.s2c;

import com.hamusuke.fallingattack.network.ClientOnlyPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.function.Supplier;

public class FallingAttackS2CPacket {
    private final int playerEntityId;
    private final boolean start;

    public FallingAttackS2CPacket(FriendlyByteBuf buffer) {
        this.playerEntityId = buffer.readVarInt();
        this.start = buffer.readBoolean();
    }

    public FallingAttackS2CPacket(int playerEntityId, boolean start) {
        this.playerEntityId = playerEntityId;
        this.start = start;
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.playerEntityId);
        buffer.writeBoolean(this.start);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientOnlyPacketHandler.handle(this)));
        ctx.get().setPacketHandled(true);
    }

    public int getPlayerEntityId() {
        return this.playerEntityId;
    }

    public boolean isStart() {
        return this.start;
    }
}
