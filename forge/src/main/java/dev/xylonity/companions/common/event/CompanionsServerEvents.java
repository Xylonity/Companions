package dev.xylonity.companions.common.event;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.companion.*;
import dev.xylonity.companions.common.entity.hostile.*;
import dev.xylonity.companions.common.entity.summon.*;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CompanionsCommon.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CompanionsServerEvents {

    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(CompanionsEntities.CORNELIUS.get(), CorneliusEntity.setAttributes());
        event.put(CompanionsEntities.TEDDY.get(), TeddyEntity.setAttributes());
        event.put(CompanionsEntities.ANTLION.get(), AntlionEntity.setAttributes());
        event.put(CompanionsEntities.DINAMO.get(), DinamoEntity.setAttributes());
        event.put(CompanionsEntities.BROKEN_DINAMO.get(), BrokenDinamoEntity.setAttributes());
        event.put(CompanionsEntities.HOSTILE_IMP.get(), HostileImpEntity.setAttributes());
        event.put(CompanionsEntities.MINION.get(), MinionEntity.setAttributes());
        event.put(CompanionsEntities.GOLDEN_ALLAY.get(), GoldenAllayEntity.setAttributes());
        event.put(CompanionsEntities.SOUL_MAGE.get(), SoulMageEntity.setAttributes());
        event.put(CompanionsEntities.LIVING_CANDLE.get(), LivingCandleEntity.setAttributes());
        event.put(CompanionsEntities.CROISSANT_DRAGON.get(), CroissantDragonEntity.setAttributes());
        event.put(CompanionsEntities.PUPPET.get(), PuppetEntity.setAttributes());
        event.put(CompanionsEntities.PUPPET_GLOVE.get(), PuppetGloveEntity.setAttributes());
        event.put(CompanionsEntities.SHADE_SWORD.get(), ShadeSwordEntity.setAttributes());
        event.put(CompanionsEntities.SHADE_MAW.get(), ShadeMawEntity.setAttributes());
        event.put(CompanionsEntities.MANKH.get(), MankhEntity.setAttributes());
        event.put(CompanionsEntities.CLOAK.get(), CloakEntity.setAttributes());

        event.put(CompanionsEntities.FIREWORK_TOAD.get(), FireworkToadEntity.setAttributes());
        event.put(CompanionsEntities.NETHER_BULLFROG.get(), NetherBullfrogEntity.setAttributes());
        event.put(CompanionsEntities.ENDER_FROG.get(), EnderFrogEntity.setAttributes());
        event.put(CompanionsEntities.BUBBLE_FROG.get(), BubbleFrogEntity.setAttributes());
        event.put(CompanionsEntities.EMBER_POLE.get(), EmberPoleEntity.setAttributes());

        event.put(CompanionsEntities.ILLAGER_GOLEM.get(), IllagerGolemEntity.setAttributes());
        event.put(CompanionsEntities.HOSTILE_PUPPET_GLOVE.get(), HostilePuppetGloveEntity.setAttributes());
        event.put(CompanionsEntities.SACRED_PONTIFF.get(), SacredPontiffEntity.setAttributes());
    }

}
