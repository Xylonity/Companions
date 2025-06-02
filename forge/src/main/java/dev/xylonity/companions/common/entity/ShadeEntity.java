package dev.xylonity.companions.common.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract class ShadeEntity extends CompanionEntity {

    private static final EntityDataAccessor<Integer> ATTACK_TYPE = SynchedEntityData.defineId(ShadeEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SHOULD_LOOK_AT_TARGET = SynchedEntityData.defineId(ShadeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_SPAWNING = SynchedEntityData.defineId(ShadeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_BLOOD = SynchedEntityData.defineId(ShadeEntity.class, EntityDataSerializers.BOOLEAN);

    protected ShadeEntity(EntityType<? extends CompanionEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public int getAttackType() {
        return this.entityData.get(ATTACK_TYPE);
    }

    public void setAttackType(int type) {
        this.entityData.set(ATTACK_TYPE, type);
    }

    public boolean shouldLookAtTarget() {
        return this.entityData.get(SHOULD_LOOK_AT_TARGET);
    }

    public void setShouldLookAtTarget(boolean shouldLookAtTarget) {
        this.entityData.set(SHOULD_LOOK_AT_TARGET, shouldLookAtTarget);
    }

    public boolean isSpawning() {
        return this.entityData.get(IS_SPAWNING);
    }

    public void setIsSpawning(boolean isSpawning) {
        this.entityData.set(IS_SPAWNING, isSpawning);
    }

    public boolean isBlood() {
        return this.entityData.get(IS_BLOOD);
    }

    public void setIsBlood(boolean isBlood) {
        this.entityData.set(IS_BLOOD, isBlood);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACK_TYPE, 0);
        this.entityData.define(SHOULD_LOOK_AT_TARGET, true);
        this.entityData.define(IS_BLOOD, false);
        this.entityData.define(IS_SPAWNING, true);
    }

}
