package dev.xylonity.companions.common.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.xylonity.knightlib.KnightLib;
import dev.xylonity.knightlib.common.recipe.input.GenericRecipeInput;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record SoulFurnaceItemRecipe(ItemStack input, ItemStack output) implements Recipe<GenericRecipeInput> {

    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(KnightLib.MOD_ID, "great_chalice_interaction");

    public static final RecipeSerializer<SoulFurnaceItemRecipe> SERIALIZER = new Serializer();
    public static final RecipeType<SoulFurnaceItemRecipe> RECIPE_TYPE = new Type();

    @Override
    public boolean matches(GenericRecipeInput genericRecipeInput, Level level) {
        return ItemStack.isSameItem(genericRecipeInput.getItem(0), input);
    }

    @Override
    public ItemStack assemble(GenericRecipeInput genericRecipeInput, HolderLookup.Provider provider) {
        return output.copy();
    }

    public static ResourceLocation getID() {
        return ID;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return true;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return RECIPE_TYPE;
    }

    public static final class Type implements RecipeType<SoulFurnaceItemRecipe> {

        @Override
        public String toString() {
            return ID.toString();
        }

    }

    public static final class Serializer implements RecipeSerializer<SoulFurnaceItemRecipe> {
        public static final MapCodec<SoulFurnaceItemRecipe> CODEC = RecordCodecBuilder.mapCodec(
                i -> i.group(
                        ItemStack.CODEC.fieldOf("ingredient").forGetter(SoulFurnaceItemRecipe::input),
                        ItemStack.CODEC.fieldOf("result").forGetter(SoulFurnaceItemRecipe::output)
                ).apply(i, SoulFurnaceItemRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, SoulFurnaceItemRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        ItemStack.STREAM_CODEC, SoulFurnaceItemRecipe::input,
                        ItemStack.STREAM_CODEC, SoulFurnaceItemRecipe::output,
                        SoulFurnaceItemRecipe::new
                );

        @Override
        public MapCodec<SoulFurnaceItemRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SoulFurnaceItemRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

}
