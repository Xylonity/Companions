package dev.xylonity.companions.client.music;

import dev.xylonity.companions.common.util.interfaces.IBossMusicProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

public class SacredPontiffMusicInstance extends AbstractTickableSoundInstance {
    private final IBossMusicProvider boss;

    private static final int FADE_TICKS = 40;
    private static final float TARGET_VOL = 1.0f;

    private int fadeCounter = FADE_TICKS;
    private boolean fadingOut;

    public SacredPontiffMusicInstance(IBossMusicProvider boss) {
        super(boss.getBossMusic(), SoundSource.MUSIC, SoundInstance.createUnseededRandom());
        this.boss = boss;
        this.looping = true;
        this.volume = 0.0f;
        this.pitch = 1.0f;
        this.attenuation = Attenuation.NONE;
        this.relative = true;
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public void tick() {
        boolean shouldPlay = boss.shouldPlayBossMusic(Minecraft.getInstance().player);

        if (!shouldPlay && !fadingOut) {
            fadingOut = true;
            fadeCounter = FADE_TICKS;
        }

        if (fadingOut) {
            volume = TARGET_VOL * (fadeCounter--) / FADE_TICKS;

            if (fadeCounter <= 0) this.stop();

        } else {
            volume = Math.min(TARGET_VOL, volume + (TARGET_VOL / FADE_TICKS));
        }

    }

}