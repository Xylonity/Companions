package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.common.entity.BaseProjectile;
import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class HolinessStartProjectile extends BaseProjectile {

    private static final EntityDataAccessor<Boolean> IS_FIRE = SynchedEntityData.defineId(HolinessStartProjectile.class, EntityDataSerializers.BOOLEAN);

    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");

    public static final float SPEED = 0.4f;
    private LivingEntity target;

    public HolinessStartProjectile(EntityType<? extends BaseProjectile> type, Level level) {
        super(type, level);
    }

    public void setTarget(LivingEntity target) {
        this.target = target;
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide && target != null && target.isAlive()) {
            Vec3 v = target.getEyePosition().subtract(position()).normalize().scale(SPEED);
            setDeltaMovement(getDeltaMovement().lerp(v, 0.035).normalize().scale(SPEED));
            hasImpulse = true;
        }

        this.move(MoverType.SELF, getDeltaMovement());

        if (!level().isClientSide) {
            Vec3 v = getDeltaMovement();
            setYRot((float)(Mth.atan2(v.x, v.z) * Mth.RAD_TO_DEG));
            setXRot((float)(Mth.atan2(v.y, v.horizontalDistance()) * Mth.RAD_TO_DEG));
        }

        if (tickCount % 8 == 0) {
            level().addParticle(CompanionsParticles.HOLINESS_STAR_TRAIL.get(), getX(), getY() - getBbHeight() * 0.5, getZ(), 0, 0, 0);
        }

        if (this.onGround()) this.discard();

    }

    @Override
    public void remove(@NotNull RemovalReason pReason) {
        if (isFire()) {
            fireParticles();
        } else {
            iceParticles();
        }
        super.remove(pReason);
    }

    public boolean isFire() {
        return this.entityData.get(IS_FIRE);
    }

    public void setIsFire(boolean isFire) {
        this.entityData.set(IS_FIRE, isFire);
    }

    private void fireParticles() {
        for (int i = 0; i < 20; i++) {
            double vx = (level().random.nextDouble() - 0.5) * this.getBbWidth();
            double vy = (level().random.nextDouble() - 0.5) * this.getBbHeight();
            double vz = (level().random.nextDouble() - 0.5) * this.getBbWidth();
            if (level() instanceof ServerLevel level) {
                if (level.random.nextFloat() < 0.35)
                    level.sendParticles(ParticleTypes.POOF, getX(), getY(), getZ(), 1, vx, vy, vz, 0.15);
                if (level.random.nextFloat() < 0.8)
                    level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, getX(), getY(), getZ(), 1, vx, vy, vz, 0.15);
                level.sendParticles(ParticleTypes.LARGE_SMOKE, getX(), getY(), getZ(), 1, vx, vy, vz, 0.15);
                if (level.random.nextFloat() < 0.5)
                    level.sendParticles(ParticleTypes.LAVA, getX(), getY(), getZ(), 1, vx, vy, vz, 0.15);
            }
        }
    }

    private void iceParticles() {
        for (int i = 0; i < 20; i++) {
            double vx = (level().random.nextDouble() - 0.5) * this.getBbWidth();
            double vy = (level().random.nextDouble() - 0.5) * this.getBbHeight();
            double vz = (level().random.nextDouble() - 0.5) * this.getBbWidth();
            if (level() instanceof ServerLevel level) {
                if (level.random.nextFloat() < 0.35)
                    level.sendParticles(ParticleTypes.POOF, getX(), getY(), getZ(), 1, vx, vy, vz, 0.15);
                if (level.random.nextFloat() < 0.8)
                    level.sendParticles(ParticleTypes.ITEM_SNOWBALL, getX(), getY(), getZ(), 1, vx, vy, vz, 0.15);
                level.sendParticles(ParticleTypes.CLOUD, getX(), getY(), getZ(), 1, vx, vy, vz, 0.15);
                level.sendParticles(ParticleTypes.SNOWFLAKE, getX(), getY(), getZ(), 1, vx, vy, vz, 0.15);
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_FIRE, level().random.nextBoolean());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("IsFire")) {
            this.setIsFire(pCompound.getBoolean("IsFire"));
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("IsFire", this.isFire());
    }

    @Override
    public void playerTouch(@NotNull Player pEntity) { ;; }

    @Override
    protected int baseLifetime() {
        return 200;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 2, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        event.setAnimation(IDLE);
        return PlayState.CONTINUE;
    }

}