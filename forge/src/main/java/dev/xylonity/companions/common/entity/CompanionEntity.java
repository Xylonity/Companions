package dev.xylonity.companions.common.entity;

import dev.xylonity.companions.config.CompanionsConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Random;

public abstract class CompanionEntity extends TamableAnimal implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // 0 sitting, 1 following, 2 wander, 3 work
    private static final EntityDataAccessor<Integer> MAIN_ACTION = SynchedEntityData.defineId(CompanionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SIT_VARIATION = SynchedEntityData.defineId(CompanionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> NO_MOVEMENT = SynchedEntityData.defineId(CompanionEntity.class, EntityDataSerializers.BOOLEAN);

    protected CompanionEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
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

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(MAIN_ACTION, 1);
        this.entityData.define(NO_MOVEMENT, false);
        this.entityData.define(SIT_VARIATION, 0);
    }

    public void setMainAction(int action, @Nullable Player player) {
        // This way we can just cycle the main action
        int newAction = action % (CompanionsConfig.COMPANIONS_SHOULD_WORK && this.canThisCompanionWork() ? 4 : 3);
        this.entityData.set(MAIN_ACTION, newAction);

        // TODO: the name of the companion
        if (player != null) {
            switch (newAction) {
                case 0:
                    player.displayClientMessage(Component
                            .translatable("main_action.companions.client_message.is_sitting"), true);
                    break;
                case 1:
                    player.displayClientMessage(Component
                            .translatable("main_action.companions.client_message.is_following"), true);
                    break;
                case 2:
                    player.displayClientMessage(Component
                            .translatable("main_action.companions.client_message.is_wandering"), true);
                    break;
                default:
                    player.displayClientMessage(Component
                            .translatable("main_action.companions.client_message.is_working"), true);
                    break;
            }
        }

    }

    public int getMainAction() {
        return this.entityData.get(MAIN_ACTION);
    }

    public boolean isNoMovement() {
        return this.entityData.get(NO_MOVEMENT);
    }

    public void setNoMovement(boolean isNoMovement) {
        this.entityData.set(NO_MOVEMENT, isNoMovement);
    }

    public int getSitVariation() {
        return this.entityData.get(SIT_VARIATION);
    }

    public void setSitVariation(int variation) {
        this.entityData.set(SIT_VARIATION, variation);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel serverLevel, @NotNull AgeableMob ageableMob) {
        return null;
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        setMainAction(pCompound.getInt("CompanionsMainAction"), null);
        setSitVariation(pCompound.getInt("CompanionsSitVariation"));
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("CompanionsMainAction", this.getMainAction());
        pCompound.putInt("CompanionsSitVariation", this.getSitVariation());
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    /**
     * This method cycles the main interaction and should be called when mobInteraction is triggered.
     * Doesn't check if the player is the owner for more modularity
     */
    public void defaultMainActionInteraction(Player pPlayer) {
        this.setMainAction(this.getMainAction() + 1, pPlayer);
        if (getMainAction() == 0) {
            this.setSitVariation(new Random().nextInt(0, this.sitAnimationsAmount()));
            this.setTarget(null);
            this.setOrderedToSit(true);
        } else {
            this.setOrderedToSit(false);
        }
    }

    /**
     * Handles when the companion is getting tamed. It also automatically sits the entity.
     */
    public void tameInteraction(Player pPlayer) {
        super.tame(pPlayer);
        this.navigation.recomputePath();
        this.setTarget(null);
        this.level().broadcastEntityEvent(this, (byte) 7);
        this.setMainAction(0, pPlayer);
        this.setSitVariation(new Random().nextInt(0, this.sitAnimationsAmount()));
        this.setOrderedToSit(true);
    }

    protected abstract boolean canThisCompanionWork();
    protected abstract int sitAnimationsAmount();

}
