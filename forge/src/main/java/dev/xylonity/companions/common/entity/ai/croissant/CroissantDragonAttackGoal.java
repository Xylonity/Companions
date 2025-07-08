package dev.xylonity.companions.common.entity.ai.croissant;

import dev.xylonity.companions.common.entity.companion.CroissantDragonEntity;
import dev.xylonity.companions.common.entity.projectile.trigger.CakeCreamTriggerProjectile;
import dev.xylonity.companions.common.particle.CakeCreamParticle;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsParticles;
import dev.xylonity.knightlib.common.api.TickScheduler;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.Random;

public class CroissantDragonAttackGoal extends Goal {
    private final CroissantDragonEntity dragon;
    private int tickCount;
    private int cooldown = 0;
    private int attacksOnSameTarget = 0;
    private LivingEntity lastTarget;

    public CroissantDragonAttackGoal(CroissantDragonEntity dragon) {
        this.dragon = dragon;
        this.setFlags(EnumSet.of(Goal.Flag.LOOK, Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (cooldown > 0) {
            cooldown--;
            return false;
        }
        LivingEntity target = dragon.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public boolean canContinueToUse() {
        return tickCount < 25 && dragon.getTarget() != null && dragon.getTarget().isAlive();
    }

    @Override
    public void start() {
        tickCount = 0;
        dragon.setAttacking(true);
        TickScheduler.scheduleServer(dragon.level(), () -> dragon.setAttacking(false), 50);

        LivingEntity currentTarget = dragon.getTarget();
        if (currentTarget != null) {
            if (lastTarget == null || !lastTarget.equals(currentTarget)) {
                lastTarget = currentTarget;
                attacksOnSameTarget = 0;
            }
        }

    }

    @Override
    public void tick() {
        tickCount++;

        LivingEntity target = dragon.getTarget();
        if (target != null) {
            dragon.getLookControl().setLookAt(target, 30.0F, 30.0F);

            //if (tickCount <= 5) {
                double dx = target.getX() - dragon.getX();
                double dz = target.getZ() - dragon.getZ();

                float angle = (float)(Math.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0F;

                dragon.setYRot(angle);

                dragon.yBodyRot = angle;
                dragon.yHeadRot = angle;
                dragon.yRotO = angle;
                dragon.yBodyRotO = angle;
                dragon.yHeadRotO = angle;
            //}
        }

        if (tickCount >= 5) {
            Vec3 eyePos = new Vec3(dragon.getX(), dragon.getY() + dragon.getEyeHeight() * 0.2, dragon.getZ());
            Vec3 viewVector = dragon.getViewVector(1.0F).normalize();
            Vec3 spawnPos = eyePos.add(viewVector.scale(1.0));

            CakeCreamParticle.setDefaultVelocity(viewVector.x * 0.5, viewVector.y * 0.5, viewVector.z * 0.5);

            if (dragon.level() instanceof ServerLevel level) {
                SimpleParticleType particle = dragon.getArmorName().equals("chocolate") ?
                    CompanionsParticles.CAKE_CREAM_CHOCOLATE.get() : dragon.getArmorName().equals("strawberry") ?
                    CompanionsParticles.CAKE_CREAM_STRAWBERRY.get() : CompanionsParticles.CAKE_CREAM.get();

                level.sendParticles(particle, spawnPos.x, spawnPos.y, spawnPos.z, 12, 0.0, 0.0, 0.0, 0.0);
            }

            if (!dragon.level().isClientSide() && tickCount % 2 == 0) {
                CakeCreamTriggerProjectile projectile = new CakeCreamTriggerProjectile(CompanionsEntities.CAKE_CREAM_TRIGGER_PROJECTILE.get(), dragon.level());
                projectile.setPos(spawnPos.x, spawnPos.y + new Random().nextDouble(0, 1), spawnPos.z);
                projectile.setArmorName(dragon.getArmorName());
                projectile.setOwner(dragon);
                projectile.setDeltaMovement(Util.randomVectorInCone(viewVector, 15.0, new Random()).scale(0.5 * (0.8 + new Random().nextDouble() * 0.4)));
                dragon.level().addFreshEntity(projectile);
            }
        }

    }

    @Override
    public void stop() {
        LivingEntity currentTarget = dragon.getTarget();
        if (currentTarget != null) {
            if (lastTarget != null && lastTarget.equals(currentTarget)) {
                attacksOnSameTarget++;
            } else {
                lastTarget = currentTarget;
                attacksOnSameTarget = 1;
            }

            if (attacksOnSameTarget >= 3) {
                dragon.setTarget(null);
                attacksOnSameTarget = 0;
            }
        } else {
            attacksOnSameTarget = 0;
            lastTarget = null;
        }

        cooldown = dragon.getRandom().nextInt(61) + 60;
    }

}