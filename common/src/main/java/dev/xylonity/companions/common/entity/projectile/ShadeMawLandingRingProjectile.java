package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.client.shader.ShadeMawLandingPostShaderSettings;
import dev.xylonity.companions.common.entity.BaseProjectile;
import dev.xylonity.companions.common.entity.companion.ShadeMawEntity;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.network.packets.ShadeMawLandingPostShaderS2C;
import dev.xylonity.companions.registry.CompanionsParticles;
import dev.xylonity.knightlib.common.entity.AbstractProjectile;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class ShadeMawLandingRingProjectile extends BaseProjectile {

    private static final EntityDataAccessor<Float> STRENGTH = SynchedEntityData.defineId(ShadeMawLandingRingProjectile.class, EntityDataSerializers.FLOAT);

    private boolean packetDispatched = false;
    private double impactRadius = 0d;

    public ShadeMawLandingRingProjectile(EntityType<? extends BaseProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setNoGravity(true);
        this.noPhysics = true;
        this.noCulling = true;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(STRENGTH, 1f);
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
            if (!packetDispatched) {
                packetDispatched = true;
                impact(serverLevel);
                sendPacket(serverLevel);
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
            if (living == owner || living == tameOwner || living == passenger) {
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

    private void sendPacket(ServerLevel server) {
        final float strength = getStrength();
        final int duration = 28 + (int) (10 * strength);
        final ShadeMawLandingPostShaderSettings settings = ShadeMawLandingPostShaderSettings.builder()
                .origin(new Vec3(getX(), getY() + 0.05d, getZ()))
                .durationTicks(duration)
                .speed(2.85f)
                .width(3f + 0.5f * strength)
                .glow(1 * strength)
                .chroma(0f)
                .intensity(1.0f)
                .radii(0.5f, 10 * strength)
                .colors(new Vec3(200/255f, 73/255f, 39/255f), new Vec3(255/255f, 73/255f, 39/255f))
                .column(7)
                .flash(0.1f)
                .build();

        for (final ServerPlayer player : server.players()) {
            if (player.distanceToSqr(position()) <= 96 * 96) {
                Companions.NETWORK.sendTo(player, ShadeMawLandingPostShaderS2C.TYPE.base(), new ShadeMawLandingPostShaderS2C(settings));
            }

        }

    }

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        return false;
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
