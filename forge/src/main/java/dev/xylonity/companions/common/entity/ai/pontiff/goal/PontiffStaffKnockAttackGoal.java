package dev.xylonity.companions.common.entity.ai.pontiff.goal;

import dev.xylonity.companions.common.entity.ai.pontiff.AbstractSacredPontiffAttackGoal;
import dev.xylonity.companions.common.entity.hostile.SacredPontiffEntity;
import dev.xylonity.companions.common.entity.projectile.PontiffFireRingProjectile;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class PontiffStaffKnockAttackGoal extends AbstractSacredPontiffAttackGoal {

    public PontiffStaffKnockAttackGoal(SacredPontiffEntity boss, int minCd, int maxCd) {
        super(boss, 31, minCd, maxCd);
    }

    @Override
    public void start() {
        super.start();
        pontiff.setNoMovement(true);
    }

    @Override
    public void stop() {
        super.stop();
        pontiff.setNoMovement(false);
    }

    @Override
    protected int getAttackType() {
        return 1;
    }

    @Override
    protected void performAttack(LivingEntity target) {
        Vec3 lookVec = pontiff.getLookAngle().normalize();
        Vec3 forwardXZ = new Vec3(lookVec.x, 0, lookVec.z).normalize();

        if (forwardXZ.lengthSqr() == 0) {
            forwardXZ = new Vec3(0, 0, 1.5);
        }

        Vec3 spawnPos = pontiff.position().add(forwardXZ).add(new Vec3(forwardXZ.z, 0, -forwardXZ.x));
        PontiffFireRingProjectile ring = CompanionsEntities.PONTIFF_FIRE_RING.get().create(pontiff.level());
        if (ring != null) {
            ring.setOwner(pontiff);

            ring.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, pontiff.getYRot(), pontiff.getXRot());
            pontiff.level().addFreshEntity(ring);

            if (pontiff.level() instanceof ServerLevel level) {
                RandomSource random = level.getRandom();
                for (int i = 0; i < 7; ++i) {
                    double velX = random.nextFloat() / 2.0;
                    double velY = 5.0E-5;
                    double velZ = random.nextFloat() / 2.0;
                    level.sendParticles(ParticleTypes.LAVA, spawnPos.x, spawnPos.y + 0.05, spawnPos.z, 1, velX, velY, velZ, 0.0);
                }

                level.sendParticles(ParticleTypes.CLOUD, spawnPos.x, spawnPos.y + 0.1, spawnPos.z, 6, 0.1, 0.1, 0.1, 0.1);
                level.sendParticles(ParticleTypes.LARGE_SMOKE, spawnPos.x, spawnPos.y + 0.1, spawnPos.z, 2, 0.01, 0.01, 0.01, 0.05);
            }
        }

    }

    @Override
    protected int attackDelay() {
        return 29;
    }

    @Override
    protected int phase() {
        return 1;
    }

}