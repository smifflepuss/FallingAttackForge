package com.hamusuke.fallingattack.network.s2c;

import com.hamusuke.fallingattack.network.ClientOnlyPacketHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class FallingAttackSyncS2CPacket {
    private final int playerEntityId;
    private final int progress;
    private final float fallingAttackYaw;

    public FallingAttackSyncS2CPacket(PacketBuffer buffer) {
        this.playerEntityId = buffer.readVarInt();
        this.progress = buffer.readVarInt();
        this.fallingAttackYaw = buffer.readFloat();
    }

    public FallingAttackSyncS2CPacket(int playerEntityId, int progress, float yaw) {
        this.playerEntityId = playerEntityId;
        this.progress = progress;
        this.fallingAttackYaw = yaw;
    }

    public void write(PacketBuffer buffer) {
        buffer.writeVarInt(this.playerEntityId);
        buffer.writeVarInt(this.progress);
        buffer.writeFloat(this.fallingAttackYaw);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        AtomicBoolean atomicBoolean = new AtomicBoolean();
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> atomicBoolean.set(ClientOnlyPacketHandler.handle(this))));
        ctx.get().setPacketHandled(atomicBoolean.get());
    }

    public int getPlayerEntityId() {
        return this.playerEntityId;
    }

    public int getProgress() {
        return this.progress;
    }

    public float getFallingAttackYaw() {
        return this.fallingAttackYaw;
    }
}
