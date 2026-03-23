package dev.xylonity.companions.common.tesla.behaviour.platform;

import dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity;
import dev.xylonity.companions.common.util.interfaces.ITeslaNodeBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class RecallPlatformPulseBehaviour implements ITeslaNodeBehaviour {

    @Override
    public void process(AbstractTeslaBlockEntity lamp, Level level, BlockPos blockPos, BlockState blockState) {

        if (lamp.cycleCounter >= 0) {

            if (lamp.cycleCounter == 0) {
                // Keeps the lamp active for a full cycle
                lamp.setActive(true);
            }

            if (lamp.cycleCounter == MAX_LAPSUS) {
                //Things here happen ONCE when the cycle is over
                lamp.cycleCounter = -1;
                lamp.setActive(false);
            }
            else {
                lamp.cycleCounter++;
                lamp.tickCount++;
            }
        }
        //With an else statement, things here happen every tick outside the cycle

    }

}