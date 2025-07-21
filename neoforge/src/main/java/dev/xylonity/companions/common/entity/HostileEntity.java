package dev.xylonity.companions.common.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class HostileEntity extends Monster implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<Boolean> NO_MOVEMENT = SynchedEntityData.defineId(HostileEntity.class, EntityDataSerializers.BOOLEAN);

    protected HostileEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
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
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(NO_MOVEMENT, false);
    }

    public boolean isNoMovement() {
        return this.entityData.get(NO_MOVEMENT);
    }

    public void setNoMovement(boolean isNoMovement) {
        this.entityData.set(NO_MOVEMENT, isNoMovement);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

}
