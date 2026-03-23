package dev.xylonity.companions.common.event;

import net.minecraft.world.entity.Entity;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CompanionsEntityTracker {

    protected static final Map<UUID,WeakReference<Entity>> ENTITIES = new ConcurrentHashMap<>();

    public static Entity getEntityByUUID(UUID uuid) {
        final WeakReference<Entity> reference = ENTITIES.get(uuid);
        return reference == null ? null : reference.get();
    }

}