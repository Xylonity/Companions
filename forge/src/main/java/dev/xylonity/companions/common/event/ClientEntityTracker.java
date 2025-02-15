package dev.xylonity.companions.common.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientEntityTracker {
    private static final Map<UUID, Entity> uuidToEntityMap = new HashMap<>();

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if (event.getLevel() instanceof ClientLevel && event.getEntity() != null) {
            uuidToEntityMap.put(event.getEntity().getUUID(), event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onEntityLeaveWorld(EntityLeaveLevelEvent event) {
        if (event.getLevel() instanceof ClientLevel && event.getEntity() != null) {
            uuidToEntityMap.remove(event.getEntity().getUUID());
        }
    }

    public static Entity getEntityByUUID(UUID uuid) {
        return uuidToEntityMap.get(uuid);
    }

}