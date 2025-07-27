package dev.xylonity.companions.common.entity.companion;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.ai.navigator.FlyingNavigator;
import dev.xylonity.companions.common.entity.CompanionEntity;
import dev.xylonity.companions.common.entity.ai.generic.CompanionsLookAtPlayerGoal;
import dev.xylonity.companions.common.entity.ai.mage.allay.control.GoldenAllayMoveControl;
import dev.xylonity.companions.common.entity.ai.mage.allay.goal.GoldenAllayRandomMoveGoal;
import dev.xylonity.companions.common.entity.projectile.SoulMageBookEntity;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsItems;
import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.dimension.DimensionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class GoldenAllayEntity extends CompanionEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("fly_idle");
    private final RawAnimation FLY = RawAnimation.begin().thenPlay("fly");
    private final RawAnimation LEVITATE = RawAnimation.begin().thenPlay("levitate");
    private final RawAnimation HAT = RawAnimation.begin().thenPlay("hat");
    private final RawAnimation SHIRT = RawAnimation.begin().thenPlay("tshirt");
    private final RawAnimation TRANSFORM = RawAnimation.begin().thenPlay("transform");

    // 0 none, 1 shirt, 2 interactable, 3 hat, 4 interactable, 5 transform
    private static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(GoldenAllayEntity.class, EntityDataSerializers.INT);

    private static final int TRANSFORMATION_ANIMATION_TICKS = 80;
    private static final int HAT_ANIMATION_TICKS = 20;
    private static final int SHIRT_ANIMATION_TICKS = 15;

    private int transformationCounter;
    private int shirtCounter;
    private int hatCounter;

    public GoldenAllayEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.moveControl = new GoldenAllayMoveControl(this);
        this.transformationCounter = -1;
        this.shirtCounter = -1;
        this.hatCounter = -1;
    }

    public void tick() {
        this.noPhysics = true;

        super.tick();

        this.noPhysics = false;
        this.setNoGravity(true);

        if (tickCount % 20 == 0 && level().isClientSide) {
            this.level().addParticle(CompanionsParticles.GOLDEN_ALLAY_TRAIL.get(), getX(), getY(), getZ(), 0.35, 0.35, 0.35);
        }

        if (!level().isClientSide) {

            if (getState() == 1 && shirtCounter == -1) {
                shirtCounter++;
            }

            if (getState() == 3 && hatCounter == -1) {
                hatCounter++;
            }

            if (getState() == 5 && transformationCounter == -1) {
                transformationCounter++;
            }

            if (shirtCounter != -1) shirtCounter++;
            if (hatCounter != -1) hatCounter++;
            if (transformationCounter != -1) transformationCounter++;

            if (shirtCounter == SHIRT_ANIMATION_TICKS) {
                cycleState();
                generatePoofParticles();
                shirtCounter = -1;
            }

            if (hatCounter == HAT_ANIMATION_TICKS) {
                cycleState();
                generatePoofParticles();
                hatCounter = -1;
            }

            if (transformationCounter == TRANSFORMATION_ANIMATION_TICKS) {

                SoulMageEntity mage = CompanionsEntities.SOUL_MAGE.create(level());
                if (mage != null) {
                    mage.moveTo(position());

                    if (getOwner() instanceof Player player) {
                        mage.tameInteraction(player);
                    } else if (getOwnerUUID() != null) {
                        mage.setOwnerUUID(getOwnerUUID());
                    }

                    level().addFreshEntity(mage);

                    SoulMageBookEntity book = CompanionsEntities.SOUL_MAGE_BOOK.create(level());
                    if (book != null) {
                        book.moveTo(position());
                        book.setOwner(mage);
                        level().addFreshEntity(book);
                    }
                }

                generatePoofParticles();
                this.discard();
            }

        }

        if (level().isClientSide) {
            Companions.PROXY.spawnGoldenAllayRibbonTrail(this, level(), getX(), getY(), getZ(), 1, 1, 160/255f, 0, getBbHeight() * 0.175f);
        }

    }

    private void generatePoofParticles() {
        for (int i = 0; i < 30; i++) {
            double dx = (this.random.nextDouble() - 0.5) * 1.25;
            double dy = (this.random.nextDouble() - 0.5) * 1.25;
            double dz = (this.random.nextDouble() - 0.5) * 1.25;
            if (this.level() instanceof ServerLevel level) {
                level.sendParticles(ParticleTypes.POOF, position().x, this.getY() + getBbHeight() * Math.random(), position().z, 1, dx, dy, dz, 0.1);
            }
        }

    }

    public static AttributeSupplier.Builder setAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 15d)
                .add(Attributes.ATTACK_DAMAGE, 5f)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.55f)
                .add(Attributes.FOLLOW_RANGE, 35.0);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        FlyingNavigator navigation = new FlyingNavigator(this, this.level());
        navigation.setCanOpenDoors(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(1, new GoldenAllayRandomMoveGoal(this));

        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Monster.class, 6.0F, 0.6f, 1));

        this.goalSelector.addGoal(6, new CompanionsLookAtPlayerGoal(this, Player.class, 6.0F));
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource pDamageSource) {
        return SoundEvents.ALLAY_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ALLAY_DEATH;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(STATE, 0);
    }

    public int getState() {
        return this.entityData.get(STATE);
    }

    public void setState(int state) {
        this.entityData.set(STATE, state);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("State")) {
            this.setState(pCompound.getInt("State"));
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("State", getState());
    }

    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player pPlayer, @NotNull InteractionHand pHand) {

        if (getState() == 1 || getState() == 3 || getState() == 5) return InteractionResult.PASS;

        if (pPlayer.getUUID().equals(getOwnerUUID()) || getOwnerUUID() == null) {

            ItemStack stack = pPlayer.getItemInHand(pHand);
            return switch (getState()) {
                case 0 -> giveFirstItem(pPlayer, pHand, stack);
                case 2 -> giveSecondItem(pPlayer, pHand, stack);
                default -> giveThirdItem(pPlayer, pHand, stack);
            };

        }

        return super.mobInteract(pPlayer, pHand);
    }

    private InteractionResult giveFirstItem(Player pPlayer, InteractionHand pHand, ItemStack stack) {
        if (stack.getItem() == CompanionsItems.MAGE_COAT.get()) {
            return handleGoodInteraction(pPlayer, pHand, stack, "mage_coat_consumed");
        } else {
            return handleBadInteraction(pPlayer, pHand, stack, "requires_mage_coat");
        }
    }

    private InteractionResult giveSecondItem(Player pPlayer, InteractionHand pHand, ItemStack stack) {
        if (stack.getItem() == CompanionsItems.MAGE_HAT.get()) {
            return handleGoodInteraction(pPlayer, pHand, stack, "mage_hat_consumed");
        } else {
            return handleBadInteraction(pPlayer, pHand, stack, "requires_hat_coat");
        }
    }

    private InteractionResult giveThirdItem(Player pPlayer, InteractionHand pHand, ItemStack stack) {
        if (stack.getItem() == CompanionsItems.MAGE_STAFF.get()) {
            return handleGoodInteraction(pPlayer, pHand, stack, "mage_staff_consumed");
        } else {
            return handleBadInteraction(pPlayer, pHand, stack, "requires_staff_coat");
        }
    }

    private InteractionResult handleGoodInteraction(Player pPlayer, InteractionHand pHand, ItemStack stack, String translationPrefix) {
        if (level().isClientSide) return InteractionResult.SUCCESS;

        if (!pPlayer.getAbilities().instabuild) stack.shrink(1);

        cycleState();

        if (getOwnerUUID() == null) setOwnerUUID(pPlayer.getUUID());

        pPlayer.displayClientMessage(Component.translatable("golden_allay.companions.client_message." + translationPrefix), true);

        pPlayer.level().playSound(null, this.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, 1.0F, 1.0F);

        return InteractionResult.SUCCESS;
    }

    private InteractionResult handleBadInteraction(Player pPlayer, InteractionHand pHand, ItemStack stack, String translationPrefix) {
        pPlayer.displayClientMessage(Component.translatable("golden_allay.companions.client_message." + translationPrefix), true);

        pPlayer.level().playSound(null, this.blockPosition(), SoundEvents.VILLAGER_NO, SoundSource.NEUTRAL, 1.0F, 1.0F);

        return InteractionResult.PASS;
    }

    public static boolean checkGoldenAllaySpawnRules(EntityType<GoldenAllayEntity> allay, ServerLevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        return isDarkEnoughToSpawn(pLevel, pPos, pRandom);
    }

    public static boolean isDarkEnoughToSpawn(ServerLevelAccessor level, BlockPos pos, RandomSource random) {
        if (level.getBrightness(LightLayer.SKY, pos) > random.nextInt(32)) {
            return false;
        } else {
            DimensionType dimensionType = level.dimensionType();
            int i = dimensionType.monsterSpawnBlockLightLimit();
            if (i < 15 && level.getBrightness(LightLayer.BLOCK, pos) > i) {
                return false;
            } else {
                int j = level.getLevel().isThundering() ? level.getMaxLocalRawBrightness(pos, 10) : level.getMaxLocalRawBrightness(pos);
                return j <= dimensionType.monsterSpawnLightTest().sample(random);
            }
        }
    }

    @Override
    protected boolean canThisCompanionWork() {
        return false;
    }

    @Override
    protected int sitAnimationsAmount() {
        return 0;
    }

    @Override
    protected boolean shouldKeepChunkLoaded() {
        return false;
    }

    public void cycleState() {
        this.entityData.set(STATE, getState() + 1);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", this::predicate));
        controllerRegistrar.add(new AnimationController<>(this, "levitateController", this::levitatePredicate));
    }

    private <T extends GeoAnimatable> PlayState levitatePredicate(AnimationState<T> event) {
        event.getController().setAnimation(LEVITATE);
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        if (getState() == 5) {
            event.getController().setAnimation(TRANSFORM);
        } else if (getState() == 3) {
            event.getController().setAnimation(HAT);
        } else if (getState() == 1) {
            event.getController().setAnimation(SHIRT);
        } else if (event.isMoving()) {
            event.getController().setAnimation(FLY);
        } else {
            event.getController().setAnimation(IDLE);
        }

        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

}