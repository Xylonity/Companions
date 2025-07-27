package dev.xylonity.companions.common.entity.ai.pontiff.goal;

import dev.xylonity.companions.common.entity.ai.pontiff.AbstractSacredPontiffAttackGoal;
import dev.xylonity.companions.common.entity.hostile.SacredPontiffEntity;
import dev.xylonity.companions.common.entity.projectile.PontiffFireRingProjectile;
import dev.xylonity.companions.common.entity.projectile.trigger.FireRayBeamEntity;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.knightlib.api.TickScheduler;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class PontiffRotatingFireRayGoal extends AbstractSacredPontiffAttackGoal {

    public PontiffRotatingFireRayGoal(SacredPontiffEntity boss, int minCd, int maxCd) {
        super(boss, 171, minCd, maxCd);
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
    public void tick() {
        super.tick();

        if (attackTicks >= 21 && attackTicks <= 150 && attackTicks % 20 == 0) {
            PontiffFireRingProjectile ring = CompanionsEntities.PONTIFF_FIRE_RING.get().create(pontiff.level());
            if (ring != null) {
                ring.setOwner(pontiff);
                ring.moveTo(pontiff.getX(), pontiff.getY(), pontiff.getZ());
                pontiff.level().addFreshEntity(ring);
            }
        }

    }

    private void doAttack() {
        if (!(pontiff.level() instanceof ServerLevel s)) return;

        Vec3 origin = pontiff.position().add(0, pontiff.getBbHeight() + 3.5, 0);
        float yaw = pontiff.getRandom().nextFloat() * 360f;
        float pitch = -pontiff.getRandom().nextFloat() * 60f;

        FireRayBeamEntity beam = new FireRayBeamEntity(s, origin, yaw, pitch, 150, 30, 1.0f, pontiff);
        s.addFreshEntity(beam);
    }

    @Override
    protected int getAttackType() {
        return 3;
    }

    @Override
    protected void performAttack(LivingEntity target) {
        for (int i = 0; i < 10; i++) {
            TickScheduler.scheduleServer(pontiff.level(), this::doAttack, new Random().nextInt(0, 10));
        }
    }

    @Override
    protected int attackDelay() {
        return 10;
    }

    @Override
    protected int attackState() {
        return 2;
    }

}