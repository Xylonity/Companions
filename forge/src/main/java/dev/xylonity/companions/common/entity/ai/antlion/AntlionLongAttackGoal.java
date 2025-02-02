package dev.xylonity.companions.common.entity.ai.antlion;

import dev.xylonity.companions.common.entity.custom.AntlionEntity;
import dev.xylonity.companions.common.tick.TickScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;
import java.util.Random;

public class AntlionLongAttackGoal extends Goal {
    private final AntlionEntity antlion;
    private final int range;
    private final int cooldownTicks;
    private final double hitProbability;
    private final float damageAmount;
    private int cooldown;

    public AntlionLongAttackGoal(AntlionEntity antlion, int range, int cooldownTicks, double hitProbability, float damageAmount) {
        this.antlion = antlion;
        this.range = range;
        this.cooldownTicks = cooldownTicks;
        this.hitProbability = hitProbability;
        this.damageAmount = damageAmount;
        this.cooldown = 0;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (cooldown > 0) {
            cooldown--;
            return false;
        }

        if (!antlion.isUnderground()) {
            return false;
        }

        Player nearestPlayer = antlion.level().getNearestPlayer(antlion, range);
        return nearestPlayer != null && antlion.hasLineOfSight(nearestPlayer);
    }

    @Override
    public void start() {
        Player target = antlion.level().getNearestPlayer(antlion, range);
        if (target != null && antlion.level() instanceof ServerLevel serverLevel) {
            antlion.setShouldDoSandAttack(true);
            antlion.setTarget(target);

            boolean hit = new Random().nextDouble() <= hitProbability;

            BlockPos blockPos = antlion.blockPosition().below();
            BlockState blockState = antlion.level().getBlockState(blockPos);

            spawnParabolicParticles(serverLevel, blockState, target, hit);


//
            //} else if (antlion.level() instanceof ServerLevel level) {
            //    TickScheduler.schedule(level, () -> spawnParticlesTowardsPlayer(serverLevel, blockState, target), 5);
//
            //}

            cooldown = cooldownTicks;
        }
    }

    @Override
    public void stop() {
        antlion.setShouldDoSandAttack(false);
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    private void spawnParabolicParticles(ServerLevel serverLevel, BlockState blockState, Player target, boolean isHit) {
        if (blockState.isAir()) {
            return;
        }

        BlockParticleOption particleOption = new BlockParticleOption(ParticleTypes.BLOCK, blockState);

        double startX = antlion.getX();
        double startY = antlion.getY() - 0.5;
        double startZ = antlion.getZ();

        double distance;
        double dirX, dirY, dirZ;

        if (isHit) {
            double endX = target.getX();
            double endY = target.getY() + target.getEyeHeight();
            double endZ = target.getZ();

            double dX = endX - startX;
            double dY = endY - startY;
            double dZ = endZ - startZ;

            distance = Math.sqrt(dX * dX + dY * dY + dZ * dZ);
            dirX = dX / distance;
            dirY = dY / distance;
            dirZ = dZ / distance;
        } else {
            Random rand = new Random();

            double offsetX = (rand.nextDouble() - 0.5) * 2.0;
            double offsetZ = (rand.nextDouble() - 0.5) * 2.0;
            double offsetY = (rand.nextDouble() - 0.25);

            double endX = target.getX() + offsetX;
            double endY = target.getY();
            double endZ = target.getZ() + offsetZ;

            double dX = endX - startX;
            double dY = endY - startY;
            double dZ = endZ - startZ;

            distance = Math.sqrt(dX * dX + dY * dY + dZ * dZ);
            dirX = dX / distance;
            dirY = dY / distance;
            dirZ = dZ / distance;
        }

        int steps = (int) (distance * 4);
        if (steps < 1) {
            steps = 1;
        }

        double arcHeight = 1.0 + distance * 0.1;

        for (int i = 0; i <= steps; i++) {
            double progress = i / (double) steps;

            double currentX = Mth.lerp(progress, startX, startX + dirX * distance);
            double currentZ = Mth.lerp(progress, startZ, startZ + dirZ * distance);

            double linearY = Mth.lerp(progress, startY, startY + dirY * distance);

            double parabola = arcHeight * 4 * progress * (1 - progress);

            double currentY = linearY + parabola;

            int delay = i / 6;
            TickScheduler.schedule(serverLevel, () -> serverLevel.sendParticles(
                    particleOption,
                    currentX, currentY, currentZ,
                    1,
                    0.0, 0.0, 0.0,
                    0.01
            ), delay);

            if (isHit && i == 0) TickScheduler.schedule(serverLevel, () -> attemptDamageToPlayer(target), steps / 6);
        }
    }

    private void attemptDamageToPlayer(Player target) {

        ////

        antlion.doHurtTarget(target);
    }
}
