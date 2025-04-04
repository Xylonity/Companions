package dev.xylonity.companions.common.entity.custom;

import dev.xylonity.companions.common.ai.navigator.GroundNavigator;
import dev.xylonity.companions.common.container.PuppetContainerMenu;
import dev.xylonity.companions.common.container.SoulMageContainerMenu;
import dev.xylonity.companions.common.entity.CompanionEntity;
import dev.xylonity.companions.common.entity.ai.puppet.goal.PuppetCannonAttackGoal;
import dev.xylonity.companions.common.entity.ai.soul_mage.goal.*;
import dev.xylonity.companions.common.entity.projectile.MagicRayCircleProjectile;
import dev.xylonity.companions.common.entity.projectile.MagicRayPieceProjectile;
import dev.xylonity.companions.common.entity.projectile.SoulMageBookEntity;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsItems;
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
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
import java.util.Optional;

public class PuppetEntity extends CompanionEntity implements RangedAttackMob, ContainerListener {
    public SimpleContainer inventory;

    private final RawAnimation SIT = RawAnimation.begin().thenPlay("sit");
    private final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private final RawAnimation ATTACK_L = RawAnimation.begin().thenPlay("attack_l");
    private final RawAnimation ATTACK_R = RawAnimation.begin().thenPlay("attack_r");

    private static final EntityDataAccessor<Integer> SIT_VARIATION = SynchedEntityData.defineId(PuppetEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Byte> DATA_ID_FLAGS = SynchedEntityData.defineId(PuppetEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<String> ATTACK_ANIMATION_NAME = SynchedEntityData.defineId(PuppetEntity.class, EntityDataSerializers.STRING);
    // 0 no, 1 left, 2 right
    private static final EntityDataAccessor<Integer> IS_ATTACKING = SynchedEntityData.defineId(PuppetEntity.class, EntityDataSerializers.INT);
    // 0 none, 1 left, 2 right, 3 both
    private static final EntityDataAccessor<Integer> ACTIVE_ARMS = SynchedEntityData.defineId(PuppetEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> ARM_NAMES = SynchedEntityData.defineId(PuppetEntity.class, EntityDataSerializers.STRING);

    public PuppetEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.createInventory();
    }

    private static final EntityDataAccessor<String> CURRENT_ATTACK_TYPE = SynchedEntityData.defineId(PuppetEntity.class, EntityDataSerializers.STRING);

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
        return 2;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));

        this.goalSelector.addGoal(1, new PuppetCannonAttackGoal(this, 30, 50));

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

    public void setActiveArms(int candleCount) {
        this.entityData.set(ACTIVE_ARMS, candleCount);
    }

    public int getActiveArms() {
        return this.entityData.get(ACTIVE_ARMS);
    }

    public String getAttackAnimationName() {
        return this.entityData.get(ATTACK_ANIMATION_NAME);
    }

    public void setAttackAnimationName(String s) {
        this.entityData.set(ATTACK_ANIMATION_NAME, s);
    }

    public int isAttacking() {
        return this.entityData.get(IS_ATTACKING);
    }

    public void setAttacking(int attacking) {
        this.entityData.set(IS_ATTACKING, attacking);
    }

    public void setArmNames(String armNames) {
        this.entityData.set(ARM_NAMES, armNames);
    }

