package dev.xylonity.companions.common.entity.hostile;

import dev.xylonity.companions.common.ai.navigator.GroundNavigator;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsParticles;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class IllagerGolemEntity extends Raider implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public List<Entity> visibleEntities = new ArrayList<>();

    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private final RawAnimation WALK = RawAnimation.begin().thenPlay("roll");
    private final RawAnimation SHOOT = RawAnimation.begin().thenPlay("shoot");

    private static final EntityDataAccessor<Boolean> ACTIVE = SynchedEntityData.defineId(IllagerGolemEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ANIMATION_START_TICK = SynchedEntityData.defineId(IllagerGolemEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> TICKCOUNT = SynchedEntityData.defineId(IllagerGolemEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> TEST_TIMER = SynchedEntityData.defineId(IllagerGolemEntity.class, EntityDataSerializers.INT);

    private static final int TIME_PER_ACTIVATION = 60;
    public static final int ELECTRICAL_CHARGE_DURATION = 8; // Amount of frames (from the spritesheet) to be played

    public IllagerGolemEntity(EntityType<? extends Raider> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.noCulling = true;
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        return new GroundNavigator(this, pLevel);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0F, 0.01F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 15.0F));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, Raider.class)).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    public static AttributeSupplier.Builder setAttributes() {
        return Raider.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40)
                .add(Attributes.ATTACK_DAMAGE, 5f)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.3f)
                .add(Attributes.FOLLOW_RANGE, 35.0);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("TickCount", getTickCount());
        tag.putInt("AnimationStartTick", getAnimationStartTick());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        setAnimationStartTick(tag.getInt("TickCount"));
        setAnimationStartTick(tag.getInt("AnimationStartTick"));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("TickCount")) {
            setTickCount(tag.getInt("TickCount"));
        } else {
            setTickCount(0);
        }

        if (tag.contains("AnimationStartTick")) {
            setAnimationStartTick(tag.getInt("AnimationStartTick"));
        } else {
            setAnimationStartTick(0);
        }

    }

    public int getAnimationStartTick() {
        return this.entityData.get(TEST_TIMER);
    }

    public void setAnimationStartTick(int tick) {
        this.entityData.set(TEST_TIMER, tick);
    }

    public int getTickCount() {
        return this.entityData.get(TICKCOUNT);
    }

    public void setTickCount(int tick) {
        this.entityData.set(TICKCOUNT, tick);
    }

    @Override
    public void applyRaidBuffs(int i, boolean b) {

    }

    @Override
    public @NotNull SoundEvent getCelebrateSound() {
        return SoundEvents.PILLAGER_CELEBRATE;
    }

    @Override
    public void tick() {
        super.tick();

        // client and server tickcounts are desynched sometimes
        if (!level().isClientSide) this.setTickCount(this.tickCount);

        if (getTickCount() % TIME_PER_ACTIVATION == 0) {
            setActive(true);
        }

        if (isActive() && getTickCount() % TIME_PER_ACTIVATION >= ELECTRICAL_CHARGE_DURATION) {
            setActive(false);
            visibleEntities.clear();
        }

        if (!this.isAlive()) {
            setActive(false);
        }

        if (isActive()) {
            // populate on both client and server
            searchAndAttack();
        }

    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime >= 20 && !this.level().isClientSide() && !this.isRemoved()) {
            this.level().broadcastEntityEvent(this, (byte) 60);

            BrokenDinamoEntity dinamo = CompanionsEntities.BROKEN_DINAMO.create(level());
            if (dinamo != null && level().random.nextFloat() < 0.15f && !level().isClientSide) {
                dinamo.moveTo(position());
                dinamo.setLifetime(new Random().nextInt(6000, 10000));
                level().addFreshEntity(dinamo);
            }

            this.remove(RemovalReason.KILLED);
        }

    }

    private void searchAndAttack() {
        visibleEntities = this.level().getEntities(this, getBoundingBox().inflate(10), entity ->
                {
                    if (entity instanceof Monster) return false;
                    if (entity instanceof Player player) return !player.isCreative() && !player.isSpectator();

                    return entity instanceof LivingEntity;
                })
                .stream()
                .filter(this::hasLineOfSight)
                .collect(Collectors.toList());

        if (!this.visibleEntities.isEmpty()) {

            if (level().isClientSide()) {
                double radius = 0.42;
                for (int i = 0; i < 360; i += 120) {
                    double rad = Math.toRadians(i);

                    double particleX = position().x + radius * Math.cos(rad);
                    double particleZ = position().z + radius * Math.sin(rad);

                    level().addParticle(CompanionsParticles.ILLAGER_GOLEM_SPARK.get(), particleX, position().y() + this.getBbHeight() - 0.60, particleZ, 0d, 0.35d, 0d);
                }

            }

            int elapsed = getTickCount() - getAnimationStartTick();
            if (!level().isClientSide && elapsed >= 3 && elapsed < ELECTRICAL_CHARGE_DURATION) {
                for (Entity entity : visibleEntities) {
                    if (entity instanceof LivingEntity e && !Util.areEntitiesLinked(this, e)) {
                        this.doHurtTarget(e);
                    }
                }

            }
        }
    }

    public void setActive(boolean active) {
        if (active && !this.isActive()) {
            setAnimationStartTick(getTickCount());
        }

        this.entityData.set(ACTIVE, active);
    }

    public boolean isActive() {
        return this.entityData.get(ACTIVE);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ACTIVE, false);
        this.entityData.define(ANIMATION_START_TICK, 0);
        this.entityData.define(TEST_TIMER, 0);
        this.entityData.define(TICKCOUNT, 0);
    }

    @Override
    protected void playStepSound(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        super.playStepSound(pPos, pState);
        this.playSound(CompanionsSounds.DINAMO_STEP.get(), 0.07875f, 1f);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return CompanionsSounds.DINAMO_IDLE.get();
    }

    @Override
    protected void playHurtSound(@NotNull DamageSource pSource) {
        playSound(CompanionsSounds.DINAMO_HURT.get(), 0.25f, 1f);
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return CompanionsSounds.DINAMO_DEATH.get();
    }

    @Override
    public int getAmbientSoundInterval() {
        return 320;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 5, this::predicate));
        controllerRegistrar.add(new AnimationController<>(this, "attackcontroller", 1, this::attackPredicate));
    }

    private <T extends GeoAnimatable> PlayState attackPredicate(AnimationState<T> event) {
        if (event.getController().getAnimationState().equals(AnimationController.State.STOPPED) && isActive() && !this.visibleEntities.isEmpty()) {
            event.getController().forceAnimationReset();
            event.getController().setAnimation(SHOOT);
        }

        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        if (event.isMoving()) {
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