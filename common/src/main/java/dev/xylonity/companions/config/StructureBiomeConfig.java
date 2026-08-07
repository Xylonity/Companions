package dev.xylonity.companions.config;

import dev.xylonity.companions.Companions;
import dev.xylonity.knightlib.api.util.ResourceLocations;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public final class StructureBiomeConfig {

    private StructureBiomeConfig() {}

    public static boolean isConfiguredPool(ResourceLocation startPoolId) {
        if (!startPoolId.getNamespace().equals(Companions.MOD_ID)) {
            return false;
        }

        return switch (startPoolId.getPath()) {
            case "companions_pools/companions_babayaga_pool",
                 "companions_pools/companions_factory_pool",
                 "companions_pools/companions_monkey_temple_pool",
                 "companions_pools/companions_teddytower_pool",
                 "companions_pools/companions_tent_pool" -> true;
            default -> false;
        };

    }

    public static boolean matches(ResourceLocation startPoolId, Holder<Biome> biome) {
        final String configuredBiomes = switch (startPoolId.getPath()) {
            case "companions_pools/companions_babayaga_pool" -> CompanionsConfig.BABAYAGA_STRUCTURE_BIOMES;
            case "companions_pools/companions_factory_pool" -> CompanionsConfig.FACTORY_STRUCTURE_BIOMES;
            case "companions_pools/companions_monkey_temple_pool" -> CompanionsConfig.MONKEY_TEMPLE_STRUCTURE_BIOMES;
            case "companions_pools/companions_teddytower_pool" -> CompanionsConfig.TEDDY_TOWER_STRUCTURE_BIOMES;
            case "companions_pools/companions_tent_pool" -> CompanionsConfig.NETHER_TENT_STRUCTURE_BIOMES;
            default -> "";
        };

        final ResourceLocation biomeId = biome.unwrap().map(ResourceKey::location, value -> null);
        for (String entry : configuredBiomes.split("[;,]")) {
            final String value = entry.trim();
            if (value.isEmpty()) {
                continue;
            }

            try {
                if (value.startsWith("#")) {
                    final ResourceLocation tagId = ResourceLocations.parse(value.substring(1));
                    if (biome.is(TagKey.create(Registries.BIOME, tagId))) {
                        return true;
                    }
                }
                else if (ResourceLocations.parse(value).equals(biomeId)) {
                    return true;
                }

            }
            catch (Exception ignored) {
                ;;
            }

        }

        return false;
    }

}
