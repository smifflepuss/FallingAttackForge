package com.hamusuke.fallingattack.mixin.client;

import com.hamusuke.fallingattack.config.Config;
import com.hamusuke.fallingattack.mixin.PlayerEntityMixin;
import com.hamusuke.fallingattack.network.c2s.FallingAttackC2SPacket;
import com.hamusuke.fallingattack.network.NetworkManager;
import com.hamusuke.fallingattack.network.c2s.SyncFallingAttackC2SPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends PlayerEntityMixin {
    @Shadow
    @Final
    protected Minecraft minecraft;

    @Nonnull
    protected PointOfView pointOfViewWhenStartFallingAttack = PointOfView.FIRST_PERSON;

    ClientPlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    public void sendFallingAttackPacket(boolean start) {
        NetworkManager.sendToServer(new FallingAttackC2SPacket(start));
    }

    public void startFallingAttack() {
        super.startFallingAttack();

        if (Config.thirdPerson.get()) {
            this.pointOfViewWhenStartFallingAttack = this.minecraft.options.getCameraType();
            this.minecraft.options.setCameraType(PointOfView.THIRD_PERSON_BACK);
        }
    }

    public void stopFallingAttack() {
        super.stopFallingAttack();

        if (Config.thirdPerson.get()) {
            PointOfView pointofview = this.minecraft.options.getCameraType();
            this.minecraft.options.setCameraType(this.pointOfViewWhenStartFallingAttack);
            if (pointofview.isFirstPerson() != this.minecraft.options.getCameraType().isFirstPerson()) {
                this.minecraft.gameRenderer.checkEntityPostEffect(this.minecraft.options.getCameraType().isFirstPerson() ? this.minecraft.getCameraEntity() : null);
            }
        }
    }

    public void sendSynchronizeFallingAttackPacket() {
        NetworkManager.sendToServer(new SyncFallingAttackC2SPacket());
    }
}
