package dev.xylonity.companions.datagen;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.xylonity.companions.config.CompanionsConfig;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class CompanionsAddItemModifier extends LootModifier {
    public static final Supplier<MapCodec<CompanionsAddItemModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.mapCodec(inst -> codecStart(inst)
                    .and(BuiltInRegistries.ITEM.byNameCodec()
                            .fieldOf("item")
                            .forGetter(m -> m.item))
                    .and(Codec.FLOAT.fieldOf("chance")
                            .forGetter(m -> m.chance))
                    .apply(inst, CompanionsAddItemModifier::new)));

    private final Item item;
    private final float chance;

    public CompanionsAddItemModifier(LootItemCondition[] conditionsIn, Item item, float chance) {
        super(conditionsIn);
        this.item = item;
        this.chance = chance;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(@NotNull ObjectArrayList<ItemStack> generatedLoot, @NotNull LootContext context) {
        for (LootItemCondition condition : this.conditions) {
            if (!condition.test(context)) {
                return generatedLoot;
            }
        }

        if (context.getRandom().nextFloat() <= CompanionsConfig.DEMON_FLESH_DROP_RATE) {
            generatedLoot.add(new ItemStack(this.item));
        }

        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }

}
