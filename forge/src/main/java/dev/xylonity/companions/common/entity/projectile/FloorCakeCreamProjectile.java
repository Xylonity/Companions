package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.common.entity.custom.CroissantDragonEntity;
import dev.xylonity.companions.registry.CompanionsEffects;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Random;

public class FloorCakeCreamProjectile extends Projectile implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<Integer> LIFETIME = SynchedEntityData.defineId(FloorCakeCreamProjectile.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> SIZE = SynchedEntityData.defineId(FloorCakeCreamProjectile.class, EntityDataSerializers.FLOAT);

    public FloorCakeCreamProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        if (!this.level().isClientSide) {
            this.entityData.set(SIZE, new Random().nextFloat(1, 2));
            this.entityData.set(LIFETIME, new Random().nextInt(80, 180));
        }
    }

    @Override
    public void tick() {
        super.tick();

            AABB effectArea = new AABB(
                    getX() - getSize(), getY() - 0.5, getZ() - getSize(),
                    getX() + getSize(), getY() + 0.5, getZ() + getSize()
            );

            List<LivingEntity> entities = level().getEntitiesOfClass(LivingEntity.class, effectArea);

            for (LivingEntity entity : entities) {
                Vec3 v = entity.getDeltaMovement();
                entity.setDeltaMovement(v.x * 0.25, v.y, v.z * 0.25);
            }


        if (tickCount >= getLifetime()) this.remove(RemovalReason.DISCARDED);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(LIFETIME, 200);
        this.entityData.define(SIZE, 1.2f);
    }

    public int getLifetime() {
        return this.entityData.get(LIFETIME);
    }

    public float getSize() {
        return this.entityData.get(SIZE);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) { ;; }

}
