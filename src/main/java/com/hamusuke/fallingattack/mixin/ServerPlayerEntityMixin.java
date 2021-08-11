package com.hamusuke.fallingattack.mixin;

import com.hamusuke.fallingattack.network.FallingAttackS2CPacket;
import com.hamusuke.fallingattack.network.NetworkManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntityMixin {
    @Shadow
    @Final
    public MinecraftServer server;

    ServerPlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    public void sendFallingAttackPacket(boolean start) {
        this.server.getPlayerList().getPlayers().forEach(serverPlayerEntity -> {
            NetworkManager.sendToClient(new FallingAttackS2CPacket(this.getId(), start), serverPlayerEntity);
        });
    }
}
