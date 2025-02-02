package dev.xylonity.companions.common.event;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.ai.illagergolem.TeslaConnectionManager;
import dev.xylonity.companions.common.entity.custom.*;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CompanionsCommon.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CompanionsServerEvents {

    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(CompanionsEntities.FROGGY.get(), FroggyEntity.setAttributes());
        event.put(CompanionsEntities.TEDDY.get(), TeddyEntity.setAttributes());
        event.put(CompanionsEntities.ANTLION.get(), AntlionEntity.setAttributes());
        event.put(CompanionsEntities.ILLAGER_GOLEM.get(), IllagerGolemEntity.setAttributes());
        event.put(CompanionsEntities.TAMED_ILLAGER_GOLEM.get(), TamedIllagerGolemEntity.setAttributes());
    }

}
