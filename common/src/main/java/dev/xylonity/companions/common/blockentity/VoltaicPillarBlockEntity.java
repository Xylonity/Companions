package dev.xylonity.companions.common.blockentity;

import dev.xylonity.companions.common.tesla.ConnectionTarget;
import dev.xylonity.companions.common.tesla.TeslaNetwork;
import dev.xylonity.companions.common.tesla.behaviour.pillar.PillarPulseBehaviour;
import dev.xylonity.companions.common.util.interfaces.ITeslaNodeBehaviour;
import dev.xylonity.companions.registry.CompanionsBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class VoltaicPillarBlockEntity extends AbstractTeslaBlockEntity {

    private final ITeslaNodeBehaviour pulseBehaviour;
    private boolean isTop;
    private boolean hasBlockOnTop;

    public VoltaicPillarBlockEntity(BlockPos pos, BlockState state) {
        super(CompanionsBlockEntities.VOLTAIC_PILLAR.get(), pos, state);
        this.pulseBehaviour = new PillarPulseBehaviour();
        this.isTop = false;
        this.hasBlockOnTop = false;
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, T t) {
        if (!(t instanceof VoltaicPillarBlockEntity pillar)) {
            return;
        }

        pillar.pulseBehaviour.process(pillar, level, blockPos, blockState);
        pillar.defaultAttackBehaviour.process(pillar, level, blockPos, blockState);

        pillar.setIsTop(!(level.getBlockEntity(pillar.getBlockPos().above()) instanceof VoltaicPillarBlockEntity));
        pillar.setHasBlockOnTop(!level.getBlockState(pillar.getBlockPos().above()).isAir());

        if (level.getBlockEntity(pillar.getBlockPos().above()) instanceof VoltaicPillarBlockEntity blockEntity) {
            pillar.setOwnerUUID(blockEntity.getOwnerUUID());
        }

        pillar.sync();
    }

    public boolean isTop() {
        return this.isTop;
    }

    public void setIsTop(boolean top) {
        this.isTop = top;
    }

    public boolean hasBlockOnTop() {
        return hasBlockOnTop;
    }

    public void setHasBlockOnTop(boolean hasBlockOnTop) {
        this.hasBlockOnTop = hasBlockOnTop;
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.isTop = tag.getBoolean("IsTop");
        this.hasBlockOnTop = tag.getBoolean("BlockOnTop");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("IsTop", isTop);
        tag.putBoolean("BlockOnTop", hasBlockOnTop);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(@NotNull HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putBoolean("IsTop", isTop);
        tag.putBoolean("BlockOnTop", hasBlockOnTop);
        return tag;
    }

    @Override
    public @NotNull Vec3 electricalChargeOriginOffset() {
        return new Vec3(0, 0.5, 0);
    }

    @Override
    public @NotNull Vec3 electricalChargeEndOffset() {
        return new Vec3(0, 0.5, 0);
    }

    @Override
    public boolean handleNodeSelection(ConnectionTarget thisNode, ConnectionTarget nodeToConnect, @Nullable UseOnContext context, Player player) {
        if (context != null && nodeToConnect.isBlock()
                && context.getLevel().getBlockEntity(nodeToConnect.blockPos()) instanceof VoltaicPillarBlockEntity
                && level != null) {

            final List<VoltaicPillarBlockEntity> thisList = new ArrayList<>();
            final List<VoltaicPillarBlockEntity> otherList = new ArrayList<>();
            pillarsBelow(thisList, thisNode.blockPos());
            pillarsBelow(otherList, nodeToConnect.blockPos());

            if (thisList.size() != otherList.size()) {
                if (player != null) {
                    player.displayClientMessage(Component.translatable(
                            "voltaic_pillar.companions.client_message.wrong_amount").withStyle(ChatFormatting.RED), true);
                }

                return false;
            }

            final TeslaNetwork network = TeslaNetwork.get(level);
            for (int i = 0; i < thisList.size(); i++) {
                final VoltaicPillarBlockEntity sourceBlockEntity = thisList.get(i);
                final VoltaicPillarBlockEntity destinationBlockEntity = otherList.get(i);
                final ConnectionTarget sourceTarget = sourceBlockEntity.asConnectionTarget();
                final ConnectionTarget destinationTarget = destinationBlockEntity.asConnectionTarget();
                sourceBlockEntity.addOutgoing(destinationTarget);
                network.onConnectionAdded(sourceTarget, destinationTarget);
            }

        }

        return super.handleNodeSelection(thisNode, nodeToConnect, context, player);
    }

    @Override
    public boolean handleNodeRemoval(ConnectionTarget thisNode, ConnectionTarget nodeToConnect, @Nullable UseOnContext context, Player player) {
        if (context != null && nodeToConnect.isBlock()
                && context.getLevel().getBlockEntity(nodeToConnect.blockPos()) instanceof VoltaicPillarBlockEntity
                && level != null) {

            final List<VoltaicPillarBlockEntity> thisList = new ArrayList<>();
            final List<VoltaicPillarBlockEntity> otherList = new ArrayList<>();
            pillarsBelow(thisList, thisNode.blockPos());
            pillarsBelow(otherList, nodeToConnect.blockPos());

            final TeslaNetwork network = TeslaNetwork.get(level);
            if (thisList.size() == otherList.size()) {
                for (int i = 0; i < thisList.size(); i++) {
                    final VoltaicPillarBlockEntity sourceBlockEntity = thisList.get(i);
                    final VoltaicPillarBlockEntity destinationBlockEntity = otherList.get(i);
                    final ConnectionTarget sourceTarget = sourceBlockEntity.asConnectionTarget();
                    final ConnectionTarget destinationTarget = destinationBlockEntity.asConnectionTarget();
                    sourceBlockEntity.removeOutgoing(destinationTarget);
                    network.onConnectionRemoved(sourceTarget, destinationTarget);
                }

            }

        }

        return super.handleNodeRemoval(thisNode, nodeToConnect, context, player);
    }

    private void pillarsBelow(List<VoltaicPillarBlockEntity> pillars, BlockPos pos) {
        if (this.level == null) {
            return;
        }

        for (int y = pos.getY() - 1; ; y--) {
            final BlockEntity blockEntity = level.getBlockEntity(new BlockPos(pos.getX(), y, pos.getZ()));
            if (blockEntity instanceof VoltaicPillarBlockEntity pillarBlock) {
                pillars.add(pillarBlock);
            }
            else {
                break;
            }

        }

    }

}