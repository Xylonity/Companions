package dev.xylonity.companions.common.entity.custom;

import dev.xylonity.companions.common.ai.navigator.FlyingNavigator;
import dev.xylonity.companions.common.ai.navigator.GroundNavigator;
import dev.xylonity.companions.common.entity.CompanionEntity;
import dev.xylonity.companions.common.entity.ai.generic.CompanionFollowOwnerGoal;
import dev.xylonity.companions.common.entity.ai.generic.CompanionRandomStrollGoal;
import dev.xylonity.companions.common.entity.ai.generic.CompanionsHurtTargetGoal;
import dev.xylonity.companions.common.entity.ai.teddy.goal.*;
import dev.xylonity.companions.registry.CompanionsItems;
import dev.xylonity.companions.registry.CompanionsParticles;
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
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

public class TeddyEntity extends CompanionEntity implements TraceableEntity {
    private final RawAnimation LAY = RawAnimation.begin().thenPlay("lay");
    private final RawAnimation SIT = RawAnimation.begin().thenPlay("sit");
    private final RawAnimation SLEEP = RawAnimation.begin().thenPlay("sleep");
    private final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private final RawAnimation STAB = RawAnimation.begin().thenPlay("stab");
    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private final RawAnimation AUTO_STAB = RawAnimation.begin().thenPlay("auto_stab");
    private final RawAnimation TRANSFORM = RawAnimation.begin().thenPlay("transform");

    private final RawAnimation MUTATED_FLY = RawAnimation.begin().thenPlay("flying");
    private final RawAnimation MUTATED_ATTACK1 = RawAnimation.begin().thenPlay("attack");
    private final RawAnimation MUTATED_ATTACK2 = RawAnimation.begin().thenPlay("stab");
    private final RawAnimation MUTATED_SIT1 = RawAnimation.begin().thenPlay("sit");
    private final RawAnimation MUTATED_SIT2 = RawAnimation.begin().thenPlay("laying");
    private final RawAnimation MUTATED_SIT3 = RawAnimation.begin().thenPlay("flying_sit");
    private final RawAnimation MUTATED_DEATH = RawAnimation.begin().thenPlay("death");

