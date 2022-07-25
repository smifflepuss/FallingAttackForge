package com.hamusuke.fallingattack.network.packet;

import com.hamusuke.fallingattack.invoker.LevelInvoker;
import com.hamusuke.fallingattack.invoker.PlayerInvoker;
import com.hamusuke.fallingattack.math.wave.ClientFallingAttackShockWave;
import com.hamusuke.fallingattack.network.packet.s2c.FallingAttackS2CPacket;
import com.hamusuke.fallingattack.network.packet.s2c.FallingAttackShockWaveS2CPacket;
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
            if (packet.isUsingFallingAttack() && mc.player.clientLevel.getEntity(packet.getPlayerEntityId()) instanceof PlayerInvoker invoker) {
                invoker.startFallingAttack();
                invoker.setFallingAttackYPos(packet.getFallingAttackYPos());
                invoker.setFallingAttackProgress(packet.getProgress());
                invoker.setYawF(packet.getFallingAttackYaw());
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void handle(FallingAttackShockWaveS2CPacket packet) {
        if (isNotClientPlayerNull()) {
            ((LevelInvoker) mc.player.clientLevel).summonShockWave(new ClientFallingAttackShockWave(packet.getPos(), packet.getAABB(), mc.player.clientLevel));
        }
    }

    private static boolean isNotClientPlayerNull() {
        return mc.player != null;
    }
}
