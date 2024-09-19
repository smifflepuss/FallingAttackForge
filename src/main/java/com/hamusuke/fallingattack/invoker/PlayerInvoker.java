package com.hamusuke.fallingattack.invoker;

public interface PlayerInvoker {
    int FIRST_FALLING_ATTACK_PROGRESS_TICKS = 10;
    int FALLING_ATTACK_END_TICKS = FIRST_FALLING_ATTACK_PROGRESS_TICKS + 6;

    default boolean fallingattack$checkFallingAttack() {
        return false;
    }

    default void fallingattack$startFallingAttack() {
    }

    default void fallingattack$stopFallingAttack() {
    }

    default int fallingattack$getFallingAttackProgress() {
        return 0;
    }

    default void fallingattack$setFallingAttackProgress(int progress) {
    }

    default float fallingattack$getFallingAttackYPos() {
        return 0.0F;
    }

    default void fallingattack$setFallingAttackYPos(float yPos) {
    }

    default boolean fallingattack$isUsingFallingAttack() {
        return false;
    }

    default void fallingattack$sendFallingAttackPacket(boolean start) {
    }

    default void fallingattack$sendSynchronizeFallingAttackPacket() {
    }

    default float fallingattack$getYawF() {
        return 0.0F;
    }

    default void fallingattack$setYawF(float yaw) {
    }
}
