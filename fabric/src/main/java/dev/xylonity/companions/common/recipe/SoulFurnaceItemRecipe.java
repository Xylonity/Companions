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

public record SoulFurnaceItemRecipe(ItemStack input, ItemStack output) implements Recipe<RecipeInput> {

    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "soul_furnace_item_interaction");
    public static final RecipeSerializer<SoulFurnaceItemRecipe> SERIALIZER = new Serializer();
    public static final RecipeType<SoulFurnaceItemRecipe> RECIPE_TYPE = new Type();

    @Override
    public boolean matches(RecipeInput inv, @NotNull Level lvl) {
        return ItemStack.isSameItem(inv.getItem(0), input);
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull RecipeInput inv, @NotNull HolderLookup.Provider reg) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider reg) {
        return output.copy();
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

        private static final MapCodec<SoulFurnaceItemRecipe> CODEC = MapCodec.unit(new SoulFurnaceItemRecipe(ItemStack.EMPTY, ItemStack.EMPTY));
        private static final StreamCodec<RegistryFriendlyByteBuf, SoulFurnaceItemRecipe> STREAM_CODEC = StreamCodec.unit(new SoulFurnaceItemRecipe(ItemStack.EMPTY, ItemStack.EMPTY));

        @Override
        public @NotNull MapCodec<SoulFurnaceItemRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, SoulFurnaceItemRecipe> streamCodec() {
            return STREAM_CODEC;
        }

    }

}
