package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.common.entity.BaseProjectile;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsParticles;
import dev.xylonity.knightlib.registry.KnightLibParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.*;

public class HealRingProjectile extends BaseProjectile implements GeoEntity {
    private final RawAnimation HEAL = RawAnimation.begin().thenPlay("heal");

    private boolean hasHealed = false;

    public HealRingProjectile(EntityType<? extends BaseProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.noPhysics = true;
        this.noCulling = true;
    }

    @Override
    public void tick() {
        super.tick();

        Entity owner = this.getOwner();

        if (owner == null) this.remove(RemovalReason.DISCARDED);

        if (owner != null && !owner.isRemoved() && owner instanceof LivingEntity) {

            if (tickCount % 10 == 0 && !hasHealed) {
                for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class, owner.getBoundingBox().inflate(2))) {
                    if (e.isInvertedHealAndHarm()) {
                        e.hurt(damageSources().magic(), (float) CompanionsConfig.HEAL_RING_HEALING);
                        spawnParticles(e, CompanionsParticles.SHADE_SUMMON.get());
                    } else {
                        e.heal((float) CompanionsConfig.HEAL_RING_HEALING);
                        spawnParticles(e, KnightLibParticles.STARSET.get());
                    }
                }

                hasHealed = true;
            }

            Vec3 targetPos = new Vec3(owner.getX(), owner.getY(), owner.getZ());
            Vec3 offset = targetPos.subtract(this.position());
            Vec3 velocity = this.getDeltaMovement();

            double K = 0.60;
            double C = 2.0 * Math.sqrt(K);

            Vec3 accel = offset.scale(K).subtract(velocity.scale(C));
            velocity = velocity.add(accel);

            this.setDeltaMovement(velocity);
            this.move(MoverType.SELF, velocity);
        }

    }

    private void spawnParticles(Entity e, SimpleParticleType p) {
        for (int i = 0; i < 10; i++) {
            double dx = (this.random.nextDouble() - 0.5) * 1.25;
            double dy = (this.random.nextDouble() - 0.5) * 1.25;
            double dz = (this.random.nextDouble() - 0.5) * 1.25;
            if (this.level() instanceof ServerLevel level) {
                if (level.random.nextFloat() < 0.45f) level.sendParticles(ParticleTypes.POOF, e.getX(), e.getY() + 0.15, e.getZ(), 1, dx, dy, dz, 0.1);
                if (level.random.nextFloat() < 0.55f) level.sendParticles(p, e.getX(), e.getY() + 0.15, e.getZ(), 1, dx, dy, dz, 0.1);
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", this::predicate));
    }

    @Override
    protected int baseLifetime() {
        return 23;
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        event.getController().setAnimation(HEAL);
        return PlayState.CONTINUE;
    }

}
