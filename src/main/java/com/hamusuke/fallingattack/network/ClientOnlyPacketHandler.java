package com.hamusuke.fallingattack.network;

import com.hamusuke.fallingattack.invoker.IPlayerEntity;
import com.hamusuke.fallingattack.network.s2c.SyncFallingAttackS2CPacket;
import com.hamusuke.fallingattack.network.s2c.FallingAttackS2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ClientOnlyPacketHandler {
    @OnlyIn(Dist.CLIENT)
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

    @OnlyIn(Dist.CLIENT)
    public static boolean handle(SyncFallingAttackS2CPacket packet) {
        Entity entity = Minecraft.getInstance().player.clientLevel.getEntity(packet.getPlayerEntityId());

        if (packet.isUsingFallingAttack() && entity instanceof AbstractClientPlayerEntity) {
            IPlayerEntity invoker = (IPlayerEntity) entity;
            invoker.startFallingAttack();
            invoker.setFallingAttackYPos(packet.getFallingAttackYPos());
            invoker.setFallingAttackProgress(packet.getProgress());
            invoker.setYawF(packet.getFallingAttackYaw());
            return true;
        }

        return false;
    }
}
