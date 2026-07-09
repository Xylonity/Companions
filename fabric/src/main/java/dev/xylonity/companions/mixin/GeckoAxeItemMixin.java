package dev.xylonity.companions.mixin;

import dev.xylonity.companions.common.item.gecko.GeckoArmorItem;
import dev.xylonity.companions.common.item.gecko.GeckoAxeItem;
import dev.xylonity.companions.common.material.ItemMaterials;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Mixin(GeckoAxeItem.class)
public abstract class GeckoAxeItemMixin implements GeoItem {

    @Unique
    private Supplier<Object> companions$renderProvider;

    @Shadow
    protected abstract Supplier<Object> createGeckoRenderer();

    @Inject(method = "<init>", at = @At("TAIL"))
    private void companions$init(Item.Properties properties, String resourceKey, ItemMaterials material, float extraDamage, float extraSpeed, CallbackInfo ci) {
        this.companions$renderProvider = GeoItem.makeRenderer(this);
    }

    @Inject(method = "createRenderer", at = @At("HEAD"), cancellable = true, remap = false)
    private void companions$createRenderer(Consumer<Object> consumer, CallbackInfo ci) {
        consumer.accept(new RenderProvider() {

            private BlockEntityWithoutLevelRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null)
                    this.renderer = (BlockEntityWithoutLevelRenderer) createGeckoRenderer().get();

                return this.renderer;
            }

        });

        ci.cancel();
    }

    @Inject(method = "getRenderProvider", at = @At("HEAD"), cancellable = true, remap = false)
    private void companions$getRenderProvider(CallbackInfoReturnable<Supplier<Object>> cir) {
        cir.setReturnValue(this.companions$renderProvider);
    }

}