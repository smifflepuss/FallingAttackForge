package com.hamusuke.fallingattack.math.wave;

import com.google.common.collect.Lists;
import com.hamusuke.fallingattack.FallingAttack;
import com.hamusuke.fallingattack.config.Config;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;

import java.util.List;
import java.util.function.BiFunction;

public class ServerFallingAttackShockWave extends AbstractFallingAttackShockWave {
    private final ServerPlayer owner;
    private final ItemStack sword;
    private final List<LivingEntity> exceptEntities = Lists.newArrayList();
    private final BiFunction<Float, Integer, Float> fallingAttackDamageComputer;
    private final BiFunction<Float, Integer, Float> knockbackStrengthComputer;

    public ServerFallingAttackShockWave(ServerPlayer owner, ItemStack swordItem, AABB box, BiFunction<Float, Integer, Float> fallingAttackDamageComputer, BiFunction<Float, Integer, Float> knockbackStrengthComputer) {
        super(owner.position(), box, owner.level);
        this.owner = owner;
        this.sword = swordItem;
        this.fallingAttackDamageComputer = fallingAttackDamageComputer;
        this.knockbackStrengthComputer = knockbackStrengthComputer;
    }

    public void tick() {
        if (!this.isDead) {
            this.primary.spread(FallingAttack.SHOCK_WAVE_SPREADING_SPEED_PER_TICK);
            this.secondary.spread(FallingAttack.SHOCK_WAVE_SPREADING_SPEED_PER_TICK);
            this.getEntitiesStruckByShockWave().forEach(livingEntity -> {
                this.damage(livingEntity, (float) ((this.box.getXsize() - this.primary.getRadius()) / this.box.getXsize()));
                this.exceptEntities.add(livingEntity);
            });

            if (this.primary.getRadius() >= this.box.getXsize()) {
                this.isDead = true;
            }
        }
    }

    protected void damage(Entity target, float damageModifier) {
        if (!ForgeHooks.onPlayerAttackTarget(this.owner, target)) {
            return;
        }

        if (target.isAttackable()) {
            if (!target.skipAttackInteraction(this.owner)) {
                float damageAmount = (float) this.owner.getAttributeValue(Attributes.ATTACK_DAMAGE);
                float attackDamage;
                if (target instanceof LivingEntity) {
                    attackDamage = EnchantmentHelper.getDamageBonus(this.sword, ((LivingEntity) target).getMobType());
                } else {
                    attackDamage = EnchantmentHelper.getDamageBonus(this.sword, MobType.UNDEFINED);
                }

                if (damageAmount > 0.0F || attackDamage > 0.0F) {
                    float distanceToTarget = (float) this.pos.distanceTo(target.position());
                    int fallingAttackLevel = EnchantmentHelper.getTagEnchantmentLevel(FallingAttack.ModRegistries.SHARPNESS_OF_FALLING_ATTACK.get(), this.sword) + 1;
                    fallingAttackLevel = Mth.clamp(fallingAttackLevel, 1, 255);
                    attackDamage += this.fallingAttackDamageComputer.apply(distanceToTarget, fallingAttackLevel);
                    this.level.playSound(null, this.owner.getX(), this.owner.getY(), this.owner.getZ(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, this.owner.getSoundSource(), 1.0F, 1.0F);

                    boolean bl3 = !this.owner.onClimbable() && !this.owner.isInWater() && !this.owner.hasEffect(MobEffects.BLINDNESS) && !this.owner.isPassenger() && target instanceof LivingEntity;
                    if (bl3) {
                        damageAmount *= 1.5F;
                    }

                    damageAmount += attackDamage;
                    float targetHealth = 0.0F;
                    boolean fireAspectEnchanted = false;
                    int fireAspectLevel = EnchantmentHelper.getFireAspect(this.owner);
                    if (target instanceof LivingEntity) {
                        targetHealth = ((LivingEntity) target).getHealth();
                        if (fireAspectLevel > 0 && !target.isOnFire()) {
                            fireAspectEnchanted = true;
                            target.setSecondsOnFire(1);
                        }
                    }

                    Vec3 vec3d = target.getDeltaMovement();
                    boolean tookDamage = target.hurt(DamageSource.playerAttack(this.owner), damageAmount * damageModifier * (Config.Common.DAMAGE_AMOUNT.get() / 100.0F));
                    if (tookDamage) {
                        float yaw = (float) -Mth.atan2(target.getX() - this.pos.x(), target.getZ() - this.pos.z());
                        float strength = this.knockbackStrengthComputer.apply(distanceToTarget, fallingAttackLevel);
                        strength *= Config.Common.KNOCKBACK_AMOUNT.get() / 100.0F;
                        if (target instanceof LivingEntity) {
                            ((LivingEntity) target).knockback(strength, Mth.sin(yaw), -Mth.cos(yaw));
                        } else {
                            target.push(-Mth.sin(yaw) * strength, 0.1D, Mth.cos(yaw) * strength);
                        }

                        this.owner.setDeltaMovement(this.owner.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
                        this.owner.setSprinting(false);

                        if (target instanceof ServerPlayer && target.hurtMarked) {
                            ((ServerPlayer) target).connection.send(new ClientboundSetEntityMotionPacket(target));
                            target.hurtMarked = false;
                            target.setDeltaMovement(vec3d);
                        }

                        if (bl3) {
                            this.level.playSound(null, this.owner.getX(), this.owner.getY(), this.owner.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, this.owner.getSoundSource(), 1.0F, 1.0F);
                            this.owner.crit(target);
                        }

                        if (attackDamage > 0.0F) {
                            this.owner.magicCrit(target);
                        }

                        this.owner.setLastHurtMob(target);

                        if (target instanceof LivingEntity) {
                            float n = targetHealth - ((LivingEntity) target).getHealth();
                            this.owner.awardStat(Stats.DAMAGE_DEALT, Math.round(n * 10.0F));
                            if (fireAspectLevel > 0) {
                                target.setSecondsOnFire(fireAspectLevel * 4);
                            }

                            if (n > 2.0F) {
                                int o = (int) ((double) n * 0.5D);
                                this.castToServer().sendParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getY(0.5D), target.getZ(), o, 0.1D, 0.0D, 0.1D, 0.2D);
                            }
                        }
                    } else {
                        this.level.playSound(null, this.owner.getX(), this.owner.getY(), this.owner.getZ(), SoundEvents.PLAYER_ATTACK_NODAMAGE, this.owner.getSoundSource(), 1.0F, 1.0F);
                        if (fireAspectEnchanted) {
                            target.clearFire();
                        }
                    }
                }
            }
        }
    }

    public List<LivingEntity> getEntitiesStruckByShockWave() {
        return this.level.getEntitiesOfClass(LivingEntity.class, this.box, livingEntity -> {
            double d = distanceTo2D(livingEntity.position(), this.pos);
            boolean flag = !this.exceptEntities.contains(livingEntity) && !livingEntity.isSpectator() && livingEntity != this.owner && d >= this.secondary.getRadius() && this.primary.getRadius() >= d;

            for (int i = 0; i < 2 && flag; i++) {
                Vec3 vec3d = new Vec3(livingEntity.getX(), livingEntity.getY(0.5D * (double) i), livingEntity.getZ());
                BlockHitResult hitResult = this.level.clip(new ClipContext(this.pos, vec3d, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.owner));
                if (hitResult.getType() == BlockHitResult.Type.MISS) {
                    return true;
                }
            }

            return false;
        });
    }

    public ServerLevel castToServer() {
        return (ServerLevel) this.level;
    }
}
