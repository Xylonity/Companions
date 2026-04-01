package dev.xylonity.companions.mixin;

import dev.xylonity.companions.common.item.gecko.GeckoArmorItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Mixin(GeckoArmorItem.class)
public abstract class GeckoArmorItemMixin implements GeoItem {

    @Shadow
    protected abstract Supplier<Object> createGeckoRenderer();

    @Unique
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private GeoArmorRenderer<GeckoArmorItem> renderer;

            @Override
            @SuppressWarnings("unchecked")
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {

                if (this.renderer == null)
                    this.renderer = (GeoArmorRenderer<GeckoArmorItem>) createGeckoRenderer().get();

                this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);

                return this.renderer;
            }

        });

    }

}