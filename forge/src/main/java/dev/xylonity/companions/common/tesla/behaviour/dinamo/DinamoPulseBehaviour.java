package dev.xylonity.companions.common.tesla.behaviour.dinamo;

import dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity;
import dev.xylonity.companions.common.entity.companion.DinamoEntity;
import dev.xylonity.companions.common.tesla.TeslaConnectionManager;
import dev.xylonity.companions.common.util.interfaces.ITeslaGeneratorBehaviour;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Set;

public class DinamoPulseBehaviour implements ITeslaGeneratorBehaviour {

    @Override
    public void tick(DinamoEntity dinamo) {

        // Deal with animation stuff
        if (dinamo.getCycleCounter() < ELECTRICAL_CHARGE_DURATION) {
            dinamo.setAnimationStartTick(dinamo.getCycleCounter());
            dinamo.setActive(true);
        }
        else if (dinamo.getCycleCounter() == ELECTRICAL_CHARGE_DURATION) {
            dinamo.setActive(false);
            dinamo.setAnimationStartTick(0);
        }

        if (dinamo.getCycleCounter() ==  TICKS_BEFORE_SENDING_PULSE){
            // Starts the cycle for all the nodes around it
            Set<TeslaConnectionManager.ConnectionNode> nodes = TeslaConnectionManager.getInstance().getOutgoing(dinamo.asConnectionNode());
            for (TeslaConnectionManager.ConnectionNode node : nodes) {
                if (!node.isEntity()) {
                    BlockEntity be = dinamo.level().getBlockEntity(node.blockPos());
                    if (be instanceof AbstractTeslaBlockEntity coil) {
                        coil.startCycle();
                    }
                }
            }
        }

        // Reset once the time is up
        if (dinamo.getCycleCounter() >= MAX_LAPSUS) {
            dinamo.setCycleCounter(0);
        }

        //Up the cicle count
        dinamo.setCycleCounter(dinamo.getCycleCounter() + 1);
    }

}
