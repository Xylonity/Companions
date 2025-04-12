package dev.xylonity.companions.common.tesla.behaviour.coil;

import dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity;
import dev.xylonity.companions.common.blockentity.TeslaCoilBlockEntity;
import dev.xylonity.companions.common.tesla.TeslaConnectionManager;
import dev.xylonity.companions.common.util.interfaces.ITeslaNodeBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import static dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity.*;

public class CoilPulseBehaviour  implements ITeslaNodeBehaviour {

    @Override
    public void process(AbstractTeslaBlockEntity coil, Level level, BlockPos blockPos, BlockState blockState) {
        coil.tickCount++;

        boolean flagDinamo = false;
        for (TeslaConnectionManager.ConnectionNode node : coil.connectionManager.getIncoming(coil.asConnectionNode())) {
            if (node.isEntity()) {
                flagDinamo = true;
                break;
            }
        }
        coil.setReceivesGenerator(flagDinamo);

        if (coil.scheduledStartTick != -1 && coil.tickCount >= coil.scheduledStartTick) {
            coil.scheduledStartTick = -1;
            coil.startCycle();
        }

        if (coil.shouldcycle) {

            if (coil.cycleCounter == 0) {
                coil.setActive(true);
                coil.setAnimationStartTick(0);
            }

            if (coil.cycleCounter < ELECTRICAL_CHARGE_DURATION) {
                coil.setActive(true);
                coil.setAnimationStartTick((coil.getAnimationStartTick() + 1) % ELECTRICAL_CHARGE_DURATION);
            } else {
                coil.setActive(false);
                coil.setAnimationStartTick(0);
            }

            coil.cycleCounter++;

            if (coil.cycleCounter >= MAX_LAPSUS) {
                coil.cycleCounter = 0;
                coil.shouldcycle = false;
                coil.setActive(false);
                coil.setAnimationStartTick(0);

                if (!coil.isPendingRemoval()) {
                    for (TeslaConnectionManager.ConnectionNode node : coil.connectionManager.getOutgoing(coil.asConnectionNode())) {
                        BlockEntity be = level.getBlockEntity(node.blockPos());
                        if (be instanceof TeslaCoilBlockEntity outCoil) {
                            if (!outCoil.isReceivesGenerator()) {
                                outCoil.scheduleCycleRelative(START_OFFSET);
                            }
                        }
                    }
                }
            }

        }
    }
}
