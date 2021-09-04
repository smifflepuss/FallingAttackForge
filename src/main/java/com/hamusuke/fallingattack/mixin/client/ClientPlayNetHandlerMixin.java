package com.hamusuke.fallingattack.mixin.client;

import com.hamusuke.fallingattack.invoker.IPlayerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.network.play.server.SJoinGamePacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(ClientPlayNetHandler.class)
public class ClientPlayNetHandlerMixin {
    @Shadow
    private Minecraft minecraft;

    @Inject(method = "handleLogin", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftGame;onStartGameSession()V", shift = At.Shift.BEFORE))
    private void handleLogin(SJoinGamePacket p_147282_1_, CallbackInfo ci) {
        ((IPlayerEntity) this.minecraft.player).sendSynchronizeFallingAttackPacket();
    }
}
