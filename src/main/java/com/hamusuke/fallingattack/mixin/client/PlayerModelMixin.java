package com.hamusuke.fallingattack.mixin.client;

import com.hamusuke.fallingattack.invoker.PlayerInvoker;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(PlayerModel.class)
public abstract class PlayerModelMixin<T extends LivingEntity> extends HumanoidModel<T> {
    PlayerModelMixin(ModelPart p_170677_) {
        super(p_170677_);
    }

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/HumanoidModel;setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", shift = At.Shift.AFTER))
    void setupAnim(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        if (livingEntity instanceof AbstractClientPlayer abstractClientPlayer) {
            PlayerInvoker invoker = (PlayerInvoker) abstractClientPlayer;
            if (invoker.isUsingFallingAttack()) {
                if (invoker.getFallingAttackProgress() < PlayerInvoker.FIRST_FALLING_ATTACK_PROGRESS_TICKS) {
                    if (Float.isNaN(invoker.getYawF())) {
                        invoker.setYawF(abstractClientPlayer.yBodyRot);
                    }

                    abstractClientPlayer.yBodyRot = invoker.getYawF() + 36.0F * invoker.getFallingAttackProgress() * (livingEntity.getMainArm() == HumanoidArm.LEFT ? 1 : -1);
                    abstractClientPlayer.yHeadRot = abstractClientPlayer.yBodyRot;
                } else {
                    this.getArm(livingEntity.getMainArm()).xRot = -85.0F * Mth.DEG_TO_RAD;
                    this.getArm(livingEntity.getMainArm().getOpposite()).xRot = 80.0F * Mth.DEG_TO_RAD;
                }
            } else if (!Float.isNaN(invoker.getYawF())) {
                invoker.setYawF(Float.NaN);
            }
        }
    }
}
