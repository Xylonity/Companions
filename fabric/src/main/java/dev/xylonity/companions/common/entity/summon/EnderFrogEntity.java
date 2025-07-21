package dev.xylonity.companions.common.entity.summon;

import dev.xylonity.companions.common.ai.navigator.FlyingNavigator;
import dev.xylonity.companions.common.entity.CompanionSummonEntity;
import dev.xylonity.companions.common.entity.ai.cornelius.summon.goal.EnderFrogFollowOwnerGoal;
import dev.xylonity.companions.common.entity.ai.cornelius.summon.goal.EnderFrogHealGoal;
import dev.xylonity.companions.common.entity.ai.cornelius.summon.goal.EnderFrogLevitateGoal;
import dev.xylonity.companions.common.entity.ai.generic.CompanionsSummonHurtTargetGoal;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.*;

public class EnderFrogEntity extends CompanionSummonEntity {

    private final RawAnimation FLY = RawAnimation.begin().thenPlay("fly");
    private final RawAnimation HEAL = RawAnimation.begin().thenPlay("heal");
    private final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");

    // 0 none, 1 heal, 2 attack

    public EnderFrogEntity(EntityType<? extends CompanionSummonEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        FlyingNavigator navigation = new FlyingNavigator(this, this.level());
        navigation.setCanOpenDoors(true);
        navigation.setCanPassDoors(true);

        this.navigation = navigation;
        this.setNoGravity(true);
        this.noPhysics = true;
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, @NotNull DamageSource pSource) {
        return false;
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        FlyingNavigator navigation = new FlyingNavigator(this, this.level());
        navigation.setCanOpenDoors(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    public static AttributeSupplier setAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 45)
                .add(Attributes.ATTACK_DAMAGE, 5f)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.55f)
                .add(Attributes.FOLLOW_RANGE, 35.0).build();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(1, new EnderFrogHealGoal(this, 20, 100));
        this.goalSelector.addGoal(1, new EnderFrogLevitateGoal(this, 20, 100));

        this.goalSelector.addGoal(4, new EnderFrogFollowOwnerGoal(this, 0.6D, 3.0F, 7.0F, 0.18f));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new CompanionsSummonHurtTargetGoal(this));
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return CompanionsSounds.MID_FROG_DEATH.get();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return CompanionsSounds.MID_FROG_IDLE.get();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 2, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {

       if (this.getAttackType() == 1) {
           event.getController().setAnimation(HEAL);
       } else if (this.getAttackType() == 2) {
           event.getController().setAnimation(ATTACK);
       } else {
           event.getController().setAnimation(FLY);
       }

        return PlayState.CONTINUE;
    }

}