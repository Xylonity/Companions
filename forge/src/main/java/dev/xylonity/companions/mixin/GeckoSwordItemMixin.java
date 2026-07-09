package dev.xylonity.companions.mixin;

import dev.xylonity.companions.common.item.gecko.GeckoSwordItem;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import software.bernie.geckolib.animatable.GeoItem;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Mixin(GeckoSwordItem.class)
public abstract class GeckoSwordItemMixin implements GeoItem {

    @Shadow
    protected abstract Supplier<Object> createGeckoRenderer();

    @Unique
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = (BlockEntityWithoutLevelRenderer) createGeckoRenderer().get();
                }

                return this.renderer;
            }

        });

    }

}