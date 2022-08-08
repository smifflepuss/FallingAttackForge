package com.hamusuke.fallingattack.network.packet.s2c;

import com.hamusuke.fallingattack.network.packet.ClientPacketHandler;
import com.hamusuke.fallingattack.network.packet.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class FallingAttackS2CPacket implements Packet {
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

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.playerEntityId);
        buffer.writeBoolean(this.start);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handle(this)));
        ctx.get().setPacketHandled(true);
    }

    public int getPlayerEntityId() {
        return this.playerEntityId;
    }

    public boolean isStart() {
        return this.start;
    }
}
