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

    @Override
    public void process(AbstractTeslaBlockEntity lamp, Level level, BlockPos blockPos, BlockState blockState) {

        boolean permutedFlag = false;

        List<TeslaConnectionManager.ConnectionNode> oldConnections =
                new ArrayList<>(lamp.connectionManager.getIncoming(lamp.asConnectionNode()));

        // Due to the need of permuting the original block with a new blockstate, we gotta
        // simulate new tesla network connections
        if (!blockState.getValue(PlasmaLampBlock.LIT) && lamp.isActive()) {
            level.setBlockAndUpdate(blockPos, blockState.setValue(PlasmaLampBlock.LIT, true));
            permutedFlag = true;
        } else if (blockState.getValue(PlasmaLampBlock.LIT) && !lamp.isActive()) {
            level.setBlockAndUpdate(blockPos, blockState.setValue(PlasmaLampBlock.LIT, false));
            permutedFlag = true;
        }

        if (permutedFlag) {
            BlockEntity newBe = level.getBlockEntity(blockPos);
            if (newBe instanceof AbstractTeslaBlockEntity newLamp) {
                for (TeslaConnectionManager.ConnectionNode node : oldConnections) {
                    newLamp.connectionManager.addConnection(node, newLamp.asConnectionNode(), false);
                }
            }
        }

        if (lamp.cycleCounter >= 0) {

            // Keeps the lamp active for a full cycle
            lamp.setActive(true);

            if (lamp.cycleCounter == MAX_LAPSUS) {
                //Things here happen ONCE when the cycle is over
                lamp.cycleCounter = -1;
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