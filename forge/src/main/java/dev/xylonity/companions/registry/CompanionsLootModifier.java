package dev.xylonity.companions.registry;

import com.mojang.serialization.Codec;
import dev.xylonity.companions.Companions;
import dev.xylonity.companions.datagen.CompanionsAddItemModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CompanionsLootModifier {

    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Companions.MOD_ID);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ADD_ITEM;

    static {
        ADD_ITEM = LOOT_MODIFIER_SERIALIZERS.register("add_item", CompanionsAddItemModifier.CODEC);
    }

}
