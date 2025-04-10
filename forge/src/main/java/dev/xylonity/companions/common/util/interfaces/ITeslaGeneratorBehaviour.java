package dev.xylonity.companions.common.util.interfaces;

import dev.xylonity.companions.common.entity.custom.DinamoEntity;

/**
 * Strategy interface for current–generation behavior.
 * Implementations are responsible for ticking the generator entity according to its mode.
 */
public interface ITeslaGeneratorBehaviour {
    void tick(DinamoEntity generator);
}
