package com.hamusuke.fallingattack.math.wave;

import com.hamusuke.fallingattack.FallingAttack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientFallingAttackShockWave extends AbstractFallingAttackShockWave {
    public ClientFallingAttackShockWave(Vec3 pos, AABB box, ClientLevel level) {
        super(pos, box, level);
    }

    public void tick() {
        if (!this.isDead) {
            this.primary.spread(FallingAttack.SHOCK_WAVE_SPREADING_SPEED_PER_TICK);
            this.forEachVec2d(vec2d -> this.castToClient().addParticle(ParticleTypes.EXPLOSION.getType(), false, vec2d.x(), this.pos.y, vec2d.y(), 1.0D, 0.0D, 0.0D), 1);
            this.secondary.spread(FallingAttack.SHOCK_WAVE_SPREADING_SPEED_PER_TICK);

            if (this.primary.getRadius() >= this.box.getXsize()) {
                this.isDead = true;
            }
        }
    }

    public ClientLevel castToClient() {
        return (ClientLevel) this.level;
    }
}
