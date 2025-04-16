package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.common.entity.custom.SoulMageEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.openal.AL;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Objects;
import java.util.Random;

public class TornadoProjectile extends Projectile implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final RawAnimation SPIN = RawAnimation.begin().thenPlay("spin");

    private static final EntityDataAccessor<Float> GROUNDY = SynchedEntityData.defineId(TornadoProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> STARTX = SynchedEntityData.defineId(TornadoProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> STARTZ = SynchedEntityData.defineId(TornadoProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> ALPHA = SynchedEntityData.defineId(TornadoProjectile.class, EntityDataSerializers.FLOAT);

    private static final int LIFETIME = 120;

    private boolean initialized = false;

    public TornadoProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(GROUNDY, 0f);
        this.entityData.define(STARTX, 0f);
        this.entityData.define(STARTZ, 0f);
        this.entityData.define(ALPHA, 0f);
    }

    public float getGroundY() {
        return this.entityData.get(GROUNDY);
    }

    public void setGroundY(float v) {
        this.entityData.set(GROUNDY, v);
    }

    public float getStartX() {
        return this.entityData.get(STARTX);
    }

    public void setStartX(float v) {
        this.entityData.set(STARTX, v);
    }

    public float getStartZ() {
        return this.entityData.get(STARTZ);
    }

    public void setStartZ(float v) {
        this.entityData.set(STARTZ, v);
    }

    public float getAlpha() {
        return this.entityData.get(ALPHA);
    }

    public void setAlpha(float v) {
        this.entityData.set(ALPHA, v);
    }

    @Override
    public void tick() {
        super.tick();

        Entity owner = this.getOwner();
        if (!initialized) {
            this.setGroundY((float) this.getY());

            this.setStartX((float) this.getX());
            this.setStartZ((float) this.getZ());

            if (owner != null) {
                Vec3 ownerPos = owner.getEyePosition(1f);
                Vec3 projPos = this.position();
                Vec3 dir = projPos.subtract(ownerPos).normalize();
                this.setAlpha((float) Math.atan2(dir.z, dir.x));
            } else {
                this.setAlpha(0);
            }

            this.setNoGravity(true);
            this.initialized = true;
        }

        if (this.tickCount >= LIFETIME) {
            onExpire();
            return;
        }

        double t = this.tickCount * 0.2;

        double xLocal = Math.sin(0.5 * t) + t;
        double zLocal = Math.cos(0.5 * t);

        double cosA = Math.cos(this.getAlpha());
        double sinA = Math.sin(this.getAlpha());
        double rotX = xLocal * cosA - zLocal * sinA;
        double rotZ = xLocal * sinA + zLocal * cosA;

        double finalX = this.getStartX() + rotX;
        double finalZ = this.getStartZ() + rotZ;

        // Synched data should fix the problem where the tornado starts tweaking if there are multiple
        // instances moving in the world towards different directions
        this.setPos(finalX, this.getGroundY(), finalZ);

        // This will push entities away if they are within the hitbox
        this.level().getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(1),
                e -> {
                    if (e.equals(getOwner())) {
                        return false;
                    }

                    if (getOwner() instanceof SoulMageEntity entity && Objects.equals(entity.getOwner(), e)) {
                        return false;
                    }

                    if (e instanceof SoulMageBookEntity entity && Objects.equals(entity.getOwner(), getOwner())) {
                        return false;
                    }

                    return true;
                })
                .forEach(e -> {
                    Vec3 dir = e.position().subtract(this.position()).normalize().scale(1.4);
                    e.push(dir.x + 0.1, dir.y + 0.1, dir.z + 0.1);
                });

        if (this.level() instanceof ServerLevel sv) {
            if (new Random().nextFloat() <= 0.8) {
                sv.sendParticles(ParticleTypes.SNOWFLAKE,
                        getX() + (getBbWidth() * 2) * Math.random(),
                        getY() + (getBbHeight() * 2) * Math.random(),
                        getZ() + (getBbWidth() * 2) * Math.random(),
                        1, 0, 0, 0, 0.05);
            }
        }

    }

    private void onExpire() {
        if (this.level().isClientSide) {
            spawnHitParticles();
        } else {
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.playSound(SoundEvents.AMETHYST_BLOCK_CHIME);
        }

        this.remove(RemovalReason.DISCARDED);
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 3) {
            spawnHitParticles();
        } else {
            super.handleEntityEvent(pId);
        }
    }

    private void spawnHitParticles() {
        for (int i = 0; i < 10; i++) {
            double x = getX() + (getBbWidth() * 2) * Math.random();
            double y = getY() + (getBbHeight() * 2) * Math.random();
            double z = getZ() + (getBbWidth() * 2) * Math.random();
            this.level().addParticle(ParticleTypes.SNOWFLAKE, x, y, z, 0.05, 0.05, 0.05);
        }
    }

    @Override
    protected void onHit(@NotNull HitResult pResult) {
        if (pResult instanceof BlockHitResult blockHit) {
            Direction hitDirection = blockHit.getDirection();
            if (hitDirection == Direction.UP || hitDirection == Direction.DOWN) {
                return;
            }
        }

        if (this.level().isClientSide) {
            spawnHitParticles();
        } else {
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.playSound(SoundEvents.AMETHYST_BLOCK_HIT);
        }

        this.remove(RemovalReason.DISCARDED);
    }

    @Override
    public void playerTouch(@NotNull Player pEntity) { ;; }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        event.getController().setAnimation(SPIN);

        return PlayState.CONTINUE;
    }

}
