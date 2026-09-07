package dev.xylonity.companions.mixin;

import com.mojang.datafixers.util.Either;
import dev.xylonity.companions.config.StructureBiomeConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Mixin(Structure.class)
public abstract class StructureMixin {

    @Unique
    private static final int companions$SITE_SAMPLE_STEP = 3;

    @Shadow
    protected abstract Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext context);

    @Inject(method = "findValidGenerationPoint", at = @At("HEAD"), cancellable = true)
    private void companions$filterConfiguredStructureBiomes(Structure.GenerationContext context, CallbackInfoReturnable<Optional<Structure.GenerationStub>> cir) {
        if (!((Object) this instanceof JigsawStructureAccessor accessor)) {
            return;
        }

        final ResourceLocation startPoolId = accessor.companions$getStartPool().unwrapKey().map(ResourceKey::location).orElse(null);
        if (startPoolId == null || !StructureBiomeConfig.isConfiguredPool(startPoolId)) {
            return;
        }

        Optional<Structure.GenerationStub> generationPoint = findGenerationPoint(context);
        if (generationPoint.isEmpty()) {
            cir.setReturnValue(Optional.empty());
            return;
        }

        if ("companions_pools/companions_tent_pool".equals(startPoolId.getPath())) {
            generationPoint = companions$relocateNetherTent(context, generationPoint.get());
            if (generationPoint.isEmpty()) {
                cir.setReturnValue(Optional.empty());
                return;
            }

        }

        final BlockPos position = generationPoint.get().position();
        final Holder<Biome> biome = context.biomeSource().getNoiseBiome(
                QuartPos.fromBlock(position.getX()),
                QuartPos.fromBlock(position.getY()),
                QuartPos.fromBlock(position.getZ()),
                context.randomState().sampler()
        );

        cir.setReturnValue(StructureBiomeConfig.matches(startPoolId, biome) ? generationPoint : Optional.empty());
    }

    @Unique
    private static Optional<Structure.GenerationStub> companions$relocateNetherTent(Structure.GenerationContext context, Structure.GenerationStub stub) {
        final StructurePiecesBuilder pieces = stub.getPiecesBuilder();
        if (pieces.isEmpty()) {
            return Optional.empty();
        }

        final BoundingBox bounds = pieces.getBoundingBox();
        final int minY = Math.max(32, context.heightAccessor().getMinBuildHeight() + 1);
        final int maxY = Math.min(100, context.heightAccessor().getMaxBuildHeight() - bounds.getYSpan());
        if (minY > maxY) {
            return Optional.empty();
        }

        // Finds a valid Y with blocks on the floor
        final Map<Long, NoiseColumn> columns = new HashMap<>();
        final int preferredY = Math.max(minY, Math.min(maxY, bounds.minY()));
        final int targetY = companions$findClosestValidTentY(context, bounds, columns, preferredY, minY, maxY);
        if (targetY == Integer.MIN_VALUE) {
            return Optional.empty();
        }

        final int offsetY = targetY - bounds.minY();

        pieces.offsetPiecesVertically(offsetY);

        return Optional.of(new Structure.GenerationStub(stub.position().offset(0, offsetY, 0), Either.right(pieces)));
    }

    @Unique
    private static int companions$findClosestValidTentY(Structure.GenerationContext context, BoundingBox bounds, Map<Long, NoiseColumn> columns, int preferredY, int minY, int maxY) {
        final int maxDistance = Math.max(preferredY - minY, maxY - preferredY);
        for (int distance = 0; distance <= maxDistance; distance++) {
            final int lowerY = preferredY - distance;
            if (lowerY >= minY && companions$isDryOpenTentSite(context, bounds, columns, lowerY)) {
                return lowerY;
            }

            final int upperY = preferredY + distance;
            if (distance != 0 && upperY <= maxY && companions$isDryOpenTentSite(context, bounds, columns, upperY)) {
                return upperY;
            }

        }

        return Integer.MIN_VALUE;
    }

    private static boolean companions$isDryOpenTentSite(Structure.GenerationContext context, BoundingBox bounds, Map<Long, NoiseColumn> columns, int baseY) {
        for (int x = bounds.minX(); x <= bounds.maxX(); x += companions$SITE_SAMPLE_STEP) {
            if (!companions$isDryOpenTentColumn(context, bounds, columns, x, baseY)) {
                return false;
            }

        }

        if ((bounds.maxX() - bounds.minX()) % companions$SITE_SAMPLE_STEP != 0 && !companions$isDryOpenTentColumn(context, bounds, columns, bounds.maxX(), baseY)) {
            return false;
        }

        return true;
    }

    @Unique
    private static boolean companions$isDryOpenTentColumn(Structure.GenerationContext context, BoundingBox bounds, Map<Long, NoiseColumn> columns, int x, int baseY) {
        for (int z = bounds.minZ(); z <= bounds.maxZ(); z += companions$SITE_SAMPLE_STEP) {
            if (!companions$isDryOpenTentColumn(context, columns, x, z, baseY, bounds.getYSpan())) {
                return false;
            }

        }

        return (bounds.maxZ() - bounds.minZ()) % companions$SITE_SAMPLE_STEP == 0
                || companions$isDryOpenTentColumn(context, columns, x, bounds.maxZ(), baseY, bounds.getYSpan());
    }

    // Validates that there is no lave underneath
    @Unique
    private static boolean companions$isDryOpenTentColumn(Structure.GenerationContext context, Map<Long, NoiseColumn> columns, int x, int z, int baseY, int height) {
        final long key = BlockPos.asLong(x, 0, z);
        final NoiseColumn column = columns.computeIfAbsent(key, ignored -> context.chunkGenerator().getBaseColumn(x, z, context.heightAccessor(), context.randomState()));
        final BlockState floor = column.getBlock(baseY - 1);
        if (!floor.blocksMotion() || !floor.getFluidState().isEmpty()) {
            return false;
        }

        for (int y = baseY; y < baseY + height; y++) {
            final BlockState state = column.getBlock(y);
            if (state.blocksMotion() || !state.getFluidState().isEmpty()) {
                return false;
            }

        }

        return true;
    }

}
