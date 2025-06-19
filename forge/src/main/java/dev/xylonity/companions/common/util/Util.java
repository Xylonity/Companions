package dev.xylonity.companions.common.util;

public class Util {

    private Util() { ;; }

    // Cubical smoothstep
    public static float smoothStep(float t) {
        return t * t * (3 - 2 * t);
    }

}
