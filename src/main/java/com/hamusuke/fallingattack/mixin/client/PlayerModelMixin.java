package com.hamusuke.fallingattack.mixin.client;

import com.hamusuke.fallingattack.invoker.IPlayerEntity;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.HandSide;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(PlayerModel.class)
public abstract class PlayerModelMixin<T extends LivingEntity> extends BipedModel<T> {
    PlayerModelMixin(float p_i1148_1_) {
        super(p_i1148_1_);
    }

    @Inject(method = "setupAnim(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/model/BipedModel;setupAnim(Lnet/minecraft/entity/LivingEntity;FFFFF)V", shift = At.Shift.AFTER))
    void setupAnim(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        if (livingEntity instanceof AbstractClientPlayerEntity) {
            AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity) livingEntity;
            IPlayerEntity invoker = (IPlayerEntity) abstractClientPlayerEntity;
            if (invoker.isUsingFallingAttack()) {
                if (invoker.getFallingAttackProgress() < IPlayerEntity.FIRST_FALLING_ATTACK_PROGRESS_TICKS) {
                    if (Float.isNaN(invoker.getYawF())) {
                        invoker.setYawF(abstractClientPlayerEntity.yBodyRot);
                    }

                    abstractClientPlayerEntity.yBodyRot = invoker.getYawF() + 36.0F * invoker.getFallingAttackProgress() * (livingEntity.getMainArm() == HandSide.LEFT ? 1 : -1);
                    abstractClientPlayerEntity.yHeadRot = abstractClientPlayerEntity.yBodyRot;
                } else {
                    this.getArm(livingEntity.getMainArm()).xRot = -85.0F * 0.017453292F;
                    this.getArm(livingEntity.getMainArm().getOpposite()).xRot = 80.0F * 0.017453292F;
                }
            } else if (!Float.isNaN(invoker.getYawF())) {
                invoker.setYawF(Float.NaN);
            }
        }
    }
}
