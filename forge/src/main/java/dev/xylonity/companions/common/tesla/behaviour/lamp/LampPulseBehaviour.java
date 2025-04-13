package dev.xylonity.companions.common.tesla.behaviour.lamp;

import dev.xylonity.companions.common.block.PlasmaLampBlock;
import dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity;
import dev.xylonity.companions.common.blockentity.TeslaCoilBlockEntity;
import dev.xylonity.companions.common.tesla.TeslaConnectionManager;
import dev.xylonity.companions.common.util.interfaces.ITeslaNodeBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class LampPulseBehaviour implements ITeslaNodeBehaviour {

    private void linkLamp(AbstractTeslaBlockEntity lamp, Level level, BlockPos blockPos, List<TeslaConnectionManager.ConnectionNode> oldConnections){
        BlockEntity newBe = level.getBlockEntity(blockPos);
        if (newBe instanceof AbstractTeslaBlockEntity newLamp) {
            for (TeslaConnectionManager.ConnectionNode node : oldConnections) {
                newLamp.connectionManager.addConnection(node, newLamp.asConnectionNode(), false);
            }
        }
    }

    @Override
    public void process(AbstractTeslaBlockEntity lamp, Level level, BlockPos blockPos, BlockState blockState) {

        List<TeslaConnectionManager.ConnectionNode> oldConnections =
                new ArrayList<>(lamp.connectionManager.getIncoming(lamp.asConnectionNode()));

        if (lamp.cycleCounter >= 0) {

            if (lamp.cycleCounter == 0) {
                // Keeps the lamp active for a full cycle
                lamp.setActive(true);
                level.setBlockAndUpdate(blockPos, blockState.setValue(PlasmaLampBlock.LIT, true));
                linkLamp(lamp, level, blockPos, oldConnections);
            }

            if (lamp.cycleCounter == MAX_LAPSUS) {
                //Things here happen ONCE when the cycle is over
                lamp.cycleCounter = -1;
                level.setBlockAndUpdate(blockPos, blockState.setValue(PlasmaLampBlock.LIT, false));
                linkLamp(lamp, level, blockPos, oldConnections);
                lamp.setActive(false);
            }
            else{
                lamp.cycleCounter++;
                lamp.tickCount++;
            }
        }
        //With an else statement, things here happen every tick outside the cycle

    }

}