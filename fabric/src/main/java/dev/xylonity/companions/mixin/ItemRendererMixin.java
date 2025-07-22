package dev.xylonity.companions.mixin;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.registry.CompanionsItems;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Map;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Shadow
    @Final
    private ItemModelShaper itemModelShaper;


    @Unique
    private static final Map<Item, ModelResourceLocation> companions$MODELS_3D;

    @ModifyVariable(method = "render", at = @At("HEAD"), argsOnly = true)
    private BakedModel replaceBaked(BakedModel bakedModel, ItemStack pStack, ItemDisplayContext pDisplayContext) {
        boolean ctx = switch (pDisplayContext) {
            case GUI, GROUND, FIXED -> false;
            default -> true;
        };

        if (ctx) {
            ModelResourceLocation loc = companions$MODELS_3D.get(pStack.getItem());
            if (loc != null) {
                return this.itemModelShaper.getModelManager().getModel(loc);
            }
        }

        return bakedModel;
    }

    static {
        companions$MODELS_3D = Map.of(
                CompanionsItems.CRYSTALLIZED_BLOOD_AXE.get(), ModelResourceLocation.inventory(ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "crystallized_blood_axe_3d")),
                CompanionsItems.CRYSTALLIZED_BLOOD_SCYTHE.get(), ModelResourceLocation.inventory(ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "crystallized_blood_scythe_3d")),
                CompanionsItems.CRYSTALLIZED_BLOOD_SWORD.get(), ModelResourceLocation.inventory(ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "crystallized_blood_sword_3d")),
                CompanionsItems.MAGE_STAFF.get(), ModelResourceLocation.inventory(ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "mage_staff_3d"))
        );

    }

}