package dev.xylonity.companions.registry;

import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.config.SpawnConfig;
import dev.xylonity.knightlib.api.spawn.KnightLibEntityBiomeSpawns;
import net.minecraft.world.entity.MobCategory;

public class CompanionsEntitySpawns {

    private static final SpawnConfig GOLDEN_ALLAY_SPAWN_CONFIG = SpawnConfig.parse(CompanionsConfig.GOLDEN_ALLAY_SPAWN);
    private static final SpawnConfig CORNELIUS_SPAWN_CONFIG = SpawnConfig.parse(CompanionsConfig.CORNELIUS_SPAWN);
    private static final SpawnConfig WILD_ANTLION_SPAWN_CONFIG = SpawnConfig.parse(CompanionsConfig.WILD_ANTLION_SPAWN);

    public static void init() {
        KnightLibEntityBiomeSpawns.builder(CompanionsEntities.GOLDEN_ALLAY, MobCategory.CREATURE)
                .spawnRate(GOLDEN_ALLAY_SPAWN_CONFIG.weight, GOLDEN_ALLAY_SPAWN_CONFIG.minCount, GOLDEN_ALLAY_SPAWN_CONFIG.maxCount)
                .biomeFilter(GOLDEN_ALLAY_SPAWN_CONFIG::matches)
                .submit();

        KnightLibEntityBiomeSpawns.builder(CompanionsEntities.CORNELIUS, MobCategory.CREATURE)
                .spawnRate(CORNELIUS_SPAWN_CONFIG.weight, CORNELIUS_SPAWN_CONFIG.minCount, CORNELIUS_SPAWN_CONFIG.maxCount)
                .biomeFilter(CORNELIUS_SPAWN_CONFIG::matches)
                .submit();

        KnightLibEntityBiomeSpawns.builder(CompanionsEntities.WILD_ANTLION, MobCategory.MONSTER)
                .spawnRate(WILD_ANTLION_SPAWN_CONFIG.weight, WILD_ANTLION_SPAWN_CONFIG.minCount, WILD_ANTLION_SPAWN_CONFIG.maxCount)
                .biomeFilter(WILD_ANTLION_SPAWN_CONFIG::matches)
                .submit();
    }

}