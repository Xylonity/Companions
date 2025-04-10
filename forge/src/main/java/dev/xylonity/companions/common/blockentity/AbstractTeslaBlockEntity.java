package dev.xylonity.companions.common.blockentity;

import dev.xylonity.companions.common.tesla.TeslaConnectionManager;
import dev.xylonity.companions.common.util.interfaces.ITeslaNodeBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * Extend this class to create new components that belong to the Tesla network.
 *
 * Each instance is registered as a node in the Tesla connection graph (in TeslaConnectionManager)
 * and holds common fields such as the active state and a network distance measure.
 *
 * Activation is controlled by an ActivationBehavior, meaning that each specific type
 * of Tesla network component can define its own activation logic.
 */
public abstract class AbstractTeslaBlockEntity extends BlockEntity {

    protected int distance;
    protected boolean isActive;
    protected boolean manuallyDisabled;
    protected ITeslaNodeBehaviour teslaBehavior;

    public AbstractTeslaBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.distance = 0;
        this.isActive = false;
        this.manuallyDisabled = false;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public boolean isActive() {
        return this.isActive;
    }

    public boolean isManuallyDisabled() {
        return this.manuallyDisabled;
    }

    /**
     * Toggles the manual override state (for example, when a player right-clicks the block).
     * When manually disabled, the component remains inactive regardless of incoming signals.
     */
    public void toggleManualOverride() {
        this.manuallyDisabled = !this.manuallyDisabled;
        if (this.manuallyDisabled) {
            setActive(!isActive);
        }
    }

    /**
     * Returns the network node that represents this block entity.
     */
    public TeslaConnectionManager.ConnectionNode asConnectionNode() {
        ResourceLocation dimension = getLevel() != null
                ? getLevel().dimension().location()
                : new ResourceLocation("overworld");
        return TeslaConnectionManager.ConnectionNode.forBlock(getBlockPos(), dimension);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        TeslaConnectionManager.getInstance().registerBlockEntity(this);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.setDistance(tag.getInt("Distance"));
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Distance", this.distance);
    }

    /**
     * Updates the active state of this component.
     *
     * @param isActive true if the component should be active, false otherwise.
     */
    public abstract void setActive(boolean isActive);

    /**
     * Returns whether the component is concurrently powered.
     *
     * @return true if active (or powered), otherwise false.
     */
    public abstract boolean hasConcurrentPower();
}