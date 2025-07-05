package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.common.entity.BaseProjectile;
import dev.xylonity.companions.common.entity.companion.SoulMageEntity;
import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;
import java.util.Random;

public class BlackHoleProjectile extends BaseProjectile implements GeoEntity {
    private static final RawAnimation APPEAR = RawAnimation.begin().thenPlay("appear");
    private static final RawAnimation DISAPPEAR = RawAnimation.begin().thenPlay("disappear");
    private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");

    private static final EntityDataAccessor<Boolean> IS_LOCKED = SynchedEntityData.defineId(BlackHoleProjectile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> TICKCOUNT = SynchedEntityData.defineId(BlackHoleProjectile.class, EntityDataSerializers.INT);

    private static final double ATTRACT_RADIUS = 12.0;
    private static final double ATTRACT_STRENGTH = 0.3;
    private static final float GRAVITY = 0.03F;

    public BlackHoleProjectile(EntityType<? extends BaseProjectile> type, Level level) {
        super(type, level);
        this.noPhysics = false;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_LOCKED, false);
        this.entityData.define(TICKCOUNT, 0);
    }

    public boolean isLocked() {
        return this.entityData.get(IS_LOCKED);
    }

    public void setLocked(boolean locked) {
        this.entityData.set(IS_LOCKED, locked);
    }

    public int getTickCount() {
        return this.entityData.get(TICKCOUNT);
    }

    public void setTickCount(int tickCount) {
        this.entityData.set(TICKCOUNT, tickCount);
    }

    private void updateRotation(Vec3 velocity) {
        double horizontalMag = velocity.horizontalDistance();
        float newYaw   = (float) (Mth.atan2(velocity.x, velocity.z) * (180F / Math.PI));
        float newPitch = (float) (Mth.atan2(velocity.y, horizontalMag) * (180F / Math.PI));

        this.setYRot(lerpRotation(this.getYRot(), newYaw));
        this.setXRot(lerpRotation(this.getXRot(), newPitch));
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide) setTickCount(tickCount);

        super.tick();

        if (new Random().nextFloat() < 0.44 && getTickCount() < getLifetime() - 20) for (int i = 0; i < 1; i++) {
            if (this.level() instanceof ServerLevel level) {
                double offsetX = (Math.random() - 0.5) * 0.5;
                double offsetZ = (Math.random() - 0.5) * 0.5;

                double speedX = (Math.random() - 0.5) * 0.1;
                double speedZ = (Math.random() - 0.5) * 0.1;

                level.sendParticles(CompanionsParticles.BLACK_HOLE_STAR.get(), this.getX() + offsetX, this.getY() - 0.2, this.getZ() + offsetZ, 1, speedX, 0.0, speedZ, 0.2);
            }
        }

        if (this.getTickCount() >= getLifetime()) {
            this.discard();
            return;
        }

        if (this.isLocked()) {
            this.setDeltaMovement(Vec3.ZERO);
            attractNearbyEntities();
            return;
        }

        if (!this.level().isClientSide && this.tickCount >= 20 && !isLocked()) {
            setLocked(true);
        }

        if (isLocked() && getTickCount() == 20) {
            this.level().playSound(null, getX(), getY(), getZ(), SoundEvents.ARROW_HIT, getSoundSource(), 1.0F, 1.0F);
        }

        Vec3 oldPos = this.position();
        Vec3 velocity = this.getDeltaMovement();
        if (!this.noPhysics) {
            velocity = velocity.add(0, -GRAVITY, 0);
        }
        velocity = velocity.scale(0.99);

        Vec3 newPos = oldPos.add(velocity);

        ClipContext ctx = new ClipContext(
                oldPos,
                newPos,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                this
        );
        BlockHitResult blockHit = level().clip(ctx);

        if (blockHit.getType() == HitResult.Type.BLOCK) {
            Vec3 hitPos = blockHit.getLocation();
            this.setPos(hitPos.x, hitPos.y, hitPos.z);

            this.setDeltaMovement(Vec3.ZERO);
            this.setLocked(true);

        }
        else {
            this.setDeltaMovement(velocity);
            this.move(MoverType.SELF, velocity);

            updateRotation(velocity);

            if (this.onGround()) {
                this.setLocked(true);
                this.setDeltaMovement(Vec3.ZERO);
            }
        }

        attractNearbyEntities();
    }

    private void attractNearbyEntities() {
        AABB area = this.getBoundingBox().inflate(ATTRACT_RADIUS);

        List<LivingEntity> nearby = level().getEntitiesOfClass(LivingEntity.class, area,
                e -> e.isAlive()
                && !e.equals(getOwner())
                && !(getOwner() instanceof SoulMageEntity
                    && ((SoulMageEntity) getOwner()).getOwner() != null
                    && ((SoulMageEntity) getOwner()).getOwner().equals(e))
                && !(e instanceof TamableAnimal
                    && ((TamableAnimal) e).getOwner() != null
                    && ((TamableAnimal) e).getOwner().equals(getOwner()))
                && !(getOwner() instanceof SoulMageEntity && e instanceof TamableAnimal
                    && ((TamableAnimal) e).getOwner() != null
                    && ((TamableAnimal) e).getOwner().equals(((SoulMageEntity) getOwner()).getOwner()))
        );

        for (LivingEntity ent : nearby) {
            Vec3 toCenter = new Vec3(this.getX() - ent.getX(),
                    this.getY() - ent.getY(),
                    this.getZ() - ent.getZ());
            double distance = toCenter.length();
            if (distance < 0.05) {
                continue;
            }

            toCenter = toCenter.normalize();

            double maxPullSpeed = 1.4;

            double exponent = 2.0;
            double linearFactor = (ATTRACT_RADIUS - distance) / ATTRACT_RADIUS;
            if (linearFactor < 0) linearFactor = 0;
            double factor = Math.pow(linearFactor, exponent);
            Vec3 desiredVel = toCenter.scale(factor * maxPullSpeed);

            double alpha = 0.15;
            Vec3 currentVel = ent.getDeltaMovement();
            Vec3 newVel = currentVel.lerp(desiredVel, alpha);

            ent.setDeltaMovement(newVel);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "extraController", this::extraPredicate));
        controllers.add(new AnimationController<>(this, "controller", this::predicate));
    }

    @Override
    protected int baseLifetime() {
        return 100;
    }

    private <T extends GeoAnimatable> PlayState extraPredicate(AnimationState<T> event) {
        if (getTickCount() >= getLifetime() - 7) event.getController().setAnimation(DISAPPEAR);
        //else if (getTickCount() <= 4) event.getController().setAnimation(APPEAR);

        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        event.getController().setAnimation(IDLE);
        return PlayState.CONTINUE;
    }

}
