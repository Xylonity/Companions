package dev.xylonity.companions.common.util.interfaces;

import dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity;

/**
 * Defines the activation behavior for Tesla network components.
 * Every block entity that is part of the Tesla network should process its activation
 * each tick using an instance of ActivationBehavior.
 */
public interface ITeslaNodeBehaviour {

    /**
     * Processes the activation logic for the given Tesla block entity.
     *
     * @param block the block entity processing its activation logic
     */
    void process(AbstractTeslaBlockEntity block);

}
