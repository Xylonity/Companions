package dev.xylonity.companions.common.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsStructureProcessors;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LootInjectionProcessor extends StructureProcessor {

    public static final LootInjectionProcessor INSTANCE = new LootInjectionProcessor();
    public static final Codec<LootInjectionProcessor> CODEC = RecordCodecBuilder.create(instance -> instance.point(INSTANCE));

    private LootInjectionProcessor() {
        ;;
    }

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader level, BlockPos jigsawPos, BlockPos pivotPos, StructureTemplate.StructureBlockInfo blockInfoLocal, StructureTemplate.StructureBlockInfo blockInfoGlobal, StructurePlaceSettings settings) {
        final BlockState state = blockInfoGlobal.state();
        if (!(state.getBlock() instanceof ChestBlock) && !(state.getBlock() instanceof BarrelBlock)) {
            return blockInfoGlobal;
        }

        final RandomSource random = settings.getRandom(blockInfoGlobal.pos());
        if (random.nextDouble() >= CompanionsConfig.STRUCTURE_CHEST_LOOT_FILL_CHANCE) {
            return blockInfoGlobal;
        }

        final List<String> tables = parseTables();
        if (tables.isEmpty()) {
            return blockInfoGlobal;
        }

        final String chosen = tables.get(random.nextInt(tables.size()));
        final CompoundTag nbt = blockInfoGlobal.nbt() == null ? new CompoundTag() : blockInfoGlobal.nbt().copy();
        nbt.putString("LootTable", chosen);
        nbt.putLong("LootTableSeed", random.nextLong());

        return new StructureTemplate.StructureBlockInfo(blockInfoGlobal.pos(), state, nbt);
    }

    private static List<String> parseTables() {
        final List<String> result = new ArrayList<>();
        final String raw = CompanionsConfig.STRUCTURE_LOOT_TABLES;
        if (raw == null || raw.isBlank()) {
            return result;
        }

        for (final String entry : raw.split("[,;]")) {
            final String name = entry.trim();
            if (!name.isEmpty()) {
                result.add(name);
            }

        }

        return result;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return CompanionsStructureProcessors.LOOT_INJECTION.get();
    }

}