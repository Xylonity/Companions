package dev.xylonity.companions.common.entity;

import dev.xylonity.companions.registry.CompanionsParticles;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class ShadeEntity extends CompanionEntity {

    private static final EntityDataAccessor<Integer> ATTACK_TYPE = SynchedEntityData.defineId(ShadeEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SHOULD_LOOK_AT_TARGET = SynchedEntityData.defineId(ShadeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_SPAWNING = SynchedEntityData.defineId(ShadeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_BLOOD = SynchedEntityData.defineId(ShadeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> LIFETIME = SynchedEntityData.defineId(ShadeEntity.class, EntityDataSerializers.INT);

    protected ShadeEntity(EntityType<? extends CompanionEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void tick() {
        super.tick();

        if (getLifetime() == 0) this.discard();
    }

    @Override
    public void remove(@NotNull RemovalReason pReason) {
        super.remove(pReason);
        for (int i = 0; i < 25; i++) {
            double vx = (level().random.nextDouble() - 0.5) * this.getBbWidth();
            double vy = (level().random.nextDouble() - 0.5) * this.getBbHeight();
            double vz = (level().random.nextDouble() - 0.5) * this.getBbWidth();
            if (level() instanceof ServerLevel level) {
                level.sendParticles(CompanionsParticles.SHADE_TRAIL.get(), getX(), getY(), getZ(), 1, vx, vy, vz, 0.15);
                if (i % 3 == 0) level.sendParticles(CompanionsParticles.SHADE_SUMMON.get(), getX(), getY(), getZ(), 1, vx, vy, vz, 0.35);
            }
        }

        playSound(CompanionsSounds.SHADE_DESPAWN.get());
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime >= 1 && !this.level().isClientSide() && !this.isRemoved()) {
            this.level().broadcastEntityEvent(this, (byte) 60);
            this.remove(RemovalReason.KILLED);
        }
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

    public int getLifetime() {
        return this.entityData.get(LIFETIME);
    }

    public void setLifetime(int tick) {
        this.entityData.set(LIFETIME, tick);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("HasSpawned", this.isSpawning());
        pCompound.putBoolean("IsBlood", this.isBlood());
        pCompound.putInt("Lifetime", this.getLifetime());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("HasSpawned")) {
            setIsSpawning(pCompound.getBoolean("HasSpawned"));
        }
        if (pCompound.contains("IsBlood")) {
            setIsBlood(pCompound.getBoolean("IsBlood"));
        }
        if (pCompound.contains("Lifetime")) {
            setLifetime(pCompound.getInt("Lifetime"));
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ATTACK_TYPE, 0);
        builder.define(SHOULD_LOOK_AT_TARGET, true);
        builder.define(IS_BLOOD, false);
        builder.define(IS_SPAWNING, true);
        builder.define(LIFETIME, getMaxLifetime());
    }

    public abstract int getMaxLifetime();

}
