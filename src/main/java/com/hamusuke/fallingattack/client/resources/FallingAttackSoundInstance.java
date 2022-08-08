package com.hamusuke.fallingattack.client.resources;

import com.hamusuke.fallingattack.invoker.IPlayerEntity;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FallingAttackSoundInstance extends TickableSound {
    private final ClientPlayerEntity player;
    private final IPlayerEntity invoker;

    public FallingAttackSoundInstance(ClientPlayerEntity localPlayer) {
        super(SoundEvents.ELYTRA_FLYING, SoundCategory.PLAYERS);
        this.player = localPlayer;
        this.invoker = (IPlayerEntity) this.player;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.1F;
        this.pitch = 1.2F;
    }

    public void tick() {
        if (!this.player.isAlive() || !this.invoker.isUsingFallingAttack()) {
            this.stop();
        }

        if (this.invoker.getFallingAttackProgress() == IPlayerEntity.FIRST_FALLING_ATTACK_PROGRESS_TICKS) {
            if (this.volume < 1.0F) {
                this.volume = 1.0F;
            }
        } else if (this.invoker.getFallingAttackProgress() > IPlayerEntity.FIRST_FALLING_ATTACK_PROGRESS_TICKS) {
            this.stop();
        }
    }
}
