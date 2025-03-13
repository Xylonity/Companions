package dev.xylonity.companions.common.entity.custom;

import dev.xylonity.companions.common.ai.navigator.FlyingNavigator;
import dev.xylonity.companions.common.entity.ai.soul_mage.control.GoldenAllayMoveControl;
import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;

public class GoldenAllayEntity extends PathfinderMob implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("fly_idle");
    private final RawAnimation FLY = RawAnimation.begin().thenPlay("fly");
    private final RawAnimation LEVITATE = RawAnimation.begin().thenPlay("levitate");

    public GoldenAllayEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

        this.moveControl = new GoldenAllayMoveControl(this);
    }

    public static AttributeSupplier setAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0D)
                .add(Attributes.ATTACK_DAMAGE, 5f)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.55f)
                .add(Attributes.FOLLOW_RANGE, 35.0).build();
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        FlyingNavigator navigation = new FlyingNavigator(this, this.level());
        navigation.setCanOpenDoors(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new GoldenAllayRandomMoveGoal());
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Monster.class, 6.0F, 0.6f, 1));
    }

    public void tick() {
        this.noPhysics = true;
        super.tick();
        this.noPhysics = false;
        this.setNoGravity(true);

        if (tickCount % 20 == 0) {
            this.level().addParticle(CompanionsParticles.GOLDEN_ALLAY_TRAIL.get(), getX(), getY(), getZ(), 0.35, 0.35, 0.35);
        }

    }

    private class GoldenAllayRandomMoveGoal extends Goal {
        public GoldenAllayRandomMoveGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            return !GoldenAllayEntity.this.getMoveControl().hasWanted() && GoldenAllayEntity.this.random.nextInt(reducedTickDelay(7)) == 0;
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void tick() {
            BlockPos $$0 = GoldenAllayEntity.this.getOnPos();

            for(int $$1 = 0; $$1 < 3; ++$$1) {
                BlockPos $$2 = $$0.offset(GoldenAllayEntity.this.random.nextInt(15) - 7, GoldenAllayEntity.this.random.nextInt(7) - 3, GoldenAllayEntity.this.random.nextInt(15) - 7);
                if (GoldenAllayEntity.this.level().isEmptyBlock($$2)) {
                    GoldenAllayEntity.this.moveControl.setWantedPosition((double)$$2.getX() + 0.5, (double)$$2.getY() + 0.5, (double)$$2.getZ() + 0.5, 0.25);
                    if (GoldenAllayEntity.this.getTarget() == null) {
                        GoldenAllayEntity.this.getLookControl().setLookAt((double)$$2.getX() + 0.5, (double)$$2.getY() + 0.5, (double)$$2.getZ() + 0.5, 180.0F, 20.0F);
                    }
                    break;
                }
            }

        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 6, this::predicate));
        controllerRegistrar.add(new AnimationController<>(this, "levitateController", 1, this::levitatePredicate));
    }

    private <T extends GeoAnimatable> PlayState levitatePredicate(AnimationState<T> event) {
        event.getController().setAnimation(LEVITATE);
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        if (event.isMoving()) {
            event.getController().setAnimation(FLY);
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