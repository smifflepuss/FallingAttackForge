package com.hamusuke.fallingattack.client.resources;

import com.hamusuke.fallingattack.invoker.PlayerInvoker;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FallingAttackSoundInstance extends AbstractTickableSoundInstance {
    private final LocalPlayer player;
    private final PlayerInvoker invoker;

    public FallingAttackSoundInstance(LocalPlayer localPlayer) {
        super(SoundEvents.ELYTRA_FLYING, SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        this.player = localPlayer;
        this.invoker = (PlayerInvoker) this.player;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.1F;
        this.pitch = 1.2F;
    }

    public void tick() {
        if (this.player.isRemoved() || !this.invoker.isUsingFallingAttack()) {
            this.stop();
        }

        if (this.invoker.getFallingAttackProgress() == PlayerInvoker.FIRST_FALLING_ATTACK_PROGRESS_TICKS) {
            if (this.volume < 1.0F) {
                this.volume = 1.0F;
            }
        } else if (this.invoker.getFallingAttackProgress() > PlayerInvoker.FIRST_FALLING_ATTACK_PROGRESS_TICKS) {
            this.stop();
        }
    }
}
