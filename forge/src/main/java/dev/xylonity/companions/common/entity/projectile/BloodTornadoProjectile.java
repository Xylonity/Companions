package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.BaseProjectile;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;

import java.util.Random;

public class BloodTornadoProjectile extends BaseProjectile {
    private final RawAnimation SPIN = RawAnimation.begin().thenPlay("spin");

    private static final EntityDataAccessor<Float> GROUNDY = SynchedEntityData.defineId(BloodTornadoProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> STARTX = SynchedEntityData.defineId(BloodTornadoProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> STARTZ = SynchedEntityData.defineId(BloodTornadoProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> ALPHA = SynchedEntityData.defineId(BloodTornadoProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> TICKS = SynchedEntityData.defineId(BloodTornadoProjectile.class, EntityDataSerializers.INT);

    private boolean initialized = false;

    public BloodTornadoProjectile(EntityType<? extends BaseProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(GROUNDY, 0f);
        builder.define(STARTX, 0f);
        builder.define(STARTZ, 0f);
        builder.define(TICKS, 0);
        builder.define(ALPHA, Float.NaN);
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

    public int getTicks() {
        return this.entityData.get(TICKS);
    }

    @Override
    public void tick() {

        if (!level().isClientSide) this.entityData.set(TICKS, tickCount);

        super.tick();

        if (!initialized) {
            setGroundY((float) getY());
            setStartX((float) getX());
            setStartZ((float) getZ());

            if (Float.isNaN(getAlpha())) {
                Vec3 dir = getDeltaMovement().normalize();
                setAlpha((float) Math.atan2(dir.z, dir.x));
            }

            setNoGravity(true);
            initialized = true;
        }

        double t = getTicks() * 0.2;

        double xLocal = 2 * Math.sin((Math.PI / 2f) * t);
        double zLocal = t;

        double cosA = Math.cos(this.getAlpha());
        double sinA = Math.sin(this.getAlpha());

        // Synched data should fix the problem where the tornado starts tweaking if there are multiple
        // instances moving in the world towards different directions
        this.setPos(this.getStartX() + (xLocal * cosA - zLocal * sinA), this.getGroundY(), this.getStartZ() + (xLocal * sinA + zLocal * cosA));

        this.level().getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(1), e -> !Util.areEntitiesLinked(this, e))
                .forEach(e -> {
                    if (e instanceof LivingEntity living) {
                        living.hurt(damageSources().magic(), (float) CompanionsConfig.BLOOD_TORNADO_DAMAGE);
                    }
                });

        if (this.level() instanceof ServerLevel sv) {
            if (new Random().nextFloat() <= 0.15) {
                sv.sendParticles(CompanionsParticles.SHADE_SUMMON.get(), getX() + (getBbWidth() * 2) * Math.random(), getY() + (getBbHeight() * 2) * Math.random(), getZ() + (getBbWidth() * 2) * Math.random(), 1, 0, 0, 0, 0.05);
            }
        }

        if (level().isClientSide) {
            if ((this.tickCount % 35 == 0 || this.tickCount == 1)) {
                for (int i = 0; i < 3; i++) {
                    Companions.PROXY.spawnBaseProjectileTrail(this, this.getBbHeight() - 0.25f, getBbHeight(), 135/255f, 0, 0);
                }

            }
        }

    }

    @Override
    public void remove(@NotNull RemovalReason pReason) {
        if (!level().isClientSide) {
            spawnRemoveParticles();
            level().playSound(null, blockPosition(), SoundEvents.AMETHYST_BLOCK_HIT, SoundSource.NEUTRAL);
        }

        super.remove(pReason);
    }

    private void spawnRemoveParticles() {
        for (int i = 0; i < 15; i++) {
            double dx = (this.random.nextDouble() - 0.5) * 1.15;
            double dy = (this.random.nextDouble() - 0.5) * 1.15;
            double dz = (this.random.nextDouble() - 0.5) * 1.15;
            if (this.level() instanceof ServerLevel level) {
                level.sendParticles(ParticleTypes.POOF, this.getX(), this.getY() + getBbHeight() * Math.random(), this.getZ(), 1, dx, dy, dz, 0.1);
                if (level.random.nextFloat() < 0.35f) level.sendParticles(CompanionsParticles.SHADE_SUMMON.get(), this.getX(), this.getY() + getBbHeight() * Math.random(), this.getZ(), 1, dx, dy, dz, 0.1225);
            }
        }
    }

    @Override
    protected void onHit(@NotNull HitResult pResult) {
        this.remove(RemovalReason.DISCARDED);
    }

    @Override
    public void playerTouch(@NotNull Player pEntity) { ;; }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", this::predicate));
    }

    @Override
    protected int baseLifetime() {
        return 120;
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        event.getController().setAnimation(SPIN);

        return PlayState.CONTINUE;
    }

}
