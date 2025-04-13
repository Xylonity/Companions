package dev.xylonity.companions.common.blockentity;

import dev.xylonity.companions.common.tesla.TeslaConnectionManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtils;

/**
 * Extend this class to create new components that belong to the Tesla network.
 *
 * Each instance is registered as a node in the Tesla connection graph (in TeslaConnectionManager)
 * and holds common fields such as the active state and a network distance measure, along with the
 * cycle scheduled lifetime.
 */
public abstract class AbstractTeslaBlockEntity extends BlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public final TeslaConnectionManager connectionManager;

    public int tickCount;
    public int activationTick;
    public int cycleCounter;
    protected boolean pendingRemoval = false;
    protected boolean receivesGenerator = false;

    protected int distance;
    protected boolean isActive;

    public AbstractTeslaBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.connectionManager = TeslaConnectionManager.getInstance();
        this.distance = 0;
        this.isActive = false;
        this.tickCount = 0;
        this.activationTick = 0;
        this.cycleCounter = 0;
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
        tag.getList("OutgoingConnections", 10).forEach(t ->
                TeslaConnectionManager.getInstance().addConnection(asConnectionNode(), TeslaConnectionManager.ConnectionNode.deserialize((CompoundTag) t), true)
        );
        this.tickCount = tag.getInt("TickCount");
        this.isActive = tag.getBoolean("IsActive");
        this.activationTick = tag.contains("ActivationTick") ? tag.getInt("ActivationTick") : 0;
        this.cycleCounter = tag.getInt("cycleCounter");
        this.receivesGenerator = tag.getBoolean("ReceivesGenerator");
        this.setDistance(tag.getInt("Distance"));
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        ListTag outgoing = new ListTag();
        connectionManager.getOutgoing(asConnectionNode()).forEach(node -> outgoing.add(node.serialize()));
        tag.put("OutgoingConnections", outgoing);

        tag.putInt("TickCount", this.tickCount);
        tag.putBoolean("IsActive", this.isActive);
        tag.putInt("ActivationTick", this.activationTick);
        tag.putInt("cycleCounter", this.cycleCounter);
        tag.putBoolean("ReceivesGenerator", this.receivesGenerator);
        tag.putInt("Distance", this.distance);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) { ;; }

    @Override
    public double getTick(Object o) {
        return RenderUtils.getCurrentTick();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        this.tickCount = tag.getInt("TickCount");
        this.isActive = tag.getBoolean("IsActive");
        this.distance = tag.getInt("Distance");
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("TickCount", this.tickCount);
        tag.putBoolean("IsActive", this.isActive);
        tag.putInt("Distance", this.distance);
        return tag;
    }

    public void markPendingRemoval() {
        this.pendingRemoval = true;
    }

    public boolean isPendingRemoval() {
        return pendingRemoval;
    }

    public void setReceivesGenerator(boolean flag) {
        this.receivesGenerator = flag;
    }

    public boolean isReceivesGenerator() {
        return this.receivesGenerator;
    }

    public void setAnimationStartTick(int tick) {
        this.activationTick = tick;
    }

    public void startCycle() {
        this.cycleCounter = 0;
    }

    public int getAnimationStartTick() {
        return activationTick;
    }

    public boolean hasConcurrentPower() {
        return this.isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

}