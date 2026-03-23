package dev.xylonity.companions.registry;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.recipe.ShadeMawAltarRecipe;
import dev.xylonity.companions.common.recipe.SoulFurnaceRecipe;
import dev.xylonity.knightlib.api.registrar.ResourceDispatcher;
import dev.xylonity.knightlib.api.registrar.ResourceEntry;
import dev.xylonity.knightlib.api.registrar.ResourceRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;

public final class CompanionsRecipeSerializers {

    public static final ResourceRegistry<RecipeSerializer<?>> RECIPE_SERIALIZERS = ResourceDispatcher.create(BuiltInRegistries.RECIPE_SERIALIZER, Companions.MOD_ID);

    public static final ResourceEntry<RecipeSerializer<ShadeMawAltarRecipe>> CHALICE_SERIALIZER = RECIPE_SERIALIZERS.register("shade_maw_altar_interaction", () -> ShadeMawAltarRecipe.SERIALIZER);
    public static final ResourceEntry<RecipeSerializer<SoulFurnaceRecipe>> SOUL_FURNACE_SERIALIZER = RECIPE_SERIALIZERS.register("soul_furnace_interaction", SoulFurnaceRecipe.Serializer::new);

}