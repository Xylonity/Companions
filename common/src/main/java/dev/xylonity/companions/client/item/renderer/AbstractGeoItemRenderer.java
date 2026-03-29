package dev.xylonity.companions.client.item.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.companions.Companions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class AbstractGeoItemRenderer<T extends Item & GeoAnimatable> extends GeoItemRenderer<T> {

    private final String resourceKey;

    public AbstractGeoItemRenderer(GeoModel<T> model, String resourceKey) {
        super(model);
        this.resourceKey = resourceKey;
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack pose, MultiBufferSource buf, int light, int overlay) {
        if (isStaticContext(context)) {
            renderStaticModel(stack, context, pose, buf, light, overlay);
            return;
        }

        super.renderByItem(stack, context, pose, buf, light, overlay);
    }

    private void renderStaticModel(ItemStack stack, ItemDisplayContext context, PoseStack pose, MultiBufferSource buf, int light, int overlay) {
        final Minecraft minecraft = Minecraft.getInstance();

        pose.pushPose();
        applyStaticTransform(context, pose);

        final ModelResourceLocation modelResourceLocation = new ModelResourceLocation(Companions.MOD_ID, resourceKey + "_icon", "inventory");
        minecraft.getItemRenderer().render(stack, context, false, pose, buf, light, overlay, minecraft.getModelManager().getModel(modelResourceLocation));

        pose.popPose();
    }

    public void applyStaticTransform(ItemDisplayContext context, PoseStack pose) {
        switch (context) {
            case GUI -> pose.translate(0.5, 0.5, 0);
            case GROUND -> pose.translate(0.5, 0.5, 0.5);
            default -> { // FIXED
                pose.translate(0.5, 0.5, 0);
                pose.scale(0.75f, 0.75f, 0.75f);
            }

        }

    }

    public boolean isStaticContext(ItemDisplayContext context) {
        return context == ItemDisplayContext.GUI || context == ItemDisplayContext.GROUND || context == ItemDisplayContext.FIXED;
    }

}