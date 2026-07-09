package dev.xylonity.companions.common.entity.companion;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.ai.navigator.GroundNavigator;
import dev.xylonity.companions.common.entity.CompanionEntity;
import dev.xylonity.companions.common.entity.ShadeEntity;
import dev.xylonity.companions.common.entity.ai.generic.CompanionFollowOwnerGoal;
import dev.xylonity.companions.common.entity.ai.generic.CompanionsHurtTargetGoal;
import dev.xylonity.companions.common.entity.projectile.ShadeMawLandingRingProjectile;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsParticles;
import dev.xylonity.companions.registry.CompanionsSounds;
import dev.xylonity.knightlib.api.scheduler.TickScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class ShadeMawEntity extends ShadeEntity implements PlayerRideableJumping {

    private final RawAnimation SPAWN = RawAnimation.begin().thenPlay("spawn");
    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private final RawAnimation RUN = RawAnimation.begin().thenPlay("run");
    private final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private final RawAnimation BITE = RawAnimation.begin().thenPlay("bite");
    private final RawAnimation SWIM = RawAnimation.begin().thenPlay("swim");

    private static final int ANIMATION_SPAWN_MAX_TICKS = 130;

    private static final float WALK_THETA = 0.02f;
    private static final float RUN_THETA = 0.375f;
    private static final float ACCELERATION = 0.04f;
    private static final float LIQUID_DRAG = 0.90f;

    private static final float JUMP_PITCH_MAX_DEGREES = 22f;
    private static final float JUMP_PITCH_RELAX_LERP = 0.18f;

    private float throttle = 0f;
    private boolean hasSpawned;
    private int lastStepSoundTick = -100;
    private int lastAboveRunThetaTick = -100;

    public float prevBodyPitch = 0f;
    public float bodyPitch = 0f;

    private float lastJumpStrength = 0f;
    private boolean landingImpactPending = false;
    private boolean wasInAirAfterJump = false;
    private int jumpReadyCooldown = 0;

    public ShadeMawEntity(EntityType<? extends CompanionEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.hasSpawned = false;
        this.noCulling = true;
        this.setMaxUpStep(1.1F);
        this.setBoundingBox(makeBoundingBox());
    }

    @Override
    public int getMaxLifetime() {
        return CompanionsConfig.SHADOW_MAW_LIFETIME;
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        return new GroundNavigator(this, pLevel);
    }

    @Override
    public @NotNull AABB makeBoundingBox() {
        float hw = (float) ((getBbWidth() * 0.8) / 2F);
        return new AABB(getX() - hw, getY(), getZ() - hw, getX() + hw, getY() + getBbHeight(), getZ() + hw);
    }

    @Override
    public float getPickRadius() {
        return (float) ((getBbWidth() - getBbWidth() * 0.8) * 0.5F);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new CompanionFollowOwnerGoal(this, 0.6D, 10.0F, 4.0F, false) {
            @Override
            public boolean canUse() {
                return super.canUse() && !isSpawning();
            }
        });

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new CompanionsHurtTargetGoal(this));
    }

    public static AttributeSupplier.Builder setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, CompanionsConfig.SHADOW_MAW_MAX_LIFE)
                .add(Attributes.ATTACK_DAMAGE, CompanionsConfig.SHADOW_MAW_DAMAGE)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.45f)
                .add(Attributes.FOLLOW_RANGE, 35.0);
    }

    @Override
    protected void playHurtSound(@NotNull DamageSource pSource) {
        playSound(CompanionsSounds.SHADE_HURT.get());
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return CompanionsSounds.SHADE_IDLE.get();
    }

    @Override
    protected void playStepSound(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        if (level().isClientSide) {
            return;
        }
        if (getDeltaMovement().horizontalDistanceSqr() > RUN_THETA * RUN_THETA) {
            lastAboveRunThetaTick = tickCount;
        }

        final boolean running = tickCount - lastAboveRunThetaTick < 10;
        final int minInterval = running ? 15 : 4;
        if (tickCount - lastStepSoundTick < minInterval) {
            return;
        }

        playSound(CompanionsSounds.SHADE_STEP.get(), 0.5f, 1);

        lastStepSoundTick = tickCount;
    }

    @Override
    public void tick() {
        super.tick();

        this.setAirSupply(this.getMaxAirSupply());

        if (this.level().isClientSide) {
            Companions.PROXY.tickShadeMaw(this);
        }

        if (!hasSpawned && isSpawning()) {
            TickScheduler.scheduleServer(level(), () -> setIsSpawning(false), ANIMATION_SPAWN_MAX_TICKS);
            TickScheduler.scheduleBoth(level(), this::refreshDimensions, ANIMATION_SPAWN_MAX_TICKS - 20);
            TickScheduler.scheduleServer(level(), () -> setInvisible(false), 5);
            hasSpawned = true;
        }

        if (tickCount % 6 == 0 && !isSpawning()) {
            double dx = (this.random.nextDouble() - 0.5) * getBbWidth();
            double dy = (this.random.nextDouble() - 0.5) * getBbWidth();
            double dz = (this.random.nextDouble() - 0.5) * getBbWidth();
            if (this.level() instanceof ServerLevel level) {
                level.sendParticles(CompanionsParticles.SHADE_TRAIL.get(), this.getX(), this.getY() + getBbHeight() * Math.random(), this.getZ(), 1, dx, dy, dz, 0.1);
            }
        }

        updateJumpBodyPitch();

        if (jumpReadyCooldown > 0) {
            jumpReadyCooldown--;
        }

        if (!level().isClientSide && landingImpactPending) {
            if (!onGround()) {
                wasInAirAfterJump = true;
            }
            else if (wasInAirAfterJump && getDeltaMovement().y <= 0.05) {
                landingImpactPending = false;
                wasInAirAfterJump = false;
                triggerImpact(lastJumpStrength);
                lastJumpStrength = 0f;
            }

        }

        setLifetime(getLifetime() - 1);
    }

    private void updateJumpBodyPitch() {
        this.prevBodyPitch = this.bodyPitch;

        if (isInAnyFluid() || isSpawning()) {
            this.bodyPitch = Mth.lerp(JUMP_PITCH_RELAX_LERP, this.bodyPitch, 0f);
            return;
        }

        float target = 0f;
        if (!onGround()) {
            final float dy = (float) Mth.clamp(getDeltaMovement().y, -1.5d, 1.5d);
            target = Mth.clamp(-dy * 24, -JUMP_PITCH_MAX_DEGREES, JUMP_PITCH_MAX_DEGREES);
        }

        final float lerp = onGround() ? JUMP_PITCH_RELAX_LERP : 0.28f;
        this.bodyPitch = Mth.lerp(lerp, this.bodyPitch, target);
    }

    private void triggerImpact(float strength) {
        if (strength <= 0.5f) {
            return;
        }

        if (level() instanceof ServerLevel server) {
            final ShadeMawLandingRingProjectile ring = CompanionsEntities.SHADE_MAW_LANDING_RING.get().create(server);
            if (ring != null) {
                ring.setOwner(this);
                ring.setStrength(strength);
                ring.moveTo(getX(), getY() + 0.04d, getZ(), getYRot(), 0f);
                server.addFreshEntity(ring);
            }

        }

        playSound(CompanionsSounds.SHADE_MAW_BITE.get(), 0.6f + strength * 0.4f, 0.55f + (1f - strength) * 0.25f);
    }

    private void startJump(int strength) {
        final int charge = Mth.clamp(strength, 0, 100);
        if (charge <= 0 || jumpReadyCooldown > 0 || isSpawning()) {
            return;
        }
        if (!onGround() && !isOnLiquidSurface()) {
            return;
        }

        double yStr;
        if (isOnLiquidSurface()) {
            yStr = 0.5d;
        }
        else {
            yStr = 0.25d + 0.008d * charge;
        }

        setDeltaMovement(getDeltaMovement().x, yStr, getDeltaMovement().z);

        this.hasImpulse = true;
        this.lastJumpStrength = Mth.clamp(charge / 100f, 0f, 1f);
        this.landingImpactPending = true;
        this.wasInAirAfterJump = false;
        this.jumpReadyCooldown = CompanionsConfig.SHADOW_MAW_JUMP_COOLDOWN;
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        if (!this.isNoAi())
            if (this.getFirstPassenger() instanceof LivingEntity e) return e;

        return null;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean canCollideWith(@NotNull Entity pEntity) {
        if (pEntity == this.getFirstPassenger()) {
            return false;
        }

        if (pEntity instanceof Player player) {
            return player.equals(getOwner());
        }

        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public void travel(@NotNull Vec3 travelVec) {
        LivingEntity rider = getControllingPassenger();
        if (rider == null && !level().getFluidState(this.blockPosition()).isEmpty()) {
            this.setDeltaMovement(getDeltaMovement().scale(0.80));
            this.setSwimming(true);
            super.travel(Vec3.ZERO);
            return;
        }

        if (isVehicle() && rider != null && !isSpawning()) {
            setYRot(rider.getYRot());
            yBodyRot = yRotO = getYRot();

            float strafe = rider.xxa * 0.5F;
            float forward = rider.zza;
            if (forward < 0F) forward *= 0.25F;

            if (Math.abs(forward) > 1.0e-3F || Math.abs(strafe) > 1.0e-3F) {
                throttle = Mth.clamp(throttle + ACCELERATION, 0F, 1F);
            }
            else {
                throttle = Mth.clamp(throttle - ACCELERATION * 1.5F, 0F, 1F);
            }

            if (horizontalCollision && !isInAnyFluid()) {
                throttle = 0.3525F;
            }

            if (isInAnyFluid()) {
                float speed = (float)getAttributeValue(Attributes.MOVEMENT_SPEED) * (0.60F + 0.20F * throttle);

                Vec3 lookDir = rider.getLookAngle().normalize();
                Vec3 motion = lookDir.scale(speed * forward);
                if (Math.abs(strafe) > 1.0e-3F) {
                    motion = motion.add(new Vec3(0, 1, 0).cross(lookDir).normalize().scale(speed * strafe));
                }

                if (isOnLiquidSurface() && forward > 0.05F) {
                    Vec3 fHoriz = new Vec3(lookDir.x, 0, lookDir.z).normalize();
                    BlockPos inFront = BlockPos.containing(
                            this.getX() + fHoriz.x * 0.8D,
                            this.getY() + 0.2D,
                            this.getZ() + fHoriz.z * 0.8D
                    );

                    if (level().getFluidState(inFront).isEmpty()) {
                        motion = motion.add(0, 0.26D, 0).add(fHoriz.scale(0.05D));
                    } else {
                        motion = motion.add(0, 0.03D, 0);
                    }

                }

                if (horizontalCollision) {
                    double upAmount = isOnLiquidSurface() ? Math.max(0.22D, speed * 0.35D) : Math.max(0.12D, speed * 0.25D);
                    Vec3 right = new Vec3(0, 1, 0).cross(lookDir).normalize();
                    motion = motion.add(0, upAmount, 0).add(right.scale(speed * 0.20F * (strafe >= 0 ? 1 : -1)));
                }

                double len = motion.length();
                if (len > 0.90D) {
                    motion = motion.scale(0.90D / len);
                }

                setXRot((float)(-Math.toDegrees(Math.atan2(motion.y, Math.sqrt(motion.x * motion.x + motion.z * motion.z)))));

                move(MoverType.SELF, motion);
                setDeltaMovement(getDeltaMovement().scale(LIQUID_DRAG));
                setSwimming(true);
            }
            else {
                setSpeed((float) getAttributeValue(Attributes.MOVEMENT_SPEED) * throttle);
                super.travel(new Vec3(strafe, travelVec.y, forward));
            }

            return;
        }

        super.travel(travelVec);
    }

    @Override
    protected void tickRidden(@NotNull Player rider, @NotNull Vec3 travelVector) {
        super.tickRidden(rider, travelVector);

        if (rider.getEffect(MobEffects.FIRE_RESISTANCE) == null) {
            rider.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 10, 0, false, false));
        }

        if (isInAnyFluid()) {
            rider.setPose(Pose.SWIMMING);
        }

        if (rider.isInWater()) {
            rider.setAirSupply(rider.getMaxAirSupply());
        }

        if (rider.isInLava()) {
            rider.extinguishFire();
            rider.clearFire();
        }

        if (!level().isClientSide && rider.swinging && rider.getMainHandItem().isEmpty()) {
            doAttack();
            rider.swinging = false;
        }

    }

    private void doAttack() {
        if (getAttackType() != 1) {
            this.setAttackType(1);
            TickScheduler.scheduleServer(level(), () -> setAttackType(0), 12);

            // Delayed damage
            TickScheduler.scheduleServer(level(), () -> {
                for (Entity e : level().getEntitiesOfClass(Entity.class, new AABB(blockPosition()).inflate(3))) {
                    if (e instanceof LivingEntity livingEntity && this.hasLineOfSight(livingEntity) && isEntityInFront(this, livingEntity, 150) && livingEntity != this) {
                        this.doHurtTarget(livingEntity);
                        livingEntity.knockback(0.5f, this.getX() - livingEntity.getX(), this.getZ() - livingEntity.getZ());
                    }
                }
            }, 10);

            playSound(CompanionsSounds.SHADE_MAW_BITE.get(), 0.3325f, 1f);
        }

    }

    public static boolean isEntityInFront(LivingEntity viewer, Entity target, double fov) {
        Vec3 view = viewer.getLookAngle().normalize();
        Vec3 toTarget = target.position().add(0, target.getEyeHeight() * 0.5, 0).subtract(viewer.getEyePosition(1)).normalize();
        double angle = Math.acos(view.dot(toTarget)) * (180.0 / Math.PI);
        return angle < (fov / 2);
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        if (!player.isSecondaryUseActive() && !player.isShiftKeyDown() && getOwner() != null && player.equals(getOwner())) {
            if (!this.level().isClientSide && !isSpawning()) {
                player.startRiding(this, true);
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return super.mobInteract(player, hand);
    }

    @Override
    protected void positionRider(@NotNull Entity pPassenger, @NotNull MoveFunction pCallback) {
        if (this.hasPassenger(pPassenger)) {
            double baseY = this.getY() + this.getPassengersRidingOffset() + pPassenger.getMyRidingOffset();

            if (isInAnyFluid()) {
                baseY -= 1.35;
            }

            pCallback.accept(pPassenger, this.getX(), baseY + 0.5, this.getZ());
        }

    }

    @Override
    public void onPlayerJump(int strength) {
        startJump(strength);
    }

    private boolean isOnLiquidSurface() {
        if (!this.isInAnyFluid()) {
            return false;
        }

        final BlockPos pos = this.blockPosition();
        final BlockPos abovePos = pos.above();
        final FluidState fluidState = level().getFluidState(pos);
        final FluidState aboveFluidState = level().getFluidState(abovePos);
        return !fluidState.isEmpty() && aboveFluidState.isEmpty();
    }

    @Override
    public boolean canJump() {
        return jumpReadyCooldown <= 0 && (onGround() || isOnLiquidSurface());
    }

    @Override
    public void handleStartJump(int strength) {
        startJump(strength);
    }

    @Override
    public void handleStopJump() {
    }

    @Override
    public int getJumpCooldown() {
        return Math.max(0, jumpReadyCooldown);
    }

    @Override
    protected boolean canAddPassenger(@NotNull Entity pPassenger) {
        return super.canAddPassenger(pPassenger);
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, @NotNull DamageSource pSource) {
        return false;
    }

    @Override
    protected void checkFallDamage(double pY, boolean pOnGround, @NotNull BlockState pState, @NotNull BlockPos pPos) {
        ;;
    }

    @Override
    public void knockback(double pStrength, double pX, double pZ) {
        if (!isSpawning()) {
            super.knockback(pStrength, pX, pZ);
        }
    }

    @Override
    protected boolean canThisCompanionWork() {
        return false;
    }

    @Override
    protected int sitAnimationsAmount() {
        return 1;
    }

    @Override
    protected boolean shouldKeepChunkLoaded() {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 2, this::predicate));
        controllerRegistrar.add(new AnimationController<>(this, "attackcontroller", 2, this::attackPredicate));
    }

    public boolean isInAnyFluid() {
        Vec3 eyePos = this.getEyePosition(1.0F);
        return !level().getFluidState(new BlockPos((int) eyePos.x, (int) eyePos.y, (int) eyePos.z)).isEmpty();
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {

        // If you're reading this hello :)
        double speed = getDeltaMovement().horizontalDistanceSqr();

        if (isSpawning()) {
            event.setAnimation(SPAWN);
        } else if (isInAnyFluid()) {
            event.setAnimation(SWIM);
        } else if (speed > RUN_THETA * RUN_THETA) {
            event.setAnimation(RUN);
        } else if (speed > WALK_THETA * WALK_THETA) {
            event.setAnimation(WALK);
        } else {
            event.setAnimation(IDLE);
        }

        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState attackPredicate(AnimationState<T> event) {

        if (getAttackType() == 1 && event.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            event.getController().forceAnimationReset();
            event.setAnimation(BITE);
        }

        return PlayState.CONTINUE;
    }

}
