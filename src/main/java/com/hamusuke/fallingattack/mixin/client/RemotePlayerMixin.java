package com.hamusuke.fallingattack.mixin.client;

import com.hamusuke.fallingattack.invoker.PlayerInvoker;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(RemotePlayer.class)
public abstract class RemotePlayerMixin extends AbstractClientPlayer implements PlayerInvoker {
    RemotePlayerMixin(ClientLevel p_108548_, GameProfile p_108549_) {
        super(p_108548_, p_108549_);
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
