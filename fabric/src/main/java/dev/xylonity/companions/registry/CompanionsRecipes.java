package dev.xylonity.companions.registry;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.recipe.ShadeMawAltarRecipe;
import dev.xylonity.companions.common.recipe.SoulFurnaceRecipe;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public final class CompanionsRecipes {

    public static final RecipeSerializer<ShadeMawAltarRecipe> CHALICE_SERIALIZER = ShadeMawAltarRecipe.SERIALIZER;
    public static final RecipeType<ShadeMawAltarRecipe> CHALICE_TYPE = new RecipeType<>() {
        @Override
        public String toString() {
            return Companions.MOD_ID + ":shade_maw_altar_interaction";
        }
    };

    public static final RecipeSerializer<SoulFurnaceRecipe> SOUL_FURNACE_SERIALIZER = new SoulFurnaceRecipe.Serializer();
    public static final RecipeType<SoulFurnaceRecipe> SOUL_FURNACE_TYPE = new RecipeType<>() {
        @Override
        public String toString() {
            return Companions.MOD_ID + ":soul_furnace";
        }
    };

    public static void init() {
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "shade_maw_altar_interaction"), CHALICE_SERIALIZER);
        Registry.register(BuiltInRegistries.RECIPE_TYPE, ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "shade_maw_altar_interaction"), CHALICE_TYPE);

        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "soul_furnace"), SOUL_FURNACE_SERIALIZER);
        Registry.register(BuiltInRegistries.RECIPE_TYPE, ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "soul_furnace"), SOUL_FURNACE_TYPE);
    }

}
