package com.hamusuke.fallingattack.network.packet;

import com.hamusuke.fallingattack.invoker.PlayerInvoker;
import com.hamusuke.fallingattack.network.packet.s2c.FallingAttackS2CPacket;
import com.hamusuke.fallingattack.network.packet.s2c.SyncFallingAttackS2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ClientPacketHandler {
    private static final Minecraft mc = Minecraft.getInstance();

    @OnlyIn(Dist.CLIENT)
    public static void handle(FallingAttackS2CPacket packet) {
        if (mc.player != null && mc.player.clientLevel.getEntity(packet.getPlayerEntityId()) instanceof PlayerInvoker invoker) {
            if (packet.isStart()) {
                invoker.startFallingAttack();
            } else {
                invoker.stopFallingAttack();
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void handle(SyncFallingAttackS2CPacket packet) {
        if (packet.isUsingFallingAttack() && mc.player != null && mc.player.clientLevel.getEntity(packet.getPlayerEntityId()) instanceof PlayerInvoker invoker) {
            invoker.startFallingAttack();
            invoker.setFallingAttackYPos(packet.getFallingAttackYPos());
            invoker.setFallingAttackProgress(packet.getProgress());
            invoker.setYawF(packet.getFallingAttackYaw());
        }
    }
}
