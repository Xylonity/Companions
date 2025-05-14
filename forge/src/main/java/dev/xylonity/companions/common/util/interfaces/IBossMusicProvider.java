package dev.xylonity.companions.common.util.interfaces;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface IBossMusicProvider {

    @NotNull
    SoundEvent getBossMusic();

    default boolean shouldPlayBossMusic(Player listener) {
        return false;
    }

    default double getMusicRangeSqr() {
        return 64.0 * 64.0;
    }

}