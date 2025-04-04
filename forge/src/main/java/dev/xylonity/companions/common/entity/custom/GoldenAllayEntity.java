package dev.xylonity.companions.common.entity.custom;

import dev.xylonity.companions.common.ai.navigator.FlyingNavigator;
import dev.xylonity.companions.common.entity.ai.soul_mage.control.GoldenAllayMoveControl;
import dev.xylonity.companions.common.tick.TickScheduler;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsItems;
import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;

public class GoldenAllayEntity extends TamableAnimal implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("fly_idle");
    private final RawAnimation FLY = RawAnimation.begin().thenPlay("fly");
    private final RawAnimation LEVITATE = RawAnimation.begin().thenPlay("levitate");
    private final RawAnimation HAT = RawAnimation.begin().thenPlay("hat");
    private final RawAnimation SHIRT = RawAnimation.begin().thenPlay("tshirt");
    private final RawAnimation TRANSFORM = RawAnimation.begin().thenPlay("transform");

    private static final EntityDataAccessor<Integer> ACTIVE_PIECES = SynchedEntityData.defineId(GoldenAllayEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> PIECE_ANIMATION = SynchedEntityData.defineId(GoldenAllayEntity.class, EntityDataSerializers.INT);

    private boolean shouldTransform = false;

    public GoldenAllayEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

        this.moveControl = new GoldenAllayMoveControl(this);
    }

    public static AttributeSupplier setAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0D)
                .add(Attributes.ATTACK_DAMAGE, 5f)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.55f)
                .add(Attributes.FOLLOW_RANGE, 35.0).build();
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
        this.goalSelector.addGoal(1, new GoldenAllayRandomMoveGoal());
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Monster.class, 6.0F, 0.6f, 1));
    }

    public void tick() {
        this.noPhysics = true;
        super.tick();
        this.noPhysics = false;
        this.setNoGravity(true);

        if (tickCount % 20 == 0) {
            this.level().addParticle(CompanionsParticles.GOLDEN_ALLAY_TRAIL.get(), getX(), getY(), getZ(), 0.35, 0.35, 0.35);
        }

        if (activePieces() == 4 && !shouldTransform) {

            Entity mage = CompanionsEntities.SOUL_MAGE.get().create(level());
            if (mage instanceof SoulMageEntity soulMageEntity) {
                soulMageEntity.moveTo(this.position());
                if (getOwner() instanceof Player player) soulMageEntity.tame(player);
                TickScheduler.scheduleBoth(this.level(), () -> level().addFreshEntity(mage), 80);
            }

            TickScheduler.scheduleBoth(this.level(), () -> this.remove(RemovalReason.DISCARDED), 80);
            TickScheduler.scheduleBoth(this.level(), () -> {
                for (int i = 0; i < 50; i++) {
                    double dx = (this.random.nextDouble() - 0.5) * 2.5;
                    double dy = (this.random.nextDouble() - 0.5) * 1.5;
                    double dz = (this.random.nextDouble() - 0.5) * 2.5;
                    if (this.level() instanceof ServerLevel level) {
                        level.sendParticles(ParticleTypes.POOF, this.getX(), this.getY() + 0.2, this.getZ(), 1, dx, dy, dz, 0.1);
                    }
                }
            }, 80);

            shouldTransform = true;
        }

        if (shouldTransform) {
            this.getNavigation().stop();
            this.setDeltaMovement(Vec3.ZERO);
        }

    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel serverLevel, @NotNull AgeableMob ageableMob) {
        return null;
    }

    private class GoldenAllayRandomMoveGoal extends Goal {
        public GoldenAllayRandomMoveGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            return !GoldenAllayEntity.this.getMoveControl().hasWanted() && GoldenAllayEntity.this.random.nextInt(reducedTickDelay(7)) == 0;
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void tick() {
            BlockPos $$0 = GoldenAllayEntity.this.getOnPos();

            for (int $$1 = 0; $$1 < 3; ++$$1) {
                BlockPos $$2 = $$0.offset(GoldenAllayEntity.this.random.nextInt(15) - 7, GoldenAllayEntity.this.random.nextInt(7) - 3, GoldenAllayEntity.this.random.nextInt(15) - 7);
                if (GoldenAllayEntity.this.level().isEmptyBlock($$2)) {
                    GoldenAllayEntity.this.moveControl.setWantedPosition((double)$$2.getX() + 0.5, (double)$$2.getY() + 0.5, (double)$$2.getZ() + 0.5, 0.25);
                    if (GoldenAllayEntity.this.getTarget() == null) {
                        GoldenAllayEntity.this.getLookControl().setLookAt((double)$$2.getX() + 0.5, (double)$$2.getY() + 0.5, (double)$$2.getZ() + 0.5, 180.0F, 20.0F);
                    }

                    break;
                }
            }

        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ACTIVE_PIECES, 0);
        this.entityData.define(PIECE_ANIMATION, 0);
    }

    public int activePieces() {
        return this.entityData.get(ACTIVE_PIECES);
    }

    public void setActivePieces(int piece) {
        this.entityData.set(ACTIVE_PIECES, piece);
    }

    public int pieceAnimation() {
        return this.entityData.get(PIECE_ANIMATION);
    }

    public void setPieceAnimation(int piece) {
        this.entityData.set(PIECE_ANIMATION, piece);
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, @NotNull InteractionHand pHand) {
        ItemStack stack = pPlayer.getItemInHand(pHand);

        if (!this.level().isClientSide) {
            if (stack.getItem() == CompanionsItems.MAGE_HAT.get() && activePieces() != 1 && activePieces() != 3) {
                this.setActivePieces(activePieces() + 1);
                this.setPieceAnimation(1);
                TickScheduler.scheduleServer(this.level(), () -> this.setPieceAnimation(0), 20);

                if (!pPlayer.getAbilities().instabuild) stack.shrink(1);

                this.playSound(SoundEvents.ARMOR_EQUIP_LEATHER, 0.5F, 1.0F);

                return InteractionResult.SUCCESS;
            } else if (stack.getItem() == CompanionsItems.MAGE_COAT.get() && activePieces() != 2 && activePieces() != 3) {
                this.setActivePieces(activePieces() + 2);
                this.setPieceAnimation(2);
                TickScheduler.scheduleServer(this.level(), () -> this.setPieceAnimation(0), 15);

                if (!pPlayer.getAbilities().instabuild) stack.shrink(1);

                this.playSound(SoundEvents.ARMOR_EQUIP_LEATHER, 0.5F, 1.0F);

                return InteractionResult.SUCCESS;
            } else if (stack.getItem() == CompanionsItems.MAGE_STAFF.get() && activePieces() == 3) {
                this.setPieceAnimation(3);
                this.setActivePieces(4);

                super.tame(pPlayer);
                this.level().broadcastEntityEvent(this, (byte) 7);
            }
        } else {
            return InteractionResult.CONSUME;
        }

        return super.mobInteract(pPlayer, pHand);
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
        if (pieceAnimation() == 3) {
            event.getController().setAnimation(TRANSFORM);
        } else if (pieceAnimation() == 1) {
            event.getController().setAnimation(HAT);
        } else if (pieceAnimation() == 2) {
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