package dev.xylonity.companions.common.entity.summon;

import dev.xylonity.companions.common.ai.navigator.GroundNavigator;
import dev.xylonity.companions.common.entity.CompanionSummonEntity;
import dev.xylonity.companions.common.entity.ai.cornelius.summon.goal.EmberPoleApproachTargetGoal;
import dev.xylonity.companions.common.entity.ai.generic.CompanionSummonFollowOwnerGoal;
import dev.xylonity.companions.common.entity.ai.generic.CompanionsSummonHurtTargetGoal;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.Random;

public class EmberPoleEntity extends CompanionSummonEntity {

    private final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private final RawAnimation EXPLODE = RawAnimation.begin().thenPlay("explode");

    private static final EntityDataAccessor<Boolean> IS_EXPLODING = SynchedEntityData.defineId(EmberPoleEntity.class, EntityDataSerializers.BOOLEAN);

    private static final int ANIMATION_EXPLODE_TICKS = 55;

    private int explodeCounter;

    public EmberPoleEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.explodeCounter = -1;
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        return new GroundNavigator(this, pLevel);
    }

    public static AttributeSupplier setAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10)
                .add(Attributes.ATTACK_DAMAGE, 5f)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.55f)
                .add(Attributes.FOLLOW_RANGE, 35.0).build();
    }

    public void setIsExploding(boolean exploding) {
        this.entityData.set(IS_EXPLODING, exploding);
    }

    public boolean isExploding() {
        return this.entityData.get(IS_EXPLODING);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_EXPLODING, false);
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {

            if (this.getTarget() != null) {
                if (distanceToSqr(getTarget()) < 9) {
                    this.setIsExploding(true);
                }
            }

            if (isExploding() && explodeCounter == -1) explodeCounter = 0;

            setNoMovement(isExploding());
            explode();

            if (explodeCounter >= 0) {
                explodeCounter++;
                if (explodeCounter >= ANIMATION_EXPLODE_TICKS) {
                    discard();
                }
            }

        }

    }

    private void explode() {
        if (explodeCounter == 3) {
            for (int i = 0; i < 20; i++) {
                double dx = (this.random.nextDouble() - 0.5) * 2.0;
                double dy = (this.random.nextDouble() - 0.5) * 2.0;
                double dz = (this.random.nextDouble() - 0.5) * 2.0;
                if (this.level() instanceof ServerLevel level) {
                    if (level.random.nextFloat() < 0.7f) level.sendParticles(ParticleTypes.POOF, this.getX(), this.getY() + getBbHeight() * 0.5, this.getZ(), 1, dx, dy, dz, 0.1);
                    if (level.random.nextFloat() < 0.65f) level.sendParticles(CompanionsParticles.EMBER_POLE_EXPLOSION.get(), this.getX(), this.getY() + getBbHeight() * 1.5, this.getZ(), 1, dx, dy, dz, 0.08);
                }
            }

            for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class, new AABB(blockPosition()).inflate(3))) {
                if (!Util.areEntitiesLinked(e, this)) {
                    this.doHurtTarget(e);
                    if (new Random().nextFloat() < 0.75) e.setSecondsOnFire(new Random().nextInt(3, 10));
                }
            }
        }

    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(1, new EmberPoleApproachTargetGoal(this, 0.45, 0.4f, 1.25f));

        this.goalSelector.addGoal(3, new CompanionSummonFollowOwnerGoal(this, 0.6D, 6.0F, 2.0F, false));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new CompanionsSummonHurtTargetGoal(this));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 2, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        if (isExploding()) {
            event.setAnimation(EXPLODE);
        } else if (event.isMoving()) {
            event.setAnimation(WALK);
        } else {
            event.setAnimation(IDLE);
        }

        return PlayState.CONTINUE;
    }

}