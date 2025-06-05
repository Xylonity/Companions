package dev.xylonity.companions.common.event;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CompanionsEntityTracker {

    private static final Map<UUID, WeakReference<Entity>> entities = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onJoin(EntityJoinLevelEvent e) {
        entities.put(e.getEntity().getUUID(), new WeakReference<>(e.getEntity()));
    }

    @SubscribeEvent
    public static void onLeave(EntityLeaveLevelEvent e) {
        entities.remove(e.getEntity().getUUID());
    }

    @Nullable
    public static Entity getEntityByUUID(UUID id) {
        WeakReference<Entity> ref = entities.get(id);
        return ref != null ? ref.get() : null;
    }

}