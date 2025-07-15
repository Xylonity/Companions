package dev.xylonity.companions.common.entity.companion;

import dev.xylonity.companions.common.ai.navigator.GroundNavigator;
import dev.xylonity.companions.common.entity.CompanionEntity;
import dev.xylonity.companions.common.entity.ShadeEntity;
import dev.xylonity.companions.common.entity.ai.generic.CompanionsHurtTargetGoal;
import dev.xylonity.companions.common.entity.ai.shade.sword.ShadeSwordMoveControl;
import dev.xylonity.companions.common.entity.ai.shade.sword.goal.*;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsParticles;
import dev.xylonity.knightlib.common.api.TickScheduler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class ShadeSwordEntity extends ShadeEntity {

    private final RawAnimation SPAWN = RawAnimation.begin().thenPlay("spawn");
    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack"); // AttackType 1
    private final RawAnimation ATTACK2 = RawAnimation.begin().thenPlay("attack2"); // AttackType 2
    private final RawAnimation GROUND_ATTACK = RawAnimation.begin().thenPlay("nail_to_ground"); // AttackType 3
    private final RawAnimation SPIN_ATTACK = RawAnimation.begin().thenPlay("spin"); // AttackType 4

    private static final int ANIMATION_SPAWN_MAX_TICKS = 72;
    private boolean hasSpawned;

    public ShadeSwordEntity(EntityType<? extends CompanionEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.noPhysics = true;
        this.hasSpawned = false;
        this.setNoGravity(true);
        this.noCulling = true;

        this.moveControl = new ShadeSwordMoveControl(this);
    }

    @Override
    public int getMaxLifetime() {
        return CompanionsConfig.SHADOW_SWORD_LIFETIME;
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        return new GroundNavigator(this, pLevel);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new ShadeSwordMeleeAttackGoal(this, 20, 60));
        this.goalSelector.addGoal(1, new ShadeSwordSpinAttackGoal(this, 70, 200));
        this.goalSelector.addGoal(1, new ShadeSwordGroundAttackGoal(this, 120, 300));

        this.goalSelector.addGoal(2, new ShadeSwordFollowTargetGoal(this));
        this.goalSelector.addGoal(3, new ShadeSwordFollowOwnerGoal(this));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new CompanionsHurtTargetGoal(this));
    }

    public static AttributeSupplier setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, CompanionsConfig.SHADOW_SWORD_MAX_LIFE)
                .add(Attributes.ATTACK_DAMAGE, CompanionsConfig.SHADOW_SWORD_DAMAGE)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.55f)
                .add(Attributes.FOLLOW_RANGE, 35.0).build();
    }

    @Override
    public void tick() {
        super.tick();

        if (!hasSpawned) {
            TickScheduler.scheduleServer(level(), () -> setIsSpawning(false), ANIMATION_SPAWN_MAX_TICKS);
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

        setLifetime(getLifetime() - 1);
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

    @Nullable
    @Override
    public LivingEntity getOwner() {
        return super.getOwner();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 2, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {

        if (isSpawning()) {
            event.setAnimation(SPAWN);
        } else if (getAttackType() == 1) {
            event.setAnimation(ATTACK);
        } else if (getAttackType() == 2) {
            event.setAnimation(ATTACK2);
        } else if (getAttackType() == 3) {
            event.setAnimation(GROUND_ATTACK);
        } else if (getAttackType() == 4) {
            event.setAnimation(SPIN_ATTACK);
        } else {
            event.setAnimation(IDLE);
        }

        return PlayState.CONTINUE;
    }

    @Override
    public void aiStep() {
        setNoMovement(getAttackType() != 0);
        super.aiStep();
    }

}