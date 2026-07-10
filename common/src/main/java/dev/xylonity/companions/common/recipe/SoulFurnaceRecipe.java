package dev.xylonity.companions.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.xylonity.companions.registry.CompanionsRecipeSerializers;
import dev.xylonity.companions.registry.CompanionsRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class SoulFurnaceRecipe implements Recipe<RecipeInput> {

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

    public SoulFurnaceRecipe(Ingredient input, int requiredCharges, int processTime, @Nullable Item resultItem, int resultCount, @Nullable EntityType<?> resultEntity, @Nullable Block resultBlock) {
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
    public boolean matches(@NotNull RecipeInput container, @NotNull Level level) {
        return input.test(container.getItem(0));
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull RecipeInput container, @NotNull HolderLookup.Provider access) {
        Item item = this.resultItem;
        return item != null ? new ItemStack(item, Math.max(1, resultCount)) : ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider access) {
        Item item = this.resultItem;
        return item != null ? new ItemStack(item, Math.max(1, resultCount)) : ItemStack.EMPTY;
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, input);
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return CompanionsRecipeSerializers.SOUL_FURNACE_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return CompanionsRecipeTypes.SOUL_FURNACE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<SoulFurnaceRecipe> {

        private static final MapCodec<SoulFurnaceRecipe> CODEC =
                RecordCodecBuilder.<SoulFurnaceRecipe>mapCodec(instance -> instance.group(
                        Ingredient.CODEC.fieldOf("input").forGetter(SoulFurnaceRecipe::input),
                        Codec.INT.fieldOf("required_charges").orElse(0).forGetter(SoulFurnaceRecipe::requiredCharges),
                        Codec.INT.fieldOf("process_time").orElse(200).forGetter(SoulFurnaceRecipe::processTime),
                        BuiltInRegistries.ITEM.byNameCodec().optionalFieldOf("result_item").forGetter(r -> Optional.ofNullable(r.resultItem())),
                        Codec.INT.fieldOf("result_count").orElse(1).forGetter(SoulFurnaceRecipe::resultCount),
                        BuiltInRegistries.ENTITY_TYPE.byNameCodec().optionalFieldOf("result_entity").forGetter(r -> Optional.ofNullable(r.resultEntity())),
                        BuiltInRegistries.BLOCK.byNameCodec().optionalFieldOf("result_block").forGetter(r -> Optional.ofNullable(r.resultBlock()))
                ).apply(instance, (Ingredient in, Integer req, Integer time, Optional<Item> itemOpt, Integer cnt, Optional<EntityType<?>> entOpt, Optional<Block> blockOpt) ->
                        new SoulFurnaceRecipe(in, req, time, itemOpt.orElse(null), Math.max(1, cnt), entOpt.orElse(null), blockOpt.orElse(null))
                )).validate(r -> exactlyOneResult(r)
                        ? DataResult.success(r)
                        : DataResult.error(() -> "soul_furnace must define exactly one of result_item, result_entity, or result_block")
                );

        private static boolean exactlyOneResult(SoulFurnaceRecipe r) {
            int n = (r.resultItem() != null ? 1 : 0) + (r.resultEntity() != null ? 1 : 0) + (r.resultBlock() != null ? 1 : 0);
            return n == 1;
        }

        private static final StreamCodec<RegistryFriendlyByteBuf, SoulFurnaceRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, r) -> {
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buf, r.input());
                    ByteBufCodecs.VAR_INT.encode(buf, r.requiredCharges());
                    ByteBufCodecs.VAR_INT.encode(buf, r.processTime());
                    if (r.resultItem() != null) {
                        buf.writeByte((byte)1);
                        ByteBufCodecs.registry(Registries.ITEM).encode(buf, Objects.requireNonNull(r.resultItem()));
                        ByteBufCodecs.VAR_INT.encode(buf, Math.max(1, r.resultCount()));
                    } else if (r.resultEntity() != null) {
                        buf.writeByte((byte)2);
                        ByteBufCodecs.registry(Registries.ENTITY_TYPE).encode(buf, r.resultEntity());
                    } else if (r.resultBlock() != null) {
                        buf.writeByte((byte)3);
                        ByteBufCodecs.registry(Registries.BLOCK).encode(buf, r.resultBlock());
                    } else {
                        buf.writeByte((byte)0);
                    }
                },
                buf -> {
                    Ingredient in = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                    int req = ByteBufCodecs.VAR_INT.decode(buf);
                    int time = ByteBufCodecs.VAR_INT.decode(buf);
                    byte kind = buf.readByte();

                    Item item = null;
                    int count = 1;
                    EntityType<?> entity = null;
                    Block block = null;

                    if (kind == 1) {
                        item = ByteBufCodecs.registry(Registries.ITEM).decode(buf);
                        count = ByteBufCodecs.VAR_INT.decode(buf);
                        if (item == Items.AIR) item = null;
                    } else if (kind == 2) {
                        entity = ByteBufCodecs.registry(Registries.ENTITY_TYPE).decode(buf);
                    } else if (kind == 3) {
                        block = ByteBufCodecs.registry(Registries.BLOCK).decode(buf);
                    }

                    return new SoulFurnaceRecipe(in, req, time, item, Math.max(1, count), entity, block);
                }
        );

        @Override
        public @NotNull MapCodec<SoulFurnaceRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, SoulFurnaceRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

}
