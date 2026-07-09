package dev.xylonity.companions.common.entity.companion;

import dev.xylonity.companions.common.entity.CompanionEntity;
import dev.xylonity.companions.common.entity.ShadeEntity;
import dev.xylonity.companions.common.entity.ai.generic.CompanionsHurtTargetGoal;
import dev.xylonity.companions.common.entity.ai.shade.bat.goal.ShadeBatBloodAttackGoal;
import dev.xylonity.companions.common.entity.ai.shade.bat.goal.ShadeBatFollowOwnerGoal;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsParticles;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.Arrays;
import java.util.UUID;

public class ShadeBatEntity extends ShadeEntity {

    private final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");
    private final RawAnimation STING = RawAnimation.begin().thenPlay("sting");
    private final RawAnimation FLY = RawAnimation.begin().thenPlay("fly");
    private final RawAnimation IDLE_FLY = RawAnimation.begin().thenPlay("idle_fly");

    private static final int FRENZY_TICKS = 100;
    private static final double BAT_PART_MIN_DISTANCE_SCALE = 1.1;
    private static final double BAT_PART_ORBIT_RADIUS_SCALE = 1.5;
    private static final double BAT_PART_ORBIT_RADIUS_EXTRA = 1.0;

    public static final int SPAWN_FADE_TICKS = 10;

    private static final int FLY_ANIMATION_HOLD_TICKS = 10;