    // Phase 1: 1 stab, 2 auto-stab
    // Phase 2: 1 attack
    private static final EntityDataAccessor<Integer> ATTACK_TYPE = SynchedEntityData.defineId(TeddyEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> PHASE = SynchedEntityData.defineId(TeddyEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SECOND_PHASE_COUNTER = SynchedEntityData.defineId(TeddyEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_ON_AIR = SynchedEntityData.defineId(TeddyEntity.class, EntityDataSerializers.BOOLEAN);

    private static final int ANIMATION_TRANSFORM_MAX_TICKS = 200;
    private static final int ANIMATION_DEAD_MAX_TICKS = 64;

    public TeddyEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        return new GroundNavigator(this, pLevel);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this) {
            @Override
            public boolean canUse() {
                if (!isTame()) return false;
                if (isInWaterOrBubble()) return false;
                if (!onGround() && getPhase() == 1) return false;
                if (getOwner() == null) return true;
                return (!(distanceToSqr(getOwner()) < 144.0) || getOwner().getLastHurtByMob() == null) && isOrderedToSit();
            }

            @Override
            public void start() {
                super.start();
                if (getPhase() == 2) {
                    double currentX = getX();
                    double currentZ = getZ();

                    BlockPos groundPos = findClosestGroundBelow(TeddyEntity.this);
                    if (groundPos != null) {
                        double y = groundPos.getY() + 1.0;
                        teleportTo(currentX, y, currentZ);
                    }
                }
            }
        });

        this.goalSelector.addGoal(2, new TeddyAttackGoal(this, 10, 30));
        this.goalSelector.addGoal(2, new TeddyVoodooAttackGoal(this, 60, 200));

        this.goalSelector.addGoal(2, new MutatedTeddyChargeAttackGoal(this, 1.5, 1.0, 2, 10, 2.5, 4.0));

        this.goalSelector.addGoal(3, new TeddyApproachTargetGoal(this, 0.45, 0.4f, 1.25f));

        this.goalSelector.addGoal(4, new MutatedTeddyFollowOwnerGoal(this, 0.6D, 3.0F, 7.0F, 0.18f));
        this.goalSelector.addGoal(4, new CompanionFollowOwnerGoal(this, 0.6D, 6.0F, 2.0F, false) {
            @Override
            public boolean canUse() {
                return super.canUse() && getPhase() == 1;
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

    private BlockPos findClosestGroundBelow(TeddyEntity entity) {
        double feetY = entity.getBoundingBox().minY + 0.01;
        Vec3 start = new Vec3(entity.getX(), feetY, entity.getZ());
        BlockHitResult trace = entity.level().clip(new ClipContext(start, start.subtract(0, 3.0, 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));

        if (trace.getType() == HitResult.Type.BLOCK) {
            return trace.getBlockPos();
        } else {
            return null;
        }
    }

    public int getPhase() {
        return this.entityData.get(PHASE);
    }

    public void setPhase(int phase) {
        this.entityData.set(PHASE, phase);
    }

    public int getSecondPhaseCounter() {
        return this.entityData.get(SECOND_PHASE_COUNTER);
    }

    public void setSecondPhaseCounter(int t) {
        this.entityData.set(SECOND_PHASE_COUNTER, t);
    }

    public int getAttackType() {
        return this.entityData.get(ATTACK_TYPE);
    }

    public void setAttackType(int type) {
        this.entityData.set(ATTACK_TYPE, type);
    }

    public boolean getIsOnAir() {
        return this.entityData.get(IS_ON_AIR);
    }

    public void setIsOnAir(boolean isOnAir) {
        this.entityData.set(IS_ON_AIR, isOnAir);
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, @NotNull DamageSource pSource) {
        return getPhase() != 2 && super.causeFallDamage(pFallDistance, pMultiplier, pSource);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PHASE, 1);
        this.entityData.define(SECOND_PHASE_COUNTER, 0);
        this.entityData.define(ATTACK_TYPE, 0);
        this.entityData.define(IS_ON_AIR, false);
    }

    @Override
    public void tick() {
        super.tick();

        if (getPhase() == 2) {
            this.setNoGravity(true);
        }

        if (getPhase() == 2 && !level().isClientSide && this.getMainAction() == 0) {
            setIsOnAir(level().getBlockState(blockPosition().below()).isAir());
        }

        if (getSecondPhaseCounter() != 0 && getSecondPhaseCounter() <= ANIMATION_TRANSFORM_MAX_TICKS) {
            setTarget(null);
            setNoMovement(true);

            if (getSecondPhaseCounter() == ANIMATION_TRANSFORM_MAX_TICKS) {
                setNoMovement(false);

                level().addParticle(CompanionsParticles.TEDDY_TRANSFORMATION_CLOUD.get(), true, getX(), getY() + 0.15F, getZ(), 0, 0, 0);

                for (int i = 0; i < 50; i++) {
                    double dx = (this.random.nextDouble() - 0.5) * 2.0;
                    double dy = (this.random.nextDouble() - 0.5) * 2.0;
                    double dz = (this.random.nextDouble() - 0.5) * 2.0;
                    if (this.level() instanceof ServerLevel level) {
                        level.sendParticles(ParticleTypes.POOF, this.getX(), this.getY() + 1, this.getZ(), 1, dx, dy, dz, 0.1);
                        if (i % 5 == 0) level.sendParticles(CompanionsParticles.TEDDY_TRANSFORMATION.get(), this.getX(), this.getY() + 1, this.getZ(), 1, dx, dy, dz, 0.2);
                    }
                }

                setPhase(2);
                this.refreshDimensions();

                FlyingNavigator navigation = new FlyingNavigator(this, this.level());
                navigation.setCanOpenDoors(true);
                navigation.setCanPassDoors(true);

                this.navigation = navigation;
            }

            setSecondPhaseCounter(getSecondPhaseCounter() + 1);
        }

    }

    @Override
    public void move(@NotNull MoverType pType, @NotNull Vec3 pPos) {
        super.move(pType, pPos);
        this.checkInsideBlocks();
    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        return super.getOwner();
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        return getPhase() == 1 ? super.getDimensions(pPose) : EntityDimensions.scalable(1F, 2F);
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item itemForTaming = Items.APPLE;
        Item item = itemstack.getItem();

        if (getSecondPhaseCounter() != 0 && getPhase() == 1) {
            return InteractionResult.PASS;
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

            } else if (itemstack.getItem().equals(CompanionsItems.ETERNAL_LIGHTER.get()) && getPhase() == 1 && this.getSecondPhaseCounter() == 0 && getMainAction() != 0) {
                this.setSecondPhaseCounter(this.getSecondPhaseCounter() + 1);
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
    protected void tickDeath() {
        ++this.deathTime;
        if (getPhase() == 2) {
            if (this.deathTime >= ANIMATION_DEAD_MAX_TICKS && !this.level().isClientSide() && !this.isRemoved()) {
                this.level().broadcastEntityEvent(this, (byte) 60);
                this.remove(RemovalReason.KILLED);
            }
        } else {
            if (this.deathTime >= 20 && !this.level().isClientSide() && !this.isRemoved()) {
                this.level().broadcastEntityEvent(this, (byte) 60);
                this.remove(RemovalReason.KILLED);
            }
        }
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> pKey) {
        super.onSyncedDataUpdated(pKey);
        if (pKey.equals(PHASE)) {
            this.refreshDimensions();
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("Phase")) {
            this.setPhase(pCompound.getInt("Phase"));
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Phase", getPhase());
        if (this.getSecondPhaseCounter() != 0) {
            pCompound.putInt("Phase", 2);
        }
    }

    @Override
    protected boolean canThisCompanionWork() {
        return false;
    }

    @Override
    protected int sitAnimationsAmount() {
        return getPhase() == 1 ? 3 : 2;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 2, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {

        if (getPhase() == 1) {
            if (this.getMainAction() == 0) {
                RawAnimation vari = getSitVariation() == 0 ? LAY : getSitVariation() == 1 ? SIT : SLEEP;
                event.getController().setAnimation(vari);
            } else if (getAttackType() == 1) {
                event.setAnimation(STAB);
            } else if (getAttackType() == 2) {
                event.setAnimation(AUTO_STAB);
            } else if (this.getSecondPhaseCounter() != 0 && this.getSecondPhaseCounter() <= ANIMATION_TRANSFORM_MAX_TICKS) {
                event.getController().setAnimation(TRANSFORM);
            } else if (event.isMoving()) {
                event.getController().setAnimation(WALK);
            } else {
                event.getController().setAnimation(IDLE);
            }
        } else {
            if (isDeadOrDying()) {
              event.getController().setAnimation(MUTATED_DEATH);
            } else if (this.getMainAction() == 0) {
                RawAnimation vari = getIsOnAir() ? MUTATED_SIT3 : getSitVariation() == 0 ? MUTATED_SIT1 : MUTATED_SIT2;
                event.getController().setAnimation(vari);
            } else {
                event.getController().setAnimation(MUTATED_FLY);
            }
        }

        return PlayState.CONTINUE;
    }

}