package com.hamusuke.fallingattack.invoker;

import com.hamusuke.fallingattack.math.wave.AbstractFallingAttackShockWave;

public interface LevelInvoker {
    default void summonShockWave(AbstractFallingAttackShockWave shockWave) {
    }
}
