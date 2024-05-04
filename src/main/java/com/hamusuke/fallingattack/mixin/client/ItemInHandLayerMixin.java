package com.hamusuke.fallingattack.mixin.client;

import com.hamusuke.fallingattack.invoker.PlayerInvoker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(ItemInHandLayer.class)
public abstract class ItemInHandLayerMixin<T extends LivingEntity, M extends EntityModel<T> & ArmedModel> extends RenderLayer<T, M> {
    @Shadow
    @Final
    private ItemInHandRenderer itemInHandRenderer;

    ItemInHandLayerMixin(RenderLayerParent<T, M> p_117346_) {
        super(p_117346_);
    }

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    void renderArmWithItem(LivingEntity p_117185_, ItemStack p_117186_, ItemDisplayContext p_117187_, HumanoidArm p_117188_, PoseStack p_117189_, MultiBufferSource p_117190_, int p_117191_, CallbackInfo ci) {
        if (!p_117186_.isEmpty() && p_117185_ instanceof PlayerInvoker invoker && invoker.getFallingAttackProgress() >= PlayerInvoker.FIRST_FALLING_ATTACK_PROGRESS_TICKS) {
            p_117189_.pushPose();
            boolean bl = p_117188_ == HumanoidArm.LEFT;
            this.getParentModel().translateToHand(p_117188_, p_117189_);
            p_117189_.mulPose(Axis.XP.rotationDegrees(90.0F));
            p_117189_.mulPose(Axis.YP.rotationDegrees(180.0F));
            p_117189_.translate((float) (bl ? -1 : 1) / 16.0F, 0.125D, 0.4D);
            this.itemInHandRenderer.renderItem(p_117185_, p_117186_, p_117187_, bl, p_117189_, p_117190_, p_117191_);
            p_117189_.popPose();
            ci.cancel();
        }
    }
}
