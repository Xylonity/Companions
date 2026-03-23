package dev.xylonity.companions.common.event;

import dev.xylonity.companions.common.entity.companion.*;
import dev.xylonity.companions.common.entity.hostile.*;
import dev.xylonity.companions.common.entity.summon.*;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.knightlib.api.event.RegisterEvent;
import dev.xylonity.knightlib.api.event.impl.server.EntityAttributeRegistrationEvent;
import dev.xylonity.knightlib.api.event.impl.server.ServerEntityJoinLevelEvent;
import dev.xylonity.knightlib.api.event.impl.server.ServerEntityLeaveLevelEvent;
import dev.xylonity.knightlib.api.event.impl.server.SpawnPlacementRegistrationEvent;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;

import java.lang.ref.WeakReference;

public final class CompanionsServerEvents {

    @RegisterEvent
    public static void registerEntityAttributes(final EntityAttributeRegistrationEvent event) {
        event.register(CompanionsEntities.CORNELIUS, CorneliusEntity::setAttributes);
        event.register(CompanionsEntities.TEDDY, TeddyEntity::setAttributes);
        event.register(CompanionsEntities.ANTLION, AntlionEntity::setAttributes);
        event.register(CompanionsEntities.DINAMO, DinamoEntity::setAttributes);
        event.register(CompanionsEntities.BROKEN_DINAMO, BrokenDinamoEntity::setAttributes);
        event.register(CompanionsEntities.MINION, MinionEntity::setAttributes);
        event.register(CompanionsEntities.GOLDEN_ALLAY, GoldenAllayEntity::setAttributes);
        event.register(CompanionsEntities.SOUL_MAGE, SoulMageEntity::setAttributes);
        event.register(CompanionsEntities.LIVING_CANDLE, LivingCandleEntity::setAttributes);
        event.register(CompanionsEntities.CROISSANT_DRAGON, CroissantDragonEntity::setAttributes);
        event.register(CompanionsEntities.PUPPET, PuppetEntity::setAttributes);
        event.register(CompanionsEntities.PUPPET_GLOVE, PuppetGloveEntity::setAttributes);
        event.register(CompanionsEntities.SHADE_SWORD, ShadeSwordEntity::setAttributes);
        event.register(CompanionsEntities.SHADE_MAW, ShadeMawEntity::setAttributes);
        event.register(CompanionsEntities.MANKH, MankhEntity::setAttributes);
        event.register(CompanionsEntities.CLOAK, CloakEntity::setAttributes);
        event.register(CompanionsEntities.ILLAGER_GOLEM, IllagerGolemEntity::setAttributes);
        event.register(CompanionsEntities.HOSTILE_PUPPET_GLOVE, HostilePuppetGloveEntity::setAttributes);
        event.register(CompanionsEntities.SACRED_PONTIFF, SacredPontiffEntity::setAttributes);
        event.register(CompanionsEntities.WILD_ANTLION, WildAntlionEntity::setAttributes);
        event.register(CompanionsEntities.HOSTILE_IMP, HostileImpEntity::setAttributes);
        event.register(CompanionsEntities.FIREWORK_TOAD, FireworkToadEntity::setAttributes);
        event.register(CompanionsEntities.NETHER_BULLFROG, NetherBullfrogEntity::setAttributes);
        event.register(CompanionsEntities.ENDER_FROG, EnderFrogEntity::setAttributes);
        event.register(CompanionsEntities.EMBER_POLE, EmberPoleEntity::setAttributes);
        event.register(CompanionsEntities.BUBBLE_FROG, BubbleFrogEntity::setAttributes);
    }

    @RegisterEvent
    public static void registerSpawnPlacements(final SpawnPlacementRegistrationEvent event) {
        event.register(CompanionsEntities.GOLDEN_ALLAY.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, GoldenAllayEntity::checkGoldenAllaySpawnRules);
        event.register(CompanionsEntities.WILD_ANTLION.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WildAntlionEntity::checkMonsterSpawnRules);
        event.register(CompanionsEntities.CORNELIUS.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CorneliusEntity::checkCorneliusSpawnRules);
    }

    @RegisterEvent
    public static void onEntityLeaveLevelEvent(final ServerEntityLeaveLevelEvent event) {
        CompanionsEntityTracker.ENTITIES.remove(event.getEntity().getUUID());
    }

    @RegisterEvent
    public static void onEntityJoinLevelEvent(final ServerEntityJoinLevelEvent event) {
        CompanionsEntityTracker.ENTITIES.put(event.getEntity().getUUID(), new WeakReference<>(event.getEntity()));
    }

}