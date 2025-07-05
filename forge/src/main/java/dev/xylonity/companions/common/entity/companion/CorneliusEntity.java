package dev.xylonity.companions.common.entity.companion;

import dev.xylonity.companions.common.ai.navigator.GroundNavigator;
import dev.xylonity.companions.common.container.CorneliusContainerMenu;
import dev.xylonity.companions.common.entity.CompanionEntity;
import dev.xylonity.companions.common.entity.ai.cornelius.goal.*;
import dev.xylonity.companions.common.entity.ai.generic.CompanionsHurtTargetGoal;
import dev.xylonity.companions.common.util.interfaces.IFrogJumpUtil;
import dev.xylonity.companions.config.CompanionsConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.*;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
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

import java.util.Random;

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

    private int summonedCounter = -1;

    public CorneliusEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.createInventory();
    }

    @Override
    public void tick() {
        super.tick();

        if (getSummonedCount() > 0) {

            if (summonedCounter == 0) {
                setSummonedCount(getSummonedCount() - 1);
            }

            if (summonedCounter < 0) {
                summonedCounter = new Random().nextInt(400, 800);
            }

            summonedCounter--;
        }

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

        this.goalSelector.addGoal(1, new CorneliusFireworkToadGoal(this, 40, 120));
        this.goalSelector.addGoal(1, new CorneliusNetherBullfrogGoal(this, 40, 120));
        this.goalSelector.addGoal(1, new CorneliusBubbleFrogGoal(this, 40, 120));
        this.goalSelector.addGoal(1, new CorneliusEnderFrogGoal(this, 40, 120));
        this.goalSelector.addGoal(1, new CorneliusEmberPoleGoal(this, 40, 120));

        this.goalSelector.addGoal(4, new HopToOwnerGoal<>(this, 0.725D, 6.0F, 2.0F, false));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new CompanionsHurtTargetGoal(this));
    }

    public boolean canAttack() {
        return this.entityData.get(CAN_ATTACK);
    }

    public void setCanAttack(boolean canAttack) {
        this.entityData.set(CAN_ATTACK, canAttack);
    }

    public static AttributeSupplier setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20D)
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
        ItemStack itemstack = player.getItemInHand(hand);

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

        if (itemstack.getItem() == Items.APPLE && !isTame()) {
            if (this.level().isClientSide) {
                return InteractionResult.CONSUME;
            } else {
                if (!player.getAbilities().instabuild) itemstack.shrink(1);

                if (!ForgeEventFactory.onAnimalTame(this, player)) {
                    if (!this.level().isClientSide) {
                        tameInteraction(player);
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
        return true;
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