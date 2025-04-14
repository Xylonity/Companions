package dev.xylonity.companions.common.tesla.behaviour.dinamo;

import dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity;
import dev.xylonity.companions.common.blockentity.TeslaCoilBlockEntity;
import dev.xylonity.companions.common.entity.custom.DinamoEntity;
import dev.xylonity.companions.common.tesla.TeslaConnectionManager;
import dev.xylonity.companions.common.util.interfaces.ITeslaGeneratorBehaviour;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Set;

public class DinamoPulseBehaviour implements ITeslaGeneratorBehaviour {

    @Override
    public void tick(DinamoEntity generator) {

        //Deal with animation stuff
        if (generator.getCicleCounter() < ELECTRICAL_CHARGE_DURATION) {
            generator.setAnimationStartTick(generator.getCicleCounter());
            generator.setActive(true);
        }
        else if(generator.getCicleCounter() == ELECTRICAL_CHARGE_DURATION) {
            generator.setActive(false);
            generator.setAnimationStartTick(0);
        }

        if(generator.getCicleCounter() ==  TICKS_BEFORE_SENDING_PULSE){
            //Starts the cycle for all the nodes around it
            Set<TeslaConnectionManager.ConnectionNode> nodes = TeslaConnectionManager.getInstance().getOutgoing(generator.asConnectionNode());
            for (TeslaConnectionManager.ConnectionNode node : nodes) {
                if (!node.isEntity()) {
                    BlockEntity be = generator.level().getBlockEntity(node.blockPos());
                    if (be instanceof AbstractTeslaBlockEntity coil) {
                        coil.startCycle();
                    }
                }
            }
        }

        //Reset once the time is up
        if (generator.getCicleCounter() >= MAX_LAPSUS) {
            generator.setCicleCounter(0);
        }

        //Up the cicle count
        generator.setCicleCounter(generator.getCicleCounter() + 1);
        generator.tickCount++;

    }

}
