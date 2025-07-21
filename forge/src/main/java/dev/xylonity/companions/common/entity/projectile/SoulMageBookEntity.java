package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.common.entity.companion.SoulMageEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SoulMageBookEntity extends Projectile implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private final RawAnimation OPEN = RawAnimation.begin().thenPlay("open");
    private final RawAnimation DEAD = RawAnimation.begin().thenPlay("dead");
    private final RawAnimation SUMMON = RawAnimation.begin().thenPlay("summon");

    private static final EntityDataAccessor<Integer> TARGET_RED = SynchedEntityData.defineId(SoulMageBookEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TARGET_GREEN = SynchedEntityData.defineId(SoulMageBookEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TARGET_BLUE = SynchedEntityData.defineId(SoulMageBookEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> CURRENT_RED = SynchedEntityData.defineId(SoulMageBookEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> CURRENT_GREEN = SynchedEntityData.defineId(SoulMageBookEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> CURRENT_BLUE = SynchedEntityData.defineId(SoulMageBookEntity.class, EntityDataSerializers.FLOAT);

    public SoulMageBookEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.noPhysics = true;
    }

    @Override
    public void tick() {
        super.tick();

        if (getOwner() == null || getOwner().isRemoved()) this.remove(RemovalReason.DISCARDED);

        if (!this.level().isClientSide) {
            updateTargetColor();
            updateCurrentColor();
        }

        if (getOwner() != null) orbit(getOwner());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(TARGET_RED, 255);
        builder.define(TARGET_GREEN, 255);
        builder.define(TARGET_BLUE, 255);
        builder.define(CURRENT_RED, 255.0f);
        builder.define(CURRENT_GREEN, 255.0f);
        builder.define(CURRENT_BLUE, 255.0f);
    }

    public float getCurrentRed() {
        return this.entityData.get(CURRENT_RED);
    }

    public float getCurrentGreen() {
        return this.entityData.get(CURRENT_GREEN);
    }

    public float getCurrentBlue() {
        return this.entityData.get(CURRENT_BLUE);
    }

    public void updateTargetColor() {
        if (this.getOwner() instanceof SoulMageEntity soulMageEntity) {
            int[] color = SoulMageEntity.ATTACK_COLORS.getOrDefault(soulMageEntity.getCurrentAttackType(), SoulMageEntity.ATTACK_COLORS.get("NONE"));
            this.entityData.set(TARGET_RED, color[0]);
            this.entityData.set(TARGET_GREEN, color[1]);
            this.entityData.set(TARGET_BLUE, color[2]);
        } else {
            this.entityData.set(TARGET_RED, 255);
            this.entityData.set(TARGET_GREEN, 255);
            this.entityData.set(TARGET_BLUE, 255);
        }

    }

    public void updateCurrentColor() {
        float curR = this.entityData.get(CURRENT_RED);
        float curG = this.entityData.get(CURRENT_GREEN);
        float curB = this.entityData.get(CURRENT_BLUE);
        int targetR = this.entityData.get(TARGET_RED);
        int targetG = this.entityData.get(TARGET_GREEN);
        int targetB = this.entityData.get(TARGET_BLUE);

        curR = lerp(curR, targetR);
        curG = lerp(curG, targetG);
        curB = lerp(curB, targetB);

        this.entityData.set(CURRENT_RED, curR);
        this.entityData.set(CURRENT_GREEN, curG);
        this.entityData.set(CURRENT_BLUE, curB);
    }

    private float lerp(float start, float target) {
        return start + 0.1F * (target - start);
    }

    private void orbit(Entity owner) {
        double angle = 0.05 * level().getGameTime();
        double radius = 2.0;
        Vec3 orbitOffset = new Vec3(radius * Math.cos(angle), 0, radius * Math.sin(angle));
        Vec3 targetPos = new Vec3(owner.getX(), owner.getY(), owner.getZ()).add(0, owner.getBbHeight(), 0).add(orbitOffset);
        Vec3 vel = this.getDeltaMovement();

        double K = 0.006;
        Vec3 accel = targetPos.subtract(position()).scale(K).subtract(vel.scale(2.0 * Math.sqrt(K)));
        vel = vel.add(accel);

        this.setDeltaMovement(vel);
        this.move(MoverType.SELF, vel);
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("TargetRed", this.entityData.get(TARGET_RED));
        pCompound.putInt("TargetGreen", this.entityData.get(TARGET_GREEN));
        pCompound.putInt("TargetBlue", this.entityData.get(TARGET_BLUE));
        pCompound.putFloat("CurrentRed", this.entityData.get(CURRENT_RED));
        pCompound.putFloat("CurrentGreen", this.entityData.get(CURRENT_GREEN));
        pCompound.putFloat("CurrentBlue", this.entityData.get(CURRENT_BLUE));
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("TargetRed")) {
            this.entityData.set(TARGET_RED, pCompound.getInt("TargetRed"));
            this.entityData.set(TARGET_GREEN, pCompound.getInt("TargetGreen"));
            this.entityData.set(TARGET_BLUE, pCompound.getInt("TargetBlue"));
        }

        if (pCompound.contains("CurrentRed")) {
            this.entityData.set(CURRENT_RED, pCompound.getFloat("CurrentRed"));
            this.entityData.set(CURRENT_GREEN, pCompound.getFloat("CurrentGreen"));
            this.entityData.set(CURRENT_BLUE, pCompound.getFloat("CurrentBlue"));
        }

    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", this::predicate));
        controllerRegistrar.add(new AnimationController<>(this, "attackController", 1, this::attackPredicate));
        controllerRegistrar.add(new AnimationController<>(this, "sizeController", 1, this::sizePredicate));
    }

    private <T extends GeoAnimatable> PlayState attackPredicate(AnimationState<T> event) {
        if (getOwner() instanceof SoulMageEntity mage) {
            if (mage.isAttacking() && event.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
                event.getController().setAnimation(OPEN);
                event.getController().forceAnimationReset();
            }
        }

        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        event.getController().setAnimation(IDLE);
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState sizePredicate(AnimationState<T> event) {
        if (getOwner() instanceof SoulMageEntity mage) {
            if (mage.isDeadOrDying()) {
                event.getController().setAnimation(DEAD);
            } else if (tickCount < 20) {
                event.getController().setAnimation(SUMMON);
            }
        }

        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

}