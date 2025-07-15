package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.BaseProjectile;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;
import java.util.Random;

public class BlackHoleProjectile extends BaseProjectile {
    private static final RawAnimation DISAPPEAR = RawAnimation.begin().thenPlay("disappear");
    private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");

    private static final EntityDataAccessor<Boolean> IS_LOCKED = SynchedEntityData.defineId(BlackHoleProjectile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> TICKCOUNT = SynchedEntityData.defineId(BlackHoleProjectile.class, EntityDataSerializers.INT);

    private static final double ATTRACT_RADIUS = CompanionsConfig.BLACK_HOLE_ATTRACTION_RADIUS;
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

    private void updateRotation(Vec3 vel) {
        double horizontalMag = vel.horizontalDistance();
        float newYaw = (float) (Mth.atan2(vel.x, vel.z) * (180F / Math.PI));
        float newPitch = (float) (Mth.atan2(vel.y, horizontalMag) * (180F / Math.PI));

        this.setYRot(lerpRotation(this.getYRot(), newYaw));
        this.setXRot(lerpRotation(this.getXRot(), newPitch));
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide) setTickCount(tickCount);

        super.tick();

        if (new Random().nextFloat() < 0.44 && getTickCount() < getLifetime() - 20) for (int i = 0; i < 1; i++) {
            if (this.level() instanceof ServerLevel level) {
                double x = (Math.random() - 0.5) * 0.5;
                double z = (Math.random() - 0.5) * 0.5;
                level.sendParticles(CompanionsParticles.BLACK_HOLE_STAR.get(), this.getX() + x, this.getY() - 0.2, this.getZ() + z, 1, (Math.random() - 0.5) * 0.1, 0.0, (Math.random() - 0.5) * 0.1, 0.2);
            }

        }

        if (this.getTickCount() >= getLifetime()) {
            this.discard();
            return;
        }

        if (this.isLocked()) {
            if (level().isClientSide) {
                for (Player player : level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(30))) {
                    Companions.PROXY.shakePlayerCamera(player, 5, 0.1f, 0.1f, 0.1f, 10);
                }
            }

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
        Vec3 vel = this.getDeltaMovement();

        if (!this.noPhysics) vel = vel.add(0, -GRAVITY, 0);

        vel = vel.scale(0.99);

        BlockHitResult blockHit = level().clip(new ClipContext(oldPos, oldPos.add(vel), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));

        if (blockHit.getType() == HitResult.Type.BLOCK) {
            Vec3 hitPos = blockHit.getLocation();
            this.setPos(hitPos.x, hitPos.y, hitPos.z);

            this.setDeltaMovement(Vec3.ZERO);
            this.setLocked(true);
        } else {
            this.setDeltaMovement(vel);
            this.move(MoverType.SELF, vel);

            updateRotation(vel);

            if (this.onGround()) {
                this.setLocked(true);
                this.setDeltaMovement(Vec3.ZERO);
            }

        }

        attractNearbyEntities();
    }

    private void attractNearbyEntities() {
        List<LivingEntity> nearby = level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(ATTRACT_RADIUS), e -> !Util.areEntitiesLinked(e, this));

        for (LivingEntity ent : nearby) {
            Vec3 toCenter = new Vec3(this.getX() - ent.getX(), this.getY() - ent.getY(), this.getZ() - ent.getZ());

            double distance = toCenter.length();
            if (distance < 0.05) continue;

            toCenter = toCenter.normalize();

            double linear = (ATTRACT_RADIUS - distance) / ATTRACT_RADIUS;
            if (linear < 0) linear = 0;
            Vec3 desiredVel = toCenter.scale(Math.pow(linear, 2) * CompanionsConfig.BLACK_HOLE_ATTRACTION_SPEED);

            Vec3 currentVel = ent.getDeltaMovement();
            Vec3 newVel = currentVel.lerp(desiredVel, 0.15);

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

        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        event.getController().setAnimation(IDLE);
        return PlayState.CONTINUE;
    }

}
