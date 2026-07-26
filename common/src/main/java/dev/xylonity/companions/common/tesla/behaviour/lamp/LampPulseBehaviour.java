package dev.xylonity.companions.common.tesla.behaviour.lamp;

import dev.xylonity.companions.common.block.PlasmaLampBlock;
import dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity;
import dev.xylonity.companions.common.util.interfaces.ITeslaNodeBehaviour;
import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class LampPulseBehaviour implements ITeslaNodeBehaviour {

    private static final int LIT_DURATION = MAX_LAPSUS * 2;

    @Override
    public void process(AbstractTeslaBlockEntity lamp, Level level, BlockPos blockPos, BlockState blockState) {

        final boolean powered = lamp.cycleCounter >= 0;

        lamp.setActive(powered);
        setLit(level, blockPos, blockState, powered);

        if (!powered) {
            return;
        }

        // Decorative particles
        if (lamp.cycleCounter % 4 == 0) {
            for (int i = 0; i < 360; i += 120) {
                if (new Random().nextFloat() < 0.6f) {
                    final double radius = 0.2;
                    final double angle = Math.toRadians(i);
                    final double px = blockPos.getX() + 0.5 + radius * Math.cos(angle);
                    final double pz = blockPos.getZ() + 0.5 + radius * Math.sin(angle);
                    final double py = blockPos.getY() + 0.5 + Math.random();
                    level.addParticle(CompanionsParticles.DINAMO_SPARK.get(), px, py, pz, 0, 0.35, 0);

                }

            }

        }

        if (lamp.cycleCounter >= LIT_DURATION) {
            lamp.cycleCounter = -1;
        }
        else {
            lamp.cycleCounter++;
            lamp.tickCount++;
        }

    }

    private void setLit(Level level, BlockPos blockPos, BlockState blockState, boolean lit) {
        if (level.isClientSide || blockState.getValue(PlasmaLampBlock.LIT) == lit) {
            return;
        }

        level.setBlock(blockPos, blockState.setValue(PlasmaLampBlock.LIT, lit), Block.UPDATE_CLIENTS);
    }

}
