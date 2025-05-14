package dev.xylonity.companions.common.entity.hostile;

import dev.xylonity.companions.common.ai.navigator.GroundNavigator;
import dev.xylonity.companions.common.entity.HostileEntity;
import dev.xylonity.companions.common.entity.ai.pontiff.goal.*;
import dev.xylonity.companions.common.tick.TickScheduler;
import dev.xylonity.companions.common.util.interfaces.IBossMusicProvider;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;

public class SacredPontiffEntity extends HostileEntity implements IBossMusicProvider {

    // Phase 1 animations
    private final RawAnimation INACTIVE = RawAnimation.begin().thenPlay("inactive");
    private final RawAnimation ACTIVATE = RawAnimation.begin().thenPlay("activate");
    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private final RawAnimation UP_ATTACK = RawAnimation.begin().thenPlay("up_attack");
    private final RawAnimation FRONT_ATTACK = RawAnimation.begin().thenPlay("front_attack");
    private final RawAnimation HOLD_ATTACK = RawAnimation.begin().thenPlay("hold_attack");
    private final RawAnimation AREA_ATTACK = RawAnimation.begin().thenPlay("area_attack");
    private final RawAnimation TRANSFORMATION = RawAnimation.begin().thenPlay("transformation");

    // 0 inactive, 1 activating, 2 active
    private static final EntityDataAccessor<Integer> ACTIVATION_PHASE = SynchedEntityData.defineId(SacredPontiffEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> PHASE = SynchedEntityData.defineId(SacredPontiffEntity.class, EntityDataSerializers.INT);
    // 0 none, 1 up, 2 front, 3 hold, 4 area
    private static final EntityDataAccessor<Integer> ATTACK_TYPE = SynchedEntityData.defineId(SacredPontiffEntity.class, EntityDataSerializers.INT);

    // Cap counters
    private static final int ANIMATION_ACTIVATION_MAX_TICKS = 65;

    // Flags
    private boolean hasBeenActivated;

    private final ServerBossEvent bossInfo = (ServerBossEvent)(new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.WHITE, BossEvent.BossBarOverlay.PROGRESS)).setDarkenScreen(true);

    public SacredPontiffEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.hasBeenActivated = false;
        this.setNoMovement(true);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        return new GroundNavigator(this, pLevel);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(1, new PontiffRotatingFireRayGoal(this, 200, 600));
        this.goalSelector.addGoal(1, new PontiffMeleeAttackGoal(this, 60, 120));
        this.goalSelector.addGoal(1, new PontiffStaffKnockAttackGoal(this, 160, 360));
        this.goalSelector.addGoal(1, new PontiffDashAttackGoal(this, 20, 80));

        this.goalSelector.addGoal(2, new PontiffStrafeAroundTargetGoal(this, 0.5, 10));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {

        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());

        super.tick();
    }

    public static AttributeSupplier setAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 50)
                .add(Attributes.ATTACK_DAMAGE, 5f)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.45f)
                .add(Attributes.FOLLOW_RANGE, 35.0).build();
    }

    @Override
    public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
        return false;
    }

    @Override
    public void setCustomName(@Nullable Component pName) {
        super.setCustomName(pName);
        this.bossInfo.setName(this.getDisplayName());
    }

    public void startSeenByPlayer(@NotNull ServerPlayer pPlayer) {
        super.startSeenByPlayer(pPlayer);
        this.bossInfo.addPlayer(pPlayer);
    }

    public void stopSeenByPlayer(@NotNull ServerPlayer pPlayer) {
        super.stopSeenByPlayer(pPlayer);
        this.bossInfo.removePlayer(pPlayer);
    }

    public int getPhase() {
        return this.entityData.get(PHASE);
    }

    public void setPhase(int phase) {
        this.entityData.set(PHASE, phase);
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, @NotNull DamageSource pSource) {
        return getPhase() != 2 && super.causeFallDamage(pFallDistance, pMultiplier, pSource);
    }

    public int getActivationPhase() {
        return this.entityData.get(ACTIVATION_PHASE);
    }

    public void setActivationPhase(int phase) {
        this.entityData.set(ACTIVATION_PHASE, phase);
    }

    public int getAttackType() {
        return this.entityData.get(ATTACK_TYPE);
    }

    public void setAttackType(int type) {
        this.entityData.set(ATTACK_TYPE, type);
    }

    @Override
    protected @NotNull InteractionResult mobInteract(@NotNull Player pPlayer, @NotNull InteractionHand pHand) {

        // Activation start
        if ((getActivationPhase() == 0 || getActivationPhase() == 1) && !hasBeenActivated) {

            // Change the inactive state to the activation sequence and hold some ticks until the animation is done
            setActivationPhase(1);
            TickScheduler.scheduleServer(level(), () -> setActivationPhase(2), ANIMATION_ACTIVATION_MAX_TICKS);
            TickScheduler.scheduleServer(level(), () -> setNoMovement(false), ANIMATION_ACTIVATION_MAX_TICKS);

            hasBeenActivated = true;
        }

        return super.mobInteract(pPlayer, pHand);
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {

        // Disables attack during the activation sequence
        if (getActivationPhase() == 0 || getActivationPhase() == 1) {
            return false;
        }

        return super.hurt(pSource, pAmount);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ACTIVATION_PHASE, 0);
        this.entityData.define(PHASE, 1);
        this.entityData.define(ATTACK_TYPE, 0);
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        return getPhase() == 1 ? super.getDimensions(pPose) : EntityDimensions.scalable(1F, 2F);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 2, this::predicate));
        controllerRegistrar.add(new AnimationController<>(this, "attackcontroller", 1, this::attackPredicate));
    }

    private <T extends GeoAnimatable> PlayState attackPredicate(AnimationState<T> event) {

        if (this.swinging && event.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            this.swinging = false;
        }

        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {

        // Default activation/inactive animations
        if (getActivationPhase() == 0) {
            event.setAnimation(INACTIVE);
        } else if (getActivationPhase() == 1) {
            event.setAnimation(ACTIVATE);
        }

        else if (getAttackType() == 1) {
            event.setAnimation(UP_ATTACK);
        } else if (getAttackType() == 2) {
            event.setAnimation(FRONT_ATTACK);
        } else if (getAttackType() == 3) {
            event.setAnimation(HOLD_ATTACK);
        } else if (getAttackType() == 4) {
            event.setAnimation(AREA_ATTACK);
        } else if (event.isMoving()) {
            event.setAnimation(WALK);
        } else {
            event.setAnimation(IDLE);
        }

        return PlayState.CONTINUE;
    }

    @Override
    public @NotNull SoundEvent getBossMusic() {
        return CompanionsSounds.ANGEL_OF_GERTRUDE.get();
    }

    @Override
    public boolean shouldPlayBossMusic(Player listener) {
        if (!this.isAlive()) return false;
        if (this.getActivationPhase() < 2) return false;
        if (this.distanceToSqr(listener) > getMusicRangeSqr()) return false;
        return true;
    }

}