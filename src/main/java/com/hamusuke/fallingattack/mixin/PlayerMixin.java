package com.hamusuke.fallingattack.mixin;

import com.hamusuke.fallingattack.FallingAttack;
import com.hamusuke.fallingattack.config.Config;
import com.hamusuke.fallingattack.invoker.PlayerInvoker;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements PlayerInvoker {
    @Shadow
    public abstract void stopFallFlying();

    @Shadow
    @Final
    private Abilities abilities;

    @Shadow
    public abstract void causeFoodExhaustion(float p_36400_);

    @Shadow
    public abstract void awardStat(ResourceLocation p_36223_, int p_36224_);

    @Shadow
    public abstract void resetAttackStrengthTicker();

    @Shadow
    public abstract void crit(Entity p_36156_);

    @Shadow
    public abstract void magicCrit(Entity p_36253_);

    @Shadow
    public abstract SoundSource getSoundSource();

    protected boolean fallingAttack;
    protected float yPosWhenStartFallingAttack;
    protected int fallingAttackProgress;
    protected int fallingAttackCooldown;
    protected float storeYaw = Float.NaN;

    protected PlayerMixin(EntityType<? extends LivingEntity> p_i48577_1_, Level p_i48577_2_) {
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
            if (!this.level.isClientSide() && !Config.Common.USABLE_ITEMS.isUsable(this.getMainHandItem().getItem())) {
                this.stopFallingAttack();
                this.sendFallingAttackPacket(false);
            }

            if (this.fallingAttackProgress < FIRST_FALLING_ATTACK_PROGRESS_TICKS) {
                if (this.fallingAttackProgress == 0) {
                    this.setDeltaMovement(0.0D, 0.5D, 0.0D);
                } else if (this.fallingAttackProgress > FIRST_FALLING_ATTACK_PROGRESS_TICKS / 2) {
                    this.setDeltaMovement(Vec3.ZERO);
                }

                if (this.fallingAttackProgress == FIRST_FALLING_ATTACK_PROGRESS_TICKS - 1) {
                    this.yPosWhenStartFallingAttack = (float) this.getY();
                }

                this.fallingAttackProgress++;
            } else if (this.fallingAttackProgress == FIRST_FALLING_ATTACK_PROGRESS_TICKS) {
                boolean b = this.level.dimensionType().minY() > this.blockPosition().getY();
                if (this.isInWater() || this.isInLava() || b) {
                    this.stopFallingAttack();
                    if (b) {
                        this.setDeltaMovement(Vec3.ZERO);
                    }
                } else if (this.onGround) {
                    this.fallingAttackProgress++;

                    if (!this.level.isClientSide()) {
                        ServerPlayer serverPlayer = (ServerPlayer) (Object) this;
                        float d = Mth.clamp(this.computeFallingAttackDistance(), 0.0F, 16.0F);
                        AABB axisAlignedBB = this.getBoundingBox().inflate(d, 0.0D, d);
                        Vec3 vector3d = this.position();

                        for (int i = 0; i < 90; i++) {
                            if (i % 6 == 0) {
                                float rad = i * Mth.DEG_TO_RAD;
                                float x = Mth.cos(rad) * d;
                                float y = Mth.sin(rad) * d;
                                ServerLevel level = (ServerLevel) this.level;
                                level.sendParticles(ParticleTypes.EXPLOSION, this.getX() + x, this.getY(), this.getZ() + y, 6, 1.0D, 0.0D, 1.0D, 1.0D);
                                level.sendParticles(ParticleTypes.EXPLOSION, this.getX() - x, this.getY(), this.getZ() + y, 6, 1.0D, 0.0D, 1.0D, 1.0D);
                                level.sendParticles(ParticleTypes.EXPLOSION, this.getX() + x, this.getY(), this.getZ() - y, 6, 1.0D, 0.0D, 1.0D, 1.0D);
                                level.sendParticles(ParticleTypes.EXPLOSION, this.getX() - x, this.getY(), this.getZ() - y, 6, 1.0D, 0.0D, 1.0D, 1.0D);
                            }
                        }

                        this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, this.getSoundSource(), 1.0F, 1.0F);
                        this.level.getEntitiesOfClass(LivingEntity.class, new AABB(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, axisAlignedBB.maxX, axisAlignedBB.minY + 1.0D, axisAlignedBB.maxZ), livingEntity -> {
                            boolean flag = !livingEntity.isSpectator() && livingEntity != this && Config.Common.ATTACKABLE_ENTITIES.isAttackable(livingEntity);

                            for (int i = 0; i < 2 && flag; i++) {
                                Vec3 vector3d1 = new Vec3(livingEntity.getX(), livingEntity.getY(0.5D * (double) i), livingEntity.getZ());
                                BlockHitResult blockHitResult = this.level.clip(new ClipContext(vector3d, vector3d1, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
                                if (blockHitResult.getType() == BlockHitResult.Type.MISS) {
                                    return true;
                                }
                            }

                            return false;
                        }).forEach(this::fallingAttack);

                        ItemStack itemStack2 = this.getMainHandItem();
                        if (!itemStack2.isEmpty()) {
                            ItemStack copy = itemStack2.copy();
                            itemStack2.hurtAndBreak(1, (Player) (Object) this, player -> player.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                            if (itemStack2.isEmpty()) {
                                ForgeEventFactory.onPlayerDestroyItem((Player) (Object) this, copy, InteractionHand.MAIN_HAND);
                                this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                            }
                        }
                        this.causeFoodExhaustion(0.1F);
                    }
                } else {
                    this.setDeltaMovement(0.0D, -3.0D, 0.0D);
                    if (!this.level.isClientSide() && this.getServer() != null && this.getServer().getTickCount() % 2 == 0) {
                        ServerLevel level = (ServerLevel) this.level;
                        ServerPlayer player = (ServerPlayer) (Object) this;
                        AABB aabb = player.getBoundingBox();
                        level.sendParticles(ParticleTypes.POOF, aabb.minX - 0.125D, aabb.minY - 1.0D, player.getZ(), 5, 0.5D, 1.0D, 0.0D, 1.0D);
                        level.sendParticles(ParticleTypes.POOF, aabb.maxX + 0.125D, aabb.minY - 1.0D, player.getZ(), 5, 0.5D, 1.0D, 0.0D, 1.0D);
                        level.sendParticles(ParticleTypes.POOF, player.getX(), aabb.minY - 1.0D, aabb.minZ - 0.125D, 5, 0.0D, 1.0D, 0.5D, 1.0D);
                        level.sendParticles(ParticleTypes.POOF, player.getX(), aabb.minY - 1.0D, aabb.maxZ + 0.125D, 5, 0.0D, 1.0D, 0.5D, 1.0D);
                    }
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
        return Mth.clamp(this.yPosWhenStartFallingAttack - (float) this.getY(), 0.0F, Float.MAX_VALUE);
    }

    protected float computeFallingAttackDamage(float distanceToTarget, int fallingAttackEnchantmentLevel) {
        float damage = (this.computeFallingAttackDistance() - distanceToTarget) * 0.1F * fallingAttackEnchantmentLevel;
        return Mth.clamp(damage, 0.0F, Float.MAX_VALUE);
    }

    protected float computeKnockbackStrength(float distanceToTarget, int fallingAttackEnchantmentLevel) {
        return Mth.clamp((this.computeFallingAttackDistance() - distanceToTarget) * 0.025F * fallingAttackEnchantmentLevel, 0.0F, Float.MAX_VALUE);
    }

    public void fallingAttack(Entity target) {
        if (!ForgeHooks.onPlayerAttackTarget((Player) (Object) this, target)) {
            return;
        }

        if (target.isAttackable()) {
            if (!target.skipAttackInteraction(this)) {
                float damageAmount = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
                float attackDamage;
                if (target instanceof LivingEntity livingEntity) {
                    attackDamage = EnchantmentHelper.getDamageBonus(this.getMainHandItem(), livingEntity.getMobType());
                } else {
                    attackDamage = EnchantmentHelper.getDamageBonus(this.getMainHandItem(), MobType.UNDEFINED);
                }

                this.resetAttackStrengthTicker();
                if (damageAmount > 0.0F || attackDamage > 0.0F) {
                    float distanceToTarget = this.distanceTo(target);
                    int i = EnchantmentHelper.getItemEnchantmentLevel(FallingAttack.SHARPNESS_OF_FALLING_ATTACK, this.getMainHandItem());
                    int fallingAttackLevel = Mth.clamp(i + 1, 1, FallingAttack.SHARPNESS_OF_FALLING_ATTACK.getMaxLevel() + 1);
                    attackDamage += this.computeFallingAttackDamage(distanceToTarget, fallingAttackLevel);
                    this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, this.getSoundSource(), 1.0F, 1.0F);

                    boolean bl3 = !this.onClimbable() && !this.isInWater() && !this.hasEffect(MobEffects.BLINDNESS) && !this.isPassenger() && target instanceof LivingEntity;
                    CriticalHitEvent hitResult = ForgeHooks.getCriticalHit((Player) (Object) this, target, bl3, bl3 ? 1.5F : 1.0F);
                    bl3 = hitResult != null;
                    if (bl3) {
                        damageAmount *= hitResult.getDamageModifier();
                    }

                    damageAmount += attackDamage;
                    float targetHealth = 0.0F;
                    boolean fireAspectEnchanted = false;
                    int fireAspectLevel = EnchantmentHelper.getFireAspect(this);
                    if (target instanceof LivingEntity livingEntity) {
                        targetHealth = livingEntity.getHealth();
                        if (fireAspectLevel > 0 && !target.isOnFire()) {
                            fireAspectEnchanted = true;
                            target.setSecondsOnFire(1);
                        }
                    }

                    Vec3 vec3d = target.getDeltaMovement();
                    boolean tookDamage = target.hurt(DamageSource.playerAttack((Player) (Object) this), damageAmount * (Config.Common.DAMAGE_AMOUNT.get() / 100.0F));
                    if (tookDamage) {
                        float yaw = (float) Mth.atan2(target.getX() - this.getX(), target.getZ() - this.getZ()) * 57.2957795F;
                        float strength = this.computeKnockbackStrength(distanceToTarget, fallingAttackLevel);
                        strength *= Config.Common.KNOCKBACK_AMOUNT.get() / 100.0F;
                        if (target instanceof LivingEntity livingEntity) {
                            livingEntity.knockback(strength, -Mth.sin(yaw * 0.017453292F), -Mth.cos(yaw * 0.017453292F));
                        } else {
                            target.push(-Mth.sin(yaw * 0.017453292F) * strength, 0.1D, Mth.cos(yaw * 0.017453292F) * strength);
                        }

                        this.setDeltaMovement(this.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
                        this.setSprinting(false);

                        if (target instanceof ServerPlayer serverPlayer && target.hurtMarked) {
                            serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(target));
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
                        if (target instanceof LivingEntity livingEntity) {
                            EnchantmentHelper.doPostHurtEffects(livingEntity, this);
                        }

                        EnchantmentHelper.doPostDamageEffects(this, target);
                        if (target instanceof LivingEntity livingEntity) {
                            float n = targetHealth - livingEntity.getHealth();
                            this.awardStat(Stats.DAMAGE_DEALT, Math.round(n * 10.0F));
                            if (fireAspectLevel > 0) {
                                target.setSecondsOnFire(fireAspectLevel * 4);
                            }

                            if (this.level instanceof ServerLevel serverLevel && n > 2.0F) {
                                int o = (int) ((double) n * 0.5D);
                                serverLevel.sendParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getY(0.5D), target.getZ(), o, 0.1D, 0.0D, 0.1D, 0.2D);
                            }
                        }
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
        AABB axisAlignedBB = this.getBoundingBox();
        return this.fallingAttackCooldown == 0 && this.level.noCollision(this, new AABB(axisAlignedBB.minX, axisAlignedBB.minY - 2.0D, axisAlignedBB.minZ, axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ)) && !this.onClimbable() && !this.isPassenger() && !this.abilities.flying && !this.isNoGravity() && !this.onGround && !this.isUsingFallingAttack() && !this.isInLava() && !this.isInWater() && !this.hasEffect(MobEffects.LEVITATION) && Config.Common.USABLE_ITEMS.isUsable(this.getMainHandItem().getItem());
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
        this.fallingAttackCooldown = 10;
        this.yPosWhenStartFallingAttack = 0.0F;
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
