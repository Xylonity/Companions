package dev.xylonity.companions.common.entity.ai.antlion.wild.goal;

import dev.xylonity.companions.common.entity.ai.antlion.tamable.AbstractAntlionAttackGoal;
import dev.xylonity.companions.common.entity.ai.antlion.wild.AbstractWildAntlionAttackGoal;
import dev.xylonity.companions.common.entity.companion.AntlionEntity;
import dev.xylonity.companions.common.entity.hostile.WildAntlionEntity;
import dev.xylonity.knightlib.api.TickScheduler;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class WildAntlionBaseLongAttackGoal extends AbstractWildAntlionAttackGoal {
    private static final double HIT_PROBABILITY = 0.85;
    private static final double MISS_OFFSET_RANGE = 2.0;
    private static final double ARC_HEIGHT_MULTIPLIER = 0.1;
    private static final int PARTICLES_PER_UNIT = 4;
    private static final int TICK_DELAY_DIVISOR = 6;

    public WildAntlionBaseLongAttackGoal(WildAntlionEntity antlion, int minCd, int maxCd) {
        super(antlion, 13, minCd, maxCd);
    }

    @Override
    public void start() {
        super.start();
        antlion.setNoMovement(true);
    }

    @Override
    public void stop() {
        super.stop();
        antlion.setNoMovement(false);
    }

    @Override
    public boolean canUse() {
        LivingEntity target = antlion.getTarget();
        if (target != null && antlion.distanceToSqr(target) > 25 * 25) {
            return false;
        }

        if (antlion.getState() == 0 && target != null) {
            antlion.cycleState();
            return false;
        }

        if (!antlion.isUnderground()) {
            return false;
        }

        return super.canUse();
    }

    @Override
    protected void performAttack(LivingEntity target) {
        spawnParabolicParticles(target);
    }

    @Override
    protected int attackDelay() {
        return 6;
    }

    @Override
    protected int attackType() {
        return 2;
    }

    private void spawnParabolicParticles(LivingEntity target) {
        ServerLevel serverLevel = (ServerLevel) antlion.level();
        BlockState blockState = serverLevel.getBlockState(antlion.blockPosition().below());

        if (blockState.isAir()) return;

        boolean isHit = serverLevel.random.nextDouble() <= HIT_PROBABILITY;
        TrajectoryData trajectory = calculateTrajectory(target, isHit);
        int steps = Math.max(1, (int)(trajectory.distance * PARTICLES_PER_UNIT));

        // parabolic path (not bezier)
        for (int i = 0; i <= steps; i++) {
            double progress = i / (double) steps;
            double x = Mth.lerp(progress, trajectory.startX, trajectory.endX);
            double y = Mth.lerp(progress, trajectory.startY, trajectory.endY) + ((1.0 + trajectory.distance * ARC_HEIGHT_MULTIPLIER) * 4 * progress * (1 - progress));
            double z = Mth.lerp(progress, trajectory.startZ, trajectory.endZ);

            TickScheduler.scheduleServer(serverLevel, () -> serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, blockState), x, y, z, 1, 0.0, 0.0, 0.0, 0.01), i / TICK_DELAY_DIVISOR);
        }

        if (isHit) {
            TickScheduler.scheduleServer(serverLevel, () -> antlion.doHurtTarget(target), steps / TICK_DELAY_DIVISOR);
            if (antlion.level().random.nextFloat() < 0.35f) TickScheduler.scheduleServer(serverLevel, () -> target.setSecondsOnFire(new Random().nextInt(1, 3)), steps / TICK_DELAY_DIVISOR);
        }

    }

    private TrajectoryData calculateTrajectory(LivingEntity target, boolean isHit) {
        double startX = antlion.getX();
        double startY = antlion.getY() - 0.5;
        double startZ = antlion.getZ();

        double endX, endY, endZ;
        if (isHit) {
            endX = target.getX();
            endY = target.getY() + target.getEyeHeight();
            endZ = target.getZ();
        } else {
            endX = target.getX() + (antlion.level().random.nextDouble() - 0.5) * MISS_OFFSET_RANGE;
            endY = target.getY();
            endZ = target.getZ() + (antlion.level().random.nextDouble() - 0.5) * MISS_OFFSET_RANGE;
        }

        double dX = endX - startX;
        double dY = endY - startY;
        double dZ = endZ - startZ;
        double distance = Math.sqrt(dX * dX + dY * dY + dZ * dZ);

        return new TrajectoryData(startX, startY, startZ, endX, endY, endZ, distance);
    }

    private record TrajectoryData(double startX, double startY, double startZ, double endX, double endY, double endZ, double distance) { ;; }

}