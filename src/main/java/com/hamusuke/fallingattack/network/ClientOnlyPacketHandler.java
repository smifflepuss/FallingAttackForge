package com.hamusuke.fallingattack.network;

import com.hamusuke.fallingattack.invoker.IPlayerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;

public class ClientOnlyPacketHandler {
    public static boolean handle(FallingAttackS2CPacket packet) {
        Entity entity = Minecraft.getInstance().player.clientLevel.getEntity(packet.getPlayerEntityId());

        if (entity instanceof AbstractClientPlayerEntity) {
            AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity) entity;
            IPlayerEntity invoker = (IPlayerEntity) abstractClientPlayerEntity;

            if (packet.isStart()) {
                invoker.startFallingAttack();
            } else {
                invoker.stopFallingAttack();
            }

            return true;
        }

        return false;
    }
}
