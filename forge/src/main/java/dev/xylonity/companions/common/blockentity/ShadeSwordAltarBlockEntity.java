package dev.xylonity.companions.common.blockentity;

import dev.xylonity.companions.registry.CompanionsBlockEntities;
import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class ShadeSwordAltarBlockEntity extends AbstractShadeAltarBlockEntity {

    private int tickCount;
    private boolean shouldSpawnParticleExplosion;

    public ShadeSwordAltarBlockEntity(BlockPos pos, BlockState state) {
        super(CompanionsBlockEntities.SHADE_SWORD_ALTAR.get(), pos, state);
        this.tickCount = 0;
        this.shouldSpawnParticleExplosion = false;
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T F) {
        if (F instanceof ShadeSwordAltarBlockEntity altar) {
            if (altar.tickCount % 20 == 0 && altar.getCharges() >= altar.getMaxCharges() - altar.getBloodCharges()) {
                double dx = (new Random().nextDouble() - 0.5) * 0.5;
                double dy = (new Random().nextDouble() - 0.5) * 0.5;
                double dz = (new Random().nextDouble() - 0.5) * 0.5;
                if (level instanceof ServerLevel sv) {
                    sv.sendParticles(CompanionsParticles.SHADE_TRAIL.get(), altar.getBlockPos().getX() + 0.5, altar.getBlockPos().getY() + 0.25 * Math.random(), altar.getBlockPos().getZ() + 0.5, 1, dx, dy, dz, 0.1);
                }
            }

            if (altar.shouldSpawnParticleExplosion && altar.getCharges() == 1) {
                for (int i = 0; i < 10; i++) {
                    double dx = (new Random().nextDouble() - 0.5) * 0.5;
                    double dy = (new Random().nextDouble() - 0.5) * 0.5;
                    double dz = (new Random().nextDouble() - 0.5) * 0.5;
                    if (level instanceof ServerLevel sv) {
                        sv.sendParticles(CompanionsParticles.SHADE_TRAIL.get(), altar.getBlockPos().getX() + 0.5, altar.getBlockPos().getY() + 0.25 * Math.random(), altar.getBlockPos().getZ() + 0.5, 1, dx, dy, dz, 0.15);
                        if (i % 3 == 0)
                            sv.sendParticles(CompanionsParticles.SHADE_SUMMON.get(), altar.getBlockPos().getX() + 0.5, altar.getBlockPos().getY() + 0.25 * Math.random(), altar.getBlockPos().getZ() + 0.5, 1, dx, dy, dz, 0.35);
                    }
                }

                altar.shouldSpawnParticleExplosion = false;
            }

            if (altar.shouldSpawnParticleExplosion && altar.getCharges() == altar.getMaxCharges() - altar.getBloodCharges()) {
                for (int i = 0; i < 20; i++) {
                    double dx = (new Random().nextDouble() - 0.5) * 0.6;
                    double dy = (new Random().nextDouble() - 0.5) * 0.6;
                    double dz = (new Random().nextDouble() - 0.5) * 0.6;
                    if (level instanceof ServerLevel sv) {
                        sv.sendParticles(CompanionsParticles.SHADE_TRAIL.get(), altar.getBlockPos().getX() + 0.5, altar.getBlockPos().getY() + 0.35 * Math.random(), altar.getBlockPos().getZ() + 0.5, 1, dx, dy, dz, 0.15);
                        if (i % 3 == 0)
                            sv.sendParticles(CompanionsParticles.SHADE_SUMMON.get(), altar.getBlockPos().getX() + 0.5, altar.getBlockPos().getY() + 0.35 * Math.random(), altar.getBlockPos().getZ() + 0.5, 1, dx, dy, dz, 0.35);
                    }
                }

                altar.shouldSpawnParticleExplosion = false;
            }

            altar.tickCount++;
        }
    }

    @Override
    protected boolean hasIncreasedFromPrevInteraction() {
        boolean ret = super.hasIncreasedFromPrevInteraction();
        if (ret) this.shouldSpawnParticleExplosion = true;
        return ret;
    }

}