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

public record ShadeAltarRecipe(ItemStack input) implements Recipe<GenericRecipeInput> {

    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "shade_altar_interaction");

    public static final RecipeSerializer<ShadeAltarRecipe> SERIALIZER = new Serializer();
    public static final RecipeType<ShadeAltarRecipe> RECIPE_TYPE = new Type();


    @Override
    public boolean matches(GenericRecipeInput genericRecipeInput, Level level) {
        return ItemStack.isSameItem(genericRecipeInput.getItem(0), input);
    }

    @Override
    public ItemStack assemble(GenericRecipeInput genericRecipeInput, HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    public static ResourceLocation getID() {
        return ID;
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

    public static final class Type implements RecipeType<ShadeAltarRecipe> {

        @Override
        public String toString() {
            return ID.toString();
        }

    }

    public static final class Serializer implements RecipeSerializer<ShadeAltarRecipe> {
        public static final MapCodec<ShadeAltarRecipe> CODEC = RecordCodecBuilder.mapCodec(
                i -> i.group(
                        ItemStack.CODEC.fieldOf("ingredient").forGetter(ShadeAltarRecipe::input)
                ).apply(i, ShadeAltarRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, ShadeAltarRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        ItemStack.STREAM_CODEC, ShadeAltarRecipe::input,
                        ShadeAltarRecipe::new
                );

        @Override
        public MapCodec<ShadeAltarRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ShadeAltarRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

}
