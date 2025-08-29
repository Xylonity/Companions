package dev.xylonity.companions.common.recipe;

import com.mojang.serialization.MapCodec;
import dev.xylonity.companions.Companions;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public final class SoulFurnaceEntityRecipe implements Recipe<RecipeInput> {

    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "soul_furnace_entity_interaction");
    public static final RecipeSerializer<SoulFurnaceEntityRecipe> SERIALIZER = new Serializer();
    public static final RecipeType<SoulFurnaceEntityRecipe> RECIPE_TYPE = new Type();

    public final ItemStack input;
    public final EntityType<?> entityType;

    public SoulFurnaceEntityRecipe(ItemStack input, EntityType<?> entityType) {
        this.input = input;
        this.entityType = entityType;
    }

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

    public static final class Type implements RecipeType<SoulFurnaceEntityRecipe> {
        @Override
        public String toString() {
            return ID.toString();
        }

    }

    public static final class Serializer implements RecipeSerializer<SoulFurnaceEntityRecipe> {

        private static final MapCodec<SoulFurnaceEntityRecipe> CODEC = MapCodec.unit(new SoulFurnaceEntityRecipe(ItemStack.EMPTY, EntityType.PIG));
        private static final StreamCodec<RegistryFriendlyByteBuf, SoulFurnaceEntityRecipe> STREAM_CODEC = StreamCodec.unit(new SoulFurnaceEntityRecipe(ItemStack.EMPTY, EntityType.PIG));

        @Override
        public @NotNull MapCodec<SoulFurnaceEntityRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, SoulFurnaceEntityRecipe> streamCodec() {
            return STREAM_CODEC;
        }

    }

}
