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

public record SoulFurnaceEntityRecipe(ItemStack input) implements Recipe<GenericRecipeInput> {

    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "soul_furnace_entity_interaction");

    public static final RecipeSerializer<SoulFurnaceEntityRecipe> SERIALIZER = new Serializer();
    public static final RecipeType<SoulFurnaceEntityRecipe> RECIPE_TYPE = new Type();

    @Override
    public boolean matches(GenericRecipeInput genericRecipeInput, Level level) {
        return ItemStack.isSameItem(genericRecipeInput.getItem(0), input);
    }

    @Override
    public ItemStack assemble(GenericRecipeInput genericRecipeInput, HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    public static ResourceLocation getID() {
        return ID;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
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

    public static final class Type implements RecipeType<SoulFurnaceEntityRecipe> {

        @Override
        public String toString() {
            return ID.toString();
        }

    }

    public static final class Serializer implements RecipeSerializer<SoulFurnaceEntityRecipe> {
        public static final MapCodec<SoulFurnaceEntityRecipe> CODEC = RecordCodecBuilder.mapCodec(
                i -> i.group(
                        ItemStack.CODEC.fieldOf("ingredient").forGetter(SoulFurnaceEntityRecipe::input)
                ).apply(i, SoulFurnaceEntityRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, SoulFurnaceEntityRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        ItemStack.STREAM_CODEC, SoulFurnaceEntityRecipe::input,
                        SoulFurnaceEntityRecipe::new
                );

        @Override
        public MapCodec<SoulFurnaceEntityRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SoulFurnaceEntityRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

}
