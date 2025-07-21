package dev.xylonity.companions.common.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.xylonity.companions.Companions;
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

public record EmptyPuppetRecipe(ItemStack input) implements Recipe<GenericRecipeInput> {

    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "empty_puppet_interaction");

    public static final RecipeSerializer<EmptyPuppetRecipe> SERIALIZER = new Serializer();
    public static final RecipeType<EmptyPuppetRecipe> RECIPE_TYPE = new Type();

    @Override
    public boolean matches(GenericRecipeInput genericRecipeInput, Level level) {
        return ItemStack.isSameItem(genericRecipeInput.getItem(0), input);
    }

    @Override
    public ItemStack assemble(GenericRecipeInput genericRecipeInput, HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return true;
    }

    public static ResourceLocation getID() {
        return ID;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return RECIPE_TYPE;
    }

    public static final class Type implements RecipeType<EmptyPuppetRecipe> {

        @Override
        public String toString() {
            return ID.toString();
        }

    }

    public static final class Serializer implements RecipeSerializer<EmptyPuppetRecipe> {
        public static final MapCodec<EmptyPuppetRecipe> CODEC = RecordCodecBuilder.mapCodec(
                i -> i.group(
                        ItemStack.CODEC.fieldOf("ingredient").forGetter(EmptyPuppetRecipe::input)
                ).apply(i, EmptyPuppetRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, EmptyPuppetRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        ItemStack.STREAM_CODEC, EmptyPuppetRecipe::input,
                        EmptyPuppetRecipe::new
                );

        @Override
        public MapCodec<EmptyPuppetRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, EmptyPuppetRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

}
