package dev.xylonity.companions.registry;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.recipe.ShadeMawAltarRecipe;
import dev.xylonity.companions.common.recipe.SoulFurnaceRecipe;
import dev.xylonity.knightlib.api.registrar.ResourceDispatcher;
import dev.xylonity.knightlib.api.registrar.ResourceEntry;
import dev.xylonity.knightlib.api.registrar.ResourceRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeType;

public final class CompanionsRecipeTypes {

    public static final ResourceRegistry<RecipeType<?>> RECIPE_TYPES = ResourceDispatcher.create(BuiltInRegistries.RECIPE_TYPE, Companions.MOD_ID);

    public static final ResourceEntry<RecipeType<ShadeMawAltarRecipe>> CHALICE_TYPE = RECIPE_TYPES.register("shade_maw_altar_interaction", () -> new RecipeType<>() {
        @Override
        public String toString() {
            return Companions.of("shade_maw_altar_interaction").toString();
        }

    });

    public static final ResourceEntry<RecipeType<SoulFurnaceRecipe>> SOUL_FURNACE_TYPE = RECIPE_TYPES.register("soul_furnace", () -> new RecipeType<>() {
        @Override
        public String toString() {
            return Companions.of("soul_furnace").toString();
        }

    });

}
