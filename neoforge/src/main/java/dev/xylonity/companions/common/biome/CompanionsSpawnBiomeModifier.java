package dev.xylonity.companions.common.biome;

import com.mojang.serialization.MapCodec;
import dev.xylonity.companions.Companions;
import dev.xylonity.companions.registry.CompanionsSpawns;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class CompanionsSpawnBiomeModifier implements BiomeModifier {

    public static final DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIER =
            DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, Companions.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<CompanionsSpawnBiomeModifier>> SERIALIZER =
            BIOME_MODIFIER.register("companions_mob_spawns", CompanionsSpawnBiomeModifier::makeCodec);

    @Override
    public void modify(@NotNull Holder<Biome> holder, @NotNull Phase phase, ModifiableBiomeInfo.BiomeInfo.@NotNull Builder builder) {
        if (phase == Phase.ADD) {
            CompanionsSpawns.addBiomeSpawns(holder, builder);
        }
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return SERIALIZER.get();
    }

    // 3) Constructor del codec (sin datos adicionales)
    public static MapCodec<CompanionsSpawnBiomeModifier> makeCodec() {
        return MapCodec.unit(CompanionsSpawnBiomeModifier::new);
    }

}
