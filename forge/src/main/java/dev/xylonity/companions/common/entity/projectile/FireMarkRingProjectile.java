package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.common.entity.BaseProjectile;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsEffects;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.*;

public class FireMarkRingProjectile extends BaseProjectile {
    private final RawAnimation ACTIVATE = RawAnimation.begin().thenPlay("activate");

    private final double RADIUS = CompanionsConfig.FIRE_MARK_EFFECT_RADIUS;

    public FireMarkRingProjectile(EntityType<? extends BaseProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            for (LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class, new AABB(getX() - RADIUS, getY() - 1, getZ() - RADIUS, getX() + RADIUS, getY() + 1, getZ() + RADIUS), e -> !Util.areEntitiesLinked(this, e))) {
                if (!entity.hasEffect(CompanionsEffects.FIRE_MARK.get())) {
                    entity.addEffect(new MobEffectInstance(CompanionsEffects.FIRE_MARK.get(), level().random.nextInt(100, 400), 0, true, true));
                    entity.playSound(CompanionsSounds.SPELL_HIT_MARK.get());
                }
            }

        }

        if (tickCount >= getLifetime()) this.remove(RemovalReason.DISCARDED);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", this::predicate));
    }

    @Override
    protected int baseLifetime() {
        return 30;
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        event.getController().setAnimation(ACTIVATE);
        return PlayState.CONTINUE;
    }

}
