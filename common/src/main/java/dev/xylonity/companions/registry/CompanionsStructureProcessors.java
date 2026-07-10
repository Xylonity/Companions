package dev.xylonity.companions.registry;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.worldgen.LootInjectionProcessor;
import dev.xylonity.knightlib.api.registrar.ResourceDispatcher;
import dev.xylonity.knightlib.api.registrar.ResourceEntry;
import dev.xylonity.knightlib.api.registrar.ResourceRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

public final class CompanionsStructureProcessors {

    public static final ResourceRegistry<StructureProcessorType<?>> PROCESSORS = ResourceDispatcher.create(BuiltInRegistries.STRUCTURE_PROCESSOR, Companions.MOD_ID);

    public static final ResourceEntry<StructureProcessorType<LootInjectionProcessor>> LOOT_INJECTION = PROCESSORS.register("loot_injection", () -> () -> LootInjectionProcessor.CODEC);

}
