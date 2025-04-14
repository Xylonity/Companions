package dev.xylonity.companions.common.tesla.behaviour.pillar;

import dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity;
import dev.xylonity.companions.common.blockentity.VoltaicPillarBlockEntity;
import dev.xylonity.companions.common.tesla.TeslaConnectionManager;
import dev.xylonity.companions.common.util.interfaces.ITeslaNodeBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PillarPulseBehaviour implements ITeslaNodeBehaviour {

    @Override
    public void process(AbstractTeslaBlockEntity pillar, Level level, BlockPos blockPos, BlockState blockState) {
        if (pillar.cycleCounter >= 0) {

            // Keeps the lamp active for a full cycle
            pillar.setActive(true);

            // Pulses pillars only
            if (pillar.cycleCounter == TICKS_BEFORE_SENDING_PULSE){
                if (!pillar.isPendingRemoval()) {
                    for (TeslaConnectionManager.ConnectionNode node : pillar.connectionManager.getOutgoing(pillar.asConnectionNode())) {
                        if (!node.isEntity()) {
                            BlockEntity be = level.getBlockEntity(node.blockPos());
                            if (be instanceof VoltaicPillarBlockEntity outCoil) {
                                if (!outCoil.isReceivesGenerator()) {
                                    outCoil.startCycle();
                                }
                            }
                        }
                    }
                }
            }

            if (pillar.cycleCounter == MAX_LAPSUS) {
                pillar.cycleCounter = -1;
                pillar.setActive(false);
            } else {
                pillar.cycleCounter++;
                pillar.tickCount++;
            }

        }

    }

}