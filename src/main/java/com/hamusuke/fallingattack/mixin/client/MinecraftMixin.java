package com.hamusuke.fallingattack.mixin.client;

import com.hamusuke.fallingattack.FallingAttack;
import com.hamusuke.fallingattack.invoker.IPlayerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
    void startAttack(CallbackInfo ci) {
        IPlayerEntity invoker = (IPlayerEntity) this.player;

        if (!invoker.isUsingFallingAttack()) {
            ItemStack itemStack = this.player.getMainHandItem();
            if (EnchantmentHelper.getItemEnchantmentLevel(FallingAttack.FALLING_ATTACK, itemStack) > 0 && ((IPlayerEntity) this.player).checkFallingAttack()) {
                invoker.sendFallingAttackPacket(true);
                ci.cancel();
            }
        } else if (invoker.isUsingFallingAttack()) {
            invoker.sendFallingAttackPacket(false);
        }
    }
}
