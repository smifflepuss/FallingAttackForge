package com.hamusuke.fallingattack.math.wave;

import com.hamusuke.fallingattack.FallingAttack;
import com.hamusuke.fallingattack.math.Circle;
import com.hamusuke.fallingattack.math.Vec2d;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractFallingAttackShockWave {
    protected final Vec3 pos;
    protected final AABB box;
    protected final Level level;
    protected final Circle primary;
    protected final Circle secondary;
    protected boolean isDead;

    protected AbstractFallingAttackShockWave(Vec3 pos, AABB box, Level level) {
        this.pos = pos;
        this.box = box;
        this.level = level;
        this.primary = new Circle(this.pos.x(), this.pos.z(), 0.0D);
        this.secondary = new Circle(this.pos.x(), this.pos.z(), -Mth.SQRT_OF_TWO * FallingAttack.SHOCK_WAVE_SPREADING_SPEED_PER_TICK);
    }

    protected static double distanceTo2D(Vec3 from, Vec3 to) {
        double x = to.x - from.x;
        double z = to.z - from.z;
        return Math.sqrt(x * x + z * z);
    }

    public void tick() {
    }

    protected void damage(Entity target, float damageModifier) {
    }

    public void forEachVec2d(Consumer<Vec2d> consumer, int slices) {
        for (int i = 0; i < 360; i++) {
            if (i % slices == 0) {
                consumer.accept(this.primary.getCoordinates((float) (i * Math.PI / 180.0F), true));
            }
        }
    }

    public List<LivingEntity> getEntitiesStruckByShockWave() {
        return Collections.emptyList();
    }

    public Vec3 getPos() {
        return this.pos;
    }

    public AABB getAABB() {
        return this.box;
    }

    public boolean isDead() {
        return this.isDead;
    }
}
