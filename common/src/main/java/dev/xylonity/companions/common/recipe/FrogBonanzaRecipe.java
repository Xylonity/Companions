package dev.xylonity.companions.common.recipe;

import com.google.gson.JsonObject;
import dev.xylonity.companions.Companions;
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

import java.util.List;

public final class FrogBonanzaRecipe implements Recipe<SimpleContainer> {

    private static final ResourceLocation ID = Companions.of("frog_bonanza_interaction");

    public static final RecipeSerializer<FrogBonanzaRecipe> SERIALIZER = new Serializer();
    public static final RecipeType<FrogBonanzaRecipe> RECIPE_TYPE = new Type();

    public final List<ItemStack> coinInputs;

    public FrogBonanzaRecipe(List<ItemStack> coinInputs) {
        this.coinInputs = coinInputs;
    }

    @Override
    public boolean matches(@NotNull SimpleContainer inv, @NotNull Level lvl) {
        return false;
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

    public static final class Type implements RecipeType<FrogBonanzaRecipe> {

        @Override
        public String toString() {
            return ID.toString();
        }

    }

    public static final class Serializer implements RecipeSerializer<FrogBonanzaRecipe> {

        @Override
        public @NotNull FrogBonanzaRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
            return new FrogBonanzaRecipe(List.of());
        }

        @Override
        public FrogBonanzaRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
            return new FrogBonanzaRecipe(List.of());
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull FrogBonanzaRecipe rec) {
            ;;
        }

    }

}
