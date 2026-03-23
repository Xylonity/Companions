package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.common.entity.BaseProjectile;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.config.CompanionsConfig;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;

public class MagicRayPieceProjectile extends BaseProjectile {
    private static final RawAnimation APPEAR = RawAnimation.begin().thenPlay("appear");
    private static final RawAnimation DISAPPEAR = RawAnimation.begin().thenPlay("disappear");
    private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");

    private static final EntityDataAccessor<Float> YAW = SynchedEntityData.defineId(MagicRayPieceProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> PITCH = SynchedEntityData.defineId(MagicRayPieceProjectile.class, EntityDataSerializers.FLOAT);

    public MagicRayPieceProjectile(EntityType<? extends BaseProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.noPhysics = false;
    }

    @Override
    protected boolean canHitEntity(@NotNull Entity entity) {
        if (Util.areEntitiesLinked(this, entity)) return false;

        return super.canHitEntity(entity);
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            List<LivingEntity> hitEntities = level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.75), this::canHitEntity);

            if (!hitEntities.isEmpty()) {
                LivingEntity hit = hitEntities.get(0);
                hit.hurt(damageSources().magic(), (float) CompanionsConfig.MAGIC_PIECE_DAMAGE);
            }
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

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(YAW, 0F);
        this.entityData.define(PITCH, 0F);
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
        if (tickCount >= getLifetime() - 6) event.getController().setAnimation(DISAPPEAR);
        else if (tickCount <= 4) event.getController().setAnimation(APPEAR);

        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        event.getController().setAnimation(IDLE);
        return PlayState.CONTINUE;
    }

}
