package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.common.entity.BaseProjectile;
import dev.xylonity.companions.registry.CompanionsEffects;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class MagicRayPieceProjectile extends BaseProjectile implements GeoEntity {
    private static final RawAnimation APPEAR = RawAnimation.begin().thenPlay("appear");
    private static final RawAnimation DISAPPEAR = RawAnimation.begin().thenPlay("disappear");
    private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");

    private static final EntityDataAccessor<Float> YAW = SynchedEntityData.defineId(MagicRayPieceProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> PITCH = SynchedEntityData.defineId(MagicRayPieceProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> TICKCOUNT = SynchedEntityData.defineId(MagicRayPieceProjectile.class, EntityDataSerializers.INT);

    public MagicRayPieceProjectile(EntityType<? extends BaseProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.noPhysics = false;
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide) setTickCount(tickCount);

        super.tick();

        if (!level().isClientSide) {
            double rad = 0.1D;
            List<LivingEntity> hitEntities = level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(rad), this::canHitEntity);

            if (!hitEntities.isEmpty()) {
                LivingEntity hit = hitEntities.get(0);
                hit.hurt(damageSources().generic(), 3.0F);
            }
        }

        if (this.tickCount > getLifetime()) {
            this.discard();
        }
    }

    public float getYaw() {
        return entityData.get(YAW);
    }

    public void setYaw(float yaw) {
        entityData.set(YAW, yaw);
    }

    public float getPitch() {
        return entityData.get(PITCH);
    }

    public void setPitch(float pitch) {
        entityData.set(PITCH, pitch);
    }

    public int getTickCount() {
        return this.entityData.get(TICKCOUNT);
    }

    public void setTickCount(int tickCount) {
        this.entityData.set(TICKCOUNT, tickCount);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(YAW, 0F);
        this.entityData.define(PITCH, 0F);
        this.entityData.define(TICKCOUNT, 0);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", this::predicate));
        controllers.add(new AnimationController<>(this, "extraController", this::extraPredicate));
    }

    @Override
    protected int baseLifetime() {
        return 18;
    }

    private <T extends GeoAnimatable> PlayState extraPredicate(AnimationState<T> event) {
        if (getTickCount() >= getLifetime() - 6) event.getController().setAnimation(DISAPPEAR);
        else if (getTickCount() <= 4) event.getController().setAnimation(APPEAR);

        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        event.getController().setAnimation(IDLE);
        return PlayState.CONTINUE;
    }

}
