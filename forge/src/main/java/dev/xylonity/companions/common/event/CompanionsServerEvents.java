package dev.xylonity.companions.common.event;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.custom.*;
import dev.xylonity.companions.common.entity.summon.FireworkToadEntity;
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
        event.put(CompanionsEntities.ILLAGER_GOLEM.get(), IllagerGolemEntity.setAttributes());
        event.put(CompanionsEntities.DINAMO.get(), DinamoEntity.setAttributes());
        event.put(CompanionsEntities.BROKEN_DINAMO.get(), BrokenDinamoEntity.setAttributes());
        event.put(CompanionsEntities.HOSTILE_IMP.get(), HostileImpEntity.setAttributes());
        event.put(CompanionsEntities.MINION.get(), MinionEntity.setAttributes());
        event.put(CompanionsEntities.GOLDEN_ALLAY.get(), GoldenAllayEntity.setAttributes());
        event.put(CompanionsEntities.SOUL_MAGE.get(), SoulMageEntity.setAttributes());
        event.put(CompanionsEntities.LIVING_CANDLE.get(), LivingCandleEntity.setAttributes());
        event.put(CompanionsEntities.CROISSANT_DRAGON.get(), CroissantDragonEntity.setAttributes());
        event.put(CompanionsEntities.HOSTILE_PUPPET_GLOVE.get(), HostilePuppetGloveEntity.setAttributes());
        event.put(CompanionsEntities.PUPPET.get(), PuppetEntity.setAttributes());
        event.put(CompanionsEntities.PUPPET_GLOVE.get(), PuppetGloveEntity.setAttributes());

        event.put(CompanionsEntities.FIREWORK_TOAD.get(), FireworkToadEntity.setAttributes());
    }

}
