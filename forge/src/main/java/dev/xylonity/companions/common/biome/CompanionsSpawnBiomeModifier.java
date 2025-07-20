package dev.xylonity.companions.common.biome;

import com.mojang.serialization.Codec;
import dev.xylonity.companions.Companions;
import dev.xylonity.companions.registry.CompanionsSpawns;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CompanionsSpawnBiomeModifier implements BiomeModifier {

    public static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, Companions.MOD_ID);

    private static final RegistryObject<Codec<? extends BiomeModifier>> SERIALIZER = RegistryObject.create(new ResourceLocation(Companions.MOD_ID, "companions_mob_spawns"), ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, Companions.MOD_ID);

    @Override
    public void modify(Holder<Biome> holder, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.ADD) {
            CompanionsSpawns.addBiomeSpawns(holder, builder);
        }

    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return SERIALIZER.get();
    }

    public static Codec<CompanionsSpawnBiomeModifier> makeCodec() {
        return Codec.unit(CompanionsSpawnBiomeModifier::new);
    }

}
