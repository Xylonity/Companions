package dev.xylonity.companions.common.util.interfaces;

import dev.xylonity.companions.common.entity.companion.DinamoEntity;

/**
 * Strategy interface for current–generation behavior.
 * Implementations are responsible for ticking the generator entity according to its mode.
 */
public interface ITeslaGeneratorBehaviour extends ITeslaUtil {

    void tick(DinamoEntity generator);

}
