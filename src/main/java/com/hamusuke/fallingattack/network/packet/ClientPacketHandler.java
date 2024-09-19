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
        if (isNotClientPlayerNull()) {
            if (mc.player.clientLevel.getEntity(packet.getPlayerEntityId()) instanceof PlayerInvoker invoker) {
                if (packet.isStart()) {
                    invoker.fallingattack$startFallingAttack();
                } else {
                    invoker.fallingattack$stopFallingAttack();
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void handle(SyncFallingAttackS2CPacket packet) {
        if (isNotClientPlayerNull()) {
            if (packet.isUsingFallingAttack() && mc.player.clientLevel.getEntity(packet.getPlayerEntityId()) instanceof PlayerInvoker invoker) {
                invoker.fallingattack$startFallingAttack();
                invoker.fallingattack$setFallingAttackYPos(packet.getFallingAttackYPos());
                invoker.fallingattack$setFallingAttackProgress(packet.getProgress());
                invoker.fallingattack$setYawF(packet.getFallingAttackYaw());
            }
        }
    }

    private static boolean isNotClientPlayerNull() {
        return mc.player != null;
    }
}
