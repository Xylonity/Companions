package dev.xylonity.companions.common.entity.custom;

import dev.xylonity.companions.common.ai.navigator.GroundNavigator;
import dev.xylonity.companions.common.entity.CompanionEntity;
import dev.xylonity.companions.common.entity.ai.antlion.tamable.goal.AntlionAdultFollowOwnerGoal;
import dev.xylonity.companions.common.entity.ai.antlion.tamable.goal.AntlionBaseAttackGoal;
import dev.xylonity.companions.common.entity.ai.antlion.tamable.goal.AntlionBaseLongAttackGoal;
import dev.xylonity.companions.common.entity.ai.antlion.tamable.goal.AntlionPupaAttackGoal;
import dev.xylonity.companions.common.entity.ai.generic.CompanionFollowOwnerGoal;
import dev.xylonity.companions.common.entity.ai.generic.CompanionRandomStrollGoal;
import dev.xylonity.companions.common.entity.ai.generic.CompanionsHurtTargetGoal;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsItems;
import dev.xylonity.knightlib.common.api.TickScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class AntlionEntity extends CompanionEntity {


    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private final RawAnimation SIT = RawAnimation.begin().thenPlay("sit");
    private final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");

    // soldier
    private final RawAnimation SIT2 = RawAnimation.begin().thenPlay("sit2");

    // base
    private final RawAnimation DIG_IN = RawAnimation.begin().thenPlay("dig");
    private final RawAnimation DIG_OUT = RawAnimation.begin().thenPlay("dig_out");
    private final RawAnimation DIG_IDLE = RawAnimation.begin().thenPlay("dig_idle");

    // adult
    private final RawAnimation FLY = RawAnimation.begin().thenPlay("fly");
    private final RawAnimation TURN = RawAnimation.begin().thenPlay("turn");
    private final RawAnimation UNSTUCK = RawAnimation.begin().thenPlay("unstuck");
    private final RawAnimation FALL_IDLE = RawAnimation.begin().thenPlay("fall_idle");
    private final RawAnimation HIT_GROUND = RawAnimation.begin().thenPlay("hit_ground");

    // soldier, base and
    private final RawAnimation ATTACK = RawAnimation.begin().thenPlay("dig_attack");
    private final RawAnimation SHOOT = RawAnimation.begin().thenPlay("dig_attack2");

    // base: 0 none, 1 attack, 2 shoot
    // pupa: 0 none, 1 attack
    // soldier: 0 none, 1 attack, 2 shoot
    private static final EntityDataAccessor<Integer> ATTACK_TYPE = SynchedEntityData.defineId(AntlionEntity.class, EntityDataSerializers.INT);
    // 0 base, 1 pupa, 2 adult, 3 soldier
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(AntlionEntity.class, EntityDataSerializers.INT);
    // base: 0 walk (none), 1 dig_in, 2 underground, 3 dig_out
    // adult: 0 fly (none), 1 turn, 2 fall_idle, 3 hit_ground, 4 unstuck
    private static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(AntlionEntity.class, EntityDataSerializers.INT);

    private static final int ANIMATION_BASE_DIG_IN_TICKS = 55;
    private static final int ANIMATION_BASE_DIG_OUT_TICKS = 15;
    private static final int NO_TARGET_MAX_TICKS = 20;

    private int diginCounter;
    private int digoutCounter;
    private int noTargetCounter;

    public AntlionEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.diginCounter = -1;
        this.digoutCounter = -1;
        this.noTargetCounter = -1;
    }

    @Override
    public void tick() {
        super.tick();

        setNoGravity(getVariant() == 2);

        if (!level().isClientSide) {

            if (getVariant() == 0) {

                setNoMovement(isUnderground() || getState() == 1);

                // digIn counter
                if (this.getState() == 1 && diginCounter == -1) {
                    this.diginCounter++;
                }

                // digOut counter
                if (this.getState() == 2 && noTargetCounter == -1 && getTarget() == null) {
                    this.noTargetCounter++;
                } else if (this.getState() == 2 && noTargetCounter == -1 && getTarget() == null) {
                    this.noTargetCounter = -1;
                }

                if (this.diginCounter != -1) this.diginCounter++;
                if (this.digoutCounter != -1) this.digoutCounter++;
                if (this.noTargetCounter != -1) this.noTargetCounter++;

                if (diginCounter == ANIMATION_BASE_DIG_IN_TICKS) {
                    cycleState();
                    diginCounter = -1;
                }

                // if there is no target for a while, cycling dig out
                if (noTargetCounter == NO_TARGET_MAX_TICKS) {
                    cycleState();
                    noTargetCounter = -1;
                    digoutCounter++;
                }

                if (digoutCounter == ANIMATION_BASE_DIG_OUT_TICKS) {
                    cycleState();
                    digoutCounter = -1;
                }

                // earthquake effects
                if (diginCounter == 20 || diginCounter == 35 || digoutCounter == 5) {
                    earthquake();
                }

            } else if (getVariant() == 2) {

            }

            if (level().getGameTime() % 24000 == 0) {
                cycleVariant();
                setState(0);
            }

        }

    }

    private void earthquake() {
        int radius = 2;
        // manhattan distance
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {

                int dist = Math.abs(x) + Math.abs(z);
                if (dist >= 1 && dist <= radius) {
                    BlockPos blockPos = blockPosition().offset(x, -1, z);
                    BlockState state = level().getBlockState(blockPos);

                    if (!state.isAir() && state.getBlock() != Blocks.BEDROCK) {
                        if (dist == 1) {
                            spawnFallingBlock(blockPos, state, 0.18);
                        } else {
                            TickScheduler.scheduleServer(level(), () -> spawnFallingBlock(blockPos, state, 0.2), 5);
                        }
                    }
                }

            }

        }

    }

    private void spawnFallingBlock(BlockPos pos, BlockState state, double yDelay) {
        FallingBlockEntity block = new FallingBlockEntity(level(), pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, state);

        block.setDeltaMovement(0.0, yDelay, 0.0);

        level().addFreshEntity(block);
        level().removeBlock(pos,false);
    }

    public void cycleState() {
        if (getVariant() == 0) {
            switch (getState()) {
                case 0 -> setState(1);
                case 1 -> setState(2);
                case 2 -> setState(3);
                case 3 -> setState(0);
            }
        } else {
            switch (getState()) {
                case 0 -> setState(1);
                case 1 -> setState(2);
                case 2 -> setState(3);
                case 3 -> setState(4);
                case 4 -> setState(0);
            }
        }
    }

    public void cycleVariant() {
        switch (getVariant()) {
            case 0 -> setVariant(1);
            case 1 -> setVariant(2);
            case 2 -> setVariant(3);
            case 3 -> setVariant(0);
        }

        spawnVariantParticles();
    }

    private void spawnVariantParticles() {
        for (int i = 0; i < 10; i++) {
            double dx = (this.level().random.nextDouble() - 0.5) * 0.75;
            double dy = (this.level().random.nextDouble() - 0.5) * 0.75;
            double dz = (this.level().random.nextDouble() - 0.5) * 0.75;
            if (this.level() instanceof ServerLevel level) {
                level.sendParticles(ParticleTypes.POOF, position().x, position().y + getBbHeight() * 0.5f, position().z, 1, dx, dy, dz, 0.1);
            }
        }
    }

    public void setState(int state) {
        this.entityData.set(STATE, state);
    }

    public int getState() {
        return this.entityData.get(STATE);
    }

    public boolean isUnderground() {
        return this.getVariant() == 0 && this.getState() == 2;
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        return new GroundNavigator(this, pLevel);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));

        this.goalSelector.addGoal(2, new AntlionBaseAttackGoal(this, 20, 60));
        this.goalSelector.addGoal(2, new AntlionBaseLongAttackGoal(this, 20, 60));
        this.goalSelector.addGoal(2, new AntlionPupaAttackGoal(this, 20, 60));

        this.goalSelector.addGoal(4, new AntlionAdultFollowOwnerGoal(this, 0.6D, 3.0F, 7.0F, 0.18f));

        this.goalSelector.addGoal(4, new CompanionFollowOwnerGoal(this, 0.6D, 6.0F, 2.0F, false) {
            @Override
            public boolean canUse() {
                return super.canUse() && getVariant() != 2;
            }
        });
        this.goalSelector.addGoal(4, new CompanionRandomStrollGoal(this, 0.43));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new CompanionsHurtTargetGoal(this));
    }

    public static AttributeSupplier setAttributes() {
        return CompanionEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 60)
                .add(Attributes.ATTACK_DAMAGE, 7f)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.55f)
                .add(Attributes.FOLLOW_RANGE, 35.0).build();
    }

    public int getVariant() {
        return this.entityData.get(VARIANT);
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT, variant);
    }

    public int getAttackType() {
        return this.entityData.get(ATTACK_TYPE);
    }

    public void setAttackType(int type) {
        this.entityData.set(ATTACK_TYPE, type);
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, @NotNull DamageSource pSource) {
        return getVariant() != 2 && super.causeFallDamage(pFallDistance, pMultiplier, pSource);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VARIANT, 0);
        this.entityData.define(ATTACK_TYPE, 0);
        this.entityData.define(STATE, 0);
    }

    @Override
    public void move(@NotNull MoverType pType, @NotNull Vec3 pPos) {
        super.move(pType, pPos);
        this.checkInsideBlocks();
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item itemForTaming = Items.APPLE;
        Item item = itemstack.getItem();

        if (item == CompanionsItems.HOURGLASS.get() && isTame() && getOwner() != null && getOwner() == player) {
            if (level().isClientSide) return InteractionResult.SUCCESS;

            this.cycleVariant();

            return InteractionResult.SUCCESS;
        }

        if (item == itemForTaming && !isTame()) {
            if (this.level().isClientSide) {
                return InteractionResult.CONSUME;
            } else {
                if (!player.getAbilities().instabuild) itemstack.shrink(1);

                if (!ForgeEventFactory.onAnimalTame(this, player)) {
                    if (!this.level().isClientSide) {
                        tameInteraction(player);
                    }
                }

                return InteractionResult.SUCCESS;
            }
        }

        if (isTame() && !this.level().isClientSide && hand == InteractionHand.MAIN_HAND && getOwner() == player) {
            if ((itemstack.getItem().equals(Items.APPLE) || itemstack.getItem().equals(Items.APPLE)) && this.getHealth() < this.getMaxHealth()) {
                if (itemstack.getItem().equals(Items.APPLE)) {
                    this.heal(16.0F);
                } else if (itemstack.getItem().equals(Items.APPLE)) {
                    this.heal(4.0F);
                }

                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }

            } else {
                defaultMainActionInteraction(player);
            }

            return InteractionResult.SUCCESS;
        }

        if (itemstack.getItem() == itemForTaming) {
            return InteractionResult.PASS;
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("Variant")) {
            this.setVariant(pCompound.getInt("Variant"));
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Variant", getVariant());
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
        return CompanionsConfig.ANTLION_KEEP_CHUNK_LOADED;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 2, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {

        switch (getVariant()) {
            case 0 -> {
                if (this.getMainAction() == 0) {
                    event.getController().setAnimation(SIT);
                } else if (getAttackType() == 1) {
                    event.getController().setAnimation(ATTACK);
                } else if (getAttackType() == 2) {
                    event.getController().setAnimation(SHOOT);
                } else if (getState() == 1) {
                    event.getController().setAnimation(DIG_IN);
                } else if (getState() == 2) {
                    event.getController().setAnimation(DIG_IDLE);
                } else if (getState() == 3) {
                    event.getController().setAnimation(DIG_OUT);
                }  else if (event.isMoving()) {
                    event.getController().setAnimation(WALK);
                } else {
                    event.getController().setAnimation(IDLE);
                }
            }
            case 1 -> {
                if (this.getMainAction() == 0) {
                    event.getController().setAnimation(SIT);
                } else if (getAttackType() == 1) {
                    event.getController().setAnimation(ATTACK);
                } else if (event.isMoving()) {
                    event.getController().setAnimation(WALK);
                } else {
                    event.getController().setAnimation(IDLE);
                }
            }
            case 2 -> {
                if (this.getMainAction() == 0) {
                    event.getController().setAnimation(SIT);
                } else if (getState() == 1) {
                    event.getController().setAnimation(TURN);
                } else if (getState() == 2) {
                    event.getController().setAnimation(FALL_IDLE);
                } else if (getState() == 3) {
                    event.getController().setAnimation(HIT_GROUND);
                } else if (getState() == 4) {
                    event.getController().setAnimation(UNSTUCK);
                } else {
                    event.getController().setAnimation(FLY);
                }
            }
            default -> {
                if (this.getMainAction() == 0) {
                    event.getController().setAnimation(getSitVariation() == 0 ? SIT : SIT2);
                } else if (getAttackType() == 1) {
                    event.getController().setAnimation(ATTACK);
                } else if (getAttackType() == 2) {
                    event.getController().setAnimation(SHOOT);
                }  else if (event.isMoving()) {
                    event.getController().setAnimation(WALK);
                } else {
                    event.getController().setAnimation(IDLE);
                }
            }
        }

        return PlayState.CONTINUE;
    }

}