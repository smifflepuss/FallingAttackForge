package com.hamusuke.fallingattack.network;

import com.hamusuke.fallingattack.FallingAttack;
import com.hamusuke.fallingattack.network.c2s.FallingAttackC2SPacket;
import com.hamusuke.fallingattack.network.c2s.SyncFallingAttackC2SPacket;
import com.hamusuke.fallingattack.network.s2c.FallingAttackS2CPacket;
import com.hamusuke.fallingattack.network.s2c.SyncFallingAttackS2CPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkManager {
    private static final String PROTOCOL_VERSION = "2";
    private static SimpleChannel INSTANCE;
    private static int ID = 0;

    private static int nextID() {
        return ID++;
    }

    public static void initNetworking() {
        if (INSTANCE != null) {
            throw new IllegalStateException("Networking is already initialized.");
        }

        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(FallingAttack.MOD_ID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
        INSTANCE.registerMessage(nextID(), FallingAttackC2SPacket.class, FallingAttackC2SPacket::write, FallingAttackC2SPacket::new, FallingAttackC2SPacket::handle);
        INSTANCE.registerMessage(nextID(), FallingAttackS2CPacket.class, FallingAttackS2CPacket::write, FallingAttackS2CPacket::new, FallingAttackS2CPacket::handle);
        INSTANCE.registerMessage(nextID(), SyncFallingAttackC2SPacket.class, SyncFallingAttackC2SPacket::write, SyncFallingAttackC2SPacket::new, SyncFallingAttackC2SPacket::handle);
        INSTANCE.registerMessage(nextID(), SyncFallingAttackS2CPacket.class, SyncFallingAttackS2CPacket::write, SyncFallingAttackS2CPacket::new, SyncFallingAttackS2CPacket::handle);
    }

    public static void sendToClient(Object packet, ServerPlayerEntity player) {
        INSTANCE.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendToServer(Object packet) {
        INSTANCE.sendToServer(packet);
    }
}
