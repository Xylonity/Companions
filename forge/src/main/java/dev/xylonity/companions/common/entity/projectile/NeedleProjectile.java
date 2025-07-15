package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.config.CompanionsConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

public class NeedleProjectile extends ThrownTrident implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");

    private final Quaternionf prevRot = new Quaternionf();
    private final Quaternionf currentRot = new Quaternionf();

    private static final EntityDataAccessor<Integer> LIFETIME = SynchedEntityData.defineId(NeedleProjectile.class, EntityDataSerializers.INT);

    @Nullable private LivingEntity targetEntity;
    private boolean launched;
    private boolean dealtDamage;
    private float lockedYaw;
    private float lockedPitch;

    @Override
    protected boolean canHitEntity(@NotNull Entity entity) {
        if (Util.areEntitiesLinked(this, entity)) return false;

        return super.canHitEntity(entity);
    }

    public NeedleProjectile(EntityType<? extends ThrownTrident> type, Level level) {
        super(type, level);
        this.pickup = Pickup.DISALLOWED;
    }

    public void setTargetEntity(@Nullable LivingEntity target) {
        this.targetEntity = target;
    }

    @Override
    public void tick() {

        if (tickCount < 2) setInvisible(true);
        else if (tickCount < 5) setInvisible(false);

        if (!launched && targetEntity != null && targetEntity.isAlive()) {

            if (tickCount < 20) {
                Vec3 dir = targetEntity.getEyePosition().subtract(position()).normalize();
                float yaw = (float) Math.atan2(dir.x, dir.z) * Mth.RAD_TO_DEG;
                float pitch = (float) Math.atan2(dir.y, Math.hypot(dir.x, dir.z)) * Mth.RAD_TO_DEG;
                setYRot(yaw);
                setXRot(pitch);
                yRotO = yaw;
                xRotO = pitch;
                orientToward(dir);
                setDeltaMovement(Vec3.ZERO);
            }

            if (tickCount == 20) {
                Vec3 dir = targetEntity.getEyePosition().subtract(position()).normalize();
                orientToward(dir);

                lockedYaw = getYRot();
                lockedPitch = getXRot();

                setNoGravity(true);
                setDeltaMovement(dir.scale(0.7));
                launched = true;
            }
        }

        if (launched) {
            setYRot(lockedYaw);
            setXRot(lockedPitch);
            yRotO = lockedYaw;
            xRotO = lockedPitch;
        }

        super.tick();

        if (level().isClientSide) {
            updateQuatFromRot();
        }

        if (getLifetime() == 0) this.discard();

        this.setLifetime(this.getLifetime() - 1);
    }

    @Override
    public void remove(@NotNull RemovalReason pReason) {
        super.remove(pReason);
        for (int i = 0; i < 8; i++) {
            double vx = (level().random.nextDouble() - 0.5) * this.getBbWidth();
            double vy = (level().random.nextDouble() - 0.5) * this.getBbHeight();
            double vz = (level().random.nextDouble() - 0.5) * this.getBbWidth();
            if (level() instanceof ServerLevel level) {
                level.sendParticles(ParticleTypes.POOF, getX(), getY(), getZ(), 1, vx, vy, vz, 0.15);
            }
        }

    }

    public int getLifetime() {
        return this.entityData.get(LIFETIME);
    }

    public void setLifetime(int tick) {
        this.entityData.set(LIFETIME, tick);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Lifetime", this.getLifetime());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("Lifetime")) {
            setLifetime(pCompound.getInt("Lifetime"));
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LIFETIME, 80);
    }

    @Nullable
    @Override
    protected EntityHitResult findHitEntity(@NotNull Vec3 pStartVec, @NotNull Vec3 pEndVec) {
        return this.dealtDamage ? null : super.findHitEntity(pStartVec, pEndVec);
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        Entity entity = pResult.getEntity();
        Entity owner = this.getOwner();
        DamageSource damageSource = this.damageSources().trident(this, (owner == null ? this : owner));
        this.dealtDamage = true;
        entity.hurt(damageSource, (float) CompanionsConfig.NEEDLE_PROJECTILE_DAMAGE);
        this.playSound(SoundEvents.TRIDENT_HIT, 5.0f, 1.0F);
    }

    private void orientToward(Vec3 dir) {
        float yawRad = (float) Math.atan2(dir.x, dir.z);
        float pitchRad = (float) Math.atan2(dir.y, Math.hypot(dir.x, dir.z));

        float yawDeg = yawRad * Mth.RAD_TO_DEG;
        float pitchDeg = pitchRad * Mth.RAD_TO_DEG;

        setYRot(yawDeg);
        setXRot(pitchDeg);
        yRotO = yawDeg;
        xRotO = pitchDeg;
        prevRot.set(currentRot);
        currentRot.set(new Quaternionf().rotateY(yawRad + (float) Math.PI).rotateX(pitchRad));
    }

    private void updateQuatFromRot() {
        float yawRad = (float) Math.toRadians(getYRot());
        float pitchRad = (float) Math.toRadians(getXRot());
        prevRot.set(currentRot);
        currentRot.set(new Quaternionf().rotateY(yawRad + (float) Math.PI).rotateX(pitchRad));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar reg) {
        reg.add(new AnimationController<>(this, "controller", 2, this::predicate));
    }

    private <T extends GeoEntity> PlayState predicate(AnimationState<T> state) {
        state.setAnimation(IDLE);
        return PlayState.CONTINUE;
    }

    @Override
    public @NotNull AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling().inflate(10);
    }

    public Quaternionf getPrevRotation() {
        return prevRot;
    }

    public Quaternionf getCurrentRotation() {
        return currentRot;
    }

}
