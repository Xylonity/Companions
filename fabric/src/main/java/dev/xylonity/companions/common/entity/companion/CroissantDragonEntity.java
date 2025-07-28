package dev.xylonity.companions.common.entity.companion;

import dev.xylonity.companions.common.ai.navigator.GroundNavigator;
import dev.xylonity.companions.common.entity.CompanionEntity;
import dev.xylonity.companions.common.entity.ai.croissant.CroissantDragonAttackGoal;
import dev.xylonity.companions.common.entity.ai.generic.CompanionFollowOwnerGoal;
import dev.xylonity.companions.common.entity.ai.generic.CompanionRandomStrollGoal;
import dev.xylonity.companions.common.entity.ai.generic.CompanionsHurtTargetGoal;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsItems;
import dev.xylonity.companions.registry.CompanionsSounds;
import dev.xylonity.knightlib.api.TickScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class CroissantDragonEntity extends CompanionEntity {
    public SimpleContainer inventory;

    private final RawAnimation SIT = RawAnimation.begin().thenPlay("sit");
    private final RawAnimation SIT2 = RawAnimation.begin().thenPlay("sit2");
    private final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");
    private final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private final RawAnimation EATEN = RawAnimation.begin().thenPlay("eaten");
    private final RawAnimation FLY = RawAnimation.begin().thenPlay("fly");

    private static final EntityDataAccessor<Boolean> IS_ATTACKING = SynchedEntityData.defineId(CroissantDragonEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> ARMOR_NAME = SynchedEntityData.defineId(CroissantDragonEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> EATEN_AMOUNT = SynchedEntityData.defineId(CroissantDragonEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> HAS_BEEN_EATEN = SynchedEntityData.defineId(CroissantDragonEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> MILK_AMOUNT = SynchedEntityData.defineId(CroissantDragonEntity.class, EntityDataSerializers.INT);

    private final int EATEN_DELAY = 10;
    private int nextEatenRecover = 0;

    public CroissantDragonEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        return new GroundNavigator(this, pLevel);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));

        this.goalSelector.addGoal(2, new CroissantDragonAttackGoal(this));

        this.goalSelector.addGoal(3, new CompanionFollowOwnerGoal(this, 0.6D, 6.0F, 2.0F, false));
        this.goalSelector.addGoal(3, new CompanionRandomStrollGoal(this, 0.43));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new CompanionsHurtTargetGoal(this));
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            if (getEatenAmount() > 0 && this.tickCount >= nextEatenRecover) {
                setEatenAmount(getEatenAmount() - 1);

                // Animation purposes
                setHasBeenEaten(true);
                TickScheduler.scheduleServer(this.level(), () -> this.setHasBeenEaten(false), EATEN_DELAY);

                nextEatenRecover = this.tickCount + this.level().getRandom().nextInt(201) + 100;
            }
        }

    }

    public static AttributeSupplier.Builder setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, CompanionsConfig.CROISSANT_DRAGON_MAX_LIFE)
                .add(Attributes.ATTACK_DAMAGE, 5f)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.55f)
                .add(Attributes.FOLLOW_RANGE, 35.0);
    }

    public void setEatenAmount(int eatenAmount) {
        this.entityData.set(EATEN_AMOUNT, eatenAmount);
    }

    public int getEatenAmount() {
        return this.entityData.get(EATEN_AMOUNT);
    }

    public void setArmorName(String name) {
        this.entityData.set(ARMOR_NAME, name);
    }

    public String getArmorName() {
        return this.entityData.get(ARMOR_NAME);
    }

    @Override
    protected void playStepSound(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        if (getEatenAmount() > 0) {
            playSound(CompanionsSounds.CROISSANT_DRAGON_CREAM_STEPS.get());
        }

    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return CompanionsSounds.CROISSANT_DRAGON_CREAM_DEATH.get();
    }

    @Override
    public void playAmbientSound() {
        playSound(CompanionsSounds.CROISSANT_DRAGON_CREAM_IDLE.get(), 0.4f, 1);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource pDamageSource) {
        return CompanionsSounds.CROISSANT_DRAGON_CREAM_HURT.get();
    }

    public boolean isAttacking() {
        return this.entityData.get(IS_ATTACKING);
    }

    public void setAttacking(boolean attacking) {
        this.entityData.set(IS_ATTACKING, attacking);
    }

    public boolean hasBeenEaten() {
        return this.entityData.get(HAS_BEEN_EATEN);
    }

    public void setHasBeenEaten(boolean eaten) {
        this.entityData.set(HAS_BEEN_EATEN, eaten);
    }

    public int getMilkAmount() {
        return this.entityData.get(MILK_AMOUNT);
    }

    public void setMilkAmount(int milk) {
        this.entityData.set(MILK_AMOUNT, milk);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_ATTACKING, false);
        this.entityData.define(ARMOR_NAME, "default");
        this.entityData.define(EATEN_AMOUNT, 0);
        this.entityData.define(HAS_BEEN_EATEN, false);
        this.entityData.define(MILK_AMOUNT, 0);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("MilkAmount")) {
            setMilkAmount(pCompound.getInt("MilkAmount"));
        }

        if (pCompound.contains("EatenAmount")) {
            setEatenAmount(pCompound.getInt("EatenAmount"));
        }

        if (pCompound.contains("ArmorName")) {
            setArmorName(pCompound.getString("ArmorName"));
        }

    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("MilkAmount", getMilkAmount());
        pCompound.putInt("EatenAmount", getEatenAmount());
        pCompound.putString("ArmorName", getArmorName());
    }

    @Override
    protected boolean canThisCompanionWork() {
        return false;
    }

    @Override
    protected int sitAnimationsAmount() {
        return 2;
    }

    @Override
    protected boolean shouldKeepChunkLoaded() {
        return CompanionsConfig.CROISSANT_DRAGON_KEEP_CHUNK_LOADED;
    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        return super.getOwner();
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (this.isTame() && this.getOwner() == player && player.isShiftKeyDown() && !this.level().isClientSide && hand == InteractionHand.MAIN_HAND && getEatenAmount() < 2 && !hasBeenEaten()) {
            if (level().isClientSide) return InteractionResult.SUCCESS;

            this.playSound(SoundEvents.WOOL_BREAK, 0.5F, 1.0F);

            setEatenAmount(getEatenAmount() + 1);
            setHasBeenEaten(true);
            TickScheduler.scheduleServer(this.level(), () -> this.setHasBeenEaten(false), EATEN_DELAY);

            nextEatenRecover = this.tickCount + this.level().getRandom().nextInt(201) + 100;

            switch (getArmorName()) {
                case "chocolate":
                    player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 200, 0));
                    break;
                case "strawberry":
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 180, 1));
                    break;
                case "vanilla":
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 600, 0));
                    break;
                default:
                    break;
            }

            return InteractionResult.SUCCESS;
        }

        if (!isTame() && itemstack.getItem() == Items.MILK_BUCKET && getMilkAmount() < 3) {
            if (this.level().isClientSide) {
                return InteractionResult.CONSUME;
            } else {
                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                    if (itemstack.isEmpty()) {
                        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BUCKET));
                    } else {
                        if (!player.getInventory().add(new ItemStack(Items.BUCKET))) {
                            player.drop(new ItemStack(Items.BUCKET), false);
                        }
                    }

                }

                if (!this.level().isClientSide) {
                    tameInteraction(player);
                }

                setMilkAmount(getMilkAmount() + 1);

                player.level().playSound(null, this.blockPosition(), SoundEvents.HONEY_DRINK, SoundSource.NEUTRAL, 1.0F, 1.0F);

                return InteractionResult.SUCCESS;
            }
        } else if (!isTame()) {
            player.displayClientMessage(
                    Component.translatable("croissant_dragon.companions.client_message.requires_milk"), true);

            player.level().playSound(null, this.blockPosition(), SoundEvents.VILLAGER_NO, SoundSource.NEUTRAL, 1.0F, 1.0F);

            return InteractionResult.PASS;
        }

        if (isTame() && !this.level().isClientSide && hand == InteractionHand.MAIN_HAND && getOwner() == player) {
            if (getArmorName().equals("default")) {
                if (level().isClientSide) return InteractionResult.SUCCESS;

                if (itemstack.getItem() == CompanionsItems.CROISSANT_DRAGON_ARMOR_STRAWBERRY.get()) {
                    this.setArmorName("strawberry");

                    if (!player.getAbilities().instabuild) itemstack.shrink(1);

                    return InteractionResult.SUCCESS;
                } else if (itemstack.getItem() == CompanionsItems.CROISSANT_DRAGON_ARMOR_VANILLA.get()) {
                    this.setArmorName("vanilla");

                    if (!player.getAbilities().instabuild) itemstack.shrink(1);

                    return InteractionResult.SUCCESS;
                } else if (itemstack.getItem() == CompanionsItems.CROISSANT_DRAGON_ARMOR_CHOCOLATE.get()) {
                    this.setArmorName("chocolate");

                    if (!player.getAbilities().instabuild) itemstack.shrink(1);

                    return InteractionResult.SUCCESS;
                }

                this.playSound(SoundEvents.WOOL_BREAK, 0.5F, 1.0F);
            }
        }

        if (handleDefaultMainActionAndHeal(player, hand)) {
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 2, this::predicate));
        controllerRegistrar.add(new AnimationController<>(this, "eatencontroller", 2, this::eatenPredicate));
    }

    private <T extends GeoAnimatable> PlayState eatenPredicate(AnimationState<T> event) {
        if (hasBeenEaten() && event.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            event.getController().forceAnimationReset();
            event.setAnimation(EATEN);
        }

        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {

        if (getMainAction() == 0) {
            event.getController().setAnimation(getSitVariation() == 0 ? SIT : SIT2);
        } else if (isAttacking()) {
            event.getController().setAnimation(ATTACK);
        } else if (event.isMoving()) {
            event.getController().setAnimation(getEatenAmount() == 0 ? FLY : WALK);
        } else {
            event.getController().setAnimation(IDLE);
        }

        return PlayState.CONTINUE;
    }


    @Override
    public void aiStep() {
        setNoMovement(isAttacking());
        super.aiStep();
    }

}