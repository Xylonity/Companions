package dev.xylonity.companions.common.event;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CompanionsEntityTracker {
    private static final Map<UUID, Entity> uuidToEntityMap = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if (event.getEntity() != null) {
            uuidToEntityMap.put(event.getEntity().getUUID(), event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onEntityLeaveWorld(EntityLeaveLevelEvent event) {
        if (event.getEntity() != null) {
            uuidToEntityMap.remove(event.getEntity().getUUID());
        }
    }

    public static Entity getEntityByUUID(UUID uuid) {
        return uuidToEntityMap.get(uuid);
    }

}