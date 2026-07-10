package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.common.entity.BaseProjectile;
import dev.xylonity.companions.common.entity.companion.ShadeMawEntity;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsParticles;
import dev.xylonity.knightlib.common.entity.AbstractProjectile;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import org.jetbrains.annotations.NotNull;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.PlayState;

public class ShadeMawLandingRingProjectile extends BaseProjectile {

    private static final EntityDataAccessor<Float> STRENGTH = SynchedEntityData.defineId(ShadeMawLandingRingProjectile.class, EntityDataSerializers.FLOAT);

    private boolean impacted = false;
    private double impactRadius = 0d;

    public ShadeMawLandingRingProjectile(EntityType<? extends BaseProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setNoGravity(true);
        this.noPhysics = true;
        this.noCulling = true;
    }

    @Override
    protected void defineSynchedData(@NotNull SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(STRENGTH, 1f);
    }

    public float getStrength() {
        return this.entityData.get(STRENGTH);
    }

    public void setStrength(float strength) {
        this.entityData.set(STRENGTH, Math.max(0f, Math.min(1f, strength)));
    }

    @Override
    public void tick() {

        double x = getX();
        double y = getY();
        double z = getZ();

        super.tick();

        this.setPos(x, y, z);

        if (level() instanceof ServerLevel serverLevel) {
            if (!impacted) {
                impacted = true;
                impact(serverLevel);
                setLifetime(computeLifetime());
            }

            spawnParticles(serverLevel);
        }

    }

    private int computeLifetime() {
        final double radius = 2.5d + 4.5d * getStrength();
        return 3 * Mth.ceil(radius) + 1;
    }

    private void impact(ServerLevel server) {
        final float strength = getStrength();
        if (strength <= 0f) {
            return;
        }

        final double radius = 10 * strength;
        this.impactRadius = radius;

        final Entity owner = getOwner();
        LivingEntity tameOwner = null;
        LivingEntity passenger = null;
        if (owner instanceof ShadeMawEntity shadeMaw) {
            tameOwner = shadeMaw.getOwner();
            passenger = shadeMaw.getControllingPassenger();
        }

        final AABB aabb = new AABB(blockPosition()).inflate(radius);
        for (final Entity entity : server.getEntitiesOfClass(Entity.class, aabb)) {
            if (!(entity instanceof LivingEntity living)) {
                continue;
            }
            if (living == owner || living == tameOwner || living == passenger || Util.areEntitiesLinked(living, tameOwner) || Util.areTeammates(living, tameOwner)) {
                continue;
            }

            final double dx = living.getX() - this.getX();
            final double dz = living.getZ() - this.getZ();
            final double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
            if (horizontalDistance > radius) {
                continue;
            }

            final float falloff = 1f - (float) (horizontalDistance / radius);
            final double knockbackStrength = ((0.4d + 0.9d * strength) * falloff) * 2;
            living.knockback(knockbackStrength, -dx, -dz);
            living.setDeltaMovement(living.getDeltaMovement().add(0d, 0.18d + 0.22d * strength * falloff, 0d));

            final float damage = (float) (CompanionsConfig.SHADOW_MAW_LANDING_DAMAGE * 0.4d * strength * falloff);
            if (damage > 0.35f && owner instanceof LivingEntity attacker) {
                living.hurt(server.damageSources().mobAttack(attacker), damage * 3);
            }

        }

    }

    private void spawnParticles(ServerLevel server) {
        final double radius = 3d + tickCount;
        if (impactRadius > 0d && radius > impactRadius) {
            return;
        }

        final int count = (int) ((6 + radius * 2) / 2f);
        for (int i = 0; i < count; i++) {
            final double angle = server.random.nextDouble() * Math.PI * 2d;
            final double distanceRadius = radius - 0.5d + server.random.nextDouble();
            final double px = getX() + Math.cos(angle) * distanceRadius;
            final double pz = getZ() + Math.sin(angle) * distanceRadius;
            final double vx = Math.cos(angle) * 0.08d;
            final double vz = Math.sin(angle) * 0.08d;
            if (server.random.nextFloat() < 0.2f) {
                server.sendParticles(CompanionsParticles.SHADE_SUMMON.get(), px, getY(), pz, 1, vx, 0.015d, vz, 0d);
            }
            else {
                server.sendParticles(CompanionsParticles.SHADE_TRAIL.get(), px, getY(), pz, 1, vx, 0.015d, vz, 0d);
            }

        }

    }

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        return pDistance < 96 * 96;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        ;;
    }

    @Override
    protected int baseLifetime() {
        return 5;
    }

}
