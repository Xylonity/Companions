package dev.xylonity.companions.common.entity.ai.cornelius.summon.goal;

import dev.xylonity.companions.common.entity.CompanionSummonEntity;
import dev.xylonity.companions.common.entity.ai.cornelius.summon.AbstractCorneliusSummonAttackGoal;
import dev.xylonity.companions.common.entity.summon.NetherBullfrogEntity;
import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class NetherBullfrogAirSlashGoal extends AbstractCorneliusSummonAttackGoal {

    public NetherBullfrogAirSlashGoal(CompanionSummonEntity summon, int minCd, int maxCd) {
        super(summon, 55, minCd, maxCd);
    }

    @Override
    public void start() {
        super.start();
        summon.setNoMovement(true);
        LivingEntity target = summon.getTarget();
        if (target != null) {
            summon.getLookControl().setLookAt(target, 30F, 30F);
        }
    }

    @Override
    public void stop() {
        super.stop();
        summon.setNoMovement(false);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && summon.getTarget() != null && summon.distanceTo(summon.getTarget()) < 3;
    }

    @Override
    public void tick() {
        LivingEntity target = summon.getTarget();
        if (attackTicks == attackDelay() && target != null && target.isAlive()) {
            performAttack(target);
        }

        if (attackTicks == attackDelay()) {
            performAttack2();
        }

        attackTicks++;
    }

    @Override
    protected int getAttackType() {
        return 4;
    }

    @Override
    protected void performAttack(LivingEntity target) {

    }

    protected void performAttack2() {
        // Solo en servidor
        if (!(summon.level() instanceof ServerLevel level)) return;

        // Vector de visión normalizado
        Vec3 look = summon.getLookAngle();

        // Punto A: a la altura de los ojos de la entidad
        double ax = summon.getX();
        double ay = summon.getY();
        double az = summon.getZ();

        // Punto B: un bloque delante
        double bx = ax + look.x;
        double by = ay + 0.15;
        double bz = az + look.z;

        // Número de partículas por llamada
        int count = 10;
        // Offset pequeño para esparcirlas mínimamente
        double spread = 0.03;
        // Velocidad a lo largo del look vector
        double speed = 0.0125;

        // Llamada en B
        level.sendParticles(
                ParticleTypes.CAMPFIRE_COSY_SMOKE,
                bx, by, bz,
                count,
                look.x * spread, spread, look.z * spread,
                speed
        );
    }

    @Override
    protected int attackDelay() {
        return 26;
    }

    @Override
    protected Class<? extends CompanionSummonEntity> summonType() {
        return NetherBullfrogEntity.class;
    }

}