package dev.xylonity.companions.common.blockentity;

import dev.xylonity.companions.common.item.HolyPorcelainPottery;
import dev.xylonity.companions.registry.CompanionsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class HolyPorcelainPotteryBlockEntity extends BlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private CompoundTag storedEntity;
    private String storedName = "";

    public HolyPorcelainPotteryBlockEntity(BlockPos pos, BlockState state) {
        super(CompanionsBlockEntities.HOLY_PORCELAIN_POTTERY.get(), pos, state);
    }

    public void loadFromItem(ItemStack stack) {
        final CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        final CompoundTag tag = data != null ? data.copyTag() : null;
        if (tag != null && tag.contains(HolyPorcelainPottery.STORED_ENTITY, Tag.TAG_COMPOUND)) {
            this.storedEntity = tag.getCompound(HolyPorcelainPottery.STORED_ENTITY).copy();
            this.storedName = tag.getString(HolyPorcelainPottery.STORED_NAME);
            setChanged();
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (this.storedEntity != null) {
            tag.put(HolyPorcelainPottery.STORED_ENTITY, this.storedEntity.copy());
            tag.putString(HolyPorcelainPottery.STORED_NAME, this.storedName);
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(HolyPorcelainPottery.STORED_ENTITY, Tag.TAG_COMPOUND)) {
            this.storedEntity = tag.getCompound(HolyPorcelainPottery.STORED_ENTITY).copy();
            this.storedName = tag.getString(HolyPorcelainPottery.STORED_NAME);
        } else {
            this.storedEntity = null;
            this.storedName = "";
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        ;;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

}