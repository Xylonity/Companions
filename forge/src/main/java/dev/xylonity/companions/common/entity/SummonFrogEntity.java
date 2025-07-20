package dev.xylonity.companions.common.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animation.AnimatableManager;

public abstract class SummonFrogEntity extends CompanionSummonEntity {

    private static final EntityDataAccessor<Integer> CYCLE_COUNTER = SynchedEntityData.defineId(SummonFrogEntity.class, EntityDataSerializers.INT);

    private static final int MAX_CYCLE_TICKS = 20;

    public SummonFrogEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public int getCycleCount() {
        return this.entityData.get(CYCLE_COUNTER);
    }

    public void setCycleCount(int count) {
        this.entityData.set(CYCLE_COUNTER, count);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CYCLE_COUNTER, -1);
    }

    @Override
    public void tick() {

        super.tick();

        if (!level().isClientSide) {
            if (getCycleCount() == 0) playSound(jumpSound(), 0.5f, 1);

            if (getCycleCount() >= 12) this.setDeltaMovement(new Vec3(0, 0, 0));

            if (getCycleCount() >= 0) setCycleCount(getCycleCount() + 1);

            if (getCycleCount() >= MAX_CYCLE_TICKS) setCycleCount(-1);
        }

    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        ;;
    }

    protected abstract SoundEvent jumpSound();

}