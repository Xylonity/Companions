package dev.xylonity.companions.common.tesla.behaviour.lamp;

import dev.xylonity.companions.common.block.PlasmaLampBlock;
import dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity;
import dev.xylonity.companions.common.blockentity.PlasmaLampBlockEntity;
import dev.xylonity.companions.common.blockentity.TeslaCoilBlockEntity;
import dev.xylonity.companions.common.tesla.TeslaConnectionManager;
import dev.xylonity.companions.common.util.interfaces.ITeslaNodeBehaviour;
import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LampPulseBehaviour implements ITeslaNodeBehaviour {

    @Override
    public void process(AbstractTeslaBlockEntity lamp, Level level, BlockPos blockPos, BlockState blockState) {

        List<TeslaConnectionManager.ConnectionNode> oldConnections =
                new ArrayList<>(lamp.connectionManager.getIncoming(lamp.asConnectionNode()));

        if (lamp.cycleCounter >= 0) {

            // Particle decoration
            if (lamp.isActive() && level instanceof ClientLevel && lamp.cycleCounter % 4 == 0) {
                for (int i = 0; i < 360; i += 120) {
                    if (new Random().nextFloat() < 0.6f) {
                        double radius = 0.2;
                        double angleRadians = Math.toRadians(i);
                        double particleX = lamp.getBlockPos().getX() + 0.5 + radius * Math.cos(angleRadians);
                        double particleZ = lamp.getBlockPos().getZ() + 0.5 + radius * Math.sin(angleRadians);
                        double particleY = lamp.getBlockPos().getY() + 0.5 + 1 * Math.random();
                        level.addParticle(CompanionsParticles.DINAMO_SPARK.get(), particleX, particleY, particleZ, 0d, 0.35d, 0d);
                    }
                }
            }

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
            else {
                lamp.cycleCounter++;
                lamp.tickCount++;
            }
        }
        //With an else statement, things here happen every tick outside the cycle

    }

    private void linkLamp(AbstractTeslaBlockEntity lamp, Level level, BlockPos blockPos, List<TeslaConnectionManager.ConnectionNode> oldConnections){
        BlockEntity newBe = level.getBlockEntity(blockPos);
        if (newBe instanceof AbstractTeslaBlockEntity newLamp) {
            for (TeslaConnectionManager.ConnectionNode node : oldConnections) {
                newLamp.connectionManager.addConnection(node, newLamp.asConnectionNode(), false);
            }

            // The new lamp state is not registered into the tesla network, so we gotta registry it again per se
            TeslaConnectionManager.getInstance().registerBlockEntity(newLamp);
        }

    }

}