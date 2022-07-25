package com.hamusuke.fallingattack.mixin.client;

import com.hamusuke.fallingattack.invoker.PlayerInvoker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    @Nullable
    public LocalPlayer player;

    @Shadow
    @Final
    public Options options;

    @Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
    void startAttack(CallbackInfoReturnable<Boolean> cir) {
        PlayerInvoker invoker = (PlayerInvoker) this.player;

        if (invoker != null) {
            if (this.options.keyShift.isDown() && !invoker.isUsingFallingAttack()) {
                if (invoker.checkFallingAttack()) {
                    this.options.keyShift.setDown(false);
                    invoker.sendFallingAttackPacket(true);
                    cir.setReturnValue(true);
                    cir.cancel();
                }
            } else if (invoker.isUsingFallingAttack()) {
                invoker.sendFallingAttackPacket(false);
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }

    @Inject(method = "continueAttack", at = @At("HEAD"), cancellable = true)
    void continueAttack(boolean p_147115_1_, CallbackInfo ci) {
        if (this.player != null && ((PlayerInvoker) this.player).isUsingFallingAttack()) {
            ci.cancel();
        }
    }
}
