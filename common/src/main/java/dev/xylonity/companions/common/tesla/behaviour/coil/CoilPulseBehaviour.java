package dev.xylonity.companions.common.tesla.behaviour.coil;

import dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity;
import dev.xylonity.companions.common.tesla.ConnectionTarget;
import dev.xylonity.companions.common.tesla.TeslaNetwork;
import dev.xylonity.companions.common.util.interfaces.ITeslaNodeBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CoilPulseBehaviour implements ITeslaNodeBehaviour {

    @Override
    public void process(AbstractTeslaBlockEntity coil, Level level, BlockPos blockPos, BlockState blockState) {
        // Checks if any incoming node is a generator (dinamo)
        boolean flagDinamo = false;
        if (!level.isClientSide) {
            for (final ConnectionTarget source : TeslaNetwork.get(level).getIncoming(coil.asConnectionTarget())) {
                if (source.isEntity()) {
                    flagDinamo = true;
                    break;
                }

            }

        }

        coil.setReceivesGenerator(flagDinamo);

        if (coil.cycleCounter >= 0) {
            if (coil.cycleCounter < ELECTRICAL_CHARGE_DURATION) {
                coil.setAnimationStartTick(coil.cycleCounter);
                coil.setActive(true);
            }
            else if (coil.cycleCounter == ELECTRICAL_CHARGE_DURATION) {
                coil.setActive(false);
                coil.setAnimationStartTick(0);
            }

            if (coil.cycleCounter == TICKS_BEFORE_SENDING_PULSE) {
                if (!coil.isPendingRemoval()) {
                    for (final ConnectionTarget target : coil.getOutgoing()) {
                        if (target.isBlock()) {
                            final BlockEntity blockEntity = level.getBlockEntity(target.blockPos());
                            if (blockEntity instanceof AbstractTeslaBlockEntity outCoil) {
                                if (!outCoil.isReceivesGenerator()) {
                                    outCoil.startCycle();
                                }
                            }

                        }

                    }

                }

            }

            final int largestWait = Math.max(ELECTRICAL_CHARGE_DURATION, TICKS_BEFORE_SENDING_PULSE);
            if (coil.cycleCounter == largestWait) {
                coil.cycleCounter = -1;
            }
            else {
                coil.cycleCounter++;
                coil.tickCount++;
            }

        }

    }

}