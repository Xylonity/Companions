package dev.xylonity.companions.common.entity.companion;

import dev.xylonity.companions.common.ai.navigator.GroundNavigator;
import dev.xylonity.companions.common.entity.CompanionEntity;
import dev.xylonity.companions.common.entity.ai.generic.CompanionFollowOwnerGoal;
import dev.xylonity.companions.common.entity.ai.generic.CompanionRandomStrollGoal;
import dev.xylonity.companions.common.entity.ai.generic.CompanionsHurtTargetGoal;
import dev.xylonity.companions.common.entity.ai.minion.tamable.gargoyle.GargoyleHealAttackGoal;
import dev.xylonity.companions.common.entity.ai.minion.tamable.gargoyle.GargoyleSpellAttackGoal;
import dev.xylonity.companions.common.entity.ai.minion.tamable.imp.ImpBraceAttackGoal;
import dev.xylonity.companions.common.entity.ai.minion.tamable.imp.ImpFireMarkAttackGoal;
import dev.xylonity.companions.common.entity.ai.minion.tamable.minion.MinionTornadoAttackGoal;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

public class MinionEntity extends CompanionEntity {

    // General anims
    // Minion's idle is fly
    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private final RawAnimation SIT = RawAnimation.begin().thenPlay("sit");
    private final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");

    // Imp doesn't fly
    private final RawAnimation FLY = RawAnimation.begin().thenPlay("fly");

    // Attack anims
    private final RawAnimation SPELL = RawAnimation.begin().thenPlay("spell");
    private final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");
    private final RawAnimation THROW = RawAnimation.begin().thenPlay("throw");
    private final RawAnimation RING = RawAnimation.begin().thenPlay("ring");
    private final RawAnimation HEAL = RawAnimation.begin().thenPlay("idle2");

    private static final EntityDataAccessor<String> VARIANT = SynchedEntityData.defineId(MinionEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> IS_LOCKED = SynchedEntityData.defineId(MinionEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_FLYING = SynchedEntityData.defineId(MinionEntity.class, EntityDataSerializers.BOOLEAN);
    // 0 no attack
    // minion: 1 default
    // imp: 1 default, 2 ring
    // gargoyle: 1 default, 2 heal
    private static final EntityDataAccessor<Integer> ATTACK_TYPE = SynchedEntityData.defineId(MinionEntity.class, EntityDataSerializers.INT);

    private ResourceKey<Level> lastDimension = null;

    public MinionEntity(EntityType<? extends CompanionEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        return new GroundNavigator(this, pLevel);
    }

    public static AttributeSupplier setAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0D)
                .add(Attributes.ATTACK_DAMAGE, 5f)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.55f)
                .add(Attributes.FOLLOW_RANGE, 35.0).build();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(1, new MinionTornadoAttackGoal(this, 20, 80));

        this.goalSelector.addGoal(1, new ImpBraceAttackGoal(this, 20, 80));
        this.goalSelector.addGoal(1, new ImpFireMarkAttackGoal(this, 20, 80));

        this.goalSelector.addGoal(1, new GargoyleSpellAttackGoal(this, 20, 80));
        this.goalSelector.addGoal(1, new GargoyleHealAttackGoal(this, 20, 80));

