package com.hamusuke.fallingattack.mixin.client;

import com.hamusuke.fallingattack.invoker.PlayerInvoker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(ClientPacketListener.class)
public class ClientPlayNetHandlerMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "handleLogin", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Game;onStartGameSession()V", shift = At.Shift.BEFORE))
    private void handleLogin(ClientboundLoginPacket p_105030_, CallbackInfo ci) {
        if (this.minecraft.player instanceof PlayerInvoker invoker) {
            invoker.sendSynchronizeFallingAttackPacket();
        }
    }
}
