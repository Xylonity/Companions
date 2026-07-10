package dev.xylonity.companions.common.tesla.behaviour.lamp;

import dev.xylonity.companions.common.block.PlasmaLampBlock;
import dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity;
import dev.xylonity.companions.common.tesla.ConnectionTarget;
import dev.xylonity.companions.common.tesla.TeslaNetwork;
import dev.xylonity.companions.common.util.interfaces.ITeslaNodeBehaviour;
import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LampPulseBehaviour implements ITeslaNodeBehaviour {

    @Override
    public void process(AbstractTeslaBlockEntity lamp, Level level, BlockPos blockPos, BlockState blockState) {

        // Snapshot of the incoming nodes before potential blockstate change
        final List<ConnectionTarget> oldIncoming = level.isClientSide
                ? List.of()
                : new ArrayList<>(TeslaNetwork.get(level).getIncoming(lamp.asConnectionTarget()));

        if (lamp.cycleCounter >= 0) {
            // Decorative particles
            if (lamp.isActive() && lamp.cycleCounter % 4 == 0) {
                for (int i = 0; i < 360; i += 120) {
                    if (new Random().nextFloat() < 0.6f) {
                        final double radius = 0.2;
                        final double angle = Math.toRadians(i);
                        final double px = lamp.getBlockPos().getX() + 0.5 + radius * Math.cos(angle);
                        final double pz = lamp.getBlockPos().getZ() + 0.5 + radius * Math.sin(angle);
                        final double py = lamp.getBlockPos().getY() + 0.5 + Math.random();
                        level.addParticle(CompanionsParticles.DINAMO_SPARK.get(), px, py, pz, 0, 0.35, 0);
                    }

                }

            }

            if (lamp.cycleCounter == 0) {
                lamp.setActive(true);
                level.setBlockAndUpdate(blockPos, blockState.setValue(PlasmaLampBlock.LIT, true));
                relinkLamp(lamp, level, blockPos, oldIncoming);
            }

            if (lamp.cycleCounter == MAX_LAPSUS) {
                lamp.cycleCounter = -1;
                level.setBlockAndUpdate(blockPos, blockState.setValue(PlasmaLampBlock.LIT, false));
                relinkLamp(lamp, level, blockPos, oldIncoming);
                lamp.setActive(false);
            }
            else {
                lamp.cycleCounter++;
                lamp.tickCount++;
            }

        }

    }

    private void relinkLamp(AbstractTeslaBlockEntity lamp, Level level, BlockPos blockPos, List<ConnectionTarget> oldIncoming) {
        if (level.isClientSide) {
            return;
        }

        final BlockEntity newBlockEntity = level.getBlockEntity(blockPos);
        if (newBlockEntity instanceof AbstractTeslaBlockEntity newLamp) {
            final TeslaNetwork network = TeslaNetwork.get(level);
            for (final ConnectionTarget source : oldIncoming) {
                if (source.isBlock()) {
                    final AbstractTeslaBlockEntity sourceBe = network.getBlockEntity(source.blockPos());
                    if (sourceBe != null) {
                        sourceBe.addOutgoing(newLamp.asConnectionTarget());
                        network.onConnectionAdded(source, newLamp.asConnectionTarget());
                    }

                }

            }

            network.registerBlockEntity(newLamp);
        }

    }

}