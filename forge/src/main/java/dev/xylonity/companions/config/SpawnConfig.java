package dev.xylonity.companions.config;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.core.Holder;

import java.util.ArrayList;
import java.util.List;

public class SpawnConfig {

    public final List<ResourceLocation> biomes;
    public final List<TagKey<Biome>> biomeTags;
    public final int weight;
    public final int minCount;
    public final int maxCount;

    private SpawnConfig(int weight, int minCount, int maxCount, List<ResourceLocation> biomes, List<TagKey<Biome>> biomeTags) {
        this.weight = weight;
        this.minCount = minCount;
        this.maxCount = maxCount;
        this.biomes = biomes;
        this.biomeTags = biomeTags;
    }

    public static SpawnConfig parse(String cfgLine) {
        String[] parts = cfgLine.split("\\s*,\\s*");
        int weight = Integer.parseInt(parts[0]);
        int min = Integer.parseInt(parts[1]);
        int max = Integer.parseInt(parts[2]);

        List<ResourceLocation> biomeList = new ArrayList<>();
        List<TagKey<Biome>> tagList = new ArrayList<>();

        for (int i = 3; i < parts.length; i++) {
            String part = parts[i];
            if (part.startsWith("#")) {
                ResourceLocation tagId = new ResourceLocation(part.substring(1));
                tagList.add(TagKey.create(Registries.BIOME, tagId));
            } else {
                biomeList.add(new ResourceLocation(part));
            }
        }

        return new SpawnConfig(weight, min, max, biomeList, tagList);
    }

    public boolean matches(Holder<Biome> biomeHolder) {
        ResourceLocation biomeName = getBiomeName(biomeHolder);
        if (biomeName == null) {
            return false;
        }

        if (biomes.contains(biomeName)) {
            return true;
        }

        for (TagKey<Biome> tag : biomeTags) {
            if (biomeHolder.is(tag)) {
                return true;
            }
        }

        return false;
    }

    private static ResourceLocation getBiomeName(Holder<Biome> biomeHolder) {
        return biomeHolder.unwrap().map(ResourceKey::location, noKey -> null);
    }

}
