package com.hamusuke.fallingattack.network.packet.s2c;

import com.hamusuke.fallingattack.math.wave.AbstractFallingAttackShockWave;
import com.hamusuke.fallingattack.network.packet.ClientPacketHandler;
import com.hamusuke.fallingattack.network.packet.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class FallingAttackShockWaveS2CPacket implements Packet {
    private final Vec3 pos;
    private final AABB aabb;

    public FallingAttackShockWaveS2CPacket(AbstractFallingAttackShockWave serverFallingAttackShockWave) {
        this.pos = serverFallingAttackShockWave.getPos();
        this.aabb = serverFallingAttackShockWave.getAABB();
    }

    public FallingAttackShockWaveS2CPacket(FriendlyByteBuf buffer) {
        this.pos = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        this.aabb = new AABB(buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeDouble(this.pos.x());
        buffer.writeDouble(this.pos.y());
        buffer.writeDouble(this.pos.z());
        buffer.writeDouble(this.aabb.minX);
        buffer.writeDouble(this.aabb.minY);
        buffer.writeDouble(this.aabb.minZ);
        buffer.writeDouble(this.aabb.maxX);
        buffer.writeDouble(this.aabb.maxY);
        buffer.writeDouble(this.aabb.maxZ);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handle(this)));
        ctx.get().setPacketHandled(true);
    }

    public Vec3 getPos() {
        return this.pos;
    }

    public AABB getAABB() {
        return this.aabb;
    }
}
