package com.hamusuke.fallingattack.mixin.client;

import com.google.common.collect.Lists;
import com.hamusuke.fallingattack.invoker.LevelInvoker;
import com.hamusuke.fallingattack.math.wave.AbstractFallingAttackShockWave;
import com.hamusuke.fallingattack.math.wave.ClientFallingAttackShockWave;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin extends Level implements LevelInvoker {
    private final List<ClientFallingAttackShockWave> shockWaves = Lists.newArrayList();

    ClientLevelMixin(WritableLevelData p_220352_, ResourceKey<Level> p_220353_, Holder<DimensionType> p_220354_, Supplier<ProfilerFiller> p_220355_, boolean p_220356_, boolean p_220357_, long p_220358_, int p_220359_) {
        super(p_220352_, p_220353_, p_220354_, p_220355_, p_220356_, p_220357_, p_220358_, p_220359_);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        synchronized (this.shockWaves) {
            this.shockWaves.forEach(AbstractFallingAttackShockWave::tick);
            this.shockWaves.removeIf(AbstractFallingAttackShockWave::isDead);
        }
    }

    @Override
    public void summonShockWave(AbstractFallingAttackShockWave shockWave) {
        this.shockWaves.add((ClientFallingAttackShockWave) shockWave);
    }
}
