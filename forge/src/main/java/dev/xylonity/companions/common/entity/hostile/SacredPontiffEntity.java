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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
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

    private final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");

    // Phase 1 animations
    private final RawAnimation INACTIVE = RawAnimation.begin().thenPlay("inactive");
    private final RawAnimation ACTIVATE = RawAnimation.begin().thenPlay("activate");
    private final RawAnimation UP_ATTACK = RawAnimation.begin().thenPlay("up_attack");
    private final RawAnimation FRONT_ATTACK = RawAnimation.begin().thenPlay("front_attack");
    private final RawAnimation HOLD_ATTACK = RawAnimation.begin().thenPlay("hold_attack");
    private final RawAnimation AREA_ATTACK = RawAnimation.begin().thenPlay("area_attack");
    private final RawAnimation TRANSFORMATION = RawAnimation.begin().thenPlay("transformation");

    // Phase 2 animations
    private final RawAnimation APPEAR = RawAnimation.begin().thenPlay("appear");
    private final RawAnimation STAB = RawAnimation.begin().thenPlay("stab");
    private final RawAnimation STAKE = RawAnimation.begin().thenPlay("stake");
    private final RawAnimation DOUBLE_THROW = RawAnimation.begin().thenPlay("double_throw");
    private final RawAnimation IMPACT = RawAnimation.begin().thenPlay("impact");
    private final RawAnimation STAR_ATTACK = RawAnimation.begin().thenPlay("star_attack");
    private final RawAnimation PHASE2_DIE = RawAnimation.begin().thenPlay("die");

    // 0 inactive, 1 activating, 2 active, 3 transformation
    private static final EntityDataAccessor<Integer> ACTIVATION_PHASE = SynchedEntityData.defineId(SacredPontiffEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> PHASE = SynchedEntityData.defineId(SacredPontiffEntity.class, EntityDataSerializers.INT);
    // phase 1: 0 none, 1 up, 2 front, 3 hold, 4 area_attack
    // phase 2: 0 none, 1 stab, 2 stake, 3 double throw, 4 impact, 5 star_attack
    private static final EntityDataAccessor<Integer> ATTACK_TYPE = SynchedEntityData.defineId(SacredPontiffEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SHOULD_SEARCH_TARGET = SynchedEntityData.defineId(SacredPontiffEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> APPEAR_ANIMATION = SynchedEntityData.defineId(SacredPontiffEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SHOULD_ATTACK = SynchedEntityData.defineId(SacredPontiffEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SHOULD_LOOK_AT_TARGET = SynchedEntityData.defineId(SacredPontiffEntity.class, EntityDataSerializers.BOOLEAN);

    // Caps
    private static final int ANIMATION_ACTIVATION_MAX_TICKS = 65;
    private static final int ANIMATION_PHASE2_DEAD = 153;
    private static final int ANIMATION_TRANSFORMATION_MAX_TICKS = 138;
    private static final int ANIMATION_APPEAR_MAX_TICKS = 115; // Healing ticks too
    private static final int INVISIBLE_BETWEEN_PHASE_TICKS = 60;
    private static final int PHASE_2_MAX_HEALTH = 350;

    // Flags
    private boolean hasBeenActivated;
    private boolean hasAppeared;
    private boolean isTransforming;

    private final ServerBossEvent bossInfo = (ServerBossEvent)(new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS)).setDarkenScreen(true);

    public SacredPontiffEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.hasBeenActivated = false;
        this.hasAppeared = false;
        this.noCulling = true;
        this.isTransforming = false;
        this.setNoMovement(true);
        this.setMaxUpStep(1f);
        this.bossInfo.setCreateWorldFog(true);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        return new GroundNavigator(this, pLevel);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        //this.goalSelector.addGoal(1, new PontiffRotatingFireRayGoal(this, 200, 600));
        //this.goalSelector.addGoal(1, new PontiffMeleeAttackGoal(this, 40, 100));
        //this.goalSelector.addGoal(1, new PontiffStaffKnockAttackGoal(this, 160, 360));
        //this.goalSelector.addGoal(1, new PontiffDashAttackGoal(this, 60, 200));

        //this.goalSelector.addGoal(1, new HolinessMeleeAttackGoal(this, 40, 100));
        //this.goalSelector.addGoal(1, new HolinessStakeAttackGoal(this, 60, 140));
        //this.goalSelector.addGoal(1, new HolinessDoubleThrowAttackGoal(this, 200, 400));
        //this.goalSelector.addGoal(1, new HolinessImpactAttackGoal(this, 80, 300));
        //this.goalSelector.addGoal(1, new HolinessStarAttackGoal(this, 200, 500));
        this.goalSelector.addGoal(1, new HolinessStarAttackGoal(this, 40, 100));

        this.goalSelector.addGoal(2, new PontiffStrafeAroundTargetGoal(this, 0.5, 10));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new PontiffNearestAttackableTargetGoal<>(this, Player.class, true));
    }

    private boolean hasRegisteredBossBar = false;

    @Override
    public void tick() {

        // Bossbar progress
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());

        // Delayed bossinfo until activation
        if (this.getActivationPhase() == 2 && !this.hasRegisteredBossBar) {
            this.hasRegisteredBossBar = true;

            this.bossInfo.setName(this.getDisplayName());
            if (this.level() instanceof ServerLevel serverLevel) {
                for (ServerPlayer p : serverLevel.getPlayers(p2 -> true)) {
                    if (p.distanceToSqr(this) < 4096) {
                        this.bossInfo.addPlayer(p);
                    }
                }
            }
        }

        // Phase 2 appear animation
        if (getPhase() == 2 && !hasAppeared) {

            // Because of the 2 tick transition of the controller
            TickScheduler.scheduleServer(level(), () -> this.setShouldPlayAppearAnimation(true), INVISIBLE_BETWEEN_PHASE_TICKS - 2);

            // The content is executed after the logical delay between phases
            TickScheduler.scheduleServer(level(), () -> {

                this.setInvisible(false);
                this.bossInfo.setName(this.getDisplayName());
                TickScheduler.scheduleServer(level(), () -> this.setShouldPlayAppearAnimation(false), ANIMATION_APPEAR_MAX_TICKS);
                TickScheduler.scheduleServer(level(), () -> this.setShouldSearchTarget(true), ANIMATION_APPEAR_MAX_TICKS);
                TickScheduler.scheduleServer(level(), () -> this.setNoMovement(false), ANIMATION_APPEAR_MAX_TICKS);
                TickScheduler.scheduleServer(level(), () -> this.setShouldAttack(true), ANIMATION_APPEAR_MAX_TICKS);
                TickScheduler.scheduleServer(level(), () -> this.setInvulnerable(false), ANIMATION_APPEAR_MAX_TICKS);
                TickScheduler.scheduleServer(level(), () -> this.setActivationPhase(2), ANIMATION_APPEAR_MAX_TICKS);

                AttributeInstance maxHealth = this.getAttribute(Attributes.MAX_HEALTH);
                if (maxHealth != null) maxHealth.setBaseValue(PHASE_2_MAX_HEALTH);

            }, INVISIBLE_BETWEEN_PHASE_TICKS);

            hasAppeared = true;
        }

        // Healing recover
        if (getPhase() == 2 && shouldPlayAppearAnimation()) {
            this.heal(this.getMaxHealth() / ANIMATION_APPEAR_MAX_TICKS);
        }

        super.tick();
    }

    public static AttributeSupplier setAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 225f)
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
        if (this.getActivationPhase() >= 2) {
            this.bossInfo.addPlayer(pPlayer);
        }
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

    public boolean shouldSearchTarget() {
        return this.entityData.get(SHOULD_SEARCH_TARGET);
    }

    public void setShouldSearchTarget(boolean shouldSearchTarget) {
        this.entityData.set(SHOULD_SEARCH_TARGET, shouldSearchTarget);
    }

    public boolean shouldAttack() {
        return this.entityData.get(SHOULD_ATTACK);
    }

    public void setShouldAttack(boolean shouldAttack) {
        this.entityData.set(SHOULD_ATTACK, shouldAttack);
    }

    public boolean shouldLookAtTarget() {
        return this.entityData.get(SHOULD_LOOK_AT_TARGET);
    }

    public void setShouldLookAtTarget(boolean shouldLookAtTarget) {
        this.entityData.set(SHOULD_LOOK_AT_TARGET, shouldLookAtTarget);
    }

    public boolean shouldPlayAppearAnimation() {
        return this.entityData.get(APPEAR_ANIMATION);
    }

    public void setShouldPlayAppearAnimation(boolean shouldPlayAppearAnimation) {
        this.entityData.set(APPEAR_ANIMATION, shouldPlayAppearAnimation);
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
        if (getActivationPhase() != 2 && !hasBeenActivated) {

            // Change the inactive state to the activation sequence and hold some ticks until the animation is done
            setActivationPhase(1);
            TickScheduler.scheduleServer(level(), () -> setActivationPhase(2), ANIMATION_ACTIVATION_MAX_TICKS);
            TickScheduler.scheduleServer(level(), () -> setNoMovement(false), ANIMATION_ACTIVATION_MAX_TICKS);

            hasBeenActivated = true;
        }

        return super.mobInteract(pPlayer, pHand);
    }

    @Override
    public boolean hurt(@NotNull DamageSource pSource, float pAmount) {

        // Phase 2 transformation start
        if (this.getHealth() - pAmount <= 0 && getPhase() == 1 && !isTransforming) {
            this.setHealth(0.1f);
            this.setInvulnerable(true);
            this.setShouldSearchTarget(false);
            this.setNoMovement(true);
            this.setActivationPhase(3);
            this.setShouldAttack(false);
            this.isTransforming = true;
            TickScheduler.scheduleServer(level(), () -> this.setPhase(2), ANIMATION_TRANSFORMATION_MAX_TICKS);
            TickScheduler.scheduleServer(level(), () -> this.setInvisible(true), ANIMATION_TRANSFORMATION_MAX_TICKS);
            TickScheduler.scheduleServer(level(), () -> this.bossInfo.setName(Component.translatable("entity.companions.sacred_pontiff_invisible")), ANIMATION_TRANSFORMATION_MAX_TICKS);

            return false;
        }

        // Disables attack during the activation sequence
        if (getActivationPhase() == 0 || getActivationPhase() == 1) {
            return false;
        }

        return super.hurt(pSource, pAmount);
    }

    @Override
    public void knockback(double pStrength, double pX, double pZ) {
        ;;
    }

    @Override
    public @NotNull Component getName() {
        if (this.getPhase() == 2) {
            return Component.translatable("entity.companions.his_holiness");
        } else {
            return Component.translatable("entity.companions.sacred_pontiff");
        }
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime >= ANIMATION_PHASE2_DEAD && !this.level().isClientSide() && !this.isRemoved()) {
            this.level().broadcastEntityEvent(this, (byte) 60);
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ACTIVATION_PHASE, 0);
        this.entityData.define(PHASE, 1);
        this.entityData.define(ATTACK_TYPE, 0);
        this.entityData.define(SHOULD_SEARCH_TARGET, true);
        this.entityData.define(APPEAR_ANIMATION, false);
        this.entityData.define(SHOULD_ATTACK, true);
        this.entityData.define(SHOULD_LOOK_AT_TARGET, true);
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
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {

        if (getPhase() == 2) {
            if (isDeadOrDying()) {
                event.setAnimation(PHASE2_DIE);
            } else if (shouldPlayAppearAnimation()) {
                event.setAnimation(APPEAR);
            } else if (getAttackType() == 1) {
                event.setAnimation(STAB);
            } else if (getAttackType() == 2) {
                event.setAnimation(STAKE);
            } else if (getAttackType() == 3) {
                event.setAnimation(DOUBLE_THROW);
            } else if (getAttackType() == 4) {
                event.setAnimation(IMPACT);
            } else if (getAttackType() == 5) {
                event.setAnimation(STAR_ATTACK);
            } else if (event.isMoving()) {
                event.setAnimation(WALK);
            } else {
                event.setAnimation(IDLE);
            }

        } else {
            if (getActivationPhase() == 0) {
                event.setAnimation(INACTIVE);
            } else if (getActivationPhase() == 1) {
                event.setAnimation(ACTIVATE);
            } else if (getActivationPhase() == 3) {
                event.setAnimation(TRANSFORMATION);
            } else if (getAttackType() == 1) {
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
