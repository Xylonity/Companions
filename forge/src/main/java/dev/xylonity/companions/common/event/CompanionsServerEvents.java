package dev.xylonity.companions.common.event;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.AntlionEntity;
import dev.xylonity.companions.common.entity.FroggyEntity;
import dev.xylonity.companions.common.entity.TeddyEntity;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CompanionsCommon.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CompanionsServerEvents {

    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(CompanionsEntities.FROGGY.get(), FroggyEntity.setAttributes());
        event.put(CompanionsEntities.TEDDY.get(), TeddyEntity.setAttributes());
        event.put(CompanionsEntities.ANTLION.get(), AntlionEntity.setAttributes());
    }

}
