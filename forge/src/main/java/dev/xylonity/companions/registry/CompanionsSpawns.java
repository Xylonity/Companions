package dev.xylonity.companions.registry;

import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.config.SpawnConfig;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

public class CompanionsSpawns {

    private static final SpawnConfig GOLDEN_ALLAY_SPAWN_CONFIG = SpawnConfig.parse(CompanionsConfig.GOLDEN_ALLAY_SPAWN);
    private static final SpawnConfig CORNELIUS_SPAWN_CONFIG = SpawnConfig.parse(CompanionsConfig.CORNELIUS_SPAWN);
    private static final SpawnConfig WILD_ANTLION_SPAWN_CONFIG = SpawnConfig.parse(CompanionsConfig.WILD_ANTLION_SPAWN);

    public static void addBiomeSpawns(Holder<Biome> biomeHolder, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (GOLDEN_ALLAY_SPAWN_CONFIG.matches(biomeHolder)) {
            int weight = GOLDEN_ALLAY_SPAWN_CONFIG.weight;
            int min = GOLDEN_ALLAY_SPAWN_CONFIG.minCount;
            int max = GOLDEN_ALLAY_SPAWN_CONFIG.maxCount;

            builder.getMobSpawnSettings().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(CompanionsEntities.GOLDEN_ALLAY.get(), weight, min, max));
        }
        if (CORNELIUS_SPAWN_CONFIG.matches(biomeHolder)) {
            int weight = CORNELIUS_SPAWN_CONFIG.weight;
            int min = CORNELIUS_SPAWN_CONFIG.minCount;
            int max = CORNELIUS_SPAWN_CONFIG.maxCount;

            builder.getMobSpawnSettings().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(CompanionsEntities.CORNELIUS.get(), weight, min, max));
        }
        if (WILD_ANTLION_SPAWN_CONFIG.matches(biomeHolder)) {
            int weight = WILD_ANTLION_SPAWN_CONFIG.weight;
            int min = WILD_ANTLION_SPAWN_CONFIG.minCount;
            int max = WILD_ANTLION_SPAWN_CONFIG.maxCount;

            builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(CompanionsEntities.WILD_ANTLION.get(), weight, min, max));
        }

    }

}
