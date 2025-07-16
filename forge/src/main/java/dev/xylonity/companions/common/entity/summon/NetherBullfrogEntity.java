package dev.xylonity.companions.common.entity.summon;

import dev.xylonity.companions.common.entity.SummonFrogEntity;
import dev.xylonity.companions.common.ai.navigator.GroundNavigator;
import dev.xylonity.companions.common.entity.ai.cornelius.goal.SummonHopToTargetGoal;
import dev.xylonity.companions.common.entity.ai.cornelius.summon.goal.*;
import dev.xylonity.companions.common.entity.ai.generic.CompanionsSummonHurtTargetGoal;
import dev.xylonity.companions.common.util.interfaces.IFrogJumpUtil;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class NetherBullfrogEntity extends SummonFrogEntity implements IFrogJumpUtil {

    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private final RawAnimation SLASH = RawAnimation.begin().thenPlay("slash");
    private final RawAnimation SLASH2 = RawAnimation.begin().thenPlay("slash2");
    private final RawAnimation SPIN_SLASH = RawAnimation.begin().thenPlay("spin_slash");
    private final RawAnimation AIR_SLASH = RawAnimation.begin().thenPlay("air_slash");

    // attacktype: 0 none, 1 slash, 2 slash_2, 3 spin slash, 4 air slash

    public NetherBullfrogEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        return new GroundNavigator(this, pLevel);
    }

    public static AttributeSupplier setAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 60)
                .add(Attributes.ATTACK_DAMAGE, 5f)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.55f)
                .add(Attributes.FOLLOW_RANGE, 35.0).build();
    }

    @Override
    public void setCanAttack(boolean canAttack) {

    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource pDamageSource) {
        return CompanionsSounds.NETHER_BULLFROG_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return CompanionsSounds.NETHER_BULLFROG_IDLE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return CompanionsSounds.NETHER_BULLFROG_DEATH.get();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));

        this.goalSelector.addGoal(1, new NetherBullfrogAirSlashGoal(this, 40, 120));
        this.goalSelector.addGoal(1, new NetherBullfrogSpinSlashGoal(this, 40, 120));
        this.goalSelector.addGoal(1, new NetherBullfrogSlashRightGoal(this, 40, 120));
        this.goalSelector.addGoal(1, new NetherBullfrogSlashLeftGoal(this, 40, 120));

        this.goalSelector.addGoal(3, new SummonHopToTargetGoal<>(this, 0.65f));
        this.goalSelector.addGoal(4, new SummonHopToOwnerGoal<>(this, 0.725D, 6.0F, 2.0F, false));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new CompanionsSummonHurtTargetGoal(this));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 2, this::predicate));
    }

    @Override
    protected SoundEvent jumpSound() {
        return CompanionsSounds.NETHER_BULLFROG_JUMP.get();
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        if (getAttackType() == 1) {
            event.setAnimation(SLASH);
        } else if (getAttackType() == 2) {
            event.setAnimation(SLASH2);
        } else if (getAttackType() == 3) {
            event.setAnimation(SPIN_SLASH);
        } else if (getAttackType() == 4) {
            event.setAnimation(AIR_SLASH);
        } else if (event.isMoving()) {
            event.setAnimation(WALK);
        } else {
            event.setAnimation(IDLE);
        }

        return PlayState.CONTINUE;
    }

}