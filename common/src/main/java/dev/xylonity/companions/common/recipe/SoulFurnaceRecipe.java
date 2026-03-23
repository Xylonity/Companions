package dev.xylonity.companions.common.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.xylonity.companions.registry.CompanionsRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SoulFurnaceRecipe implements Recipe<Container> {

    private final ResourceLocation id;
    private final Ingredient input;
    private final int requiredCharges;
    private final int processTime;

    @Nullable
    private final Item resultItem;
    private final int resultCount;

    @Nullable
    private final EntityType<?> resultEntity;

    @Nullable
    private final Block resultBlock;

    public SoulFurnaceRecipe(ResourceLocation id, Ingredient input, int requiredCharges, int processTime, @Nullable Item resultItem, int resultCount, @Nullable EntityType<?> resultEntity, @Nullable Block resultBlock) {
        this.id = id;
        this.input = input;
        this.requiredCharges = requiredCharges;
        this.processTime = processTime;
        this.resultItem = resultItem;
        this.resultCount = resultCount;
        this.resultEntity = resultEntity;
        this.resultBlock = resultBlock;
    }

    public Ingredient input() {
        return input;
    }

    public int requiredCharges() {
        return requiredCharges;
    }

    public int processTime() {
        return processTime;
    }

    @Nullable
    public Item resultItem() {
        return resultItem;
    }

    public int resultCount() {
        return resultCount;
    }

    @Nullable
    public EntityType<?> resultEntity() {
        return resultEntity;
    }

    @Nullable
    public Block resultBlock() {
        return resultBlock;
    }

    public boolean outputsItem() {
        return resultItem != null;
    }

    public boolean outputsEntity() {
        return resultEntity != null;
    }

    public boolean outputsBlock() {
        return resultBlock != null;
    }

    @Override
    public boolean matches(@NotNull Container container, @NotNull Level level) {
        return input.test(container.getItem(0));
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull Container container, @NotNull RegistryAccess access) {
        return outputsItem() ? new ItemStack(resultItem, Math.max(1, resultCount)) : ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess access) {
        return outputsItem() ? new ItemStack(resultItem, Math.max(1, resultCount)) : ItemStack.EMPTY;
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, input);
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return CompanionsRecipes.SOUL_FURNACE_SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return CompanionsRecipes.SOUL_FURNACE_TYPE;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return id;
    }

    public static class Serializer implements RecipeSerializer<SoulFurnaceRecipe> {
        @Override
        public @NotNull SoulFurnaceRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
            Ingredient input = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "input"));
            int required = GsonHelper.getAsInt(json, "required_charges", 0);
            int time = GsonHelper.getAsInt(json, "process_time", 200);

            Item item = null;
            int count = GsonHelper.getAsInt(json, "result_count", 1);
            EntityType<?> entity = null;
            Block block = null;

            if (json.has("result_item")) {
                ResourceLocation rl = new ResourceLocation(GsonHelper.getAsString(json, "result_item"));
                item = BuiltInRegistries.ITEM.get(rl);

                if (item == Items.AIR) {
                    item = null;
                }

            }

            if (json.has("result_entity")) {
                ResourceLocation rl = new ResourceLocation(GsonHelper.getAsString(json, "result_entity"));
                entity = BuiltInRegistries.ENTITY_TYPE.get(rl);
            }

            if (json.has("result_block")) {
                ResourceLocation rl = new ResourceLocation(GsonHelper.getAsString(json, "result_block"));
                block = BuiltInRegistries.BLOCK.get(rl);
            }

            if (((item != null ? 1 : 0) + (entity != null ? 1 : 0) + (block != null ? 1 : 0)) != 1) {
                throw new JsonParseException("soul_furnace must define exactly one of result_item, result_entity, result_block");
            }

            return new SoulFurnaceRecipe(id, input, required, time, item, Math.max(1, count), entity, block);
        }

        @Override
        public @NotNull SoulFurnaceRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
            Ingredient input = Ingredient.fromNetwork(buf);
            int required = buf.readVarInt();
            int time = buf.readVarInt();
            byte kind = buf.readByte();

            Item item = null;
            int count = 1;

            EntityType<?> entity = null;
            Block block = null;

            if (kind == 1) {
                ResourceLocation rl = buf.readResourceLocation();
                item = BuiltInRegistries.ITEM.get(rl);
                count = buf.readVarInt();

                if (item == Items.AIR) {
                    item = null;
                }

            } else if (kind == 2) {
                ResourceLocation rl = buf.readResourceLocation();
                entity = BuiltInRegistries.ENTITY_TYPE.get(rl);
            } else if (kind == 3) {
                ResourceLocation rl = buf.readResourceLocation();
                block = BuiltInRegistries.BLOCK.get(rl);
            }

            return new SoulFurnaceRecipe(id, input, required, time, item, Math.max(1, count), entity, block);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull SoulFurnaceRecipe r) {
            r.input.toNetwork(buf);
            buf.writeVarInt(r.requiredCharges);
            buf.writeVarInt(r.processTime);
            if (r.resultItem != null) {
                buf.writeByte((byte) 1);
                ResourceLocation key = BuiltInRegistries.ITEM.getKey(r.resultItem);

                if (key == null) key = new ResourceLocation("minecraft", "air");

                buf.writeResourceLocation(key);
                buf.writeVarInt(Math.max(1, r.resultCount));
            } else if (r.resultEntity != null) {
                buf.writeByte((byte) 2);
                ResourceLocation key = BuiltInRegistries.ENTITY_TYPE.getKey(r.resultEntity);

                if (key == null) key = new ResourceLocation("minecraft", "pig");

                buf.writeResourceLocation(key);
            } else if (r.resultBlock != null) {
                buf.writeByte((byte) 3);
                ResourceLocation key = BuiltInRegistries.BLOCK.getKey(r.resultBlock);

                if (key == null) key = new ResourceLocation("minecraft", "air");

                buf.writeResourceLocation(key);
            } else {
                buf.writeByte((byte) 0);
            }

        }

    }

}
