package com.hamusuke.fallingattack.invoker;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;

public interface PlayerInvoker {
    int FIRST_FALLING_ATTACK_PROGRESS_TICKS = 10;
    int FALLING_ATTACK_END_TICKS = FIRST_FALLING_ATTACK_PROGRESS_TICKS + 6;

    default boolean checkFallingAttack() {
        return false;
    }

    default void startFallingAttack() {
    }

    default void stopFallingAttack() {
    }

    default int getFallingAttackProgress() {
        return 0;
    }

    default void setFallingAttackProgress(int progress) {
    }

    default float getFallingAttackYPos() {
        return 0.0F;
    }

    default void setFallingAttackYPos(float yPos) {
    }

    default boolean isUsingFallingAttack() {
        return false;
    }

    default void sendFallingAttackPacket(boolean start) {
    }

    default void sendSynchronizeFallingAttackPacket() {
    }

    default float getYawF() {
        return 0.0F;
    }

    default void setYawF(float yaw) {
    }

    default void addFallingParticle(ServerPlayer player) {
        AABB aabb = player.getBoundingBox();
        player.getLevel().sendParticles(player, ParticleTypes.POOF, true, aabb.minX - 0.125D, aabb.minY - 1.0D, player.getZ(), 5, 0.5D, 1.0D, 0.0D, 1.0D);
        player.getLevel().sendParticles(player, ParticleTypes.POOF, true, aabb.maxX + 0.125D, aabb.minY - 1.0D, player.getZ(), 5, 0.5D, 1.0D, 0.0D, 1.0D);
        player.getLevel().sendParticles(player, ParticleTypes.POOF, true, player.getX(), aabb.minY - 1.0D, aabb.minZ - 0.125D, 5, 0.0D, 1.0D, 0.5D, 1.0D);
        player.getLevel().sendParticles(player, ParticleTypes.POOF, true, player.getX(), aabb.minY - 1.0D, aabb.maxZ + 0.125D, 5, 0.0D, 1.0D, 0.5D, 1.0D);
    }
}
