package dev.xylonity.companions.common.entity.custom;

import dev.xylonity.companions.common.ai.navigator.GroundNavigator;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
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

import java.util.Optional;

public class HostilePuppetGlove extends Monster implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");

    private final RawAnimation BROOM_IDLE = RawAnimation.begin().thenPlay("broom_idle");

    public HostilePuppetGlove(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.moveControl = new net.minecraft.world.entity.ai.control.MoveControl(this);
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

    @Override
    public InteractionResult interactAt(Player pPlayer, Vec3 pVec, InteractionHand pHand) {
        if (pVec.y - this.getBoundingBox().minY < (this.getBoundingBox().getYsize() / 2)) {
            return InteractionResult.FAIL;
        }

        return super.interactAt(pPlayer, pVec, pHand);
    }

    // TODO: This is meant for the full puppet hurt interaction
    //@Override
    //public boolean hurt(DamageSource pSource, float pAmount) {
    //
    //    if (pSource.getEntity() instanceof Player player) {
    //        Optional<Vec3> h = getBoundingBox().clip(player.getEyePosition(), player.getEyePosition(1f).add(player.getLookAngle().scale(5)));
    //        if (h.isPresent()) {
    //            if (h.get().y - this.getBoundingBox().minY < (this.getBoundingBox().getYsize() / 2)) {
    //                return false;
    //            }
    //        }
    //    }
    //
    //    return super.hurt(pSource, pAmount);
    //}

    @Override
    protected void registerGoals() {
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 1, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        if (event.isMoving()) {
            event.setAnimation(WALK);
        } else {
            event.setAnimation(BROOM_IDLE);
        }

        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

}
