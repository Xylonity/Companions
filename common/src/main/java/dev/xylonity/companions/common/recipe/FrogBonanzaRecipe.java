package dev.xylonity.companions.common.recipe;

import com.mojang.serialization.MapCodec;
import dev.xylonity.companions.Companions;
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

import java.util.List;

public final class FrogBonanzaRecipe implements Recipe<RecipeInput> {

    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "frog_bonanza_interaction");

    public static final RecipeSerializer<FrogBonanzaRecipe> SERIALIZER = new Serializer();
    public static final RecipeType<FrogBonanzaRecipe> RECIPE_TYPE = new Type();

    public final List<ItemStack> coinInputs;

    public FrogBonanzaRecipe(List<ItemStack> coinInputs) {
        this.coinInputs = coinInputs;
    }

    @Override
    public boolean matches(@NotNull RecipeInput inv, @NotNull Level lvl) {
        return false;
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

    public static final class Type implements RecipeType<FrogBonanzaRecipe> {

        @Override
        public String toString() {
            return ID.toString();
        }

    }

    public static final class Serializer implements RecipeSerializer<FrogBonanzaRecipe> {

        private static final MapCodec<FrogBonanzaRecipe> CODEC = MapCodec.unit(new FrogBonanzaRecipe(List.of()));
        private static final StreamCodec<RegistryFriendlyByteBuf, FrogBonanzaRecipe> STREAM_CODEC = StreamCodec.unit(new FrogBonanzaRecipe(List.of()));

        @Override
        public @NotNull MapCodec<FrogBonanzaRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, FrogBonanzaRecipe> streamCodec() {
            return STREAM_CODEC;
        }

    }

}
