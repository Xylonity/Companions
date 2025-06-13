package dev.xylonity.companions.common.blockentity;

import dev.xylonity.companions.common.tesla.TeslaConnectionManager;
import dev.xylonity.companions.common.tesla.behaviour.DefaultAttackBehaviour;
import dev.xylonity.companions.common.util.interfaces.ITeslaNodeBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
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
 * Each instance is registered as a node in the tesla network graph (see TeslaConnectionManager)
 * and holds common fields such as the active state and a network distance measure, along with the
 * cycle scheduled lifetime.
 */
public abstract class AbstractTeslaBlockEntity extends BlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public final TeslaConnectionManager connectionManager;

    public int tickCount;
    public int activationTick;
    public int cycleCounter;

    protected boolean pendingRemoval;
    protected boolean receivesGenerator;
    protected int distance;
    protected boolean isActive;

    protected ITeslaNodeBehaviour defaultAttackBehaviour;

    public AbstractTeslaBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.connectionManager = TeslaConnectionManager.getInstance();
        this.distance = 0;
        this.isActive = false;
        this.tickCount = 0;
        this.activationTick = 0;
        this.cycleCounter = -1;
        this.pendingRemoval = false;
        this.receivesGenerator = false;
        this.defaultAttackBehaviour = new DefaultAttackBehaviour();
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
        tag.getList("OutgoingConnections", 10).forEach(t -> {
            TeslaConnectionManager.getInstance().addConnection(asConnectionNode(), TeslaConnectionManager.ConnectionNode.deserialize((CompoundTag) t), true);
        });
        this.tickCount = tag.getInt("TickCount");
        this.isActive = tag.getBoolean("IsActive");
        this.activationTick = tag.contains("ActivationTick") ? tag.getInt("ActivationTick") : 0;
        if (this.cycleCounter >= 0) {
            tag.putInt("CycleCounter", this.cycleCounter);
        }
        this.receivesGenerator = tag.getBoolean("ReceivesGenerator");
        this.setDistance(tag.getInt("Distance"));
        this.setAnimationStartTick(tag.getInt("AnimationTick"));
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        ListTag outgoing = new ListTag();
        connectionManager.getOutgoing(asConnectionNode()).forEach(node -> {
            outgoing.add(node.serialize());
        });
        tag.put("OutgoingConnections", outgoing);

        tag.putInt("TickCount", this.tickCount);
        tag.putBoolean("IsActive", this.isActive);
        tag.putInt("ActivationTick", this.activationTick);
        tag.putInt("CycleCounter", this.cycleCounter);
        tag.putBoolean("ReceivesGenerator", this.receivesGenerator);
        tag.putInt("Distance", this.distance);
        tag.putInt("AnimationTick", this.getAnimationStartTick());
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
        this.setAnimationStartTick(tag.getInt("AnimationTick"));
        this.cycleCounter = tag.getInt("CycleCounter");
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("TickCount", this.tickCount);
        tag.putBoolean("IsActive", this.isActive);
        tag.putInt("Distance", this.distance);
        tag.putInt("AnimationTick", this.getAnimationStartTick());
        tag.putInt("CycleCounter", this.cycleCounter);
        return tag;
    }

    public void sync() {
        if (!(level instanceof ServerLevel serverLevel)) return;

        Packet<ClientGamePacketListener> pkt = ClientboundBlockEntityDataPacket.create(this);
        ChunkPos chunkPos = new ChunkPos(worldPosition);

        serverLevel.getChunkSource().chunkMap.getPlayers(chunkPos, false).forEach(p -> p.connection.send(pkt));
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
        this.setChanged();
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

    // Can the module connect to other modules
    public boolean canConnectToOtherModules() {
        return true;
    }

    // Position offset where the electrical charge is emitted (from x + 0.5, y, z + 0.5)
    public @NotNull abstract Vec3 electricalChargeOriginOffset();

    // Position where the electrical charge is received
    public @NotNull abstract Vec3 electricalChargeEndOffset();

    /**
     * Defers the call from the wrench item when this node is getting connected to another one
     * Don't override if the connection is simple (one node to another node)
     */
    public void handleNodeSelection(TeslaConnectionManager.ConnectionNode thisNode, TeslaConnectionManager.ConnectionNode nodeToConnect, @Nullable UseOnContext ctx) {
        connectionManager.addConnection(thisNode, nodeToConnect);
    }

    /**
     * Defers the call from the wrench item when this node is getting removed from the tesla network
     * Don't override if the connection is simple (one node to another node)
     */
    public void handleNodeRemoval(TeslaConnectionManager.ConnectionNode thisNode, TeslaConnectionManager.ConnectionNode nodeToConnect, @Nullable UseOnContext ctx) {
        connectionManager.removeConnection(thisNode, nodeToConnect);
    }

}