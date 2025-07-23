package dev.xylonity.companions.common.entity.hostile;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.ai.navigator.GroundNavigator;
import dev.xylonity.companions.common.entity.HostileEntity;
import dev.xylonity.companions.common.entity.ai.pontiff.goal.*;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsItems;
import dev.xylonity.companions.registry.CompanionsSounds;
import dev.xylonity.knightlib.api.IBossMusicProvider;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;
import java.util.Set;

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

    // 0 inactive, 1 activating, 2 phase_1, 3 phase_1 transformation, 4 invisible, 5 phase_2 transformation, 6 phase_2, 7 dead
    private static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(SacredPontiffEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> STATE_COUNTER = SynchedEntityData.defineId(SacredPontiffEntity.class, EntityDataSerializers.INT);
    // phase 1: 0 none, 1 up, 2 front, 3 hold, 4 area_attack
    // phase 2: 0 none, 1 stab, 2 stake, 3 double throw, 4 impact, 5 star_attack
    private static final EntityDataAccessor<Integer> ATTACK_TYPE = SynchedEntityData.defineId(SacredPontiffEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> APPEAR_ANIMATION = SynchedEntityData.defineId(SacredPontiffEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SHOULD_ATTACK = SynchedEntityData.defineId(SacredPontiffEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SHOULD_LOOK_AT_TARGET = SynchedEntityData.defineId(SacredPontiffEntity.class, EntityDataSerializers.BOOLEAN);

    // Caps
    private static final int ANIMATION_ACTIVATION_MAX_TICKS = 65;
    private static final int ANIMATION_PHASE2_DEAD_MAX_TICKS = 153;
    private static final int ANIMATION_PHASE1_DEAD_MAX_TICKS = 166;
    private static final int ANIMATION_PHASE2_APPEAR_MAX_TICKS = 158; // Healing ticks too
    private static final int INVISIBLE_MAX_TICKS = 60;

    private boolean hasRegisteredBossBar = false;

    private final ServerBossEvent bossInfo = (ServerBossEvent)(new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS)).setDarkenScreen(true);

    private static final Set<Integer> SHAKE_TICKS = Set.of(70, 80, 93, 104, 117, 128, 159);

    public SacredPontiffEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.noCulling = true;
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

        this.goalSelector.addGoal(1, new PontiffRotatingFireRayGoal(this, 200, 500));
        this.goalSelector.addGoal(1, new PontiffMeleeAttackGoal(this, 10, 60));
        this.goalSelector.addGoal(1, new PontiffStaffKnockAttackGoal(this, 160, 360));
        this.goalSelector.addGoal(1, new PontiffDashAttackGoal(this, 60, 200));

        this.goalSelector.addGoal(1, new HolinessMeleeAttackGoal(this, 40, 100));
        this.goalSelector.addGoal(1, new HolinessStakeAttackGoal(this, 60, 140));
        this.goalSelector.addGoal(1, new HolinessDoubleThrowAttackGoal(this, 200, 700));
        this.goalSelector.addGoal(1, new HolinessImpactAttackGoal(this, 160, 800));
        this.goalSelector.addGoal(1, new HolinessStarAttackGoal(this, 200, 500));

        this.goalSelector.addGoal(2, new PontiffStrafeAroundTargetGoal(this, 0.5, 10));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && shouldSearchTarget();
            }
        });
        this.targetSelector.addGoal(2, new PontiffNearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new PontiffNearestAttackableTargetGoal<>(this, TamableAnimal.class, true));
        this.targetSelector.addGoal(2, new PontiffNearestAttackableTargetGoal<>(this, Turtle.class, true));
    }

    @Override
    public void tick() {

        // Bossbar progress
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());

        // Delayed bossinfo until activation
        if (this.getState() == 2 && !this.hasRegisteredBossBar) {
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

        // Healing recover
        if (getState() == 5) {
            this.heal(this.getMaxHealth() / ANIMATION_PHASE2_APPEAR_MAX_TICKS);
        }

        if (getTicksFrozen() > 0) setTicksFrozen(0);
        if (hasEffect(MobEffects.LEVITATION)) removeEffect(MobEffects.LEVITATION);

        // camera shaking when the pontiff punches its chest
        if (level().isClientSide && getState() == 5) {
            if (SHAKE_TICKS.contains(getStateCounter())) {
                for (Player player : level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(30))) {
                    Companions.PROXY.shakePlayerCamera(player, 5, 0.1f, 0.1f, 0.1f, 10);
                }
            }

        }

        super.tick();

        if (!level().isClientSide) stateMachine();

    }

    @Override
    public void push(double pX, double pY, double pZ) {
        ;;
    }

    private void stateMachine() {
        if (getState() == 1) {
            if (getStateCounter() == -1) setStateCounter(0);
            if (getStateCounter() == ANIMATION_ACTIVATION_MAX_TICKS) {
                cycleState();
                setNoMovement(false);
            }
        }
        else if (getState() == 3) {
            if (getStateCounter() == -1) setStateCounter(0);
            if (getStateCounter() == ANIMATION_PHASE1_DEAD_MAX_TICKS) {
                cycleState();
                setInvisible(true);
                bossInfo.setName(Component.translatable("entity.companions.sacred_pontiff_invisible"));
            }
        }
        else if (getState() == 4) {
            if (getStateCounter() == -1) setStateCounter(0);
            if (getStateCounter() == INVISIBLE_MAX_TICKS) {
                cycleState();
                bossInfo.setName(getName());

                AttributeInstance maxHealth = this.getAttribute(Attributes.MAX_HEALTH);
                if (maxHealth != null) maxHealth.setBaseValue(CompanionsConfig.HIS_HOLINESS_MAX_LIFE);

                AttributeInstance newDmg = this.getAttribute(Attributes.ATTACK_DAMAGE);
                if (newDmg != null) newDmg.setBaseValue(CompanionsConfig.HIS_HOLINESS_DAMAGE);
            }
        }
        else if (getState() == 5) {
            if (getStateCounter() == -1) setStateCounter(0);
            if (getStateCounter() == 2) setInvisible(false); // due to the controller transition ticks, it's required to delay the invis
            if (getStateCounter() == ANIMATION_PHASE2_APPEAR_MAX_TICKS) {
                cycleState();
                this.setNoMovement(false);
            }
        }

        if (getStateCounter() != -1 && getState() != 0) setStateCounter(getStateCounter() + 1);

    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource pDamageSource) {
        return CompanionsSounds.PONTIFF_HURT.get();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return CompanionsSounds.PONTIFF_IDLE.get();
    }

    @Override
    protected void playStepSound(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        playSound(CompanionsSounds.PONTIFF_STEP.get(), 0.55f, 1f);
    }

    public static AttributeSupplier setAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, CompanionsConfig.SACRED_PONTIFF_MAX_LIFE)
                .add(Attributes.ATTACK_DAMAGE, CompanionsConfig.SACRED_PONTIFF_DAMAGE)
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
        if (this.getState() >= 1) {
            this.bossInfo.addPlayer(pPlayer);
        }
    }

    public void stopSeenByPlayer(@NotNull ServerPlayer pPlayer) {
        super.stopSeenByPlayer(pPlayer);
        this.bossInfo.removePlayer(pPlayer);
    }

    public int getStateCounter() {
        return this.entityData.get(STATE_COUNTER);
    }

    public void setStateCounter(int counter) {
        this.entityData.set(STATE_COUNTER, counter);
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, @NotNull DamageSource pSource) {
        return false;
    }

    /**
     * 0 inactive, 1 activating, 2 phase_1, 3 phase_1 transformation, 4 invisible, 5 phase_2 transformation, 6 phase_2, 7 dead
     */
    public int getState() {
        return this.entityData.get(STATE);
    }

    public void cycleState() {
        this.entityData.set(STATE, getState() + 1);
        this.setStateCounter(-1); // resets state counter when the state is cycled
    }

    public void setState(int state) {
        this.entityData.set(STATE, state);
    }

    public boolean shouldSearchTarget() {
        return getState() == 2 || getState() == 6;
    }

    public boolean shouldAttack() {
        return this.entityData.get(SHOULD_ATTACK);
    }

    public boolean shouldLookAtTarget() {
        return this.entityData.get(SHOULD_LOOK_AT_TARGET);
    }

    public void setShouldLookAtTarget(boolean shouldLookAtTarget) {
        this.entityData.set(SHOULD_LOOK_AT_TARGET, shouldLookAtTarget);
    }

    public int getAttackType() {
        return this.entityData.get(ATTACK_TYPE);
    }

    public void setAttackType(int type) {
        this.entityData.set(ATTACK_TYPE, type);
    }

    @Override
    protected @NotNull InteractionResult mobInteract(@NotNull Player pPlayer, @NotNull InteractionHand pHand) {

        // phase 1 activation
        if (getState() == 0) {
            cycleState();
            playSound(CompanionsSounds.PONTIFF_ACTIVATE.get());
        }

        return super.mobInteract(pPlayer, pHand);
    }

    @Override
    public boolean hurt(@NotNull DamageSource pSource, float pAmount) {

        // phase 1 idle or transformation
        if (getState() == 0 || getState() == 1 || getState() == 3 || getState() == 4 || getState() == 5) {
            return false;
        }

        // Phase 1 dead
        if (this.getHealth() - pAmount <= 0 && getState() == 2) {
            cycleState(); // cycle state to 3
            playSound(CompanionsSounds.PONTIFF_GROUND_DESPAWN.get());
            this.setHealth(0.1f);
            this.setNoMovement(true);
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
        if (this.getState() >= 5) {
            return Component.translatable("entity.companions.his_holiness");
        } else {
            return Component.translatable("entity.companions.sacred_pontiff");
        }
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (deathTime == 1 && !level().isClientSide) {
            setNoMovement(true);
            playSound(CompanionsSounds.HOLINESS_DEATH.get(), 2f, 1f);
        }
        if (this.deathTime >= ANIMATION_PHASE2_DEAD_MAX_TICKS && !this.level().isClientSide() && !this.isRemoved()) {
            this.level().broadcastEntityEvent(this, (byte) 60);

            spawnAtLocation(new ItemStack(CompanionsItems.RELIC_GOLD.get(), getRandom().nextInt(2, 6)));
            spawnAtLocation(new ItemStack(CompanionsItems.OLD_CLOTH.get(), getRandom().nextInt(2, 6)));

            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(STATE, 0);
        this.entityData.define(STATE_COUNTER, -1);
        this.entityData.define(ATTACK_TYPE, 0);
        this.entityData.define(APPEAR_ANIMATION, false);
        this.entityData.define(SHOULD_ATTACK, true);
        this.entityData.define(SHOULD_LOOK_AT_TARGET, true);
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        return getState() <= 3 ? super.getDimensions(pPose) : EntityDimensions.scalable(1F, 2F);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("State")) {
            this.setState(pCompound.getInt("State"));
        }
        if (pCompound.contains("IsInvisible")) {
            this.setInvisible(pCompound.getBoolean("IsInvisible"));
        }
        this.setStateCounter(-1);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("State", getState());
        pCompound.putInt("StateCounter", getStateCounter());
        pCompound.putBoolean("IsInvisible", isInvisible());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 2, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {

        if (getState() >= 5) {
            if (isDeadOrDying()) {
                event.setAnimation(PHASE2_DIE);
            } else if (getState() == 5) {
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
            if (getState() == 0) {
                event.setAnimation(INACTIVE);
            } else if (getState() == 1) {
                event.setAnimation(ACTIVATE);
            } else if (getState() == 3) {
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
        return CompanionsSounds.SAINT_KLIMT.get();
    }

    @Override
    public boolean shouldPlayBossMusic(Player listener) {
        if (!this.isAlive()) return false;
        if (this.getState() < 2) return false;
        if (this.distanceToSqr(listener) > getMusicRangeSqr()) return false;
        return true;
    }

}
