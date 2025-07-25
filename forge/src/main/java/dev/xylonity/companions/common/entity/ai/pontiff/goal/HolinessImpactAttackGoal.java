package dev.xylonity.companions.common.entity.ai.pontiff.goal;

import dev.xylonity.companions.common.entity.HostileEntity;
import dev.xylonity.companions.common.entity.ai.pontiff.AbstractSacredPontiffAttackGoal;
import dev.xylonity.companions.common.entity.hostile.SacredPontiffEntity;
import dev.xylonity.companions.common.entity.projectile.FireGeiserProjectile;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsParticles;
import dev.xylonity.companions.registry.CompanionsSounds;
import dev.xylonity.knightlib.api.TickScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class HolinessImpactAttackGoal extends AbstractSacredPontiffAttackGoal {

    public HolinessImpactAttackGoal(SacredPontiffEntity boss, int minCd, int maxCd) {
        super(boss, 64, minCd, maxCd);
    }

    @Override
    public void start() {
        super.start();
        pontiff.setNoMovement(true);
        pontiff.setShouldLookAtTarget(false);
    }

    @Override
    public void stop() {
        super.stop();
        pontiff.setNoMovement(false);
        pontiff.setShouldLookAtTarget(true);
    }

    @Override
    protected int getAttackType() {
        return 4;
    }

    @Override
    protected void performAttack(LivingEntity target) {
        for (LivingEntity e : target.level().getEntitiesOfClass(LivingEntity.class,
                new AABB(pontiff.blockPosition()).inflate(2), e -> !(e instanceof HostileEntity)))
        {
            pontiff.doHurtTarget(e);
            e.knockback(1f, pontiff.getX() - target.getX(), pontiff.getZ() - target.getZ());
        }

        if (pontiff.level() instanceof ServerLevel serverLevel) {
            doEarthquake(serverLevel, pontiff.blockPosition().relative(pontiff.getDirection()));
        }
    }

    private void doEarthquake(ServerLevel serverLevel, BlockPos center) {
        int radius = 10;

        boolean[][] claimedPortions = new boolean[radius * 2 + 1][radius * 2 + 1];
        List<List<BlockPos>> plates = new ArrayList<>();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (pontiff.getRandom().nextFloat() > 0.2) continue;

                int ci = dx + radius;
                int cj = dz + radius;
                if (claimedPortions[ci][cj]) continue;

                int size = 3 + pontiff.getRandom().nextInt(2);
                List<BlockPos> plate = new ArrayList<>(size * size);

                for (int x = 0; x < size; x++) {
                    for (int z = 0; z < size; z++) {
                        int rx = dx + x, rz = dz + z;
                        if (Math.abs(rx) > radius || Math.abs(rz) > radius) continue;

                        int ii = rx + radius;
                        int jj = rz + radius;
                        if (claimedPortions[ii][jj]) continue;

                        claimedPortions[ii][jj] = true;
                        plate.add(center.offset(rx, -1, rz));
                    }
                }

                if (!plate.isEmpty()) plates.add(plate);
            }

        }

        int tickDelay = 0;
        int plateCount = 0;
        for (int i = 0; i < plates.size(); i++) {

            if (plateCount == 4) {
                tickDelay += 3;
                plateCount = 0;
            }

            double yDelay = 0.18 + 0.005 * i;
            List<BlockPos> plate = plates.get(i);
            Runnable task = () -> {
                for (BlockPos bp : plate) {
                    BlockState state = serverLevel.getBlockState(bp);
                    if (!state.isAir() && state.getBlock() != Blocks.BEDROCK) {
                        spawnFallingBlock(serverLevel, bp, state, yDelay);
                    }
                }

                BlockPos randPos = plate.get(pontiff.getRandom().nextInt(plate.size()));
                double x = randPos.getX() + pontiff.getRandom().nextDouble();
                double z = randPos.getZ() + pontiff.getRandom().nextDouble();

                FireGeiserProjectile geiser = CompanionsEntities.FIRE_GEISER_PROJECTILE.get().create(serverLevel);
                if (geiser != null && serverLevel.random.nextFloat() < 0.35f) {
                    geiser.moveTo(x, randPos.getY() + 0.75, z, 0f, 0f);
                    serverLevel.addFreshEntity(geiser);
                }
                if (pontiff.getRandom().nextFloat() < 0.65f) {
                    for (int j = 0; j < 10; j++) {
                        double dx = (pontiff.getRandom().nextDouble() - 0.5) * 2.0;
                        double dy = (pontiff.getRandom().nextDouble() - 0.5) * 2.0;
                        double dz = (pontiff.getRandom().nextDouble() - 0.5) * 2.0;
                        if (pontiff.level() instanceof ServerLevel level) {
                            level.sendParticles(ParticleTypes.POOF, x, randPos.getY() + 1, z, 1, dx, dy, dz, 0.2);
                        }
                    }
                }

            };

            if (tickDelay == 0) {
                task.run();
            } else {
                TickScheduler.scheduleServer(serverLevel, task, tickDelay);
            }

            plateCount++;
        }

    }

    private void spawnFallingBlock(ServerLevel level, BlockPos pos, BlockState state, double yDelay) {
        FallingBlockEntity fallingBlock = new FallingBlockEntity(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, state);

        fallingBlock.setDeltaMovement(0.0, yDelay, 0.0);

        level.addFreshEntity(fallingBlock);
        level.removeBlock(pos, false);
    }

    @Override
    protected int attackDelay() {
        return 35;
    }

    @Override
    protected int attackState() {
        return 6;
    }

}