    private static final EntityDataAccessor<Integer> SHADE_BAT_TARGET_ID = SynchedEntityData.defineId(ShadeBatEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ALIVE_PARTS_MASK = SynchedEntityData.defineId(ShadeBatEntity.class, EntityDataSerializers.INT);

    private final UUID[] batPartUUIDs;
    private final float[] batPartHealth;
    private int frenzyTicks;
    // Blood phase clientside debounce so the fly animation doesn't flicker back to idle_fly
    private int flyAnimationHold;

    // Bat parts are applied by default, and purged if the main bat is on blood phase
    public ShadeBatEntity(EntityType<? extends CompanionEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.noPhysics = true;
        this.setNoGravity(true);
        this.noCulling = true;
        this.batPartUUIDs = new UUID[getConfiguredBatPartsAmount()];
        this.batPartHealth = new float[this.batPartUUIDs.length];
        Arrays.fill(this.batPartHealth, (float) CompanionsConfig.SHADOW_BAT_PART_MAX_LIFE);
    }

    @Override
    public int getMaxLifetime() {
        return CompanionsConfig.SHADOW_BAT_LIFETIME;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new ShadeBatBloodAttackGoal(this, ShadeBatBloodAttackGoal.TYPE_ATTACK, 30, 60));
        this.goalSelector.addGoal(1, new ShadeBatBloodAttackGoal(this, ShadeBatBloodAttackGoal.TYPE_STING, 100, 180));
        this.goalSelector.addGoal(2, new ShadeBatFollowOwnerGoal(this));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new CompanionsHurtTargetGoal(this));
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
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, @NotNull DamageSource pSource) {
        return false;
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        return isBlood() ? EntityDimensions.scalable(0.75F, 1.5F) : super.getDimensions(pPose);
    }

    @Override
    public boolean isPickable() {
        return isBlood() && super.isPickable();
    }

    @Override
    public boolean canBeHitByProjectile() {
        return isBlood() && super.canBeHitByProjectile();
    }

    public static AttributeSupplier.Builder setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, CompanionsConfig.SHADOW_BAT_MAX_LIFE)
                .add(Attributes.ATTACK_DAMAGE, CompanionsConfig.SHADOW_BAT_DAMAGE)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.55f)
                .add(Attributes.FOLLOW_RANGE, 35.0);
    }

    @Override
    public void tick() {
        super.tick();

        if (isSpawning()) {
            setIsSpawning(false);
        }
        if (frenzyTicks > 0) {
            frenzyTicks--;
        }

        setInvisible(!isBlood());

        if (!level().isClientSide) {
            // Syncs the target to each bat part alive
            syncShadeBatTarget();

            if (isBlood()) {
                removeBatParts();
            }
            else {
                // Handles the existence of each bat part (as an orchestrator)
                tickBatParts();
            }

        }
        else {
            if (isBlood() && wantsFlyAnimation()) {
                flyAnimationHold = FLY_ANIMATION_HOLD_TICKS;
            }
            else if (flyAnimationHold > 0) {
                flyAnimationHold--;
            }

        }

        // The frenzy after a sting makes the trail denser
        if (isBlood() && tickCount % (isFrenzied() ? 2 : 6) == 0 && level() instanceof ServerLevel level) {
            double dx = (this.random.nextDouble() - 0.5) * getBbWidth();
            double dy = (this.random.nextDouble() - 0.5) * getBbWidth();
            double dz = (this.random.nextDouble() - 0.5) * getBbWidth();
            level.sendParticles(CompanionsParticles.SHADE_TRAIL.get(), this.getX(), this.getY() + getBbHeight() * Math.random(), this.getZ(), 1, dx, dy, dz, 0.1);
        }

        setLifetime(getLifetime() - 1);
    }

    public boolean isFrenzied() {
        return frenzyTicks > 0;
    }

    public void startFrenzy() {
        this.frenzyTicks = FRENZY_TICKS;
    }

    private void tickBatParts() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }

        for (int i = 0; i < batPartUUIDs.length; i++) {
            if (batPartHealth[i] <= 0.0F) {
                continue;
            }
            if (batPartUUIDs[i] != null && serverLevel.getEntity(batPartUUIDs[i]) instanceof ShadeBatPartEntity existing && !existing.isRemoved()) {
                continue;
            }

            final ShadeBatPartEntity part = CompanionsEntities.SHADE_BAT_PART.get().create(serverLevel);
            if (part == null) {
                continue;
            }

            part.init(this, i);
            part.moveTo(getX(), getY(), getZ(), getYRot(), getXRot());
            serverLevel.addFreshEntity(part);
            batPartUUIDs[i] = part.getUUID();
        }

    }

    boolean hurtBatPart(int index, DamageSource source, float amount) {
        if (!isValidBatPartIndex(index) || batPartHealth[index] <= 0.0F) {
            return false;
        }
        if (isInvulnerableTo(source)) {
            return false;
        }

        if (ShadeBatPartEntity.isAlliedPart(this, source.getEntity()) || ShadeBatPartEntity.isAlliedPart(this, source.getDirectEntity())) {
            return false;
        }
        if (source.getEntity() != null && source.getEntity().equals(getOwner()) && !source.getEntity().isShiftKeyDown()) {
            return false;
        }

        final float damage = Math.min(amount, batPartHealth[index]);
        if (damage <= 0.0F) {
            return false;
        }

        playHurtSound(source);
        batPartHealth[index] -= damage;
        if (batPartHealth[index] <= 0.0F) {
            discardBatPart(index, true);
            refreshAlivePartsMask();
            if (areAllBatPartsDead()) {
                discard();
            }

        }

        return true;
    }

    private void refreshAlivePartsMask() {
        int mask = 0;
        for (int i = 0; i < batPartHealth.length && i < 32; i++) {
            if (batPartHealth[i] > 0.0F) {
                mask |= 1 << i;
            }

        }

        this.entityData.set(ALIVE_PARTS_MASK, mask);
    }

    private boolean isBatPartAlive(int index) {
        return index >= 32 || (this.entityData.get(ALIVE_PARTS_MASK) & (1 << index)) != 0;
    }

    private boolean isValidBatPartIndex(int index) {
        return index >= 0 && index < batPartUUIDs.length;
    }

    private boolean areAllBatPartsDead() {
        for (final float health : batPartHealth) {
            if (health > 0.0F) {
                return false;
            }

        }

        return true;
    }

    public Vec3 batPartOffset(int index, float partialTick) {
        if (!isValidBatPartIndex(index)) {
            return Vec3.ZERO;
        }

        final int[] indices = new int[batPartUUIDs.length];
        int count = 0;
        for (int i = 0; i < batPartUUIDs.length; i++) {
            if (i == index || isBatPartAlive(i)) {
                indices[count++] = i;
            }

        }

        final double animationTick = tickCount + partialTick;
        final Vec3[] offsets = new Vec3[count];
        int slot = 0;
        for (int k = 0; k < count; k++) {
            offsets[k] = computeBatPartOffset(indices[k], k, count, animationTick);
            if (indices[k] == index) {
                slot = k;
            }

        }

        separateBatPartOffsets(offsets);

        return offsets[slot];
    }

    public Vec3 getBatPartPosition(int index) {
        return position().add(0, getEyeHeight(), 0).add(batPartOffset(index, 0.0F));
    }

    public Vec3 getBatPartRenderPosition(int index, float partialTick) {
        return getPosition(partialTick).add(0, getEyeHeight(), 0).add(batPartOffset(index, partialTick));
    }

    public Vec3 getBatPartMovementDirection(int index, float partialTick) {
        return batPartOffset(index, partialTick + 1.0F).subtract(batPartOffset(index, partialTick));
    }

    @Nullable
    public LivingEntity getShadeBatTarget() {
        final LivingEntity target = getTarget();
        if (isValidShadeBatTarget(target)) {
            return target;
        }

        final Entity entity = level().getEntity(this.entityData.get(SHADE_BAT_TARGET_ID));
        return entity instanceof LivingEntity living && isValidShadeBatTarget(living) ? living : null;
    }

    private boolean isValidShadeBatTarget(@Nullable LivingEntity target) {
        return target != null && target.isAlive() && !target.isSpectator() && !ShadeBatPartEntity.isAlliedPart(this, target);
    }

    private void syncShadeBatTarget() {
        LivingEntity target = getTarget();
        if (target != null && !isValidShadeBatTarget(target)) {
            setTarget(null);
            target = null;
        }

        this.entityData.set(SHADE_BAT_TARGET_ID, target != null ? target.getId() : -1);
    }

    private Vec3 computeBatPartOffset(int index, int slot, int slotCount, double animationTick) {
        final double basePhase = (Math.PI * 2.0 * slot) / slotCount;
        final double randomPhase = randomUnit(index, 0) * Math.PI * 2.0;
        final double randomSpeed = 0.85 + randomUnit(index, 1) * 0.35;
        final double time = animationTick * 0.075 * randomSpeed;

        final double yaw = basePhase + time + Math.sin(animationTick * 0.027 + randomPhase) * 0.35;
        final double pitch = Math.sin(animationTick * 0.043 * randomSpeed + basePhase + randomPhase) * 0.65;
        final double radius = getBbWidth() * BAT_PART_ORBIT_RADIUS_SCALE + BAT_PART_ORBIT_RADIUS_EXTRA;

        final double horizontal = Math.cos(pitch) * radius;
        return new Vec3(
                Math.cos(yaw) * horizontal,
                Math.sin(pitch) * getBbWidth() * 1.7,
                Math.sin(yaw) * horizontal
        );

    }

    private void separateBatPartOffsets(Vec3[] offsets) {
        final double minDistance = getBbWidth() * BAT_PART_MIN_DISTANCE_SCALE;
        final double minDistanceSqr = minDistance * minDistance;

        for (int iteration = 0; iteration < 2; iteration++) {
            for (int i = 0; i < offsets.length; i++) {
                for (int j = i + 1; j < offsets.length; j++) {
                    Vec3 delta = offsets[i].subtract(offsets[j]);
                    double distanceSqr = delta.lengthSqr();
                    if (distanceSqr >= minDistanceSqr) {
                        continue;
                    }

                    if (distanceSqr < 1.0E-5) {
                        delta = new Vec3(Math.cos(i), 0.1, Math.sin(i));
                        distanceSqr = delta.lengthSqr();
                    }

                    final double distance = Math.sqrt(distanceSqr);
                    final Vec3 push = delta.scale((minDistance - distance) / distance * 0.5);
                    offsets[i] = offsets[i].add(push);
                    offsets[j] = offsets[j].subtract(push);
                }

            }

        }

    }

    private double randomUnit(int index, int extra) {
        final double value = Math.sin((getUUID().getLeastSignificantBits() % 1000L) + index * 78.233 + extra * 37.719) * 43758.5453;
        return value - Math.floor(value);
    }

    public int getBatPartsAmount() {
        return batPartUUIDs.length;
    }

    public static int getConfiguredBatPartsAmount() {
        return Math.max(1, CompanionsConfig.SHADOW_BAT_PARTS_AMOUNT);
    }

    public float getSpawnFade(float partialTick) {
        if (!isBlood()) {
            return 0.0F;
        }

        return Math.min(1.0F, Math.max(0.0F, (tickCount + partialTick) / (float) SPAWN_FADE_TICKS));
    }

    private void removeBatParts() {
        for (int i = 0; i < batPartUUIDs.length; i++) {
            discardBatPart(i, false);
        }

    }

    private void discardBatPart(int index, boolean spawnDeathParticles) {
        if (batPartUUIDs[index] != null && level() instanceof ServerLevel serverLevel && serverLevel.getEntity(batPartUUIDs[index]) instanceof ShadeBatPartEntity part) {
            if (spawnDeathParticles) {
                part.spawnDeathParticles();
            }

            part.discard();
        }

        batPartUUIDs[index] = null;
    }

    @Override
    protected boolean shouldSpawnDespawnParticles() {
        return isBlood();
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.put("BatPartHealth", Util.floatsToList(batPartHealth));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("BatPartHealth")) {
            Util.listToFloats(pCompound.getList("BatPartHealth", Tag.TAG_FLOAT), batPartHealth);
            refreshAlivePartsMask();
        }

    }

    @Override
    public void remove(@NotNull RemovalReason pReason) {
        removeBatParts();
        super.remove(pReason);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SHADE_BAT_TARGET_ID, -1);
        this.entityData.define(ALIVE_PARTS_MASK, -1);
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
        return false;
    }

    @Override
    public boolean wantsToAttack(@NotNull LivingEntity pTarget, @NotNull LivingEntity pOwner) {
        return !ShadeBatPartEntity.isAlliedPart(this, pTarget) && super.wantsToAttack(pTarget, pOwner);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 2, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        event.setAnimation(switch (getAttackType()) {
            case 1 -> ATTACK;
            case 2 -> STING;
            default -> isBlood() && flyAnimationHold <= 0 ? IDLE_FLY : FLY;
        });

        return PlayState.CONTINUE;
    }

    private boolean wantsFlyAnimation() {
        if (getShadeBatTarget() != null) {
            return true;
        }

        final Vec3 batMovement = new Vec3(getX() - xOld, getY() - yOld, getZ() - zOld);
        if (batMovement.lengthSqr() > 0.09) {
            return true;
        }

        final LivingEntity owner = getOwner();
        if (owner == null) {
            return false;
        }

        final Vec3 ownerMovement = new Vec3(owner.getX() - owner.xOld, 0, owner.getZ() - owner.zOld);
        return ownerMovement.lengthSqr() > 0.0025;
    }

    public void smoothRotate(float targetYaw, float targetPitch, float maxStepDegrees) {
        final float yaw = Mth.approachDegrees(getYRot(), targetYaw, maxStepDegrees);
        setYRot(yaw);
        setYBodyRot(yaw);
        setXRot(Mth.approachDegrees(getXRot(), targetPitch, maxStepDegrees));
    }

    public Vec3 keepAboveTerrain(Vec3 pos) {
        final Vec3 start = pos.add(0, getBbHeight(), 0);
        final Vec3 end = pos.add(0, -0.5, 0);

        final BlockHitResult hit = level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (hit.getType() == HitResult.Type.MISS) {
            return pos;
        }

        return new Vec3(pos.x, hit.getLocation().y + 0.3, pos.z);
    }

    @Override
    public void aiStep() {
        setNoMovement(getAttackType() != 0);
        super.aiStep();
    }

}