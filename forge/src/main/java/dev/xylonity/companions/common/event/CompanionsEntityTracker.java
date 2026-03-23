package dev.xylonity.companions.common.event;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CompanionsEntityTracker {

    private static final Map<UUID, WeakReference<Entity>> ENTITIES = new ConcurrentHashMap<>();

    @Nullable
    public static Entity getEntityByUUID(UUID id) {
        final WeakReference<Entity> reference = ENTITIES.get(id);
        return reference != null ? reference.get() : null;
    }

}