        this.goalSelector.addGoal(3, new CompanionFollowOwnerGoal(this, 0.6D, 6.0F, 2.0F, false));
        this.goalSelector.addGoal(3, new CompanionRandomStrollGoal(this, 0.43));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new CompanionsHurtTargetGoal(this));
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.entityData.get(IS_LOCKED)) {
            updateVariantByDimension();
        }

        if (getVariant().equals(Variant.OVERWORLD.getName())) {

        } else if (getVariant().equals(Variant.END.getName())) {

        } else {

        }


    }

    private void updateVariantByDimension() {
        ResourceKey<Level> currentDim = this.level().dimension();

        if (currentDim.equals(lastDimension)) {
            return;
        }

        lastDimension = currentDim;

        Variant newVariant;
        if (currentDim.equals(Level.NETHER)) {
            newVariant = Variant.NETHER;
        } else if (currentDim.equals(Level.END)) {
            newVariant = Variant.END;
        } else {
            newVariant = Variant.OVERWORLD;
        }

        setVariant(newVariant.getName());
    }

    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {

        if (level().isClientSide) return InteractionResult.SUCCESS;

        if (isTame() && player == getOwner() && player.getItemInHand(hand).getItem() == CompanionsItems.NETHERITE_CHAINS.get()) {
            if (!player.getAbilities().instabuild) player.getItemInHand(hand).shrink(1);

            setIsPhaseLocked(true);
            return InteractionResult.SUCCESS;
        }

        if (handleDefaultMainActionAndHeal(player, hand)) {
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("IsVariantLocked")) {
            setIsPhaseLocked(pCompound.getBoolean("IsVariantLocked"));
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("IsVariantLocked", isPhaseLocked());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VARIANT, Variant.NETHER.getName());
        this.entityData.define(IS_LOCKED, false);
        this.entityData.define(IS_FLYING, false);
        this.entityData.define(ATTACK_TYPE, 0);
    }

    @Override
    protected boolean canThisCompanionWork() {
        return false;
    }

    @Override
    protected int sitAnimationsAmount() {
        return 1;
    }

    @Override
    protected boolean shouldKeepChunkLoaded() {
        return CompanionsConfig.MINION_KEEP_CHUNK_LOADED;
    }

    public boolean isPhaseLocked() {
        return this.entityData.get(IS_LOCKED);
    }

    public void setIsPhaseLocked(boolean phase) {
        this.entityData.set(IS_LOCKED, phase);
    }

    public boolean isFlying() {
        return this.entityData.get(IS_FLYING);
    }

    public void setFlying(boolean flying) {
        this.entityData.set(IS_FLYING, flying);
    }

    public String getVariant() {
        return this.entityData.get(VARIANT);
    }

    public void setVariant(String variant) {
        this.entityData.set(VARIANT, variant);
    }

    public int getAttackType() {
        return this.entityData.get(ATTACK_TYPE);
    }

    public void setAttackType(int attacking) {
        this.entityData.set(ATTACK_TYPE, attacking);
    }

    @Override
    public @NotNull Component getName() {
        if (this.hasCustomName()) {
            return super.getName();
        } else if (this.level().dimension().equals(Level.NETHER)) {
            return Component.literal("Imp");
        } else if (this.level().dimension().equals(Level.END)) {
            return Component.literal("Gargoyle");
        } else {
            return Component.literal("Minion");
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 2, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {

        if (getVariant().equals(Variant.OVERWORLD.getName())) {
            if (this.getMainAction() == 0) {
                event.getController().setAnimation(SIT);
            } else if (this.getAttackType() == 1) {
                event.getController().setAnimation(ATTACK);
            } else {
                event.getController().setAnimation(FLY);
            }

        } else if (getVariant().equals(Variant.END.getName())) {
            if (this.getMainAction() == 0) {
                event.getController().setAnimation(SIT);
            } else if (this.getAttackType() == 1) {
                event.getController().setAnimation(SPELL);
            } else if (this.getAttackType() == 2) {
                event.getController().setAnimation(HEAL);
            } else if (event.isMoving()) {
                event.getController().setAnimation(WALK);
            } else {
                event.getController().setAnimation(IDLE);
            }

        } else {
            if (this.getMainAction() == 0) {
                event.getController().setAnimation(SIT);
            } else if (this.getAttackType() == 1) {
                event.getController().setAnimation(THROW);
            } else if (this.getAttackType() == 2) {
                event.getController().setAnimation(RING);
            } else if (event.isMoving()) {
                event.getController().setAnimation(WALK);
            } else {
                event.getController().setAnimation(IDLE);
            }
        }

        return PlayState.CONTINUE;
    }

    public enum Variant {
        OVERWORLD("minion"),
        NETHER("imp"),
        END("gargoyle");

        private final String name;

        Variant(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }


}