package com.hamusuke.fallingattack.network;

import com.hamusuke.fallingattack.invoker.IPlayerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.concurrent.atomic.AtomicBoolean;
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
        AtomicBoolean atomicBoolean = new AtomicBoolean();
        ctx.get().enqueueWork(() -> {
            Entity entity = Minecraft.getInstance().player.clientLevel.getEntity(this.playerEntityId);

            if (entity instanceof AbstractClientPlayerEntity) {
                AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity) entity;
                IPlayerEntity invoker = (IPlayerEntity) abstractClientPlayerEntity;

                if (this.start) {
                    invoker.startFallingAttack();
                } else {
                    invoker.stopFallingAttack();
                }

                atomicBoolean.set(true);
            }
        });
        ctx.get().setPacketHandled(atomicBoolean.get());
    }
}
