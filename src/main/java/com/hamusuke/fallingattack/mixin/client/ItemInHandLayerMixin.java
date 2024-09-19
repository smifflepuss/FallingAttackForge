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

    ItemInHandLayerMixin(RenderLayerParent<T, M> parent) {
        super(parent);
    }

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    void renderArmWithItem(LivingEntity entity, ItemStack stack, ItemDisplayContext itemDisplayContext, HumanoidArm humanoidArm, PoseStack poseStack, MultiBufferSource bufferSource, int light, CallbackInfo ci) {
        if (!stack.isEmpty() && entity instanceof PlayerInvoker invoker && invoker.fallingattack$getFallingAttackProgress() >= PlayerInvoker.FIRST_FALLING_ATTACK_PROGRESS_TICKS) {
            poseStack.pushPose();
            boolean left = humanoidArm == HumanoidArm.LEFT;
            this.getParentModel().translateToHand(humanoidArm, poseStack);
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            poseStack.translate((float) (left ? -1 : 1) / 16.0F, 0.125D, 0.4D);
            this.itemInHandRenderer.renderItem(entity, stack, itemDisplayContext, left, poseStack, bufferSource, light);
            poseStack.popPose();
            ci.cancel();
        }
    }
}
