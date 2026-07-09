package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.common.entity.BaseProjectile;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.config.CompanionsConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class HolyRingProjectile extends BaseProjectile implements GeoEntity {

    public static final int RING_LIFETIME = 27;
    public static final float MAX_RADIUS = 10f;

    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");

    private final Set<UUID> affected = new HashSet<>();

    public HolyRingProjectile(EntityType<? extends BaseProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.noPhysics = true;
        this.noCulling = true;
    }

    public static float radius(float ageInTicks) {
        float progress = Mth.clamp(ageInTicks / RING_LIFETIME, 0f, 1f);
        return MAX_RADIUS * (1f - (1f - progress) * (1f - progress));
    }

    @Override
    public void tick() {

        final double px = getX();
        final double py = getY();
        final double pz = getZ();

        super.tick();

        this.setPos(px, py, pz);

        if (level().isClientSide) {
            return;
        }

        final float currentRadius = radius(tickCount);

        for (final LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(MAX_RADIUS, 4, MAX_RADIUS))) {
            if (entity == this.getOwner() || affected.contains(entity.getUUID())) {
                continue;
            }

            final double dx = entity.getX() - getX();
            final double dz = entity.getZ() - getZ();
            if (dx * dx + dz * dz > currentRadius * currentRadius) {
                continue;
            }

            affected.add(entity.getUUID());

            if (Util.areEntitiesLinked(this, entity)) {
                entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, random.nextInt(80, 240), 1, true, true, true));
                entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, random.nextInt(150, 500), 0, true, true, true));
                spawnBlessParticles(entity);
            }
            else if (entity instanceof Enemy || isOwnersTarget(entity)) {
                final float damage = (float) CompanionsConfig.TEDDY_HOLY_NOVA_DAMAGE * (entity.isInvertedHealAndHarm() ? 1.5f : 1f);
                entity.hurt(damageSources().indirectMagic(this, getOwner()), damage);
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, random.nextInt(60, 140), 1, true, true, true));
                entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, random.nextInt(60, 120), 0, true, true, true));
            }

        }

        if (tickCount % 2 == 0 && level() instanceof ServerLevel level) {
            final int num = Math.max(6, (int) (currentRadius * 2.5f));
            for (int i = 0; i < num; i++) {
                double angle = this.random.nextDouble() * Math.PI * 2;
                level.sendParticles(ParticleTypes.END_ROD, getX() + Math.cos(angle) * currentRadius, getY() + 0.25, getZ() + Math.sin(angle) * currentRadius, 1, 0, 0.05, 0, 0.015);
            }

        }

    }

    private boolean isOwnersTarget(LivingEntity e) {
        return getOwner() instanceof net.minecraft.world.entity.Mob mob && mob.getTarget() == e;
    }

    private void spawnBlessParticles(LivingEntity e) {
        if (level() instanceof ServerLevel level) {
            for (int i = 0; i < 10; i++) {
                final double dx = (this.random.nextDouble() - 0.5) * 1.25;
                final double dy = this.random.nextDouble() * 1.25;
                final double dz = (this.random.nextDouble() - 0.5) * 1.25;
                level.sendParticles(ParticleTypes.END_ROD, e.getX(), e.getY() + 0.25, e.getZ(), 1, dx, dy, dz, 0.08);
            }

        }

    }

    @Override
    protected int baseLifetime() {
        return RING_LIFETIME;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        event.getController().setAnimation(IDLE);
        return PlayState.CONTINUE;
    }

    @Override
    public boolean shouldRender(double pX, double pY, double pZ) {
        return true;
    }

}
