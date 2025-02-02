package dev.xylonity.companions.common.entity.custom;

import dev.xylonity.companions.common.ai.navigator.GroundNavigator;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
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
import java.util.stream.Collectors;

public class IllagerGolemEntity extends Raider implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public List<Entity> visibleEntities = new ArrayList<>();

    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private final RawAnimation WALK = RawAnimation.begin().thenPlay("roll");

    private static final EntityDataAccessor<Boolean> ACTIVE = SynchedEntityData.defineId(IllagerGolemEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ANIMATION_START_TICK = SynchedEntityData.defineId(IllagerGolemEntity.class, EntityDataSerializers.INT);

    private static final int TIME_PER_ACTIVATION = 60;
    public static final int ELECTRICAL_CHARGE_DURATION = 8; // Power of 2, because the amount of frames of the sprite is 8 and can't be odd
    private static final int MAX_RADIUS = 10;

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

    }

    public static AttributeSupplier setAttributes() {
        return Raider.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0D)
                .add(Attributes.ATTACK_DAMAGE, 5f)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.55f)
                .add(Attributes.FOLLOW_RANGE, 35.0).build();
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

        if (this.tickCount % TIME_PER_ACTIVATION == 0) {
            setActive(true);
        }

        if (isActive() && this.tickCount % TIME_PER_ACTIVATION >= ELECTRICAL_CHARGE_DURATION) {
            setActive(false);
            visibleEntities.clear();
        }

        if (isActive()) {
            Vec3 currentPosition = this.position();
            AABB searchBox = new AABB(
                    currentPosition.x - MAX_RADIUS, currentPosition.y - MAX_RADIUS, currentPosition.z - MAX_RADIUS,
                    currentPosition.x + MAX_RADIUS, currentPosition.y + MAX_RADIUS, currentPosition.z + MAX_RADIUS
            );

            visibleEntities = this.level().getEntities(this, searchBox, entity -> !(entity instanceof Monster))
                    .stream()
                    .filter(this::hasLineOfSight)
                    .collect(Collectors.toList());
        }

    }

    public void setActive(boolean active) {
        if (active && !this.isActive()) {
            this.entityData.set(ANIMATION_START_TICK, this.tickCount);
        }

        this.entityData.set(ACTIVE, active);
    }

    public int getAnimationStartTick() {
        return this.entityData.get(ANIMATION_START_TICK);
    }

    public boolean isActive() {
        return this.entityData.get(ACTIVE);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ACTIVE, false);
        this.entityData.define(ANIMATION_START_TICK, 0);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 5, this::predicate));
        controllerRegistrar.add(new AnimationController<>(this, "attackcontroller", 1, this::attackPredicate));
    }

    private <T extends GeoAnimatable> PlayState attackPredicate(AnimationState<T> event) {
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