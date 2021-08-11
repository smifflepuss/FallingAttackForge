package com.hamusuke.fallingattack.invoker;

public interface IPlayerEntity {
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

    default boolean isUsingFallingAttack() {
        return false;
    }

    default void sendFallingAttackPacket(boolean start) {
    }

    default float getYawF() {
        return 0.0F;
    }

    default void setYawF(float yaw) {
    }
}
