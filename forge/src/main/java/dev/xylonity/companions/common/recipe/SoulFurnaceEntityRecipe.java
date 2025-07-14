package dev.xylonity.companions.common.recipe;

import com.google.gson.JsonObject;
import dev.xylonity.companions.Companions;
import dev.xylonity.companions.registry.CompanionsBlocks;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public final class SoulFurnaceEntityRecipe implements Recipe<SimpleContainer> {

    private static final ResourceLocation ID = new ResourceLocation(Companions.MOD_ID, "soul_furnace_entity_interaction");

    public static final RecipeSerializer<SoulFurnaceEntityRecipe> SERIALIZER = new Serializer();
    public static final RecipeType<SoulFurnaceEntityRecipe> RECIPE_TYPE = new Type();

    public final ItemStack input = new ItemStack(Items.CANDLE);

    @Override
    public boolean matches(SimpleContainer inv, @NotNull Level lvl) {
        return ItemStack.isSameItem(inv.getItem(0), input);
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull SimpleContainer inv, @NotNull RegistryAccess reg) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess reg) {
        return ItemStack.EMPTY;
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

    public static final class Type implements RecipeType<SoulFurnaceEntityRecipe> {

        @Override
        public String toString() {
            return ID.toString();
        }

    }

    public static final class Serializer implements RecipeSerializer<SoulFurnaceEntityRecipe> {
        @Override
        public @NotNull SoulFurnaceEntityRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
            return new SoulFurnaceEntityRecipe();
        }

        @Override
        public SoulFurnaceEntityRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
            return new SoulFurnaceEntityRecipe();
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull SoulFurnaceEntityRecipe rec) { ;; }
    }

}
