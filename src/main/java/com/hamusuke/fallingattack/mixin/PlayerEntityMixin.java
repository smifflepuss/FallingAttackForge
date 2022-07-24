package com.hamusuke.fallingattack.mixin;

import com.hamusuke.fallingattack.FallingAttack;
import com.hamusuke.fallingattack.config.Config;
import com.hamusuke.fallingattack.invoker.IPlayerEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements IPlayerEntity {
    @Shadow
    public abstract void resetAttackStrengthTicker();

    @Shadow
    public abstract SoundCategory getSoundSource();

    @Shadow
    public abstract void crit(Entity p_71009_1_);

    @Shadow
    public abstract void magicCrit(Entity p_71047_1_);

    @Shadow
    public abstract void awardStat(ResourceLocation p_195067_1_, int p_195067_2_);

    @Shadow
    public abstract void causeFoodExhaustion(float p_71020_1_);

    @Shadow
    @Final
    public PlayerAbilities abilities;

    @Shadow
    public abstract void stopFallFlying();

    protected boolean fallingAttack;
    protected float yPosWhenStartFallingAttack;
    protected int fallingAttackProgress;
    protected int fallingAttackCooldown;
    protected float storeYaw = Float.NaN;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> p_i48577_1_, World p_i48577_2_) {
        super(p_i48577_1_, p_i48577_2_);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    void tickV(CallbackInfo ci) {
        if (!this.isUsingFallingAttack() && this.fallingAttackCooldown > 0) {
            this.fallingAttackCooldown--;
        }
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    void aiStepV(CallbackInfo ci) {
        if (this.isUsingFallingAttack()) {
            if (!this.level.isClientSide() && !(this.getMainHandItem().getItem() instanceof SwordItem)) {
                this.stopFallingAttack();
                this.sendFallingAttackPacket(false);
            }

            if (this.fallingAttackProgress < FIRST_FALLING_ATTACK_PROGRESS_TICKS) {
                if (this.fallingAttackProgress == 0) {
                    this.setDeltaMovement(0.0D, 0.5D, 0.0D);
                } else if (this.fallingAttackProgress > FIRST_FALLING_ATTACK_PROGRESS_TICKS / 2) {
                    this.setDeltaMovement(Vector3d.ZERO);
                }

                if (this.fallingAttackProgress == FIRST_FALLING_ATTACK_PROGRESS_TICKS - 1) {
                    this.yPosWhenStartFallingAttack = (float) this.getY();
                }

                this.fallingAttackProgress++;
            } else if (this.fallingAttackProgress == FIRST_FALLING_ATTACK_PROGRESS_TICKS) {
                if (this.isInWater() || this.isInLava() || 0 > this.blockPosition().getY()) {
                    this.stopFallingAttack();
                    this.setDeltaMovement(Vector3d.ZERO);
                } else if (this.onGround) {
                    this.fallingAttackProgress++;
                    if (!this.level.isClientSide()) {
                        AxisAlignedBB axisAlignedBB = this.getBoundingBox().inflate(3.0D, 0.0D, 3.0D);
                        Vector3d vector3d = this.position();

                        this.level.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, axisAlignedBB.maxX, axisAlignedBB.maxY - 1.0D, axisAlignedBB.maxZ), livingEntity -> {
                            boolean flag = !livingEntity.isSpectator() && livingEntity != this;

                            for (int i = 0; i < 2 && flag; i++) {
                                Vector3d vector3d1 = new Vector3d(livingEntity.getX(), livingEntity.getY(0.5D * (double) i), livingEntity.getZ());
                                RayTraceResult raytraceresult = this.level.clip(new RayTraceContext(vector3d, vector3d1, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this));
                                if (raytraceresult.getType() == RayTraceResult.Type.MISS) {
                                    return true;
                                }
                            }

                            return false;
                        }).forEach(this::fallingAttack);
                    }
                } else {
                    this.setDeltaMovement(0.0D, -3.0D, 0.0D);
                }
            } else if (this.fallingAttackProgress < FALLING_ATTACK_END_TICKS) {
                this.fallingAttackProgress++;
            } else if (this.isUsingFallingAttack()) {
                this.stopFallingAttack();
            }
        }
    }

    protected int calculateFallDamage(float p_225508_1_, float p_225508_2_) {
        int damage = super.calculateFallDamage(p_225508_1_, p_225508_2_);
        return this.isUsingFallingAttack() ? (int) (damage * 0.25F) : damage;
    }

    protected float computeFallingAttackDistance() {
        return MathHelper.clamp(this.yPosWhenStartFallingAttack - (float) this.getY(), 0.0F, Float.MAX_VALUE);
    }

    protected float computeFallingAttackDamage(float distanceToTarget, int fallingAttackEnchantmentLevel) {
        float damage = (this.computeFallingAttackDistance() - distanceToTarget) * 0.1F * fallingAttackEnchantmentLevel;
        return MathHelper.clamp(damage, 0.0F, Float.MAX_VALUE);
    }

    protected float computeKnockbackStrength(float distanceToTarget, int fallingAttackEnchantmentLevel) {
        return MathHelper.clamp((this.computeFallingAttackDistance() - distanceToTarget) * 0.025F * fallingAttackEnchantmentLevel, 0.0F, Float.MAX_VALUE);
    }

    public void fallingAttack(Entity target) {
        if (!ForgeHooks.onPlayerAttackTarget((PlayerEntity) (Object) this, target)) {
            return;
        }

        if (target.isAttackable()) {
            if (!target.skipAttackInteraction(this)) {
                float damageAmount = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
                float attackDamage;
                if (target instanceof LivingEntity) {
                    attackDamage = EnchantmentHelper.getDamageBonus(this.getMainHandItem(), ((LivingEntity) target).getMobType());
                } else {
                    attackDamage = EnchantmentHelper.getDamageBonus(this.getMainHandItem(), CreatureAttribute.UNDEFINED);
                }

                this.resetAttackStrengthTicker();
                if (damageAmount > 0.0F || attackDamage > 0.0F) {
                    float distanceToTarget = this.distanceTo(target);
                    int i = EnchantmentHelper.getItemEnchantmentLevel(FallingAttack.SHARPNESS_OF_FALLING_ATTACK, this.getMainHandItem());
                    int fallingAttackLevel = MathHelper.clamp(i + 1, 1, FallingAttack.SHARPNESS_OF_FALLING_ATTACK.getMaxLevel() + 1);
                    attackDamage += this.computeFallingAttackDamage(distanceToTarget, fallingAttackLevel);
                    this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, this.getSoundSource(), 1.0F, 1.0F);

                    boolean bl3 = !this.onClimbable() && !this.isInWater() && !this.hasEffect(Effects.BLINDNESS) && !this.isPassenger() && target instanceof LivingEntity;
                    CriticalHitEvent hitResult = ForgeHooks.getCriticalHit((PlayerEntity) (Object)this, target, bl3, bl3 ? 1.5F : 1.0F);
                    bl3 = hitResult != null;
                    if (bl3) {
                        damageAmount *= hitResult.getDamageModifier();
                    }

                    damageAmount += attackDamage;
                    float targetHealth = 0.0F;
                    boolean fireAspectEnchanted = false;
                    int fireAspectLevel = EnchantmentHelper.getFireAspect(this);
                    if (target instanceof LivingEntity) {
                        targetHealth = ((LivingEntity) target).getHealth();
                        if (fireAspectLevel > 0 && !target.isOnFire()) {
                            fireAspectEnchanted = true;
                            target.setSecondsOnFire(1);
                        }
                    }

                    Vector3d vec3d = target.getDeltaMovement();
                    boolean tookDamage = target.hurt(DamageSource.playerAttack((PlayerEntity) (Object) this), damageAmount * (Config.Common.DAMAGE_AMOUNT.get() / 100.0F));
                    if (tookDamage) {
                        float yaw = (float) MathHelper.atan2(target.getX() - this.getX(), target.getZ() - this.getZ()) * 57.2957795F;
                        float strength = this.computeKnockbackStrength(distanceToTarget, fallingAttackLevel);
                        strength *= Config.Common.KNOCKBACK_AMOUNT.get() / 100.0F;
                        if (target instanceof LivingEntity) {
                            ((LivingEntity) target).knockback(strength, -MathHelper.sin(yaw * 0.017453292F), -MathHelper.cos(yaw * 0.017453292F));
                        } else {
                            target.push(-MathHelper.sin(yaw * 0.017453292F) * strength, 0.1D, MathHelper.cos(yaw * 0.017453292F) * strength);
                        }

                        this.setDeltaMovement(this.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
                        this.setSprinting(false);

                        if (target instanceof ServerPlayerEntity && target.hurtMarked) {
                            ((ServerPlayerEntity) target).connection.send(new SEntityVelocityPacket(target));
                            target.hurtMarked = false;
                            target.setDeltaMovement(vec3d);
                        }

                        if (bl3) {
                            this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, this.getSoundSource(), 1.0F, 1.0F);
                            this.crit(target);
                        }

                        if (attackDamage > 0.0F) {
                            this.magicCrit(target);
                        }

                        this.setLastHurtMob(target);
                        if (target instanceof LivingEntity) {
                            EnchantmentHelper.doPostHurtEffects((LivingEntity) target, this);
                        }

                        EnchantmentHelper.doPostDamageEffects(this, target);
                        ItemStack itemStack2 = this.getMainHandItem();
                        Entity entity = target;
                        if (target instanceof PartEntity) {
                            entity = ((PartEntity<?>) target).getParent();
                        }

                        if (!this.level.isClientSide && !itemStack2.isEmpty() && entity instanceof LivingEntity) {
                            ItemStack copy = itemStack2.copy();
                            itemStack2.hurtEnemy((LivingEntity) entity, (PlayerEntity) (Object) this);
                            if (itemStack2.isEmpty()) {
                                ForgeEventFactory.onPlayerDestroyItem((PlayerEntity) (Object) this, copy, Hand.MAIN_HAND);
                                this.setItemInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
                            }
                        }

                        if (target instanceof LivingEntity) {
                            float n = targetHealth - ((LivingEntity) target).getHealth();
                            this.awardStat(Stats.DAMAGE_DEALT, Math.round(n * 10.0F));
                            if (fireAspectLevel > 0) {
                                target.setSecondsOnFire(fireAspectLevel * 4);
                            }

                            if (this.level instanceof ServerWorld && n > 2.0F) {
                                int o = (int) ((double) n * 0.5D);
                                ((ServerWorld) this.level).sendParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getY(0.5D), target.getZ(), o, 0.1D, 0.0D, 0.1D, 0.2D);
                            }
                        }

                        this.causeFoodExhaustion(0.1F);
                    } else {
                        this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_NODAMAGE, this.getSoundSource(), 1.0F, 1.0F);
                        if (fireAspectEnchanted) {
                            target.clearFire();
                        }
                    }
                }
            }
        }
    }

    public boolean checkFallingAttack() {
        AxisAlignedBB axisAlignedBB = this.getBoundingBox();
        return this.fallingAttackCooldown == 0 && this.level.noCollision(this, new AxisAlignedBB(axisAlignedBB.minX, axisAlignedBB.minY - 2.0D, axisAlignedBB.minZ, axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ)) && !this.onClimbable() && !this.isPassenger() && !this.abilities.flying && !this.isNoGravity() && !this.onGround && !this.isUsingFallingAttack() && !this.isInLava() && !this.isInWater() && !this.hasEffect(Effects.LEVITATION) && this.getMainHandItem().getItem() instanceof SwordItem;
    }

    public void startFallingAttack() {
        this.fallingAttack = true;

        if (this.isFallFlying()) {
            this.stopFallFlying();
        }
    }

    @Inject(method = "startFallFlying", at = @At("HEAD"), cancellable = true)
    private void startFallFlying(CallbackInfo ci) {
        if (this.fallingAttack) {
            ci.cancel();
        }
    }

    public void stopFallingAttack() {
        this.fallingAttack = false;
        this.fallingAttackProgress = 0;
        this.fallingAttackCooldown = 30;
        this.yPosWhenStartFallingAttack = 0.0F;
        this.setDeltaMovement(0.0D, 0.0D, 0.0D);
    }

    public int getFallingAttackProgress() {
        return this.fallingAttackProgress;
    }

    public void setFallingAttackProgress(int fallingAttackProgress) {
        this.fallingAttackProgress = fallingAttackProgress;
    }

    public float getFallingAttackYPos() {
        return this.yPosWhenStartFallingAttack;
    }

    public void setFallingAttackYPos(float yPos) {
        this.yPosWhenStartFallingAttack = yPos;
    }

    public boolean isUsingFallingAttack() {
        return this.fallingAttack;
    }

    public float getYawF() {
        return this.storeYaw;
    }

    public void setYawF(float yaw) {
        this.storeYaw = yaw;
    }
}
