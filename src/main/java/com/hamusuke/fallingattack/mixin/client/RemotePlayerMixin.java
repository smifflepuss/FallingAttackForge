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
    RemotePlayerMixin(ClientLevel p_234112_, GameProfile p_234113_) {
        super(p_234112_, p_234113_);
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    void aiStep(CallbackInfo ci) {
        if (this.fallingattack$isUsingFallingAttack()) {
            if (this.fallingattack$getFallingAttackProgress() < FIRST_FALLING_ATTACK_PROGRESS_TICKS) {
                this.fallingattack$setFallingAttackProgress(this.fallingattack$getFallingAttackProgress() + 1);
            } else if (this.fallingattack$getFallingAttackProgress() == FIRST_FALLING_ATTACK_PROGRESS_TICKS) {
                if (this.isInWater() || this.isInLava() || this.level().dimensionType().minY() > this.blockPosition().getY()) {
                    this.fallingattack$stopFallingAttack();
                } else if (this.onGround()) {
                    this.fallingattack$setFallingAttackProgress(this.fallingattack$getFallingAttackProgress() + 1);
                }
            } else if (this.fallingattack$getFallingAttackProgress() < FALLING_ATTACK_END_TICKS) {
                this.fallingattack$setFallingAttackProgress(this.fallingattack$getFallingAttackProgress() + 1);
            } else if (this.fallingattack$isUsingFallingAttack()) {
                this.fallingattack$stopFallingAttack();
            }
        }
    }
}
