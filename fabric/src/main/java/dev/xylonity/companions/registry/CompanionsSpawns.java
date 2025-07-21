package dev.xylonity.companions.registry;

import dev.xylonity.companions.common.entity.companion.CorneliusEntity;
import dev.xylonity.companions.common.entity.companion.GoldenAllayEntity;
import dev.xylonity.companions.common.entity.hostile.WildAntlionEntity;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.config.SpawnConfig;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;

public class CompanionsSpawns {

    private static final SpawnConfig GOLDEN_ALLAY_SPAWN_CONFIG = SpawnConfig.parse(CompanionsConfig.GOLDEN_ALLAY_SPAWN);
    private static final SpawnConfig CORNELIUS_SPAWN_CONFIG = SpawnConfig.parse(CompanionsConfig.CORNELIUS_SPAWN);
    private static final SpawnConfig WILD_ANTLION_SPAWN_CONFIG = SpawnConfig.parse(CompanionsConfig.WILD_ANTLION_SPAWN);

    public static void init() {
        BiomeModifications.addSpawn(ctx -> GOLDEN_ALLAY_SPAWN_CONFIG.matches(ctx.getBiomeRegistryEntry()), MobCategory.CREATURE,
                CompanionsEntities.GOLDEN_ALLAY,
                GOLDEN_ALLAY_SPAWN_CONFIG.weight,
                GOLDEN_ALLAY_SPAWN_CONFIG.minCount,
                GOLDEN_ALLAY_SPAWN_CONFIG.maxCount
        );

        BiomeModifications.addSpawn(context -> CORNELIUS_SPAWN_CONFIG.matches(context.getBiomeRegistryEntry()), MobCategory.CREATURE,
                CompanionsEntities.CORNELIUS,
                CORNELIUS_SPAWN_CONFIG.weight,
                CORNELIUS_SPAWN_CONFIG.minCount,
                CORNELIUS_SPAWN_CONFIG.maxCount
        );

        BiomeModifications.addSpawn(context -> WILD_ANTLION_SPAWN_CONFIG.matches(context.getBiomeRegistryEntry()), MobCategory.MONSTER,
                CompanionsEntities.WILD_ANTLION,
                WILD_ANTLION_SPAWN_CONFIG.weight,
                WILD_ANTLION_SPAWN_CONFIG.minCount,
                WILD_ANTLION_SPAWN_CONFIG.maxCount
        );

        SpawnPlacements.register(CompanionsEntities.GOLDEN_ALLAY, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, GoldenAllayEntity::checkGoldenAllaySpawnRules);
        SpawnPlacements.register(CompanionsEntities.WILD_ANTLION, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WildAntlionEntity::checkMonsterSpawnRules);
        SpawnPlacements.register(CompanionsEntities.CORNELIUS, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CorneliusEntity::checkCorneliusSpawnRules);
    }

}
