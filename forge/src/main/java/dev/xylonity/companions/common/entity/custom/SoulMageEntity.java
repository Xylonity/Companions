package dev.xylonity.companions.common.entity.custom;

import dev.xylonity.companions.common.ai.navigator.GroundNavigator;
import dev.xylonity.companions.common.entity.CompanionEntity;
import dev.xylonity.companions.common.entity.ai.soul_mage.goal.*;
import dev.xylonity.companions.common.container.SoulMageContainerMenu;
import dev.xylonity.companions.common.entity.projectile.*;
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
import net.minecraft.world.entity.*;
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

import java.util.*;

public class SoulMageEntity extends CompanionEntity implements RangedAttackMob, ContainerListener {
    public SimpleContainer inventory;

    private final RawAnimation SIT = RawAnimation.begin().thenPlay("sit");
    private final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");

    private static final EntityDataAccessor<Integer> SIT_VARIATION = SynchedEntityData.defineId(SoulMageEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_ATTACKING = SynchedEntityData.defineId(SoulMageEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Byte> DATA_ID_FLAGS = SynchedEntityData.defineId(SoulMageEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<String> ATTACK_ANIMATION_NAME = SynchedEntityData.defineId(SoulMageEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> CANDLE_COUNT = SynchedEntityData.defineId(SoulMageEntity.class, EntityDataSerializers.INT);

    public static final int MAX_CANDLES_COUNT = 6;
    public List<LivingCandleEntity> candles = new LinkedList<>();

    public SoulMageEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.createInventory();
    }

    private SoulMageBookEntity book;

    private static final EntityDataAccessor<Integer> BOOK_ID = SynchedEntityData.defineId(SoulMageEntity.class, EntityDataSerializers.INT);

    public static final Map<String, int[]> ATTACK_COLORS = Map.of(
            "MAGIC_RAY", new int[]{173, 216, 230},
            "BLACK_HOLE", new int[]{128, 0, 128},
            "STONE_SPIKES", new int[]{139, 69, 19},
            "HEAL_RING", new int[]{110, 252, 85},
            "ICE_SHARD", new int[]{134, 236, 255},
            "NONE", new int[]{169, 134, 60}
    );

    private static final EntityDataAccessor<String> CURRENT_ATTACK_TYPE = SynchedEntityData.defineId(SoulMageEntity.class, EntityDataSerializers.STRING);

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

        this.goalSelector.addGoal(3, new SoulMageMagicRayGoal(this, 80, 120));
        this.goalSelector.addGoal(3, new SoulMageBlackHoleGoal(this, 100, 200));
        this.goalSelector.addGoal(3, new SoulMageStoneSpikesGoal(this, 120, 240));
        this.goalSelector.addGoal(3, new SoulMageHealRingGoal(this, 100, 160));
        this.goalSelector.addGoal(3, new SoulMageIceShardGoal(this, 100, 160));

        this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 0.6D, 6.0F, 2.0F, false));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new SoulMageOwnerHurtTargetGoal(this));
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (pAmount > this.getHealth()) {
            if (!candles.isEmpty()) {
                LivingCandleEntity candle = candles.remove(0);
                setCandleCount(getCandleCount() - 1);
                candle.doKill();
                return false;
            }
        }

        return super.hurt(pSource, pAmount);
    }

    public SoulMageBookEntity getBook() {
        if (book == null || book.isRemoved()) {
            if (level().isClientSide) {
                int id = this.entityData.get(BOOK_ID);
                if (id != -1) {
                    Entity entity = level().getEntity(id);
                    if (entity instanceof SoulMageBookEntity) {
                        book = (SoulMageBookEntity) entity;
                    }
                }
            }
        }
        return book;
    }

    public void setBook(SoulMageBookEntity book) {
        this.book = book;
        if (book != null) {
            this.entityData.set(BOOK_ID, book.getId());
        } else {
            this.entityData.set(BOOK_ID, -1);
        }
    }

    public String getCurrentAttackType() {
        return this.entityData.get(CURRENT_ATTACK_TYPE);
    }

    public void setCurrentAttackType(String attackType) {
        this.entityData.set(CURRENT_ATTACK_TYPE, attackType);
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCount == 2) {
            if (getBook() == null) {
                Projectile bookEntity = CompanionsEntities.SOUL_MAGE_BOOK.get().create(this.level());
                if (bookEntity instanceof SoulMageBookEntity) {
                    SoulMageBookEntity book = (SoulMageBookEntity) bookEntity;
                    book.setOwner(this);
                    book.moveTo(this.getX(), this.getY(), this.getZ());
                    this.level().addFreshEntity(book);
                    setBook(book);
                }
            }
        }
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

    public void setCandleCount(int candleCount) {
        this.entityData.set(CANDLE_COUNT, candleCount);
    }

    public int getCandleCount() {
        return this.entityData.get(CANDLE_COUNT);
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
        this.entityData.define(CANDLE_COUNT, 0);
        this.entityData.define(BOOK_ID, -1);
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

        if (this.isTame() && this.getOwner() == player && player.isShiftKeyDown() && !this.level().isClientSide && hand == InteractionHand.MAIN_HAND) {
            NetworkHooks.openScreen(
                    (ServerPlayer) player, new MenuProvider() {
                        @Override
                        public @NotNull Component getDisplayName() {
                            return SoulMageEntity.this.getName();
                        }

                        @Override
                        public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInv, @NotNull Player player) {
                            return new SoulMageContainerMenu(id, playerInv, SoulMageEntity.this);
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
        if (pCompound.contains("BookEntityId")) {
            this.entityData.set(BOOK_ID, pCompound.getInt("BookEntityId"));
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.put("Inventory", this.inventory.createTag());
        int bookId = this.entityData.get(BOOK_ID);
        if (bookId != -1) {
            pCompound.putInt("BookEntityId", bookId);
        }
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