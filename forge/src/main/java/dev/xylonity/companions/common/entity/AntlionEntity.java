package dev.xylonity.companions.common.entity;

import dev.xylonity.companions.common.ai.navigator.GroundNavigator;
import dev.xylonity.companions.common.entity.ai.antlion.AntlionMeleeAttackGoal;
import dev.xylonity.companions.common.entity.ai.teddy.TeddyLookAtPlayerGoal;
import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class AntlionEntity extends Monster implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private final RawAnimation DIG = RawAnimation.begin().thenPlay("dig");
    private final RawAnimation EMERGE = RawAnimation.begin().thenPlay("dig_out");
    private final RawAnimation DIG_IDLE = RawAnimation.begin().thenPlay("dig_idle");
    private final RawAnimation ATTACK = RawAnimation.begin().thenPlay("dig_attack");

    private static final EntityDataAccessor<Integer> PHASE = SynchedEntityData.defineId(AntlionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DIG_IN_COUNTER = SynchedEntityData.defineId(AntlionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> NO_MOVEMENT = SynchedEntityData.defineId(AntlionEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_NEAR_PLAYER = SynchedEntityData.defineId(AntlionEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_UNDERGROUND = SynchedEntityData.defineId(AntlionEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> NO_PLAYERS_NEAR_COUNTER = SynchedEntityData.defineId(AntlionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> EMERGE_COUNTER = SynchedEntityData.defineId(AntlionEntity.class, EntityDataSerializers.INT);

    private static final int ANIMATION_DIG_MAX_TICKS = 55;
    private static final int ANIMATION_EMERGE_MAX_TICKS = 15;

    private boolean isDigging = false;
    private boolean isEmerging = false;

    public AntlionEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        return new GroundNavigator(this, pLevel);
    }

    @Override
    public boolean isPushable() {
        return !isUnderground();
    }

    @Override
    public void push(@NotNull Entity pEntity) {
        if (!isUnderground()) super.push(pEntity);
    }

    @Override
    protected void pushEntities() {
        if (!isUnderground()) super.pushEntities();
    }

    @Override
    public void playerTouch(@NotNull Player pPlayer) {
        super.playerTouch(pPlayer);
    }

    @Override
    protected void registerGoals() {
        //this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AntlionMeleeAttackGoal(this, 0.6D, true));
        //this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier setAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0D)
                .add(Attributes.ATTACK_DAMAGE, 5f)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.55f)
                .add(Attributes.FOLLOW_RANGE, 35.0).build();
    }

    public int getPhase() {
        return this.entityData.get(PHASE);
    }

    public void setPhase(int phase) {
        this.entityData.set(PHASE, phase);
    }

    public int getDigInCounter() {
        return this.entityData.get(DIG_IN_COUNTER);
    }

    public void setDigInCounter(int t) {
        this.entityData.set(DIG_IN_COUNTER, t);
    }

    public boolean isNoMovement() {
        return this.entityData.get(NO_MOVEMENT);
    }

    public void setNoMovement(boolean isNoMovement) {
        this.entityData.set(NO_MOVEMENT, isNoMovement);
    }

    public boolean isUnderground() {
        return this.entityData.get(IS_UNDERGROUND);
    }

    public void setIsUnderground(boolean isUnderground) {
        this.entityData.set(IS_UNDERGROUND, isUnderground);
    }

    public int getEmergeCounter() {
        return this.entityData.get(EMERGE_COUNTER);
    }

    public void setEmergeCounter(int emergeCounter) {
        this.entityData.set(EMERGE_COUNTER, emergeCounter);
    }

    public int getNoPlayersNearCounter() {
        return this.entityData.get(NO_PLAYERS_NEAR_COUNTER);
    }

    public void setNoPlayersNearCounter(int playersNearCounter) {
        this.entityData.set(NO_PLAYERS_NEAR_COUNTER, playersNearCounter);
    }

    public boolean isNearPlayer() {
        return this.entityData.get(IS_NEAR_PLAYER);
    }

    public void setNearPlayer(boolean value) {
        this.entityData.set(IS_NEAR_PLAYER, value);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PHASE, 1);
        this.entityData.define(DIG_IN_COUNTER, 0);
        this.entityData.define(NO_PLAYERS_NEAR_COUNTER, 0);
        this.entityData.define(EMERGE_COUNTER, 0);
        this.entityData.define(NO_MOVEMENT, false);
        this.entityData.define(IS_NEAR_PLAYER, false);
        this.entityData.define(IS_UNDERGROUND, false);
    }

    protected void spawnDiggingParticles(BlockState blockState) {
        BlockParticleOption particleOption = new BlockParticleOption(ParticleTypes.BLOCK, blockState);
        for (int i = 0; i < 3; i++) {
            double xSpeed = this.random.nextGaussian() * 0.02;
            double ySpeed = this.random.nextGaussian() * 0.02;
            double zSpeed = this.random.nextGaussian() * 0.02;

            this.level().addParticle(particleOption,
                    this.getRandomX(0.45), this.getRandomY() - 0.1, this.getRandomZ(0.45),
                    xSpeed, ySpeed, zSpeed);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (isNoMovement()) {
            this.getNavigation().stop();
            this.setDeltaMovement(Vec3.ZERO);
        }

        if (getEmergeCounter() != 0 && getEmergeCounter() <= ANIMATION_EMERGE_MAX_TICKS && getNoPlayersNearCounter() == 25) {
            setEmergeCounter(getEmergeCounter() + 1);

            if (getEmergeCounter() == ANIMATION_EMERGE_MAX_TICKS) {
                setIsUnderground(false);
                setEmergeCounter(0);
                setNoMovement(false);
                this.isDigging = !this.isDigging;
            }

        }

        if (getDigInCounter() != 0 && getDigInCounter() <= ANIMATION_DIG_MAX_TICKS) {
            setNoMovement(true);
            setDigInCounter(getDigInCounter() + 1);

            if (getDigInCounter() != 0 && getDigInCounter() < ANIMATION_DIG_MAX_TICKS - 20) {
                spawnDiggingParticles(this.level().getBlockState(new BlockPos((int) getX(), (int) (getY() - 1), (int) getZ())));
            }

            if (getDigInCounter() == ANIMATION_DIG_MAX_TICKS) {
                setIsUnderground(true);
                setDigInCounter(0);
            }

        }

        if (this.level().getNearestPlayer(this, 10D) != null && !isDigging) {
            setDigInCounter(getDigInCounter() + 1);
            setNoPlayersNearCounter(0);
            this.isDigging = !this.isDigging;
        } else if (this.level().getNearestPlayer(this, 10D) == null && isUnderground() && getNoPlayersNearCounter() < 25) {
            setNoPlayersNearCounter(getNoPlayersNearCounter() + 1);
            if (getNoPlayersNearCounter() == 24) {
                this.isEmerging = !this.isEmerging;
                setEmergeCounter(getEmergeCounter() + 1);
            }
        }

    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 5, this::predicate));
        controllerRegistrar.add(new AnimationController<>(this, "attackcontroller", 1, this::attackPredicate));
    }

    private <T extends GeoAnimatable> PlayState attackPredicate(AnimationState<T> event) {
        if (this.swinging && event.getController().getAnimationState().equals(AnimationController.State.STOPPED) && isUnderground()) {
            event.getController().forceAnimationReset();
            event.getController().setAnimation(ATTACK);
            this.swinging = false;
        }

        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {

        if (getEmergeCounter() != 0 && getEmergeCounter() <= ANIMATION_EMERGE_MAX_TICKS && isUnderground()) {
            event.getController().setAnimation(EMERGE);
        } else if (getDigInCounter() != 0 && getDigInCounter() <= ANIMATION_DIG_MAX_TICKS) {
            event.getController().setAnimation(DIG);
        } else if (isUnderground()) {
            event.getController().setAnimation(DIG_IDLE);
        } else if (event.isMoving()) {
            event.getController().setAnimation(WALK);
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
