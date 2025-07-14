package dev.xylonity.companions.client.event;

import dev.xylonity.companions.CompanionsCommon;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = CompanionsCommon.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class CameraShakeManager {

    private static final Map<UUID, ShakeData> shakes = new ConcurrentHashMap<>();

    public static void shakePlayer(Player player, int durationTicks, float intensityX, float intensityY, float intensityZ, int fadeStartTick) {
        shakes.put(player.getUUID(), new ShakeData(player.level(), Util.getMillis(), durationTicks, intensityX, intensityY, intensityZ, fadeStartTick));
    }

    public static ShakeData getShake(Player player) {
        ShakeData data = shakes.get(player.getUUID());
        if (data != null && !data.isExpired()) return data;

        shakes.remove(player.getUUID());

        return null;
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.level.isClientSide()) return;

        shakes.values().removeIf(ShakeData::isExpired);
    }

    public static class ShakeData {
        private final Level world;
        private final long startTimeMs;
        private final int durationTicks;
        private final float ix, iy, iz;
        private final int fadeStart;

        public ShakeData(Level world, long startTimeMs, int durationTicks, float ix, float iy, float iz, int fadeStart) {
            this.world = world;
            this.startTimeMs = startTimeMs;
            this.durationTicks = durationTicks;
            this.ix = ix;
            this.iy = iy;
            this.iz = iz;
            this.fadeStart = fadeStart;
        }

        public void applyToCamera(Camera camera) {
            int elapsed = tickcount();
            float fade = 1.0f;

            if (fadeStart >= 0 && elapsed >= fadeStart) {
                fade = 1.0f - Math.min(1f, (float)(elapsed - fadeStart) / (durationTicks - fadeStart));
            }

            double ox = (world.random.nextDouble() - 0.5) * ix * fade;
            double oy = (world.random.nextDouble() - 0.5) * iy * fade;
            double oz = (world.random.nextDouble() - 0.5) * iz * fade;

            camera.move(ox, oy, oz);
        }

        private int tickcount() {
            return (int) ((Util.getMillis() - startTimeMs) / 50);
        }

        public boolean isExpired() {
            return tickcount() >= durationTicks;
        }

    }

}
