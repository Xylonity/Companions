package dev.xylonity.companions.mixin;

import dev.xylonity.companions.CompanionsCommon;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {

    @Shadow
    protected abstract void loadSpecialItemModelAndDependencies(ModelResourceLocation loc);

    @Unique
    private static final List<ModelResourceLocation> companions$MODELS;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/ModelBakery;loadSpecialItemModelAndDependencies(Lnet/minecraft/client/resources/model/ModelResourceLocation;)V", ordinal = 1))
    private void redirectLoadTopLevel(CallbackInfo ci) {
        for (ModelResourceLocation extra : companions$MODELS) {
            loadSpecialItemModelAndDependencies(extra);
        }
    }

    static {
        companions$MODELS = List.of(
                ModelResourceLocation.inventory(ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "crystallized_blood_axe_3d")),
                ModelResourceLocation.inventory(ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "crystallized_blood_scythe_3d")),
                ModelResourceLocation.inventory(ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "crystallized_blood_sword_3d")),
                ModelResourceLocation.inventory(ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "mage_staff_3d"))
        );
    }

}