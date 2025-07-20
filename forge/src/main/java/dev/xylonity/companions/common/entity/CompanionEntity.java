package dev.xylonity.companions.common.entity;

import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.knightlib.registry.KnightLibItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class CompanionEntity extends TamableAnimal implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // 0 sitting, 1 following, 2 wander, 3 work
    private static final EntityDataAccessor<Integer> MAIN_ACTION = SynchedEntityData.defineId(CompanionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SIT_VARIATION = SynchedEntityData.defineId(CompanionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> NO_MOVEMENT = SynchedEntityData.defineId(CompanionEntity.class, EntityDataSerializers.BOOLEAN);

    private ChunkPos lastChunkPos;

    protected CompanionEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.noCulling = true;
    }

    @Override
    public void tick() {
        super.tick();

        if (isNoMovement()) {
            this.getNavigation().stop();
            this.setDeltaMovement(Vec3.ZERO);
        }

        if (shouldKeepChunkLoaded() && level() instanceof ServerLevel level) {
            ChunkPos currentChunkPos = new ChunkPos(blockPosition());
            if (!currentChunkPos.equals(lastChunkPos)) {
                if (lastChunkPos != null) {
                    level.setChunkForced(lastChunkPos.x, lastChunkPos.z, false);
                }

                level.setChunkForced(currentChunkPos.x, currentChunkPos.z, true);
                lastChunkPos = currentChunkPos;
            }
        }

    }

    @Override
    public void checkDespawn() {
        if (isTame() && shouldKeepChunkLoaded()) return;

        super.checkDespawn();
    }

    @Override
    public boolean isPersistenceRequired() {
        return isTame() && shouldKeepChunkLoaded();
    }

    @Override
    public boolean requiresCustomPersistence() {
        return isTame() && shouldKeepChunkLoaded();
    }

    @Override
    public void remove(@NotNull RemovalReason pReason) {
        if (level() instanceof ServerLevel level && lastChunkPos != null) {
            level.setChunkForced(lastChunkPos.x, lastChunkPos.z, false);
        }

        super.remove(pReason);
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (pSource.getEntity() != null && pSource.getEntity().equals(getOwner()) & !pSource.getEntity().isShiftKeyDown()) {
            return false;
        }

        return super.hurt(pSource, pAmount);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(MAIN_ACTION, 1);
        builder.define(NO_MOVEMENT, false);
        builder.define(SIT_VARIATION, 0);
    }

    public void setMainAction(int action, @Nullable Player player) {
        // This way we can just cycle the main action
        List<Integer> allowedActions = new ArrayList<>();
        allowedActions.add(0);
        allowedActions.add(1);

        if (CompanionsConfig.SHOULD_COMPANIONS_WANDER) {
            allowedActions.add(2);
        }

        //if (CompanionsConfig.SHOULD_COMPANIONS_WORK && this.canThisCompanionWork()) {
        //    allowedActions.add(3);
        //}

        int newAction = allowedActions.get(action % allowedActions.size());

        this.entityData.set(MAIN_ACTION, newAction);

        if (player != null) {
            switch (newAction) {
                case 0:
                    player.displayClientMessage(Component
                            .translatable("main_action.companions.client_message.is_sitting", getName()), true);
                    break;
                case 1:
                    player.displayClientMessage(Component
                            .translatable("main_action.companions.client_message.is_following", getName()), true);
                    break;
                case 2:
                    player.displayClientMessage(Component
                            .translatable("main_action.companions.client_message.is_wandering", getName()), true);
                    break;
                default:
                    player.displayClientMessage(Component
                            .translatable("main_action.companions.client_message.is_working", getName()), true);
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

        if (pCompound.contains("CompanionsMainAction")) {
            setMainAction(pCompound.getInt("CompanionsMainAction"), null);
        }

        if (pCompound.contains("CompanionsSitVariation")) {
            setSitVariation(pCompound.getInt("CompanionsSitVariation"));
        }
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
     * This method either cycles the main interaction if the player is the owner or heals the entity using
     * the default 'healing' items per se
     */
    public boolean handleDefaultMainActionAndHeal(Player pPlayer, InteractionHand hand) {
        ItemStack stack = pPlayer.getItemInHand(hand);
        Item item = stack.getItem();

        if (isTame() && pPlayer == getOwner()) {
            if (item == KnightLibItems.SMALL_ESSENCE.get()) {
                if (!pPlayer.getAbilities().instabuild) stack.shrink(1);
                this.heal(getHealth() * 0.05f);
            } else if (item == KnightLibItems.GREAT_ESSENCE.get()) {
                if (!pPlayer.getAbilities().instabuild) stack.shrink(1);
                this.heal(getHealth() * 0.2f);
            } else {
                defaultMainActionInteraction(pPlayer);
            }

            return true;
        }

        return false;
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
    protected abstract boolean shouldKeepChunkLoaded();

    @Override
    public boolean canChangeDimensions(@NotNull Level level1, @NotNull Level level2) {
        return true;
    }

    @Override
    public boolean isFood(@NotNull ItemStack itemStack) {
        return false;
    }

}