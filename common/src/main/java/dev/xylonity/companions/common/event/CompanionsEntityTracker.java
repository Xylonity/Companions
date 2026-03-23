package dev.xylonity.companions.common.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CompanionsEntityTracker {
    private static final Map<UUID,WeakReference<Entity>> ENTITIES = new ConcurrentHashMap<>();

    public static void init() {
        ServerEntityEvents.ENTITY_LOAD.register((Entity e, ServerLevel world) ->
                ENTITIES.put(e.getUUID(), new WeakReference<>(e))
        );
        ServerEntityEvents.ENTITY_UNLOAD.register((Entity e, ServerLevel world) ->
                ENTITIES.remove(e.getUUID())
        );
    }

    public static Entity getEntityByUUID(UUID id) {
        WeakReference<Entity> ref = ENTITIES.get(id);
        return ref == null ? null : ref.get();
    }

}