package com.hamusuke.fallingattack.mixin.client;

import com.hamusuke.fallingattack.invoker.IPlayerEntity;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(RemoteClientPlayerEntity.class)
public abstract class RemoteClientPlayerEntityMixin extends AbstractClientPlayerEntity implements IPlayerEntity {
    RemoteClientPlayerEntityMixin(ClientWorld p_i50991_1_, GameProfile p_i50991_2_) {
        super(p_i50991_1_, p_i50991_2_);
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    void aiStep(CallbackInfo ci) {
        if (this.isUsingFallingAttack()) {
            if (this.getFallingAttackProgress() < FIRST_FALLING_ATTACK_PROGRESS_TICKS) {
                this.setFallingAttackProgress(this.getFallingAttackProgress() + 1);
            } else if (this.getFallingAttackProgress() == FIRST_FALLING_ATTACK_PROGRESS_TICKS) {
                if (this.isInWater() || this.isInLava() || 0 > this.blockPosition().getY()) {
                    this.stopFallingAttack();
                } else if (this.onGround) {
                    this.setFallingAttackProgress(this.getFallingAttackProgress() + 1);
                }
            } else if (this.getFallingAttackProgress() < FALLING_ATTACK_END_TICKS) {
                this.setFallingAttackProgress(this.getFallingAttackProgress() + 1);
            } else if (this.isUsingFallingAttack()) {
                this.stopFallingAttack();
            }
        }
    }
}
