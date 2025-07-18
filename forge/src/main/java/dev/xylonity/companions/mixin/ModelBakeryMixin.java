package dev.xylonity.companions.mixin;

import dev.xylonity.companions.CompanionsCommon;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {

    @Shadow
    protected abstract void loadTopLevel(ModelResourceLocation loc);

    @Shadow
    @Final
    public static ModelResourceLocation MISSING_MODEL_LOCATION;

    @Unique
    private boolean companions$itemsAdded = false;

    @Unique
    private static final List<ModelResourceLocation> companions$MODELS;

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/ModelBakery;loadTopLevel(Lnet/minecraft/client/resources/model/ModelResourceLocation;)V"))
    private void redirectLoadTopLevel(ModelBakery bakery, ModelResourceLocation modelLocation) {
        loadTopLevel(modelLocation);

        if (!companions$itemsAdded && modelLocation.equals(MISSING_MODEL_LOCATION)) {
            companions$itemsAdded = true;
            for (ModelResourceLocation extra : companions$MODELS) {
                loadTopLevel(extra);
            }

        }
    }

    static {
        companions$MODELS = List.of(
                new ModelResourceLocation(CompanionsCommon.MOD_ID, "crystallized_blood_axe_3d", "inventory"),
                new ModelResourceLocation(CompanionsCommon.MOD_ID, "crystallized_blood_scythe_3d","inventory"),
                new ModelResourceLocation(CompanionsCommon.MOD_ID, "crystallized_blood_sword_3d", "inventory"),
                new ModelResourceLocation(CompanionsCommon.MOD_ID, "mage_staff_3d", "inventory")
        );
    }

}