package dev.xylonity.companions.common.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public abstract class CompanionSummonEntity extends TamableAnimal implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<Boolean> NO_MOVEMENT = SynchedEntityData.defineId(CompanionSummonEntity.class, EntityDataSerializers.BOOLEAN);

    protected CompanionSummonEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void tick() {
        super.tick();

        if (isNoMovement()) {
            this.getNavigation().stop();
            this.setDeltaMovement(Vec3.ZERO);
        }

    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (pSource.getEntity() != null && pSource.getEntity().equals(getOwner()) & !pSource.getEntity().isShiftKeyDown()) {
            return false;
        }

        return super.hurt(pSource, pAmount);
    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        UUID ownerUUID = this.getOwnerUUID();

        if (ownerUUID == null) return null;
        if (!(this.level() instanceof ServerLevel serverLevel)) return null;

        Entity owner = serverLevel.getEntity(ownerUUID);
        return owner instanceof LivingEntity ? (LivingEntity) owner : null;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(NO_MOVEMENT, false);
    }

    public boolean isNoMovement() {
        return this.entityData.get(NO_MOVEMENT);
    }

    public void setNoMovement(boolean isNoMovement) {
        this.entityData.set(NO_MOVEMENT, isNoMovement);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel serverLevel, @NotNull AgeableMob ageableMob) {
        return null;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

}
