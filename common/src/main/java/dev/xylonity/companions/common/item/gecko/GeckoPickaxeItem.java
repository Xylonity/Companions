package dev.xylonity.companions.common.item.gecko;

import dev.xylonity.companions.common.material.ItemMaterials;
import net.minecraft.world.item.PickaxeItem;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class GeckoPickaxeItem extends PickaxeItem implements GeoItem {

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    protected final String resourceKey;

    public GeckoPickaxeItem(Properties properties, String resourceKey, ItemMaterials material, float extraDamage, float extraSpeed) {
        super(material, (int) extraDamage, extraSpeed, properties);
        this.resourceKey = resourceKey;
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    protected abstract Supplier<Object> createGeckoRenderer();

    /**
     * Dead code, stub to satisfy the fabric compiler
     */
    public void createRenderer(Consumer<Object> consumer) {
        ;;
    }

    /**
     * Dead code, stub to satisfy the fabric compiler
     */
    public Supplier<Object> getRenderProvider() {
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        ;;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

}