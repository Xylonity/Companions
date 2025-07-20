package dev.xylonity.companions.datagen;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsItems;
import dev.xylonity.companions.tag.CompanionsTags;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

import java.util.concurrent.CompletableFuture;

public class CompanionsLootModifierGenerator extends GlobalLootModifierProvider {

    public CompanionsLootModifierGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> registrar) {
        super(output, Companions.MOD_ID, registrar);
    }

    @Override
    protected void start(HolderLookup.Provider provider) {
        LootItemCondition condition = LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().of(CompanionsTags.DEMON_FLESH_DROP).build()).build();

        add("demon_flesh_hostiles", new CompanionsAddItemModifier(
                new LootItemCondition[]{condition},
                CompanionsItems.DEMON_FLESH.get(),
                (float) CompanionsConfig.DEMON_FLESH_DROP_RATE
        ));
    }

}