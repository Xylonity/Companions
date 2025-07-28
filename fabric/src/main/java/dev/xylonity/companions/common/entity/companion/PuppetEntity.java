package dev.xylonity.companions.common.entity.companion;

import dev.xylonity.companions.common.ai.navigator.GroundNavigator;
import dev.xylonity.companions.common.container.PuppetContainerMenu;
import dev.xylonity.companions.common.entity.CompanionEntity;
import dev.xylonity.companions.common.entity.ai.generic.CompanionsHurtTargetGoal;
import dev.xylonity.companions.common.entity.ai.puppet.goal.*;
import dev.xylonity.companions.common.entity.projectile.MagicRayCircleProjectile;
import dev.xylonity.companions.common.entity.projectile.MagicRayPieceProjectile;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsItems;
import dev.xylonity.companions.registry.CompanionsParticles;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.Optional;

public class PuppetEntity extends CompanionEntity implements RangedAttackMob, ContainerListener {
    public SimpleContainer inventory;

    private final RawAnimation SIT = RawAnimation.begin().thenPlay("sit");
    private final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private final RawAnimation ATTACK_L = RawAnimation.begin().thenPlay("attack_l");
    private final RawAnimation ATTACK_R = RawAnimation.begin().thenPlay("attack_r");

    private static final EntityDataAccessor<Byte> DATA_ID_FLAGS = SynchedEntityData.defineId(PuppetEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<String> ATTACK_ANIMATION_NAME = SynchedEntityData.defineId(PuppetEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> IS_ATTACKING_RIGHT = SynchedEntityData.defineId(PuppetEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_ATTACKING_LEFT = SynchedEntityData.defineId(PuppetEntity.class, EntityDataSerializers.BOOLEAN);
    // 0 none, 1 left, 2 right, 3 both
    private static final EntityDataAccessor<Integer> ACTIVE_ARMS = SynchedEntityData.defineId(PuppetEntity.class, EntityDataSerializers.INT);
    // none,none -> left/right
    private static final EntityDataAccessor<String> ARM_NAMES = SynchedEntityData.defineId(PuppetEntity.class, EntityDataSerializers.STRING);

    private final ItemStack[] lastStacks = new ItemStack[] { ItemStack.EMPTY.copy(), ItemStack.EMPTY.copy() };

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
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));

        this.goalSelector.addGoal(2, new PuppetRightCannonAttackGoal(this, 10, 50));
        this.goalSelector.addGoal(2, new PuppetRightBladeAttackGoal(this, 10, 50));
        this.goalSelector.addGoal(2, new PuppetRightMutantAttackGoal(this, 10, 50));
        this.goalSelector.addGoal(2, new PuppetRightWhipAttackGoal(this, 10, 50));
        this.goalSelector.addGoal(2, new PuppetLeftCannonAttackGoal(this, 10, 50));
        this.goalSelector.addGoal(2, new PuppetLeftBladeAttackGoal(this, 10, 50));
        this.goalSelector.addGoal(2, new PuppetLeftMutantAttackGoal(this, 10, 50));
        this.goalSelector.addGoal(2, new PuppetLeftWhipAttackGoal(this, 10, 50));

        this.goalSelector.addGoal(3, new PuppetApproachTargetGoal(this, 0.5, 0.4f, 1.25f));

        this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 0.6D, 6.0F, 2.0F, false));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new CompanionsHurtTargetGoal(this));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof Player player) {
            Optional<Vec3> h = getBoundingBox().clip(player.getEyePosition(), player.getEyePosition(1f).add(player.getLookAngle().scale(5)));
            if (h.isPresent()) {
                if (h.get().y - this.getBoundingBox().minY < 2) {
                    return false;
                }
            }
        }

        boolean ret = super.hurt(source, amount);

        if (!level().isClientSide && amount > 4.0f && hasArm(CompanionsItems.MUTANT_ARM.get()) && getTarget() != null) {
            spawnMutantParticles(15);
            tpOppositeSide();
            spawnMutantParticles(15);
        }

        return ret;
    }

    private void spawnMutantParticles(int amount) {
        for (int i = 0; i < amount; i++) {
            double dx = (this.random.nextDouble() - 0.5) * 2.0;
            double dy = (this.random.nextDouble() - 0.5) * 2.0;
            double dz = (this.random.nextDouble() - 0.5) * 2.0;
            if (this.level() instanceof ServerLevel level) {
                level.sendParticles(ParticleTypes.POOF, this.getX(), this.getY() + 1, this.getZ(), 1, dx, dy, dz, 0.1);
                if (i % 5 == 0) level.sendParticles(CompanionsParticles.TEDDY_TRANSFORMATION.get(), this.getX(), this.getY() + 1, this.getZ(), 1, dx, dy, dz, 0.2);
            }
        }

    }

    private void tpOppositeSide() {
        LivingEntity target = getTarget();
        if (target == null) return;

        Vec3 pos = target.position();
        Vec3 newPos = pos.subtract(position().x - pos.x, 0, position().z - pos.z);

        if (isValidTeleportPos(newPos)) {
            setPos(newPos.x, position().y, newPos.z);
        }
    }

    private boolean isValidTeleportPos(Vec3 newPos) {
        BlockPos feet = new BlockPos((int) newPos.x, (int) newPos.y, (int) newPos.z);
        BlockPos head = new BlockPos((int) newPos.x, (int) newPos.y + 1, (int) newPos.z);
        BlockPos floor = new BlockPos((int) newPos.x, (int) newPos.y - 1, (int) newPos.z);

        boolean f1 = level().getBlockState(feet).isAir() && level().getBlockState(head).isAir();
        boolean f2  = level().getBlockState(floor).isSolidRender(level(), floor);
        return f1 && f2;
    }

    public boolean hasArm(Item armItem) {
        for (int i = 0; i < this.inventory.getContainerSize(); i++) {
            ItemStack stack = this.inventory.getItem(i);
            if (!stack.isEmpty() && stack.getItem() == armItem) {
                return true;
            }
        }

        return false;
    }

    public static AttributeSupplier.Builder setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, CompanionsConfig.PUPPET_MAX_LIFE)
                .add(Attributes.ATTACK_DAMAGE, CompanionsConfig.PUPPET_DAMAGE)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.55f)
                .add(Attributes.FOLLOW_RANGE, 35.0);
    }

    public void setActiveArms(int candleCount) {
        this.entityData.set(ACTIVE_ARMS, candleCount);
    }

    public int getActiveArms() {
        return this.entityData.get(ACTIVE_ARMS);
    }

    public boolean isAttackingRight() {
        return this.entityData.get(IS_ATTACKING_RIGHT);
    }

    public void setAttackingRight(boolean attacking) {
        this.entityData.set(IS_ATTACKING_RIGHT, attacking);
    }

    public boolean isAttackingLeft() {
        return this.entityData.get(IS_ATTACKING_LEFT);
    }

    public void setAttackingLeft(boolean attacking) {
        this.entityData.set(IS_ATTACKING_LEFT, attacking);
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
        this.entityData.define(IS_ATTACKING_RIGHT, false);
        this.entityData.define(IS_ATTACKING_LEFT, false);
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

    @Nullable
    @Override
    public LivingEntity getOwner() {
        return super.getOwner();
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (this.isTame() && this.getOwner() == player && player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND) {
            if (!level().isClientSide) {
                player.openMenu(new ExtendedScreenHandlerFactory() {
                    @Override
                    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                        return new PuppetContainerMenu(i, inventory, PuppetEntity.this);
                    }

                    @Override
                    public Component getDisplayName() {
                        return PuppetEntity.this.getName();
                    }

                    @Override
                    public void writeScreenOpeningData(ServerPlayer serverPlayer, FriendlyByteBuf buf) {
                        buf.writeInt(PuppetEntity.this.getId());
                    }
                });

                this.playSound(SoundEvents.ARMOR_EQUIP_LEATHER, 0.5F, 1.0F);
            }

            return InteractionResult.SUCCESS;
        }

        if (itemstack.getItem() == Items.APPLE && !isTame()) {
            if (this.level().isClientSide) {
                return InteractionResult.CONSUME;
            } else {
                if (!player.getAbilities().instabuild) itemstack.shrink(1);

                if (!this.level().isClientSide) {
                    tameInteraction(player);
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
                defaultMainActionInteraction(player);
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.inventory.fromTag(pCompound.getList("Inventory", 10));
        this.updateContainerEquipment();
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.put("Inventory", this.inventory.createTag());
    }

    @Override
    protected boolean canThisCompanionWork() {
        return true;
    }

    @Override
    protected int sitAnimationsAmount() {
        return 1;
    }

    @Override
    protected boolean shouldKeepChunkLoaded() {
        return CompanionsConfig.PUPPET_KEEP_CHUNK_LOADED;
    }

    @Override
    public void performRangedAttack(@NotNull LivingEntity target, float v) {

    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource pDamageSource) {
        return CompanionsSounds.PUPPET_HURT.get();
    }

    @Override
    protected void playStepSound(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        playSound(CompanionsSounds.PUPPET_WALK.get(), 0.45f, 1f);
    }

    @Override
    public void containerChanged(@NotNull Container container) {
        for (int i = 0; i < this.inventory.getContainerSize(); i++) {
            ItemStack now = this.inventory.getItem(i);
            ItemStack old = lastStacks[i];

            if (!ItemStack.matches(old, now)) {
                lastStacks[i] = now.copy();

                if (!now.isEmpty()) {
                    Item item = now.getItem();
                    if (item == CompanionsItems.CANNON_ARM.get()) {
                        playSound(CompanionsSounds.PUPPET_EQUIP_CANON.get(), 0.75f, 1f);
                    } else if (item == CompanionsItems.MUTANT_ARM.get()) {
                        playSound(CompanionsSounds.PUPPET_EQUIP_MUTANT.get(), 0.75f, 1f);
                    } else if (item == CompanionsItems.WHIP_ARM.get()) {
                        playSound(CompanionsSounds.PUPPET_EQUIP_WHIP.get(), 0.75f, 1f);
                    } else if (item == CompanionsItems.BLADE_ARM.get()) {
                        playSound(CompanionsSounds.PUPPET_EQUIP_BLADE.get(), 0.75f, 1f);
                    }
                }
            }

        }

        updateContainerEquipment();
    }

    protected void updateContainerEquipment() {
        if (!this.level().isClientSide) {
            this.setFlag(!this.inventory.getItem(0).isEmpty());

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

    protected void setFlag(boolean b) {
        byte $$2 = this.entityData.get(DATA_ID_FLAGS);
        if (b) {
            this.entityData.set(DATA_ID_FLAGS, (byte)($$2 | 4));
        } else {
            this.entityData.set(DATA_ID_FLAGS, (byte)($$2 & ~4));
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

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "rightAttackcontroller", 1, this::rightAttackPredicate));
        controllerRegistrar.add(new AnimationController<>(this, "leftAttackcontroller", 1, this::leftAttackPredicate));
        controllerRegistrar.add(new AnimationController<>(this, "controller", 1, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState rightAttackPredicate(AnimationState<T> event) {
        if (event.getController().getAnimationState().equals(AnimationController.State.STOPPED) && isAttackingRight()) {
            event.getController().forceAnimationReset();
            event.setAnimation(ATTACK_R);
        }

        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState leftAttackPredicate(AnimationState<T> event) {
        if (event.getController().getAnimationState().equals(AnimationController.State.STOPPED) && isAttackingLeft()) {
            event.getController().forceAnimationReset();
            event.setAnimation(ATTACK_L);
        }

        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        if (this.getMainAction() == 0) {
            event.setAnimation(SIT);
        } else if (event.isMoving()) {
            event.setAnimation(WALK);
        } else {
            event.setAnimation(IDLE);
        }

        return PlayState.CONTINUE;
    }

}