package dev.xylonity.companions.common.tesla.behaviour;

import dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity;
import dev.xylonity.companions.common.blockentity.TeslaCoilBlockEntity;
import dev.xylonity.companions.common.tesla.TeslaConnectionManager;
import dev.xylonity.companions.common.util.interfaces.ITeslaNodeBehaviour;

/**
 * Implements the logic for the Tesla coil.
 * The Tesla coil only activates if its primary (first) incoming connection is from a block
 * that is active. In addition, if the coil has been manually toggled off (i.e. manually disabled),
 * it remains inactive even if it receives an activation signal.
 */
public class TeslaCoilBehaviour implements ITeslaNodeBehaviour {

    @Override
    public void process(AbstractTeslaBlockEntity block) {
        // We assume that the block is a TeslaCoilBlockEntity.
        TeslaCoilBlockEntity coil = (TeslaCoilBlockEntity) block;

        // If the coil is manually disabled (e.g. via right click) then force it inactive.
        if (coil.isManuallyDisabled()) {
            coil.setActive(false);
            return;
        }

        // Retrieve the first incoming connection (if any) from the connection manager.
        TeslaConnectionManager.ConnectionNode primaryIncoming = TeslaConnectionManager.getInstance()
                .getIncoming(coil.asConnectionNode()).stream().findFirst().orElse(null);

        if (primaryIncoming != null && primaryIncoming.isBlock()) {
            AbstractTeslaBlockEntity incomingBlock = TeslaConnectionManager.getInstance().getBlockEntity(primaryIncoming);
            // Activate only if the primary incoming block is active.
            if (incomingBlock != null && incomingBlock.isActive()) {
                coil.setActive(true);
            } else {
                coil.setActive(false);
            }
        } else {
            // No valid primary incoming connection means remain inactive.
            coil.setActive(false);
        }
    }

}
