package dev.xylonity.companions.common.entity.custom;

import dev.xylonity.companions.common.ai.navigator.GroundNavigator;
import dev.xylonity.companions.common.container.SoulMageContainerMenu;
import dev.xylonity.companions.common.entity.CompanionEntity;
import dev.xylonity.companions.common.entity.ai.froggy.goal.FroggyFireworkToadGoal;
import dev.xylonity.companions.common.entity.ai.soul_mage.goal.*;
import dev.xylonity.companions.common.entity.projectile.MagicRayCircleProjectile;
import dev.xylonity.companions.common.entity.projectile.MagicRayPieceProjectile;
import dev.xylonity.companions.common.entity.projectile.SoulMageBookEntity;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FroggyEntity extends CompanionEntity implements ContainerListener {
    public SimpleContainer inventory;

    private final RawAnimation SIT = RawAnimation.begin().thenPlay("sit");
    private final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");

    private static final EntityDataAccessor<Integer> SIT_VARIATION = SynchedEntityData.defineId(FroggyEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_ATTACKING = SynchedEntityData.defineId(FroggyEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Byte> DATA_ID_FLAGS = SynchedEntityData.defineId(FroggyEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<String> ATTACK_ANIMATION_NAME = SynchedEntityData.defineId(FroggyEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> CURRENT_ATTACK_TYPE = SynchedEntityData.defineId(FroggyEntity.class, EntityDataSerializers.STRING);

    public FroggyEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.createInventory();
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        return new GroundNavigator(this, pLevel);
    }

    protected void createInventory() {
        SimpleContainer oldInv = this.inventory;
        this.inventory = new SimpleContainer(getInventorySize());

        if (oldInv != null) {
            int min = Math.min(oldInv.getContainerSize(), this.inventory.getContainerSize());
            for(int i = 0; i < min; i++){
                ItemStack stack = oldInv.getItem(i);
                this.inventory.setItem(i, stack.copy());
            }
        }

        this.inventory.addListener(this);
        this.updateContainerEquipment();
    }

    protected int getInventorySize() {
        return 3;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));

        this.goalSelector.addGoal(3, new FroggyFireworkToadGoal(this, 80, 120));

        this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 0.6D, 6.0F, 2.0F, false));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new SoulMageOwnerHurtTargetGoal(this));
    }

    public String getCurrentAttackType() {
        return this.entityData.get(CURRENT_ATTACK_TYPE);
    }

    public void setCurrentAttackType(String attackType) {
        this.entityData.set(CURRENT_ATTACK_TYPE, attackType);
    }

    public static AttributeSupplier setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20D)
                .add(Attributes.ATTACK_DAMAGE, 5f)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.55f)
                .add(Attributes.FOLLOW_RANGE, 35.0).build();
    }

    private void setSitVariation(int variation) {
        this.entityData.set(SIT_VARIATION, variation);
    }

    private int getSitVariation() {
        return this.entityData.get(SIT_VARIATION);
    }

    public String getAttackAnimationName() {
        return this.entityData.get(ATTACK_ANIMATION_NAME);
    }

    public void setAttackAnimationName(String s) {
        this.entityData.set(ATTACK_ANIMATION_NAME, s);
    }

    public boolean isAttacking() {
        return this.entityData.get(IS_ATTACKING);
    }

    public void setAttacking(boolean attacking) {
        this.entityData.set(IS_ATTACKING, attacking);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SIT_VARIATION, 0);
        this.entityData.define(IS_ATTACKING, false);
        this.entityData.define(DATA_ID_FLAGS, (byte)0);
        this.entityData.define(ATTACK_ANIMATION_NAME, "");
        this.entityData.define(CURRENT_ATTACK_TYPE, "NONE");
    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        return super.getOwner();
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (itemstack.getItem() == Items.APPLE && !isTame()) {
            if (this.level().isClientSide) {
                return InteractionResult.CONSUME;
            } else {
                if (!player.getAbilities().instabuild) itemstack.shrink(1);

                if (!ForgeEventFactory.onAnimalTame(this, player)) {
                    if (!this.level().isClientSide) {
                        super.tame(player);
                        this.navigation.recomputePath();
                        this.setTarget(null);
                        this.level().broadcastEntityEvent(this, (byte) 7);
                        setSitting(true);
                    }
                }
                setSitVariation(getRandom().nextInt(0, 3));
                return InteractionResult.SUCCESS;
            }
        }

        if (isTame() && !this.level().isClientSide && hand == InteractionHand.MAIN_HAND && getOwner() == player) {
            if (itemstack.getItem() == Items.APPLE && this.getHealth() < this.getMaxHealth()) {
                this.heal(16.0F);
                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
            } else {
                setSitting(!isSitting());
                setSitVariation(getRandom().nextInt(0, 3));
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 1, this::predicate));
        controllerRegistrar.add(new AnimationController<>(this, "attackcontroller", 1, this::attackPredicate));
    }

    private <T extends GeoAnimatable> PlayState attackPredicate(AnimationState<T> event) {
        if (isAttacking() && event.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            event.getController().forceAnimationReset();
            event.setAnimation(RawAnimation.begin().thenPlay(getAttackAnimationName()));
        }

        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        if (this.isSitting()) {
            event.setAnimation(SIT);
        } else if (event.isMoving()) {
            event.setAnimation(WALK);
        } else {
            event.setAnimation(IDLE);
        }

        return PlayState.CONTINUE;
    }

    @Override
    public void aiStep() {
        setNoMovement(isAttacking());
        super.aiStep();
    }

    private boolean isPassableBlock(Level level, BlockPos pos) {
        return level.getBlockState(pos).getCollisionShape(level, pos).isEmpty();
    }

    private void spawnRayPiece(Level pLevel, LivingEntity caster, Vec3 piecePos, Vec3 direction, boolean isFirstPiece) {
        if (isFirstPiece) {
            MagicRayCircleProjectile circle = CompanionsEntities.MAGIC_RAY_PIECE_CIRCLE_PROJECTILE.get().create(pLevel);
            if (circle != null) {
                circle.setPos(piecePos.x, piecePos.y, piecePos.z);
                setProjectileRotation(circle, direction);
                pLevel.addFreshEntity(circle);
            }
        } else {
            MagicRayPieceProjectile rayPiece = CompanionsEntities.MAGIC_RAY_PIECE_PROJECTILE.get().create(pLevel);
            if (rayPiece != null) {
                rayPiece.setPos(piecePos.x, piecePos.y, piecePos.z);
                setProjectileRotation(rayPiece, direction);
                pLevel.addFreshEntity(rayPiece);
            }
        }
    }

    private void setProjectileRotation(MagicRayPieceProjectile projectile, Vec3 direction) {
        Vec3 dir = direction.normalize();
        float yaw = (float) (Math.atan2(dir.z, dir.x) * (180.0F / Math.PI)) - 90.0F;
        float pitch = (float) (-(Math.atan2(dir.y, Math.sqrt(dir.x * dir.x + dir.z * dir.z))) * (180.0F / Math.PI));

        projectile.setPitch(pitch);
        projectile.setYaw(yaw);
    }

    @Override
    public void containerChanged(@NotNull Container container) {
        this.updateContainerEquipment();
    }

    protected void updateContainerEquipment() {
        if (!this.level().isClientSide) {
            this.setFlag(4, !this.inventory.getItem(0).isEmpty());
        }
    }

    protected void setFlag(int $$0, boolean $$1) {
        byte $$2 = this.entityData.get(DATA_ID_FLAGS);
        if ($$1) {
            this.entityData.set(DATA_ID_FLAGS, (byte)($$2 | $$0));
        } else {
            this.entityData.set(DATA_ID_FLAGS, (byte)($$2 & ~$$0));
        }

    }

    protected void dropEquipment() {
        super.dropEquipment();
        if (this.inventory != null) {
            for(int i = 0; i < this.inventory.getContainerSize(); i++) {
                ItemStack itemStack = this.inventory.getItem(i);
                if (!itemStack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemStack)) {
                    this.spawnAtLocation(itemStack);
                }
            }

        }
    }

}