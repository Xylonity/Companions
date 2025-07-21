package dev.xylonity.companions.common.tesla.behaviour.coil;

import dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity;
import dev.xylonity.companions.common.tesla.TeslaConnectionManager;
import dev.xylonity.companions.common.util.interfaces.ITeslaNodeBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CoilPulseBehaviour implements ITeslaNodeBehaviour {

    @Override
    public void process(AbstractTeslaBlockEntity coil, Level level, BlockPos blockPos, BlockState blockState) {
        //Check if the last node that sent a signal was the dinamo for further checks later
        boolean flagDinamo = false;
        for (TeslaConnectionManager.ConnectionNode node : coil.connectionManager.getIncoming(coil.asConnectionNode())) {
            if (node.isEntity()) {
                flagDinamo = true;
                break;
            }
        }
        coil.setReceivesGenerator(flagDinamo);

        if (coil.cycleCounter >= 0) {

            //Deal with animation stuff
            if (coil.cycleCounter < ELECTRICAL_CHARGE_DURATION) {
                coil.setAnimationStartTick(coil.cycleCounter);
                coil.setActive(true);
            }
            else if (coil.cycleCounter == ELECTRICAL_CHARGE_DURATION){
                coil.setActive(false);
                coil.setAnimationStartTick(0);
            }

            //Send the next pulse after a certain delay
            if(coil.cycleCounter == TICKS_BEFORE_SENDING_PULSE){
                if (!coil.isPendingRemoval()) {
                    for (TeslaConnectionManager.ConnectionNode node : coil.connectionManager.getOutgoing(coil.asConnectionNode())) {
                        if (!node.isEntity()) {
                            BlockEntity be = level.getBlockEntity(node.blockPos());
                            if (be instanceof AbstractTeslaBlockEntity outCoil) {
                                if (!outCoil.isReceivesGenerator()) { //If the next coil ISN'T the dinamo
                                    outCoil.startCycle();
                                }
                            }
                        }
                    }
                }

            }

            //Reset the counter when either the animation's finished, or when the pulse has been sent
            //Whichever number comes last
            int largestWait = Math.max(ELECTRICAL_CHARGE_DURATION, TICKS_BEFORE_SENDING_PULSE);

            if (coil.cycleCounter == largestWait) {
                coil.cycleCounter = -1;
            }
            else{
                coil.cycleCounter++;
                coil.tickCount++;
            }
        }
    }
}
