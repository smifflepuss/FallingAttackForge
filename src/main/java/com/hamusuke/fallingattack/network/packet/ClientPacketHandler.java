package com.hamusuke.fallingattack.network.packet;

import com.hamusuke.fallingattack.invoker.IPlayerEntity;
import com.hamusuke.fallingattack.network.packet.s2c.FallingAttackS2CPacket;
import com.hamusuke.fallingattack.network.packet.s2c.SyncFallingAttackS2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ClientPacketHandler {
    private static final Minecraft mc = Minecraft.getInstance();

    @OnlyIn(Dist.CLIENT)
    public static void handle(FallingAttackS2CPacket packet) {
        if (isNotClientPlayerNull()) {
            Entity entity = mc.player.clientLevel.getEntity(packet.getPlayerEntityId());
            if (entity instanceof IPlayerEntity) {
                IPlayerEntity invoker = (IPlayerEntity) entity;
                if (packet.isStart()) {
                    invoker.startFallingAttack();
                } else {
                    invoker.stopFallingAttack();
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void handle(SyncFallingAttackS2CPacket packet) {
        if (isNotClientPlayerNull()) {
            Entity entity = mc.player.clientLevel.getEntity(packet.getPlayerEntityId());
            if (entity instanceof IPlayerEntity) {
                IPlayerEntity invoker = (IPlayerEntity) entity;
                invoker.startFallingAttack();
                invoker.setFallingAttackYPos(packet.getFallingAttackYPos());
                invoker.setFallingAttackProgress(packet.getProgress());
                invoker.setYawF(packet.getFallingAttackYaw());
            }
        }
    }

    private static boolean isNotClientPlayerNull() {
        return mc.player != null;
    }
}
