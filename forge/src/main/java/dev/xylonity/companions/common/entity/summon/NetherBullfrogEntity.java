package dev.xylonity.companions.common.entity.summon;

import dev.xylonity.companions.common.ai.navigator.GroundNavigator;
import dev.xylonity.companions.common.entity.CompanionSummonEntity;
import dev.xylonity.companions.common.entity.ai.cornelius.summon.goal.SummonHopToOwnerGoal;
import dev.xylonity.companions.common.entity.ai.generic.CompanionsSummonHurtTargetGoal;
import dev.xylonity.companions.common.util.interfaces.IFrogJumpUtil;
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
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class NetherBullfrogEntity extends CompanionSummonEntity implements IFrogJumpUtil {

    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private final RawAnimation SLASH = RawAnimation.begin().thenPlay("slash");
    private final RawAnimation SLASH2 = RawAnimation.begin().thenPlay("slash2");
    private final RawAnimation SPIN_SLASH = RawAnimation.begin().thenPlay("spin_slash");
    private final RawAnimation AIR_SLASH = RawAnimation.begin().thenPlay("air_slash");

    public NetherBullfrogEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
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

    @Override
    public void setCanAttack(boolean canAttack) {

    }

    @Override
    public int getAttackType() {
        return 0;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));

        this.goalSelector.addGoal(4, new SummonHopToOwnerGoal<>(this, 0.725D, 6.0F, 2.0F, false));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new CompanionsSummonHurtTargetGoal(this));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 2, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        //if (this.getMainAction() == 0) {
        //    event.setAnimation(getSitVariation() == 0 ? SIT : SIT2);
        //} else
        if (getAttackType() == 1) {
            event.setAnimation(SLASH);
        } else if (getAttackType() == 2) {
            event.setAnimation(SPIN_SLASH);
        } else if (getAttackType() == 3) {
            event.setAnimation(AIR_SLASH);
        } else if (event.isMoving()) {
            event.setAnimation(WALK);
        } else {
            event.setAnimation(IDLE);
        }

        return PlayState.CONTINUE;
    }

}