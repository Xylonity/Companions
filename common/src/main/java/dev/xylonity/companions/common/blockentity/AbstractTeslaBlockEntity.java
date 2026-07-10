package dev.xylonity.companions.common.blockentity;

import dev.xylonity.companions.common.tesla.ConnectionTarget;
import dev.xylonity.companions.common.tesla.TeslaNetwork;
import dev.xylonity.companions.common.tesla.behaviour.DefaultAttackBehaviour;
import dev.xylonity.companions.common.util.interfaces.ITeslaNodeBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import dev.xylonity.knightlib.api.util.ResourceLocations;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Base class for all Tesla network block entities.
 *
 * Extend this class to create new components that belong to the Tesla network.
 *
 * Each instance stores its own outgoing connections (synced to the client for rendering)
 * and registers itself with the server side {@link TeslaNetwork} for graph operations.
 */
public abstract class AbstractTeslaBlockEntity extends BlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final Set<ConnectionTarget> outgoing = ConcurrentHashMap.newKeySet();

    public int tickCount;
    public int activationTick;
    public int cycleCounter;

    protected boolean pendingRemoval;
    protected boolean receivesGenerator;
    protected int distance;
    protected boolean isActive;

    protected UUID ownerUUID;

    protected ITeslaNodeBehaviour defaultAttackBehaviour;

    private boolean registeredInTesla = false;

    public AbstractTeslaBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.distance = 0;
        this.isActive = false;
        this.tickCount = 0;
        this.activationTick = 0;
        this.cycleCounter = -1;
        this.pendingRemoval = false;
        this.receivesGenerator = false;
        this.defaultAttackBehaviour = new DefaultAttackBehaviour();
    }

    public ConnectionTarget asConnectionTarget() {
        final ResourceLocation dimensionRL = getLevel() != null
                ? getLevel().dimension().location()
                : ResourceLocations.parse("overworld");

        return ConnectionTarget.forBlock(getBlockPos(), dimensionRL);
    }

    public Set<ConnectionTarget> getOutgoing() {
        return outgoing;
    }

    public void addOutgoing(ConnectionTarget target) {
        outgoing.add(target);
        setChanged();
    }

    public void removeOutgoing(ConnectionTarget target) {
        outgoing.remove(target);
        setChanged();
    }

    @Override
    public void setLevel(@NotNull Level level) {
        super.setLevel(level);
        if (!level.isClientSide && !registeredInTesla) {
            TeslaNetwork.get(level).registerBlockEntity(this);
            registeredInTesla = true;
        }

    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        outgoing.clear();
        if (tag.contains("OutgoingConnections", Tag.TAG_LIST)) {
            tag.getList("OutgoingConnections", Tag.TAG_COMPOUND).forEach(tagg ->
                    outgoing.add(ConnectionTarget.deserialize((CompoundTag) tagg)));
        }

        this.tickCount = tag.getInt("TickCount");
        this.isActive = tag.getBoolean("IsActive");
        this.activationTick = tag.contains("ActivationTick") ? tag.getInt("ActivationTick") : 0;

        if (tag.contains("CycleCounter")) {
            this.cycleCounter = tag.getInt("CycleCounter");
        }

        this.receivesGenerator = tag.getBoolean("ReceivesGenerator");
        this.distance = tag.getInt("Distance");
        this.activationTick = tag.getInt("AnimationTick");
        if (tag.contains("OwnerUUID")) {
            this.ownerUUID = tag.getUUID("OwnerUUID");
        }

    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        final ListTag list = new ListTag();
        outgoing.forEach(node -> list.add(node.serialize()));
        tag.put("OutgoingConnections", list);

        tag.putInt("TickCount", this.tickCount);
        tag.putBoolean("IsActive", this.isActive);
        tag.putInt("ActivationTick", this.activationTick);
        tag.putInt("CycleCounter", this.cycleCounter);
        tag.putBoolean("ReceivesGenerator", this.receivesGenerator);
        tag.putInt("Distance", this.distance);
        tag.putInt("AnimationTick", this.activationTick);
        if (ownerUUID != null) {
            tag.putUUID("OwnerUUID", ownerUUID);
        }

    }

    /**
     * Now includes outgoing connections so the client can render electric arcs.
     */
    @Override
    public @NotNull CompoundTag getUpdateTag(@NotNull HolderLookup.Provider registries) {
        final CompoundTag tag = super.getUpdateTag(registries);

        final ListTag list = new ListTag();
        outgoing.forEach(node -> list.add(node.serialize()));
        tag.put("OutgoingConnections", list);

        tag.putInt("TickCount", this.tickCount);
        tag.putBoolean("IsActive", this.isActive);
        tag.putInt("Distance", this.distance);
        tag.putInt("AnimationTick", this.activationTick);
        tag.putInt("CycleCounter", this.cycleCounter);
        if (ownerUUID != null) {
            tag.putUUID("OwnerUUID", ownerUUID);
        }

        return tag;
    }

    public void sync() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        final Packet<ClientGamePacketListener> packet = ClientboundBlockEntityDataPacket.create(this);
        final ChunkPos chunkPos = new ChunkPos(worldPosition);
        serverLevel.getChunkSource().chunkMap.getPlayers(chunkPos, false)
                .forEach(serverPlayer -> serverPlayer.connection.send(packet));
    }

    public boolean handleNodeSelection(ConnectionTarget thisNode, ConnectionTarget nodeToConnect, @Nullable UseOnContext ctx, Player player) {
        if (level != null) {
            addOutgoing(nodeToConnect);
            TeslaNetwork.get(level).onConnectionAdded(thisNode, nodeToConnect);
            return true;
        }

        return false;
    }

    public boolean handleNodeRemoval(ConnectionTarget thisNode, ConnectionTarget nodeToConnect, @Nullable UseOnContext ctx, Player player) {
        if (level != null) {
            removeOutgoing(nodeToConnect);
            TeslaNetwork.get(level).onConnectionRemoved(thisNode, nodeToConnect);
            return true;
        }

        return false;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
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

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public boolean isPendingRemoval() {
        return pendingRemoval;
    }

    public void setReceivesGenerator(boolean receivesGenerator) {
        this.receivesGenerator = receivesGenerator;
    }

    public boolean isReceivesGenerator() {
        return this.receivesGenerator;
    }

    public void setAnimationStartTick(int animationStartTick) {
        this.activationTick = animationStartTick;
    }

    public int getAnimationStartTick() {
        return activationTick;
    }

    public void setOwnerUUID(UUID uuid) {
        this.ownerUUID = uuid;
    }

    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    public boolean hasConcurrentPower() {
        return this.isActive;
    }

    public void startCycle() {
        this.cycleCounter = 0;
        this.setChanged();

        // Sync data to client
        if (this.level instanceof ServerLevel) {
            this.sync();
        }

    }

    /** Whether this module can form new outgoing connections. */
    public boolean canConnectToOtherModules() {
        return true;
    }

    /** Offset where the electrical charge is emitted (from block center) */
    public abstract @NotNull Vec3 electricalChargeOriginOffset();

    /** Offset where the electrical charge is received (from block center) */
    public abstract @NotNull Vec3 electricalChargeEndOffset();

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        ;;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

}