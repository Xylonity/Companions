package dev.xylonity.companions.mixin;

import dev.xylonity.companions.config.StructureBiomeConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Structure.class)
public abstract class StructureMixin {

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

        final Optional<Structure.GenerationStub> generationPoint = findGenerationPoint(context);
        if (generationPoint.isEmpty()) {
            cir.setReturnValue(Optional.empty());
            return;
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

}
