package com.hamusuke.fallingattack.mixin;

import com.hamusuke.fallingattack.network.s2c.FallingAttackS2CPacket;
import com.hamusuke.fallingattack.network.NetworkManager;
import com.hamusuke.fallingattack.network.s2c.SyncFallingAttackS2CPacket;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntityMixin {
    @Shadow
    @Final
    public MinecraftServer server;

    ServerPlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void addAdditionalSaveData(CompoundNBT nbt, CallbackInfo ci) {
        nbt.putBoolean("UsingFallingAttack", this.fallingAttack);
        nbt.putFloat("StartFallingAttackYPos", this.yPosWhenStartFallingAttack);
        nbt.putInt("FallingAttackProgress", this.fallingAttackProgress);
        nbt.putFloat("StartFallingAttackYaw", this.storeYaw);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void readAdditionalSaveData(CompoundNBT nbt, CallbackInfo ci) {
        this.fallingAttack = nbt.getBoolean("UsingFallingAttack");
        this.yPosWhenStartFallingAttack = nbt.getFloat("StartFallingAttackYPos");
        this.fallingAttackProgress = nbt.getInt("FallingAttackProgress");
        this.storeYaw = nbt.getFloat("StartFallingAttackYaw");
    }

    public void sendFallingAttackPacket(boolean start) {
        this.server.getPlayerList().getPlayers().forEach(serverPlayerEntity -> {
            NetworkManager.sendToClient(new FallingAttackS2CPacket(this.getId(), start), serverPlayerEntity);
        });
    }

    public void sendSynchronizeFallingAttackPacket() {
        this.server.getPlayerList().getPlayers().forEach(serverPlayerEntity -> {
            NetworkManager.sendToClient(new SyncFallingAttackS2CPacket(this.getId(), this.fallingAttack, this.yPosWhenStartFallingAttack, this.fallingAttackProgress, this.storeYaw), serverPlayerEntity);
        });
    }
}
