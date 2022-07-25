package com.hamusuke.fallingattack.mixin;

import com.hamusuke.fallingattack.network.NetworkManager;
import com.hamusuke.fallingattack.network.s2c.FallingAttackS2CPacket;
import com.hamusuke.fallingattack.network.s2c.SyncFallingAttackS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends PlayerMixin {
    @Shadow
    @Final
    public MinecraftServer server;

    ServerPlayerMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void addAdditionalSaveData(CompoundTag nbt, CallbackInfo ci) {
        nbt.putBoolean("UsingFallingAttack", this.fallingAttack);
        nbt.putFloat("StartFallingAttackYPos", this.yPosWhenStartFallingAttack);
        nbt.putInt("FallingAttackProgress", this.fallingAttackProgress);
        nbt.putFloat("StartFallingAttackYaw", this.storeYaw);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void readAdditionalSaveData(CompoundTag nbt, CallbackInfo ci) {
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
