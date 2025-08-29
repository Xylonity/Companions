package dev.xylonity.companions.common.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;
import dev.xylonity.companions.Companions;
import dev.xylonity.companions.registry.CompanionsItems;

public record CroissantDragonArmorRecipe(ItemStack input) implements Recipe<RecipeInput> {

    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "croissant_dragon_armor_interaction");

    public static final RecipeSerializer<CroissantDragonArmorRecipe> SERIALIZER = new Serializer();
    public static final RecipeType<CroissantDragonArmorRecipe> RECIPE_TYPE = new Type();

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

    public static final class Type implements RecipeType<CroissantDragonArmorRecipe> {

        @Override
        public String toString() {
            return ID.toString();
        }

    }

    public static final class Serializer implements RecipeSerializer<CroissantDragonArmorRecipe> {
        private static final CroissantDragonArmorRecipe DEFAULT = new CroissantDragonArmorRecipe(new ItemStack(CompanionsItems.CROISSANT_DRAGON_ARMOR_VANILLA.get()));

        private static final MapCodec<CroissantDragonArmorRecipe> CODEC = MapCodec.unit(DEFAULT);

        private static final StreamCodec<RegistryFriendlyByteBuf, CroissantDragonArmorRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        ItemStack.STREAM_CODEC, CroissantDragonArmorRecipe::input,
                        CroissantDragonArmorRecipe::new
                );

        @Override
        public MapCodec<CroissantDragonArmorRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CroissantDragonArmorRecipe> streamCodec() {
            return STREAM_CODEC;
        }

    }

}
