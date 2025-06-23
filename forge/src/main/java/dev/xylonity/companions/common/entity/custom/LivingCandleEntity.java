package dev.xylonity.companions.common.entity.custom;

import dev.xylonity.companions.common.ai.navigator.GroundNavigator;
import dev.xylonity.companions.common.entity.CompanionEntity;
import dev.xylonity.companions.common.entity.CompanionSummonEntity;
import dev.xylonity.companions.common.event.CompanionsEntityTracker;
import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;

public class LivingCandleEntity extends CompanionSummonEntity {
    private final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");

    public LivingCandleEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.moveControl = new net.minecraft.world.entity.ai.control.MoveControl(this);
        this.tickCount = this.getRandom().nextInt(6);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        return new GroundNavigator(this, pLevel);
    }

    public static AttributeSupplier setAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0D)
                .add(Attributes.ATTACK_DAMAGE, 5f)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.55f)
                .add(Attributes.FOLLOW_RANGE, 35.0)
                .build();
    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        return getOwnerUUID() == null ? null : (LivingEntity) CompanionsEntityTracker.getEntityByUUID(getOwnerUUID());
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FollowOwnerGoal(this, 0.6D, 2.0F, 50.0F, false));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.6D));
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (!this.isTame() || this.getOwner() == null) {
                ServerLevel serverLevel = (ServerLevel) this.level();
                findNearestSoulMage(serverLevel);
            }
        }
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 3) {
            spawnDeadParticles();
        } else {
            super.handleEntityEvent(pId);
        }
    }

    private void spawnDeadParticles() {
        for (int i = 0; i < 15; i++) {
            double dx = ((this.random.nextDouble() - 0.5) * 2.0) * 0.2;
            double dy = ((this.random.nextDouble() - 0.5) * 2.0) * 0.2;
            double dz = ((this.random.nextDouble() - 0.5) * 2.0) * 0.2;
            level().addParticle(ParticleTypes.POOF, this.getX(), this.getY() + getBbHeight() * 0.5, this.getZ(), dx * 1.05, dy * 1.05, dz * 1.05);
            if (i % 5 == 0) level().addParticle(CompanionsParticles.GOLDEN_ALLAY_TRAIL.get(), this.getX(), this.getY() + getBbHeight() * 0.5, this.getZ(), dx, dy,dz);
        }
    }

    public void doKill() {
        if (this.level().isClientSide) {
            spawnDeadParticles();
        } else {
            this.level().playSound(null, blockPosition(), SoundEvents.ENDER_EYE_DEATH, SoundSource.BLOCKS, 1f, 1f);
            this.level().broadcastEntityEvent(this, (byte) 3);
        }

        this.discard();
    }

    private void findNearestSoulMage(ServerLevel serverLevel) {
        List<SoulMageEntity> nearbyMages = serverLevel.getEntitiesOfClass(SoulMageEntity.class, this.getBoundingBox().inflate(20.0D), EntitySelector.NO_SPECTATORS);

        if (!nearbyMages.isEmpty()) {
            SoulMageEntity closestMage = nearbyMages.get(0);
            double closestDistance = this.distanceToSqr(closestMage);
            for (int i = 1; i < nearbyMages.size(); i++) {
                SoulMageEntity mage = nearbyMages.get(i);
                double distance = this.distanceToSqr(mage);
                if (distance < closestDistance) {
                    closestMage = mage;
                    closestDistance = distance;
                }
            }

            if (closestMage.getCandleCount() < SoulMageEntity.MAX_CANDLES_COUNT) {
                this.setOwnerUUID(closestMage.getUUID());
                closestMage.setCandleCount(closestMage.getCandleCount() + 1);
                closestMage.candles.add(this);
                this.setTame(true);
            }

        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 1, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        if (event.isMoving()) {
            event.setAnimation(WALK);
        } else {
            event.setAnimation(IDLE);
        }

        return PlayState.CONTINUE;
    }

}
