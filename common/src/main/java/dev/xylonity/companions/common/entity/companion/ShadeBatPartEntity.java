package dev.xylonity.companions.common.entity.companion;

import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtil;

import java.util.UUID;

public class ShadeBatPartEntity extends Mob implements GeoAnimatable {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<Integer> PARENT_ID = SynchedEntityData.defineId(ShadeBatPartEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> PART_INDEX = SynchedEntityData.defineId(ShadeBatPartEntity.class, EntityDataSerializers.INT);

    private final RawAnimation FLY = RawAnimation.begin().thenPlay("fly");
    private final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");

    private static final int ATTACK_INTERVAL = 12;
    private static final int ATTACK_DURATION = 12;
    private static final int ATTACK_HIT_TICK = 6;
    private static final int IDLE_PARTICLE_STAGGER_TICKS = 8;
    private static final int IDLE_PARTICLE_MIN_INTERVAL = 24;
    private static final int HURT_INVULNERABILITY_TICKS = 10;
    private static final double ORBIT_FOLLOW_LERP = 0.14D;
    private static final double TARGET_FOLLOW_LERP = 0.20D;
    private static final double ATTACK_DASH_LERP = 0.82D;

    private UUID parentUuid;

    public ShadeBatPartEntity(EntityType<? extends ShadeBatPartEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.noPhysics = true;
        this.noCulling = true;
        this.setNoGravity(true);
    }

    public static AttributeSupplier.Builder setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, CompanionsConfig.SHADOW_BAT_PART_MAX_LIFE)
                .add(Attributes.MOVEMENT_SPEED, 0.0D);
    }

    public void init(ShadeBatEntity parent, int index) {
        this.parentUuid = parent.getUUID();
        this.entityData.set(PARENT_ID, parent.getId());
        this.entityData.set(PART_INDEX, index);
        this.setNoGravity(true);
    }

    public int getPartIndex() {
        return this.entityData.get(PART_INDEX);
    }

    public void orbit(Vec3 target, Vec3 lookDirection, double lerpFactor) {
        final Vec3 nextPosition = this.position().lerp(target, lerpFactor);
        final Vec3 movement = lookDirection.lengthSqr() > 1.0E-6 ? lookDirection : nextPosition.subtract(this.position());
        float yaw = getYRot();
        float pitch = getXRot();

        if (movement.lengthSqr() > 1.0E-6) {
            final double horizontal = Math.sqrt(movement.x * movement.x + movement.z * movement.z);
            yaw = (float) (Mth.atan2(movement.z, movement.x) * Mth.RAD_TO_DEG) - 90.0F;
            pitch = (float) (-(Mth.atan2(movement.y, horizontal) * Mth.RAD_TO_DEG));
        }

        this.setPos(nextPosition.x, nextPosition.y, nextPosition.z);
        this.setYRot(yaw);
        this.setXRot(pitch);
        this.setDeltaMovement(Vec3.ZERO);
    }

    @Nullable
    public ShadeBatEntity getParent() {
        if (this.parentUuid != null && level() instanceof ServerLevel level) {
            final Entity entity = level.getEntity(this.parentUuid);
            return entity instanceof ShadeBatEntity shadeBat ? shadeBat : null;
        }

        final Entity entity = level().getEntity(this.entityData.get(PARENT_ID));
        return entity instanceof ShadeBatEntity shadeBat ? shadeBat : null;
    }

    @Override
    public void tick() {
        super.tick();

        oldStateForRender();

        this.noPhysics = true;
        this.setNoGravity(true);

        final ShadeBatEntity parent = getParent();
        if (parent == null || parent.isRemoved() || !parent.isAlive() || parent.isBlood()) {
            if (!level().isClientSide) {
                this.discard();
            }

            return;
        }

        final int index = this.entityData.get(PART_INDEX);
        Vec3 targetPosition = parent.getBatPartPosition(index);
        final LivingEntity target = parent.getShadeBatTarget();
        final int attackPhase = getAttackPhase(parent);
        if (target != null) {
            targetPosition = getAttackDashPosition(targetPosition, target, attackPhase);
        }

        final Vec3 desiredMovement = getDesiredMovement(parent, target, index, targetPosition);
        orbit(targetPosition, desiredMovement, target == null ? ORBIT_FOLLOW_LERP : getTargetFollowLerp(attackPhase));

        if (!level().isClientSide && target != null) {
            tickDesyncedAttack(parent, target);
        }

        if (!level().isClientSide) {
            idleParticles(parent);
        }

    }

    private void oldStateForRender() {
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
        this.xOld = this.getX();
        this.yOld = this.getY();
        this.zOld = this.getZ();
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    @Override
    public void lerpTo(double pX, double pY, double pZ, float pYRot, float pXRot, int pSteps) {
        final ShadeBatEntity parent = getParent();
        if (parent != null && !parent.isRemoved() && !parent.isBlood()) {
            return;
        }

        super.lerpTo(pX, pY, pZ, pYRot, pXRot, pSteps);
    }

    private Vec3 getDesiredMovement(ShadeBatEntity parent, @Nullable LivingEntity target, int index, Vec3 targetPosition) {
        if (target != null) {
            return target.getEyePosition(1.0F).subtract(this.position());
        }

        final Vec3 toTargetPosition = targetPosition.subtract(this.position());
        return toTargetPosition.lengthSqr() > 1.0E-4 ? toTargetPosition : parent.getBatPartMovementDirection(index, 0.0F);
    }

    private Vec3 getAttackDashPosition(Vec3 orbitPosition, LivingEntity target, int attackPhase) {
        final double dashProgress = getAttackDashProgress(attackPhase);
        if (dashProgress <= 0.0D) {
            return orbitPosition;
        }

        Vec3 targetPosition = target.getEyePosition(1.0F);
        final Vec3 awayFromTarget = orbitPosition.subtract(targetPosition);
        if (awayFromTarget.lengthSqr() > 1.0E-6) {
            final double stopDistance = Math.max(target.getBbWidth() * 0.5D, 0.35D);
            targetPosition = targetPosition.add(awayFromTarget.normalize().scale(stopDistance));
        }

        return orbitPosition.lerp(targetPosition, dashProgress);
    }

    private double getAttackDashProgress(int attackPhase) {
        if (attackPhase >= ATTACK_DURATION) {
            return 0.0D;
        }

        final double progress = attackPhase <= ATTACK_HIT_TICK
                ? attackPhase / (double) ATTACK_HIT_TICK
                : (ATTACK_DURATION - attackPhase) / (double) (ATTACK_DURATION - ATTACK_HIT_TICK);
        return Mth.clamp(progress, 0.0D, 1.0D);
    }

    private double getTargetFollowLerp(int attackPhase) {
        return attackPhase < ATTACK_DURATION ? ATTACK_DASH_LERP : TARGET_FOLLOW_LERP;
    }

    private void tickDesyncedAttack(ShadeBatEntity parent, LivingEntity target) {
        if (getAttackPhase(parent) != ATTACK_HIT_TICK) {
            return;
        }

        final float damage = (float) CompanionsConfig.SHADOW_BAT_PART_DAMAGE;
        target.hurt(damageSources().mobAttack(this), damage);
    }

    private int getAttackPhase(@Nullable ShadeBatEntity parent) {
        final int index = Math.max(0, this.entityData.get(PART_INDEX));
        final int baseTick = parent != null ? parent.tickCount : tickCount;
        return Math.floorMod(baseTick + index * ATTACK_INTERVAL, getAttackCycle(parent));
    }

    private int getAttackCycle(@Nullable ShadeBatEntity parent) {
        final int partsAmount = parent != null ? parent.getBatPartsAmount() : ShadeBatEntity.getConfiguredBatPartsAmount();
        return ATTACK_INTERVAL * Math.max(1, partsAmount);
    }

    private boolean isAttackAnimationActive() {
        final ShadeBatEntity parent = getParent();
        return parent != null && parent.getShadeBatTarget() != null && getAttackPhase(parent) < ATTACK_DURATION;
    }

    @Override
    public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
        if (level().isClientSide) {
            clearForcedMovement();
            return false;
        }

        if (this.invulnerableTime > 0) {
            return false;
        }

        final ShadeBatEntity parent = getParent();
        if (parent == null || parent.isRemoved() || !parent.isAlive()) {
            this.discard();
            return false;
        }

        final Entity attacker = pSource.getEntity();
        final Entity direct = pSource.getDirectEntity();
        if (isAlliedPart(parent, attacker) || isAlliedPart(parent, direct)) {
            return false;
        }
        if (attacker == parent || direct == parent) {
            return false;
        }

        final boolean hurt = parent.hurtBatPart(this.entityData.get(PART_INDEX), pSource, pAmount);
        if (hurt) {
            markPartHurt();
            clearForcedMovement();
        }

        return hurt;
    }

    @Override
    public void knockback(double pStrength, double pX, double pZ) {
        clearForcedMovement();
    }

    @Override
    public void push(double pX, double pY, double pZ) {
        clearForcedMovement();
    }

    private void markPartHurt() {
        this.invulnerableTime = HURT_INVULNERABILITY_TICKS;
        this.hurtDuration = HURT_INVULNERABILITY_TICKS;
        this.hurtTime = HURT_INVULNERABILITY_TICKS;
    }

    private void clearForcedMovement() {
        this.setDeltaMovement(Vec3.ZERO);
        this.hasImpulse = false;
    }

    public static boolean isAlliedPart(ShadeBatEntity parent, @Nullable Entity entity) {
        if (!(entity instanceof ShadeBatPartEntity part)) {
            return false;
        }

        final ShadeBatEntity otherParent = part.getParent();
        if (otherParent == parent) {
            return true;
        }

        final UUID ownerUUID = parent.getOwnerUUID();
        return ownerUUID != null && otherParent != null && ownerUUID.equals(otherParent.getOwnerUUID());
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean isAttackable() {
        return true;
    }

    @Override
    public boolean canBeHitByProjectile() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    public void checkDespawn() {
        ;;
    }

    public float getSpawnFade(float partialTick) {
        return Mth.clamp((tickCount + partialTick) / (float) ShadeBatEntity.SPAWN_FADE_TICKS, 0.0F, 1.0F);
    }

    private void idleParticles(ShadeBatEntity parent) {
        final int interval = Math.max(IDLE_PARTICLE_MIN_INTERVAL, parent.getBatPartsAmount() * IDLE_PARTICLE_STAGGER_TICKS);
        final int phase = Math.floorMod(parent.tickCount + getPartIndex() * IDLE_PARTICLE_STAGGER_TICKS, interval);
        if (phase != 0 || !(level() instanceof ServerLevel level)) {
            return;
        }

        final double dx = (this.random.nextDouble() - 0.5) * getBbWidth();
        final double dy = (this.random.nextDouble() - 0.5) * getBbWidth();
        final double dz = (this.random.nextDouble() - 0.5) * getBbWidth();
        level.sendParticles(CompanionsParticles.SHADE_TRAIL.get(), getX(), getY() + getBbHeight() * Math.random(), getZ(), 1, dx, dy, dz, 0.08);
    }

    public void spawnDeathParticles() {
        if (!(level() instanceof ServerLevel level)) {
            return;
        }

        for (int i = 0; i < 10; i++) {
            final double vx = (this.random.nextDouble() - 0.5) * getBbWidth();
            final double vy = (this.random.nextDouble() - 0.5) * getBbHeight();
            final double vz = (this.random.nextDouble() - 0.5) * getBbWidth();
            level.sendParticles(CompanionsParticles.SHADE_TRAIL.get(), getX(), getY(), getZ(), 1, vx, vy, vz, 0.12);
            if (i % 3 == 0) {
                level.sendParticles(CompanionsParticles.SHADE_SUMMON.get(), getX(), getY(), getZ(), 1, vx, vy, vz, 0.25);
            }
        }

    }

    @Override
    public boolean save(CompoundTag pCompound) {
        return false;
    }

    @Override
    public boolean saveAsPassenger(CompoundTag pCompound) {
        return false;
    }

    @Override
    protected void defineSynchedData(@NotNull SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(PARENT_ID, -1);
        pBuilder.define(PART_INDEX, -1);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        event.setAnimation(isAttackAnimationActive() ? ATTACK : FLY);
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public double getTick(Object o) {
        return RenderUtil.getCurrentTick();
    }

}