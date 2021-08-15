package com.hamusuke.fallingattack.network;

import com.hamusuke.fallingattack.invoker.IPlayerEntity;
import com.hamusuke.fallingattack.network.s2c.FallingAttackSyncS2CPacket;
import com.hamusuke.fallingattack.network.s2c.FallingAttackS2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;

public class ClientOnlyPacketHandler {
    public static boolean handle(FallingAttackS2CPacket packet) {
        Entity entity = Minecraft.getInstance().player.clientLevel.getEntity(packet.getPlayerEntityId());

        if (entity instanceof AbstractClientPlayerEntity) {
            IPlayerEntity invoker = (IPlayerEntity) entity;

            if (packet.isStart()) {
                invoker.startFallingAttack();
            } else {
                invoker.stopFallingAttack();
            }

            return true;
        }

        return false;
    }

    public static boolean handle(FallingAttackSyncS2CPacket packet) {
        Entity entity = Minecraft.getInstance().player.clientLevel.getEntity(packet.getPlayerEntityId());

        if (entity instanceof AbstractClientPlayerEntity) {
            IPlayerEntity invoker = (IPlayerEntity) entity;
            invoker.setFallingAttackProgress(packet.getProgress());
            invoker.setYawF(packet.getFallingAttackYaw());
            return true;
        }

        return false;
    }
}
