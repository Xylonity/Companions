package dev.xylonity.companions.common.entity.companion;

import dev.xylonity.companions.common.ai.navigator.GroundNavigator;
import dev.xylonity.companions.common.container.CorneliusContainerMenu;
import dev.xylonity.companions.common.entity.CompanionEntity;
import dev.xylonity.companions.common.entity.ai.cornelius.goal.*;
import dev.xylonity.companions.common.entity.ai.generic.CompanionRandomHopStrollGoal;
import dev.xylonity.companions.common.entity.ai.generic.CompanionsHurtTargetGoal;
import dev.xylonity.companions.common.util.interfaces.IFrogJumpUtil;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
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
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class CorneliusEntity extends CompanionEntity implements ContainerListener, IFrogJumpUtil {

    public SimpleContainer inventory;

    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private final RawAnimation SIT = RawAnimation.begin().thenPlay("sit");
    private final RawAnimation SIT2 = RawAnimation.begin().thenPlay("sit2");
    private final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private final RawAnimation SUMMON = RawAnimation.begin().thenPlay("summon");
    private final RawAnimation SUMMON2 = RawAnimation.begin().thenPlay("summon2");
    private final RawAnimation SUMMON3 = RawAnimation.begin().thenPlay("summon3");

    private static final EntityDataAccessor<Byte> DATA_ID_FLAGS = SynchedEntityData.defineId(CorneliusEntity.class, EntityDataSerializers.BYTE);
    // 0 none, 1,2,3: summon
    private static final EntityDataAccessor<Integer> ATTACK_TYPE = SynchedEntityData.defineId(CorneliusEntity.class, EntityDataSerializers.INT);
    // Prevent attacking while the moving cycle is active
    private static final EntityDataAccessor<Boolean> CAN_ATTACK = SynchedEntityData.defineId(CorneliusEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> SUMMONED_COUNT = SynchedEntityData.defineId(CorneliusEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CYCLE_COUNTER = SynchedEntityData.defineId(CorneliusEntity.class, EntityDataSerializers.INT);

    private static final int MAX_CYCLE_TICKS = 20;

    public CorneliusEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
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
    }

    public int getCycleCount() {
        return this.entityData.get(CYCLE_COUNTER);
    }

    public void setCycleCount(int count) {
        this.entityData.set(CYCLE_COUNTER, count);
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    protected int getInventorySize() {
        return 6;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));

        this.goalSelector.addGoal(2, new CorneliusFireworkToadGoal(this, 60, 260));
        this.goalSelector.addGoal(2, new CorneliusNetherBullfrogGoal(this, 60, 260));
        this.goalSelector.addGoal(2, new CorneliusBubbleFrogGoal(this, 60, 260));
        this.goalSelector.addGoal(2, new CorneliusEnderFrogGoal(this, 60, 260));
        this.goalSelector.addGoal(2, new CorneliusEmberPoleGoal(this, 60, 260));

        this.goalSelector.addGoal(3, new CorneliusMoveToBeeGoal(this, 0.725D));
        this.goalSelector.addGoal(3, new CorneliusAttackBeeGoal(this, 10, 30));

        this.goalSelector.addGoal(4, new HopToOwnerGoal<>(this, 0.725D, 6.0F, 2.0F, false));
        this.goalSelector.addGoal(4, new CompanionRandomHopStrollGoal(this, 0.725D));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new CompanionsHurtTargetGoal(this));
    }

    public boolean canAttack() {
        return this.entityData.get(CAN_ATTACK);
    }

    public void setCanAttack(boolean canAttack) {
        this.entityData.set(CAN_ATTACK, canAttack);
    }

    @Override
    public void tick() {

        super.tick();

        if (!level().isClientSide) {
            if (getCycleCount() == 0) playSound(CompanionsSounds.FROGGY_JUMP.get(), 0.5f, 1);

            if (getCycleCount() >= 12) this.setDeltaMovement(new Vec3(0, 0, 0));

            if (getCycleCount() >= 0) setCycleCount(getCycleCount() + 1);

            if (getCycleCount() >= MAX_CYCLE_TICKS) setCycleCount(-1);
        }

    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return CompanionsSounds.FROGGY_IDLE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource pDamageSource) {
        return CompanionsSounds.FROGGY_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return CompanionsSounds.FROGGY_DEATH.get();
    }

    public static AttributeSupplier setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, CompanionsConfig.CORNELIUS_MAX_LIFE)
                .add(Attributes.ATTACK_DAMAGE, 5f)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.55f)
                .add(Attributes.FOLLOW_RANGE, 35.0).build();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_FLAGS, (byte) 0);
        this.entityData.define(CAN_ATTACK, true);
        this.entityData.define(ATTACK_TYPE, 0);
        this.entityData.define(SUMMONED_COUNT, 0);
        this.entityData.define(CYCLE_COUNTER, -1);
    }

    public int getAttackType() {
        return this.entityData.get(ATTACK_TYPE);
    }

    public void setAttackType(int type) {
        this.entityData.set(ATTACK_TYPE, type);
    }

    public int getSummonedCount() {
        return this.entityData.get(SUMMONED_COUNT);
    }

    public void setSummonedCount(int c) {
        this.entityData.set(SUMMONED_COUNT, c);
    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        return super.getOwner();
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        if (this.isTame() && this.getOwner() == player && player.isShiftKeyDown() && !this.level().isClientSide && hand == InteractionHand.MAIN_HAND) {
            NetworkHooks.openScreen(
                    (ServerPlayer) player, new MenuProvider() {
                        @Override
                        public @NotNull Component getDisplayName() {
                            return CorneliusEntity.this.getName();
                        }

                        @Override
                        public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInv, @NotNull Player player) {
                            return new CorneliusContainerMenu(id, playerInv, CorneliusEntity.this);
                        }
                    },
                    buf -> buf.writeInt(this.getId())
            );

            this.playSound(SoundEvents.ARMOR_EQUIP_LEATHER, 0.5F, 1.0F);
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
        if (pCompound.contains("SummonedCount")) {
            this.setSummonedCount(pCompound.getInt("SummonedCount"));
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.put("Inventory", this.inventory.createTag());
        pCompound.putInt("SummonedCount", this.getSummonedCount());
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
        return CompanionsConfig.CORNELIUS_KEEP_CHUNK_LOADED;
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

    public static boolean checkCorneliusSpawnRules(EntityType<CorneliusEntity> cornelius, LevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        return pRandom.nextInt(20) == 0 && checkMobSpawnRules(cornelius, pLevel, pSpawnType, pPos, pRandom) && checkAnimalSpawnRules(cornelius, pLevel, pSpawnType, pPos, pRandom);
    }

    @Override
    public void containerChanged(@NotNull Container container) {
        ;;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 2, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        if (this.getMainAction() == 0) {
            event.setAnimation(getSitVariation() == 0 ? SIT : SIT2);
        } else if (getAttackType() == 1) {
            event.setAnimation(SUMMON);
        } else if (getAttackType() == 2) {
            event.setAnimation(SUMMON2);
        } else if (getAttackType() == 3) {
            event.setAnimation(SUMMON3);
        } else if (event.isMoving()) {
            event.setAnimation(WALK);
        } else {
            event.setAnimation(IDLE);
        }

        return PlayState.CONTINUE;
    }

}