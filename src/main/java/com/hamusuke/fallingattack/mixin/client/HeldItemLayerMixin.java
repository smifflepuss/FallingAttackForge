package com.hamusuke.fallingattack.mixin.client;

import com.hamusuke.fallingattack.invoker.IPlayerEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(HeldItemLayer.class)
public abstract class HeldItemLayerMixin<T extends LivingEntity, M extends EntityModel<T> & IHasArm> extends LayerRenderer<T, M> {
    HeldItemLayerMixin(IEntityRenderer<T, M> p_i50926_1_) {
        super(p_i50926_1_);
    }

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    void renderArmWithItem(LivingEntity p_229135_1_, ItemStack p_229135_2_, ItemCameraTransforms.TransformType p_229135_3_, HandSide p_229135_4_, MatrixStack p_229135_5_, IRenderTypeBuffer p_229135_6_, int p_229135_7_, CallbackInfo ci) {
        if (!p_229135_2_.isEmpty() && p_229135_1_ instanceof AbstractClientPlayerEntity && ((IPlayerEntity) p_229135_1_).getFallingAttackProgress() >= IPlayerEntity.FIRST_FALLING_ATTACK_PROGRESS_TICKS) {
            p_229135_5_.pushPose();
            boolean bl = p_229135_4_ == HandSide.LEFT;
            this.getParentModel().translateToHand(p_229135_4_, p_229135_5_);
            p_229135_5_.mulPose(Vector3f.XP.rotationDegrees(90.0F));
            p_229135_5_.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            p_229135_5_.translate((float) (bl ? -1 : 1) / 16.0F, 0.125D, 0.4D);
            Minecraft.getInstance().getItemInHandRenderer().renderItem(p_229135_1_, p_229135_2_, p_229135_3_, bl, p_229135_5_, p_229135_6_, p_229135_7_);
            p_229135_5_.popPose();
            ci.cancel();
        }
    }
}
