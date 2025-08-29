package dev.xylonity.companions.registry;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.recipe.ShadeMawAltarRecipe;
import dev.xylonity.companions.common.recipe.SoulFurnaceRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class CompanionsRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, Companions.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, Companions.MOD_ID);

    public static final Supplier<RecipeSerializer<ShadeMawAltarRecipe>> CHALICE_SERIALIZER;
    public static final Supplier<RecipeType<ShadeMawAltarRecipe>> CHALICE_TYPE;

    public static final Supplier<RecipeSerializer<SoulFurnaceRecipe>> SOUL_FURNACE_SERIALIZER;
    public static final Supplier<RecipeType<SoulFurnaceRecipe>> SOUL_FURNACE_TYPE;

    static {
        CHALICE_SERIALIZER = SERIALIZERS.register("shade_maw_altar_interaction", () -> ShadeMawAltarRecipe.SERIALIZER);
        CHALICE_TYPE = TYPES.register("shade_maw_altar_interaction", () -> ShadeMawAltarRecipe.RECIPE_TYPE);

        SOUL_FURNACE_SERIALIZER = SERIALIZERS.register("soul_furnace", SoulFurnaceRecipe.Serializer::new);
        SOUL_FURNACE_TYPE = TYPES.register("soul_furnace", () -> new RecipeType<>() {
            @Override
            public String toString() {
                return Companions.MOD_ID + ":soul_furnace";
            }
        });
    }

}
