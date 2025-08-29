package dev.xylonity.companions.common.recipe;

import com.mojang.serialization.MapCodec;
import dev.xylonity.companions.Companions;
import dev.xylonity.companions.registry.CompanionsBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public final class CroissantEggRecipe implements Recipe<RecipeInput> {

    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "croissant_egg_interaction");

    public static final RecipeSerializer<CroissantEggRecipe> SERIALIZER = new Serializer();
    public static final RecipeType<CroissantEggRecipe> RECIPE_TYPE = new Type();

    public final ItemStack input = new ItemStack(CompanionsBlocks.CROISSANT_EGG.get());

    @Override
    public boolean matches(RecipeInput inv, @NotNull Level lvl) {
        return ItemStack.isSameItem(inv.getItem(0), input);
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull RecipeInput inv, @NotNull HolderLookup.Provider reg) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider reg) {
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

    public static final class Type implements RecipeType<CroissantEggRecipe> {

        @Override
        public String toString() {
            return ID.toString();
        }

    }

    public static final class Serializer implements RecipeSerializer<CroissantEggRecipe> {
        private static final MapCodec<CroissantEggRecipe> CODEC = MapCodec.unit(new CroissantEggRecipe());
        private static final StreamCodec<RegistryFriendlyByteBuf, CroissantEggRecipe> STREAM_CODEC = StreamCodec.unit(new CroissantEggRecipe());

        @Override
        public @NotNull MapCodec<CroissantEggRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, CroissantEggRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

}
