package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.common.entity.BaseProjectile;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsEffects;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class FireMarkRingProjectile extends BaseProjectile implements GeoEntity {
    private final RawAnimation ACTIVATE = RawAnimation.begin().thenPlay("activate");

    private static final EntityDataAccessor<Boolean> MAY_AFFECT_OWNER = SynchedEntityData.defineId(FireMarkRingProjectile.class, EntityDataSerializers.BOOLEAN);
    // This way we prevent the fire mark mage attack from affecting the mage's owner
    private static final EntityDataAccessor<String> MAGE_OWNER_UUID = SynchedEntityData.defineId(FireMarkRingProjectile.class, EntityDataSerializers.STRING);

    private final double RADIUS = CompanionsConfig.FIRE_MARK_EFFECT_RADIUS;

    public FireMarkRingProjectile(EntityType<? extends BaseProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            List<LivingEntity> entities = level().getEntitiesOfClass(LivingEntity.class,
            new AABB(getX() - RADIUS, getY() - 1, getZ() - RADIUS, getX() + RADIUS, getY() + 1, getZ() + RADIUS),
                    e -> {
                        if (this.mayAffectOwner() && e.equals(getOwner())) {
                            return true;
                        }

                        if (!this.mayAffectOwner() && e.equals(getOwner())) {
                            return false;
                        }

                        if (e.hasEffect(CompanionsEffects.FIRE_MARK.get())) {
                            return false;
                        }

                        if (this.mageOwnerUUID().equals(e.getUUID().toString())) {
                            return false;
                        }

                        return true;
            });

            for (LivingEntity entity : entities) {
                entity.addEffect(new MobEffectInstance(CompanionsEffects.FIRE_MARK.get(), 100, 0, true, true));
            }
        }

        if (tickCount >= getLifetime()) this.remove(RemovalReason.DISCARDED);
    }

    public boolean mayAffectOwner() {
        return this.entityData.get(MAY_AFFECT_OWNER);
    }

    public void setmayAffectOwner(boolean c) {
        this.entityData.set(MAY_AFFECT_OWNER, c);
    }

    public String mageOwnerUUID() {
        return this.entityData.get(MAGE_OWNER_UUID);
    }

    public void setMageOwnerUUID(String s) {
        this.entityData.set(MAGE_OWNER_UUID, s);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(MAY_AFFECT_OWNER, false);
        this.entityData.define(MAGE_OWNER_UUID, "");
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
