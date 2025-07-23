package dev.xylonity.companions.common.entity.companion;

import dev.xylonity.companions.common.ai.navigator.GroundNavigator;
import dev.xylonity.companions.common.container.PuppetContainerMenu;
import dev.xylonity.companions.common.container.SoulMageContainerMenu;
import dev.xylonity.companions.common.entity.CompanionEntity;
import dev.xylonity.companions.common.entity.ai.generic.CompanionFollowOwnerGoal;
import dev.xylonity.companions.common.entity.ai.generic.CompanionRandomStrollGoal;
import dev.xylonity.companions.common.entity.ai.generic.CompanionsHurtTargetGoal;
import dev.xylonity.companions.common.entity.ai.mage.goal.*;
import dev.xylonity.companions.common.entity.summon.LivingCandleEntity;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SoulMageEntity extends CompanionEntity implements ContainerListener {
    public SimpleContainer inventory;

    private final RawAnimation SIT = RawAnimation.begin().thenPlay("sit");
    private final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");

    private static final EntityDataAccessor<Integer> SIT_VARIATION = SynchedEntityData.defineId(SoulMageEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_ATTACKING = SynchedEntityData.defineId(SoulMageEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Byte> DATA_ID_FLAGS = SynchedEntityData.defineId(SoulMageEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<String> ATTACK_ANIMATION_NAME = SynchedEntityData.defineId(SoulMageEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> CANDLE_COUNT = SynchedEntityData.defineId(SoulMageEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> BOOK_ID = SynchedEntityData.defineId(SoulMageEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> CURRENT_ATTACK_TYPE = SynchedEntityData.defineId(SoulMageEntity.class, EntityDataSerializers.STRING);

    public static final int MAX_CANDLES_COUNT = 6;
    public List<LivingCandleEntity> candles = new LinkedList<>();

    public static final Map<String, int[]> ATTACK_COLORS = Map.of(
            "MAGIC_RAY", new int[]{173, 216, 230},
            "BLACK_HOLE", new int[]{128, 0, 128},
            "STONE_SPIKES", new int[]{139, 69, 19},
            "HEAL_RING", new int[]{110, 252, 85},
            "ICE_SHARD", new int[]{134, 236, 255},
            "FIRE_MARK", new int[]{225, 45, 45},
            "TORNADO", new int[]{242, 242, 242},
            "NONE", new int[]{169, 134, 60}
    );

    public SoulMageEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
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
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));

        this.goalSelector.addGoal(3, new SoulMageMagicRayGoal(this, 70, 160));
        this.goalSelector.addGoal(3, new SoulMageBlackHoleGoal(this, 70, 160));
        this.goalSelector.addGoal(3, new SoulMageStoneSpikesGoal(this, 70, 160));
        this.goalSelector.addGoal(3, new SoulMageHealRingGoal(this, 70, 160));
        this.goalSelector.addGoal(3, new SoulMageIceShardGoal(this, 70, 160));
        this.goalSelector.addGoal(3, new SoulMageFireMarkGoal(this, 70, 160));
        this.goalSelector.addGoal(3, new SoulMageTornadoGoal(this, 70, 160));
        this.goalSelector.addGoal(3, new SoulMageBraceGoal(this, 70, 160));
        this.goalSelector.addGoal(3, new SoulMageNaginataGoal(this, 70, 160));

        this.goalSelector.addGoal(4, new CompanionFollowOwnerGoal(this, 0.6D, 6.0F, 2.0F, false));
        this.goalSelector.addGoal(4, new CompanionRandomStrollGoal(this, 0.43));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new CompanionsHurtTargetGoal(this));
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

    public String getCurrentAttackType() {
        return this.entityData.get(CURRENT_ATTACK_TYPE);
    }

    public void setCurrentAttackType(String attackType) {
        this.entityData.set(CURRENT_ATTACK_TYPE, attackType);
    }

    public static AttributeSupplier.Builder setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, CompanionsConfig.SOUL_MAGE_MAX_LIFE)
                .add(Attributes.ATTACK_DAMAGE, 5f)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.55f)
                .add(Attributes.FOLLOW_RANGE, 35.0);
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

    public int getBookId() {
        return this.entityData.get(BOOK_ID);
    }

    public void setBookId(int id) {
        this.entityData.set(BOOK_ID, id);
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
    public @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {

        if (level().isClientSide) return InteractionResult.SUCCESS;

        if (this.isTame() && this.getOwner() == player && player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND) {
            if (!level().isClientSide) {
                player.openMenu(new ExtendedScreenHandlerFactory() {
                        @Override
                        public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                            return new SoulMageContainerMenu(i, inventory, SoulMageEntity.this);
                        }

                        @Override
                        public Component getDisplayName() {
                            return SoulMageEntity.this.getName();
                        }

                        @Override
                        public void writeScreenOpeningData(ServerPlayer serverPlayer, FriendlyByteBuf buf) {
                            buf.writeInt(SoulMageEntity.this.getId());
                        }
                    }
                );

                this.playSound(SoundEvents.ARMOR_EQUIP_LEATHER, 0.5F, 1.0F);
            }

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
        this.inventory.fromTag(pCompound.getList("Inventory", 10));
        if (pCompound.contains("BookEntityId")) {
            this.entityData.set(BOOK_ID, pCompound.getInt("BookEntityId"));
        }
        this.setBookId(pCompound.getInt("BookId"));
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.put("Inventory", this.inventory.createTag());
        pCompound.putInt("BookId", getBookId());
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return CompanionsSounds.MAGE_IDLE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return CompanionsSounds.MAGE_DEATH.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource pDamageSource) {
        return CompanionsSounds.MAGE_HURT.get();
    }

    @Override
    protected void playStepSound(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        playSound(CompanionsSounds.MAGE_STEP.get(), 0.2f, 1f);
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
        return CompanionsConfig.SOUL_MAGE_KEEP_CHUNK_LOADED;
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
        if (this.getMainAction() == 0) {
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