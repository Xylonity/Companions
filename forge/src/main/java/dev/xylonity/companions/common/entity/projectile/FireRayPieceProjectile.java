package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.common.entity.BaseProjectile;
import dev.xylonity.companions.common.entity.projectile.trigger.FireRayBeamEntity;
import dev.xylonity.companions.common.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FireRayPieceProjectile extends BaseProjectile {
    private static final EntityDataAccessor<Integer> INDEX = SynchedEntityData.defineId(FireRayPieceProjectile.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<UUID>> PARENT_UUID = SynchedEntityData.defineId(FireRayPieceProjectile.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Float> YAW = SynchedEntityData.defineId(FireRayPieceProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> PITCH = SynchedEntityData.defineId(FireRayPieceProjectile.class, EntityDataSerializers.FLOAT);

    private static final RawAnimation APPEAR = RawAnimation.begin().thenPlay("appear");
    private static final RawAnimation DISAPPEAR = RawAnimation.begin().thenPlay("disappear");

    public FireRayPieceProjectile(EntityType<? extends FireRayPieceProjectile> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(INDEX, 0);
        entityData.define(PARENT_UUID, Optional.empty());
        entityData.define(YAW, 0f);
        entityData.define(PITCH, 0f);
    }

    @Override
    protected int baseLifetime() {
        return 140;
    }

    public void initAsChild(Entity parent, int index) {
        this.entityData.set(INDEX, index);
        this.entityData.set(PARENT_UUID, Optional.of(parent.getUUID()));
    }

    public void syncPosition(Vec3 pos) {
        setPos(pos.x, pos.y, pos.z);

        Vec3 dir = pos.subtract(getOwnerOrigin()).normalize();
        float yaw = (float) (Mth.atan2(dir.z, dir.x) * Mth.RAD_TO_DEG) - 90f;
        float pitch = (float) (-Mth.atan2(dir.y, Math.sqrt(dir.x*dir.x + dir.z*dir.z)) * Mth.RAD_TO_DEG);

        entityData.set(YAW,   yaw);
        entityData.set(PITCH, pitch);
    }

    private Vec3 getOwnerOrigin() {
        Optional<UUID> ouuid = this.entityData.get(PARENT_UUID);
        if (level() instanceof ServerLevel serverLevel && ouuid.isPresent()) {
            if (serverLevel.getEntity(ouuid.get()) instanceof FireRayBeamEntity beam) {
                return beam.position();
            }
        }

        return this.position();
    }

    public float getPieceYaw() {
        return entityData.get(YAW);
    }

    public float getPiecePitch() {
        return entityData.get(PITCH);
    }

    @Override public void tick() {
        super.tick();

        if (!level().isClientSide && !isInvisible()) {
            List<LivingEntity> entities = level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.25), e -> !e.equals(getOwner()));

            if (!entities.isEmpty()) {
                LivingEntity victim = entities.get(0);
                victim.hurt(this.damageSources().indirectMagic(this, getOwner()), 0.1F);
            }
        }

    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        if (tickCount >= baseLifetime() - 6) event.getController().setAnimation(DISAPPEAR);
        else if (tickCount <= 4) event.getController().setAnimation(APPEAR);

        return PlayState.CONTINUE;
    }

}
