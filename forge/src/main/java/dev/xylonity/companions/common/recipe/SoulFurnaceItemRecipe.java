package dev.xylonity.companions.common.recipe;

import com.google.gson.JsonObject;
import dev.xylonity.knightlib.KnightLib;
import dev.xylonity.knightlib.common.recipe.GreatChaliceRecipe;
import dev.xylonity.knightlib.registry.KnightLibItems;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record SoulFurnaceItemRecipe(ItemStack input, ItemStack output) implements Recipe<SimpleContainer> {

    private static final ResourceLocation ID = new ResourceLocation(KnightLib.MOD_ID, "great_chalice_interaction");

    public static final RecipeSerializer<GreatChaliceRecipe> SERIALIZER = new Serializer();
    public static final RecipeType<GreatChaliceRecipe> RECIPE_TYPE = new Type();

    @Override
    public boolean matches(SimpleContainer inv, @NotNull Level lvl) {
        return ItemStack.isSameItem(inv.getItem(0), input);
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull SimpleContainer inv, @NotNull RegistryAccess reg) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess reg) {
        return output.copy();
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return ID;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return RECIPE_TYPE;
    }

    public static final class Type implements RecipeType<GreatChaliceRecipe> {

        @Override
        public String toString() {
            return ID.toString();
        }

    }

    public static final class Serializer implements RecipeSerializer<GreatChaliceRecipe> {
        @Override
        public @NotNull GreatChaliceRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
            return new GreatChaliceRecipe();
        }

        @Override
        public GreatChaliceRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
            return new GreatChaliceRecipe();
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull GreatChaliceRecipe rec) { ;; }
    }

}