    public String getArmNames() {
        return this.entityData.get(ARM_NAMES);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SIT_VARIATION, 0);
        this.entityData.define(IS_ATTACKING, 0);
        this.entityData.define(DATA_ID_FLAGS, (byte)0);
        this.entityData.define(ATTACK_ANIMATION_NAME, "");
        this.entityData.define(ACTIVE_ARMS, 0);
        this.entityData.define(CURRENT_ATTACK_TYPE, "NONE");
        this.entityData.define(ARM_NAMES, "none,none");
    }

    //@Override
    //public @NotNull InteractionResult interactAt(Player pPlayer, Vec3 pVec, InteractionHand pHand) {
    //    if (pVec.y - this.getBoundingBox().minY < 2) {
    //        return InteractionResult.FAIL;
    //    }

    //    return super.interactAt(pPlayer, pVec, pHand);
    //}

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (pSource.getEntity() instanceof Player player) {
            Optional<Vec3> h = getBoundingBox().clip(player.getEyePosition(), player.getEyePosition(1f).add(player.getLookAngle().scale(5)));
            if (h.isPresent()) {
                if (h.get().y - this.getBoundingBox().minY < 2) {
                    return false;
                }
            }
        }

        return super.hurt(pSource, pAmount);
    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        return super.getOwner();
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (this.isTame() && this.getOwner() == player && player.isShiftKeyDown() && !this.level().isClientSide && hand == InteractionHand.MAIN_HAND) {
            NetworkHooks.openScreen(
                    (ServerPlayer) player, new MenuProvider() {
                        @Override
                        public @NotNull Component getDisplayName() {
                            return PuppetEntity.this.getName();
                        }

                        @Override
                        public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInv, @NotNull Player player) {
                            return new PuppetContainerMenu(id, playerInv, PuppetEntity.this);
                        }
                    },
                    buf -> buf.writeInt(this.getId())
            );
            this.playSound(SoundEvents.ARMOR_EQUIP_LEATHER, 0.5F, 1.0F);
            return InteractionResult.SUCCESS;
        }

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
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.inventory.fromTag(pCompound.getList("Inventory", 10));
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.put("Inventory", this.inventory.createTag());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 1, this::predicate));
        controllerRegistrar.add(new AnimationController<>(this, "attackcontroller", 1, this::attackPredicate));
    }

    private <T extends GeoAnimatable> PlayState attackPredicate(AnimationState<T> event) {
        if (event.getController().getAnimationState().equals(AnimationController.State.STOPPED) && isAttacking() != 0) {
            event.getController().forceAnimationReset();
            event.setAnimation(ATTACK_R);
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
        setNoMovement(isAttacking() == 1 || isAttacking() == 2);
        super.aiStep();
    }

    @Override
    public void performRangedAttack(@NotNull LivingEntity target, float v) {
        if (!level().isClientSide) {
            double maxDistance = 30.0D;
            Vec3 startPos = this.getEyePosition(1.0F);
            Vec3 targetPos = target.getEyePosition(1.0F);
            Vec3 direction = targetPos.subtract(startPos).normalize();

            double offset = 1.0D;
            Vec3 spawnPos = startPos.add(direction.scale(offset));

            double traveled  = 0.0;
            double increment = 1.0D;
            int maxSteps = (int)(maxDistance / increment);

            for (int i = 0; i < maxSteps; i++) {
                Vec3 piecePos = spawnPos.add(direction.scale(traveled));
                traveled += increment;

                BlockPos blockPos = BlockPos.containing(piecePos);
                if (!isPassableBlock(level(), blockPos)) {
                    spawnRayPiece(level(), this, piecePos, direction, (i == 0));
                    break;
                }

                spawnRayPiece(level(), this, piecePos, direction, (i == 0));
            }
        }
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

            int arms = 0;
            String leftArm = "none";
            String rightArm = "none";

            if (!inventory.getItem(0).isEmpty()) {
                arms |= 2;
                Item item = inventory.getItem(0).getItem();
                if (item == CompanionsItems.CANNON_ARM.get()) {
                    rightArm = "cannon";
                } else if (item == CompanionsItems.MUTANT_ARM.get()) {
                    rightArm = "mutant";
                } else if (item == CompanionsItems.WHIP_ARM.get()) {
                    rightArm = "whip";
                } else if (item == CompanionsItems.BLADE_ARM.get()) {
                    rightArm = "blade";
                }
            }

            if (!inventory.getItem(1).isEmpty()) {
                arms |= 1;
                Item item = inventory.getItem(1).getItem();
                if (item == CompanionsItems.CANNON_ARM.get()) {
                    leftArm = "cannon";
                } else if (item == CompanionsItems.MUTANT_ARM.get()) {
                    leftArm = "mutant";
                } else if (item == CompanionsItems.WHIP_ARM.get()) {
                    leftArm = "whip";
                } else if (item == CompanionsItems.BLADE_ARM.get()) {
                    leftArm = "blade";
                }
            }

            this.setActiveArms(arms);
            this.setArmNames(leftArm + "," + rightArm);
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

}