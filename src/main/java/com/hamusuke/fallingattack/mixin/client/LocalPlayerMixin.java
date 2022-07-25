package com.hamusuke.fallingattack.mixin.client;

import com.hamusuke.fallingattack.config.Config;
import com.hamusuke.fallingattack.mixin.PlayerMixin;
import com.hamusuke.fallingattack.network.NetworkManager;
import com.hamusuke.fallingattack.network.packet.c2s.FallingAttackC2SPacket;
import com.hamusuke.fallingattack.network.packet.c2s.SyncFallingAttackC2SPacket;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends PlayerMixin {
    @Shadow
    @Final
    protected Minecraft minecraft;

    @Nonnull
    protected CameraType camTypeWhenAttack = CameraType.FIRST_PERSON;

    LocalPlayerMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    public void sendFallingAttackPacket(boolean start) {
        NetworkManager.sendToServer(new FallingAttackC2SPacket(start));
    }

    public void startFallingAttack() {
        super.startFallingAttack();

        if (Config.Client.THIRD_PERSON.get()) {
            this.camTypeWhenAttack = this.minecraft.options.getCameraType();
            this.minecraft.options.setCameraType(CameraType.THIRD_PERSON_BACK);
        }
    }

    public void stopFallingAttack() {
        super.stopFallingAttack();

        if (Config.Client.THIRD_PERSON.get()) {
            CameraType cameraType = this.minecraft.options.getCameraType();
            this.minecraft.options.setCameraType(this.camTypeWhenAttack);
            if (cameraType.isFirstPerson() != this.minecraft.options.getCameraType().isFirstPerson()) {
                this.minecraft.gameRenderer.checkEntityPostEffect(this.minecraft.options.getCameraType().isFirstPerson() ? this.minecraft.getCameraEntity() : null);
            }
        }
    }

    public void sendSynchronizeFallingAttackPacket() {
        NetworkManager.sendToServer(new SyncFallingAttackC2SPacket());
    }
}
