package dev.xylonity.companions.common.entity;

import dev.xylonity.companions.common.entity.companion.CorneliusEntity;
import dev.xylonity.companions.common.util.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;
import java.util.UUID;

public abstract class CompanionSummonEntity extends TamableAnimal implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // 0 follow companion, 1 follow player
    private static final EntityDataAccessor<Integer> MAIN_ACTION = SynchedEntityData.defineId(CompanionSummonEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> NO_MOVEMENT = SynchedEntityData.defineId(CompanionSummonEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ATTACK_TYPE = SynchedEntityData.defineId(CompanionSummonEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<UUID>> SECOND_OWNER_UUID = SynchedEntityData.defineId(CompanionSummonEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    protected CompanionSummonEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void tick() {
        super.tick();

        if (isNoMovement()) {
            this.getNavigation().stop();
            this.setDeltaMovement(0, getDeltaMovement().y(),  0);
        }

        if (tickCount % 20 == 0) {
            checkOwnersStatus();
        }

    }

    private void checkOwnersStatus() {
        if (!level().isClientSide) {
            LivingEntity owner1 = getOwner();
            LivingEntity owner2 = getSecondOwner();

            if (owner1 == null || !owner1.isAlive() && !(owner1 instanceof Player)) {
                if (owner2 != null && owner2.isAlive()) {
                    promoteSecondOwnerToPrimary();
                }
            }

            if (owner2 == null || !owner2.isAlive()) {
                if (getSecondOwnerUUID() != null) {
                    if (!(getOwner() instanceof CompanionEntity)) {
                        setSecondOwnerUUID(null);
                    }
                }
            }
        }

    }

    private void promoteSecondOwnerToPrimary() {
        UUID cache = getSecondOwnerUUID();
        if (cache != null) {
            this.setOwnerUUID(cache);
            this.setSecondOwnerUUID(null);
            this.entityData.set(MAIN_ACTION, 0);
            this.setTame(true);
        }
    }

    public UUID getSecondOwnerUUID() {
        return this.entityData.get(SECOND_OWNER_UUID).orElse(null);
    }

    public void setSecondOwnerUUID(@Nullable UUID pUuid) {
        this.entityData.set(SECOND_OWNER_UUID, Optional.ofNullable(pUuid));
    }

    @Override
    public void die(@NotNull DamageSource pCause) {
        super.die(pCause);
        if (getOwner() != null && getOwner() instanceof CorneliusEntity e) {
            if (e.getSummonedCount() > 0) e.setSummonedCount(e.getSummonedCount() - 1);
        }
    }

    public int getAttackType() {
        return this.entityData.get(ATTACK_TYPE);
    }

    public void setAttackType(int type) {
        this.entityData.set(ATTACK_TYPE, type);
    }

    public void cycleMainAction(@Nullable Player player) {

        if (!canCycleOwners()) return;

        this.entityData.set(MAIN_ACTION, (getMainAction() + 1) % 2);

        if (player != null) {
            cycleOwners();
            if (getOwner() instanceof Player) {
                player.displayClientMessage(Component
                        .translatable("main_action_summon.companions.client_message.following_player"), true);
            } else {
                player.displayClientMessage(Component
                        .translatable("main_action_summon.companions.client_message.following_companion"), true);
            }

        }

    }

    private boolean canCycleOwners() {
        LivingEntity owner = getOwner();
        LivingEntity owner2 = getSecondOwner();

        if (owner == null && owner2 != null && owner2.isAlive()) {
            return true;
        }

        return owner != null && owner.isAlive() && owner2 != null && owner2.isAlive();
    }

    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player pPlayer, @NotNull InteractionHand pHand) {

        if (pHand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        if (Util.areEntitiesLinked(pPlayer, this)) {
            if (level().isClientSide) {
                if (getSecondOwner() == null) return InteractionResult.PASS;

                return InteractionResult.SUCCESS;
            }

            cycleMainAction(pPlayer);

            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(pPlayer, pHand);
    }

    private void cycleOwners() {
        UUID cache = getOwnerUUID();
        this.setOwnerUUID(getSecondOwnerUUID());
        this.setSecondOwnerUUID(cache);
        this.setTame(true);
    }

    public int getMainAction() {
        return this.entityData.get(MAIN_ACTION);
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (pSource.getEntity() != null && pSource.getEntity().equals(getOwner()) & !pSource.getEntity().isShiftKeyDown()) {
            return false;
        }

        return super.hurt(pSource, pAmount);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("SecondaryOwnerUUID")) {
            this.setSecondOwnerUUID(pCompound.getUUID("SecondaryOwnerUUID"));
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        if (this.getSecondOwnerUUID() != null) {
            pCompound.putUUID("SecondaryOwnerUUID", this.getSecondOwnerUUID());
        }
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

    @Nullable
    public LivingEntity getSecondOwner() {
        UUID secondOwnerUUID = this.getSecondOwnerUUID();

        if (secondOwnerUUID == null) return null;
        if (!(this.level() instanceof ServerLevel serverLevel)) return null;

        Entity owner = serverLevel.getEntity(secondOwnerUUID);
        return owner instanceof LivingEntity ? (LivingEntity) owner : null;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACK_TYPE, 0);
        this.entityData.define(MAIN_ACTION, 0);
        this.entityData.define(NO_MOVEMENT, false);
        this.entityData.define(SECOND_OWNER_UUID, Optional.empty());
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