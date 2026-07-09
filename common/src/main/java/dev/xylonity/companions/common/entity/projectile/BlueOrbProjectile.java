package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.common.entity.BaseProjectile;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.mixin.ProjectileAccessor;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;

public class BlueOrbProjectile extends BaseProjectile {

    private static final EntityDataAccessor<Boolean> FIRED = SynchedEntityData.defineId(BlueOrbProjectile.class, EntityDataSerializers.BOOLEAN);

    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");

    public static float SPEED = 0.65f;

    private LivingEntity target;
    private int fireDelay = 20;
    private float arcAngle;

    public BlueOrbProjectile(EntityType<? extends BaseProjectile> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.noCulling = true;
    }

    public void setUp(LivingEntity target, int fireDelay, float arcAngle) {
        this.target = target;
        this.fireDelay = fireDelay;
        this.arcAngle = arcAngle;
    }

    public boolean isFired() {
        return this.entityData.get(FIRED);
    }

    public void setFired(boolean fired) {
        this.entityData.set(FIRED, fired);
    }

    @Nullable
    protected EntityHitResult findHitEntity(Vec3 pStartVec, Vec3 pEndVec) {
        return ProjectileUtil.getEntityHitResult(this.level(), this, pStartVec, pEndVec, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1), this::canHitEntity);
    }

    @Override
    protected boolean canHitEntity(@NotNull Entity pTarget) {
        if (Util.areEntitiesLinked(this, pTarget)) {
            return false;
        }

        if (!pTarget.canBeHitByProjectile()) {
            return false;
        }
        else {
            final Entity entity = this.getOwner();
            return entity == null || ((ProjectileAccessor) this).companions$getLeftOwner() || !entity.isPassengerOfSameVehicle(pTarget);
        }

    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            if (isFired() && tickCount % 2 == 0) {
                level().addParticle(ParticleTypes.END_ROD, getX(), getY(), getZ(), 0, 0, 0);
            }

            return;
        }

        if (!isFired()) {
            if (getOwner() instanceof LivingEntity anchor && anchor.isAlive()) {
                final float yaw = anchor.yBodyRot * Mth.DEG_TO_RAD;
                final Vec3 right = new Vec3(Math.cos(yaw), 0, Math.sin(yaw));
                final Vec3 wanted = anchor.position()
                        .add(right.scale(Math.cos(arcAngle) * 1.7))
                        .add(0, 0.9 + Math.sin(arcAngle) * 1.7 + Math.sin((tickCount + arcAngle * 12) * 0.25) * 0.09, 0);
                setPos(position().lerp(wanted, 0.3));
            }

            if (tickCount >= fireDelay) {
                if (target == null || !target.isAlive()) {
                    discard();
                    return;
                }

                setDeltaMovement(target.getEyePosition().subtract(position()).normalize().scale(SPEED));
                setFired(true);
                playSound(SoundEvents.SHULKER_SHOOT, 0.5f, 1.6f);
            }

            return;
        }

        if (target != null && target.isAlive()) {
            final Vec3 speed = target.getEyePosition().subtract(position()).normalize().scale(SPEED);
            setDeltaMovement(getDeltaMovement().lerp(speed, 0.11).normalize().scale(SPEED));
            hasImpulse = true;
        }

        this.move(MoverType.SELF, getDeltaMovement());

        final Vec3 pos = this.position();
        Vec3 vec33 = pos.add(this.getDeltaMovement());
        HitResult hitresult = this.level().clip(new ClipContext(pos, vec33, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));

        if (hitresult.getType() != HitResult.Type.MISS) {
            vec33 = hitresult.getLocation();
        }

        while (!this.isRemoved()) {
            EntityHitResult entityhitresult = this.findHitEntity(pos, vec33);
            if (entityhitresult != null) {
                hitresult = entityhitresult;
            }

            if (hitresult != null && hitresult.getType() == HitResult.Type.ENTITY) {
                final Entity entity = ((EntityHitResult) hitresult).getEntity();
                final Entity entity1 = this.getOwner();
                if (entity instanceof Player && entity1 instanceof Player && !((Player)entity1).canHarmPlayer((Player)entity)) {
                    hitresult = null;
                    entityhitresult = null;
                }

            }

            if (hitresult != null && hitresult.getType() != HitResult.Type.MISS) {
                this.onHit(hitresult);
                this.hasImpulse = true;
            }

            if (entityhitresult == null) {
                break;
            }

            hitresult = null;
        }

    }

    @Override
    public void remove(@NotNull RemovalReason pReason) {
        if (level() instanceof ServerLevel level) {
            for (int i = 0; i < 8; i++) {
                final double dx = (this.random.nextDouble() - 0.5) * 0.6;
                final double dy = (this.random.nextDouble() - 0.5) * 0.6;
                final double dz = (this.random.nextDouble() - 0.5) * 0.6;
                level.sendParticles(ParticleTypes.END_ROD, getX(), getY(), getZ(), 1, dx, dy, dz, 0.06);
            }

        }

        super.remove(pReason);
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        final Entity entity = pResult.getEntity();
        final Entity owner = this.getOwner();
        final DamageSource damageSource = this.damageSources().indirectMagic(this, owner == null ? this : owner);
        entity.hurt(damageSource, (float) CompanionsConfig.TEDDY_HOLY_ORB_DAMAGE);
        this.playSound(SoundEvents.AMETHYST_CLUSTER_BREAK, 1.0f, 1.3f);
        this.remove(RemovalReason.KILLED);
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult pResult) {
        super.onHitBlock(pResult);
        this.discard();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FIRED, false);
    }

    @Override
    public void playerTouch(@NotNull Player pEntity) { ;; }

    @Override
    protected int baseLifetime() {
        return 160;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 2, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        event.setAnimation(IDLE);
        return PlayState.CONTINUE;
    }

    @Override
    public boolean shouldRender(double pX, double pY, double pZ) {
        return true;
    }

    @Override
    public @NotNull AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling().inflate(10);
    }

}
