package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.common.entity.BaseProjectile;
import dev.xylonity.companions.common.entity.ShadeEntity;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.*;

public class ShadeSwordImpactProjectile extends BaseProjectile {

    private final RawAnimation SHOOT = RawAnimation.begin().thenPlay("shoot");

    private static final EntityDataAccessor<Direction> DIRECTION = SynchedEntityData.defineId(ShadeSwordImpactProjectile.class, EntityDataSerializers.DIRECTION);

    public ShadeSwordImpactProjectile(EntityType<? extends ShadeSwordImpactProjectile> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
        this.noPhysics = true;
        this.noCulling = true;
    }

    @Override
    protected int baseLifetime() {
        return 35;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        return pDistance < 32 * 32;
    }

    @Override
    public void tick() {
        super.tick();

        if (tickCount % 8 == 0) {
            double dx = (this.random.nextDouble() - 0.5) * getBbWidth();
            double dy = (this.random.nextDouble() - 0.5) * getBbWidth();
            double dz = (this.random.nextDouble() - 0.5) * getBbWidth();
            if (this.level() instanceof ServerLevel level) {
                level.sendParticles(CompanionsParticles.SHADE_SUMMON.get(), this.getX(), this.getY() + getBbHeight() * Math.random(), this.getZ(), 1, dx, dy, dz, 0.1);
            }
        }

        Vec3 v = this.getDeltaMovement();
        this.setPos(this.getX() + v.x, this.getY() + v.y, this.getZ() + v.z);

        if (!this.level().isClientSide) {
            for (LivingEntity victim : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox(), e -> !Util.areEntitiesLinked(e, this))) {
                if (this.getOwner() != null && this.getOwner() instanceof ShadeEntity entity) {
                    entity.doHurtTarget(victim);
                }
            }
        }

    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DIRECTION, Direction.NORTH);
    }

    public @NotNull Direction getDirection() {
        return this.entityData.get(DIRECTION);
    }

    public void setDirection(Direction direction) {
        this.entityData.set(DIRECTION, direction);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        event.getController().setAnimation(SHOOT);
        return PlayState.CONTINUE;
    }
}
