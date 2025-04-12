package dev.xylonity.companions.common.tesla.behaviour.dinamo;

import dev.xylonity.companions.common.blockentity.TeslaCoilBlockEntity;
import dev.xylonity.companions.common.entity.custom.DinamoEntity;
import dev.xylonity.companions.common.tesla.TeslaConnectionManager;
import dev.xylonity.companions.common.util.interfaces.ITeslaGeneratorBehaviour;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Set;

public class DinamoPulseBehaviour implements ITeslaGeneratorBehaviour {

    @Override
    public void tick(DinamoEntity generator) {

        if (generator.getCicleCounter() == 0) {
            generator.setActive(true);
            generator.setAnimationStartTick(0);
        }

        if (generator.getCicleCounter() < 8) {
            int newTick = generator.getAnimationStartTick() + 1;
            generator.setAnimationStartTick(newTick > 8 ? 0 : newTick);
            generator.setActive(true);
        } else {
            generator.setActive(false);
            generator.setAnimationStartTick(0);
        }

        generator.setCicleCounter(generator.getCicleCounter() + 1);

        if (generator.getCicleCounter() >= MAX_LAPSUS) {
            generator.setCicleCounter(0);
            generator.setActive(false);

            Set<TeslaConnectionManager.ConnectionNode> nodes = TeslaConnectionManager.getInstance().getOutgoing(generator.asConnectionNode());
            for (TeslaConnectionManager.ConnectionNode node : nodes) {
                BlockEntity be = generator.level().getBlockEntity(node.blockPos());
                if (be instanceof TeslaCoilBlockEntity coil) {
                    coil.startCycle();
                }
            }
        }

    }

}
