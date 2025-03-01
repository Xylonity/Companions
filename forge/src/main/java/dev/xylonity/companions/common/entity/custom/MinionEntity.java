package dev.xylonity.companions.common.entity.custom;

import dev.xylonity.companions.common.ai.navigator.GroundNavigator;
import dev.xylonity.companions.common.entity.CompanionEntity;
import net.minecraft.client.gui.font.providers.UnihexProvider;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MinionEntity extends CompanionEntity implements GeoEntity {

    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private final RawAnimation SIT = RawAnimation.begin().thenPlay("sit");
    private final RawAnimation FLY = RawAnimation.begin().thenPlay("fly");
    private final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private final RawAnimation SPELL = RawAnimation.begin().thenPlay("spell");
    private final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");
    private final RawAnimation THROW = RawAnimation.begin().thenPlay("throw");

    private static final EntityDataAccessor<String> VARIANT = SynchedEntityData.defineId(MinionEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> IS_LOCKED = SynchedEntityData.defineId(MinionEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_FLYING = SynchedEntityData.defineId(MinionEntity.class, EntityDataSerializers.BOOLEAN);

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
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 0.4D, 6.0F, 2.0F, false));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 10.0F));
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
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item itemForTaming = Items.APPLE;
        Item item = itemstack.getItem();

        if (item == itemForTaming && !isTame()) {
            if (this.level().isClientSide) {
                return InteractionResult.CONSUME;
            } else {
                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }

                if (!ForgeEventFactory.onAnimalTame(this, player)) {
                    if (!this.level().isClientSide) {
                        super.tame(player);
                        this.navigation.recomputePath();
                        this.setTarget(null);
                        this.level().broadcastEntityEvent(this, (byte)7);
                        setSitting(true);
                    }
                }

                return InteractionResult.SUCCESS;
            }
        }

        if (isTame() && !this.level().isClientSide && hand == InteractionHand.MAIN_HAND && getOwner() == player) {
            if ((itemstack.getItem().equals(Items.APPLE) || itemstack.getItem().equals(Items.APPLE))
                    && this.getHealth() < this.getMaxHealth()) {

                if (itemstack.getItem().equals(Items.APPLE)) {
                    this.heal(16.0F);
                } else if (itemstack.getItem().equals(Items.APPLE)) {
                    this.heal(4.0F);
                }

                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }

            } else {
                setSitting(!isSitting());
            }

            return InteractionResult.SUCCESS;
        }

        if (itemstack.getItem() == itemForTaming) {
            return InteractionResult.PASS;
        }

        return super.mobInteract(player, hand);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VARIANT, Variant.NETHER.getName());
        this.entityData.define(IS_LOCKED, false);
        this.entityData.define(IS_FLYING, false);
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

    @Override
    public @NotNull Component getName() {
        if (this.level().dimension().equals(Level.NETHER)) {
            return Component.literal("Imp");
        } else if (this.level().dimension().equals(Level.END)) {
            return Component.literal("Gargoyle");
        } else {
            return Component.literal("Minion");
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 1, this::predicate));
        //controllerRegistrar.add(new AnimationController<>(this, "attackcontroller", 1, this::attackPredicate));
    }

    //private <T extends GeoAnimatable> PlayState attackPredicate(AnimationState<T> event) {

    //    if (this.swinging && event.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
    //        event.getController().forceAnimationReset();
    //        event.getController().setAnimation(RawAnimation.begin().then("attack", Animation.LoopType.PLAY_ONCE));
    //        this.swinging = false;
    //    }

    //    return PlayState.CONTINUE;
    //}

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {

        if (getVariant().equals(Variant.OVERWORLD.getName())) {
            event.getController().setAnimation(FLY);
        } else if (getVariant().equals(Variant.END.getName())) {

        } else {

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