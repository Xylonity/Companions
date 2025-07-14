package dev.xylonity.companions.registry;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.recipe.ShadeMawAltarRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class CompanionsRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Companions.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, Companions.MOD_ID);

    public static final RegistryObject<RecipeSerializer<ShadeMawAltarRecipe>> CHALICE_SERIALIZER;
    public static final RegistryObject<RecipeType<ShadeMawAltarRecipe>> CHALICE_TYPE;

    static {
        CHALICE_SERIALIZER = SERIALIZERS.register("shade_maw_altar_interaction", () -> ShadeMawAltarRecipe.SERIALIZER);
        CHALICE_TYPE = TYPES.register("shade_maw_altar_interaction", () -> ShadeMawAltarRecipe.RECIPE_TYPE);
    }

}
