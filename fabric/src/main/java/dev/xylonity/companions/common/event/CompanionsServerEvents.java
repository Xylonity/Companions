package dev.xylonity.companions.common.event;

import dev.xylonity.companions.common.entity.companion.*;
import dev.xylonity.companions.common.entity.hostile.*;
import dev.xylonity.companions.common.entity.summon.*;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

public final class CompanionsServerEvents {
    public static void init() {
        FabricDefaultAttributeRegistry.register(CompanionsEntities.CORNELIUS, CorneliusEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(CompanionsEntities.TEDDY, TeddyEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(CompanionsEntities.ANTLION, AntlionEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(CompanionsEntities.DINAMO, DinamoEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(CompanionsEntities.BROKEN_DINAMO, BrokenDinamoEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(CompanionsEntities.MINION, MinionEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(CompanionsEntities.GOLDEN_ALLAY, GoldenAllayEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(CompanionsEntities.SOUL_MAGE, SoulMageEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(CompanionsEntities.LIVING_CANDLE, LivingCandleEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(CompanionsEntities.CROISSANT_DRAGON, CroissantDragonEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(CompanionsEntities.PUPPET, PuppetEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(CompanionsEntities.PUPPET_GLOVE, PuppetGloveEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(CompanionsEntities.SHADE_SWORD, ShadeSwordEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(CompanionsEntities.SHADE_MAW, ShadeMawEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(CompanionsEntities.MANKH, MankhEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(CompanionsEntities.CLOAK, CloakEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(CompanionsEntities.ILLAGER_GOLEM, IllagerGolemEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(CompanionsEntities.HOSTILE_PUPPET_GLOVE, HostilePuppetGloveEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(CompanionsEntities.SACRED_PONTIFF, SacredPontiffEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(CompanionsEntities.WILD_ANTLION, WildAntlionEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(CompanionsEntities.HOSTILE_IMP, HostileImpEntity.setAttributes());
    }

}