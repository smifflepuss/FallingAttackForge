package com.hamusuke.fallingattack.mixin;

import com.google.common.collect.Lists;
import com.hamusuke.fallingattack.invoker.LevelInvoker;
import com.hamusuke.fallingattack.math.wave.AbstractFallingAttackShockWave;
import com.hamusuke.fallingattack.math.wave.ServerFallingAttackShockWave;
import com.hamusuke.fallingattack.network.NetworkManager;
import com.hamusuke.fallingattack.network.packet.s2c.FallingAttackShockWaveS2CPacket;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level implements LevelInvoker {
    @Shadow
    @Final
    List<ServerPlayer> players;
    private final List<ServerFallingAttackShockWave> shockWaves = Lists.newArrayList();

    ServerLevelMixin(WritableLevelData p_220352_, ResourceKey<Level> p_220353_, Holder<DimensionType> p_220354_, Supplier<ProfilerFiller> p_220355_, boolean p_220356_, boolean p_220357_, long p_220358_, int p_220359_) {
        super(p_220352_, p_220353_, p_220354_, p_220355_, p_220356_, p_220357_, p_220358_, p_220359_);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        synchronized (this.shockWaves) {
            this.shockWaves.forEach(ServerFallingAttackShockWave::tick);
            this.shockWaves.removeIf(ServerFallingAttackShockWave::isDead);
        }
    }

    @Override
    public void summonShockWave(AbstractFallingAttackShockWave shockWave) {
        this.shockWaves.add((ServerFallingAttackShockWave) shockWave);

        FallingAttackShockWaveS2CPacket packet = new FallingAttackShockWaveS2CPacket(shockWave);
        this.players.forEach(serverPlayer -> NetworkManager.sendToClient(packet, serverPlayer));
    }
}
