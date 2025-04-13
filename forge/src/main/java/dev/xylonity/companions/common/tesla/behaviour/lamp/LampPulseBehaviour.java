package dev.xylonity.companions.common.tesla.behaviour.lamp;

import dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity;
import dev.xylonity.companions.common.blockentity.TeslaCoilBlockEntity;
import dev.xylonity.companions.common.tesla.TeslaConnectionManager;
import dev.xylonity.companions.common.util.interfaces.ITeslaNodeBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class LampPulseBehaviour implements ITeslaNodeBehaviour {

    @Override
    public void process(AbstractTeslaBlockEntity coil, Level level, BlockPos blockPos, BlockState blockState) {
        if (coil.shouldcycle) {

            coil.setActive(true);

            if (coil.cycleCounter == MAX_LAPSUS) {
                coil.shouldcycle = false;
            }

            coil.cycleCounter++;
            coil.tickCount++;
        } else {
            coil.setActive(false);
        }

    }

}