package dev.xylonity.companions.common.event;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.client.music.SacredPontiffMusicInstance;
import dev.xylonity.companions.common.util.interfaces.IBossMusicProvider;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Companions.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientBossMusicHandler {

    private static final Int2ObjectMap<AbstractTickableSoundInstance> ACTIVE_PLAYERS = new Int2ObjectOpenHashMap<>();

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.ClientTickEvent.Phase.END) return;

        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) return;

        ACTIVE_PLAYERS.int2ObjectEntrySet().removeIf(e -> e.getValue().isStopped());

        for (Entity e : player.level().getEntities(player, player.getBoundingBox().inflate(64), en -> en instanceof IBossMusicProvider e)) {
            IBossMusicProvider provider = (IBossMusicProvider) e;
            int id = e.getId();

            if (ACTIVE_PLAYERS.containsKey(id)) continue;

            if (provider.shouldPlayBossMusic(player)) {
                SacredPontiffMusicInstance sound = new SacredPontiffMusicInstance(provider);
                minecraft.getSoundManager().play(sound);
                ACTIVE_PLAYERS.put(id, sound);
            }
        }

    }

}