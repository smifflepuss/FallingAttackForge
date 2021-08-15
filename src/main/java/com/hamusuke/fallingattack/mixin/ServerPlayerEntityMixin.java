package com.hamusuke.fallingattack.mixin;

import com.hamusuke.fallingattack.network.s2c.FallingAttackS2CPacket;
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

    /*TODO
    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void addAdditionalSaveData(CompoundNBT p_213281_1_, CallbackInfo ci) {
        p_213281_1_.putBoolean("usingFallingAttack", this.fallingAttack);
        p_213281_1_.putInt("fallingAttackProgress", this.fallingAttackProgress);
        p_213281_1_.putFloat("fallDistanceWhenStartFallingAttack", this.fallDistanceWhenStartFallingAttack);
        p_213281_1_.putFloat("fallingAttackYaw", this.storeYaw);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void readAdditionalSaveData(CompoundNBT p_70037_1_, CallbackInfo ci) {
        if (p_70037_1_.getBoolean("usingFallingAttack")) {
            if (this.checkFallingAttack()) {
                this.startFallingAttack();
                this.fallingAttackProgress = p_70037_1_.getInt("fallingAttackProgress");
                this.fallDistanceWhenStartFallingAttack = p_70037_1_.getFloat("fallDistanceWhenStartFallingAttack");
                this.storeYaw = p_70037_1_.getFloat("fallingAttackYaw");
                this.sendFallingAttackPacket(true);
                this.server.getPlayerList().getPlayers().forEach(serverPlayerEntity -> {
                    NetworkManager.sendToClient(new FallingAttackSyncS2CPacket(this.getId(), this.fallingAttackProgress, this.storeYaw), serverPlayerEntity);
                });
            }
        }
    }
    */

    public void sendFallingAttackPacket(boolean start) {
        this.server.getPlayerList().getPlayers().forEach(serverPlayerEntity -> {
            NetworkManager.sendToClient(new FallingAttackS2CPacket(this.getId(), start), serverPlayerEntity);
        });
    }
}
