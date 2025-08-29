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
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public final class EmptyPuppetRecipe implements Recipe<RecipeInput> {

    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "empty_puppet_interaction");

    public static final RecipeSerializer<EmptyPuppetRecipe> SERIALIZER = new Serializer();
    public static final RecipeType<EmptyPuppetRecipe> RECIPE_TYPE = new Type();

    public final ItemStack input = new ItemStack(CompanionsBlocks.EMPTY_PUPPET.get());

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

    public static final class Type implements RecipeType<EmptyPuppetRecipe> {

        @Override
        public String toString() {
            return ID.toString();
        }

    }

    public static final class Serializer implements RecipeSerializer<EmptyPuppetRecipe> {
        private static final MapCodec<EmptyPuppetRecipe> CODEC = MapCodec.unit(new EmptyPuppetRecipe());
        private static final StreamCodec<RegistryFriendlyByteBuf, EmptyPuppetRecipe> STREAM_CODEC = StreamCodec.unit(new EmptyPuppetRecipe());

        @Override
        public @NotNull MapCodec<EmptyPuppetRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, EmptyPuppetRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

}
