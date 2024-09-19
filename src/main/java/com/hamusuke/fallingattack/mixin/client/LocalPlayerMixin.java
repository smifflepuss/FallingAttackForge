package com.hamusuke.fallingattack.mixin.client;

import com.hamusuke.fallingattack.client.resources.FallingAttackSoundInstance;
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
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends PlayerMixin {
    @Shadow
    @Final
    protected Minecraft minecraft;

    @Unique
    @Nonnull
    protected CameraType fallingattack$camTypeWhenAttack = CameraType.FIRST_PERSON;

    LocalPlayerMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    public void fallingattack$sendFallingAttackPacket(boolean start) {
        NetworkManager.sendToServer(new FallingAttackC2SPacket(start));
    }

    public void fallingattack$startFallingAttack() {
        super.fallingattack$startFallingAttack();

        this.minecraft.getSoundManager().play(new FallingAttackSoundInstance((LocalPlayer) (Object) this));
        if (Config.Client.THIRD_PERSON.get()) {
            this.fallingattack$camTypeWhenAttack = this.minecraft.options.getCameraType();
            this.minecraft.options.setCameraType(CameraType.THIRD_PERSON_BACK);
        }
    }

    public void fallingattack$stopFallingAttack() {
        super.fallingattack$stopFallingAttack();

        if (Config.Client.THIRD_PERSON.get()) {
            CameraType cameraType = this.minecraft.options.getCameraType();
            this.minecraft.options.setCameraType(this.fallingattack$camTypeWhenAttack);
            if (cameraType.isFirstPerson() != this.minecraft.options.getCameraType().isFirstPerson()) {
                this.minecraft.gameRenderer.checkEntityPostEffect(this.minecraft.options.getCameraType().isFirstPerson() ? this.minecraft.getCameraEntity() : null);
            }
        }
    }

    public void fallingattack$sendSynchronizeFallingAttackPacket() {
        NetworkManager.sendToServer(new SyncFallingAttackC2SPacket());
    }
}
