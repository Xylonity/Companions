package dev.xylonity.companions.common.recipe;

import com.google.gson.JsonObject;
import dev.xylonity.companions.Companions;
import dev.xylonity.companions.registry.CompanionsBlocks;
import dev.xylonity.companions.registry.CompanionsItems;
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

public record CroissantDragonArmorRecipe(ItemStack input) implements Recipe<SimpleContainer> {

    private static final ResourceLocation ID = new ResourceLocation(Companions.MOD_ID, "croissant_dragon_armor_interaction");

    public static final RecipeSerializer<CroissantDragonArmorRecipe> SERIALIZER = new Serializer();
    public static final RecipeType<CroissantDragonArmorRecipe> RECIPE_TYPE = new Type();

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

    public static final class Type implements RecipeType<CroissantDragonArmorRecipe> {

        @Override
        public String toString() {
            return ID.toString();
        }

    }

    public static final class Serializer implements RecipeSerializer<CroissantDragonArmorRecipe> {
        @Override
        public @NotNull CroissantDragonArmorRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
            return new CroissantDragonArmorRecipe(new ItemStack(CompanionsItems.CROISSANT_DRAGON_ARMOR_VANILLA.get()));
        }

        @Override
        public CroissantDragonArmorRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
            ItemStack input = buf.readItem();
            return new CroissantDragonArmorRecipe(input);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull CroissantDragonArmorRecipe rec) {
            buf.writeItem(rec.input);
        }

    }

}
