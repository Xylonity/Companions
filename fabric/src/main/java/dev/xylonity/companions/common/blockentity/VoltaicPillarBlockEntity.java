package dev.xylonity.companions.common.blockentity;

import dev.xylonity.companions.common.tesla.TeslaConnectionManager;
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

    public VoltaicPillarBlockEntity(BlockPos pos, BlockState state) {
        super(CompanionsBlockEntities.VOLTAIC_PILLAR, pos, state);
        this.pulseBehaviour = new PillarPulseBehaviour();
        this.isTop = false;
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, T t) {
        if (!(t instanceof VoltaicPillarBlockEntity pillar)) return;

        pillar.pulseBehaviour.process(pillar, level, blockPos, blockState);
        pillar.defaultAttackBehaviour.process(pillar, level, blockPos, blockState);

        pillar.setIsTop(!(level.getBlockEntity(pillar.getBlockPos().above()) instanceof VoltaicPillarBlockEntity));

        if (level.getBlockEntity(pillar.getBlockPos().above()) instanceof VoltaicPillarBlockEntity be) {
            pillar.setOwnerUUID(be.getOwnerUUID());
        }

        pillar.sync();
    }

    public boolean isTop() {
        return this.isTop;
    }

    public void setIsTop(boolean top) {
        this.isTop = top;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.setIsTop(tag.getBoolean("IsTop"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putBoolean("IsTop", this.isTop());
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);
        tag.putBoolean("IsTop", this.isTop());
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
    public boolean handleNodeSelection(TeslaConnectionManager.ConnectionNode thisNode, TeslaConnectionManager.ConnectionNode nodeToConnect, @Nullable UseOnContext ctx, Player player) {
        if (ctx != null) {
            if (ctx.getLevel().getBlockEntity(nodeToConnect.blockPos()) instanceof VoltaicPillarBlockEntity) {
                List<VoltaicPillarBlockEntity> thisList = new ArrayList<>();
                List<VoltaicPillarBlockEntity> otherList = new ArrayList<>();
                pillarsBelow(thisList, thisNode.blockPos());
                pillarsBelow(otherList, nodeToConnect.blockPos());
                if (thisList.size() == otherList.size()) {
                    for (int i = 0; i < thisList.size(); i++) {
                        connectionManager.addConnection(thisList.get(i).asConnectionNode(), otherList.get(i).asConnectionNode());
                    }
                } else {
                    if (player != null) {
                        player.displayClientMessage(Component.translatable("voltaic_pillar.companions.client_message.wrong_amount").withStyle(ChatFormatting.RED), true);
                    }

                    return false;
                }
            }

        }

        return super.handleNodeSelection(thisNode, nodeToConnect, ctx, player);
    }

    @Override
    public boolean handleNodeRemoval(TeslaConnectionManager.ConnectionNode thisNode, TeslaConnectionManager.ConnectionNode nodeToConnect, @Nullable UseOnContext ctx, Player player) {
        if (ctx != null) {
            if (ctx.getLevel().getBlockEntity(nodeToConnect.blockPos()) instanceof VoltaicPillarBlockEntity) {
                List<VoltaicPillarBlockEntity> thisList = new ArrayList<>();
                List<VoltaicPillarBlockEntity> otherList = new ArrayList<>();
                pillarsBelow(thisList, thisNode.blockPos());
                pillarsBelow(otherList, nodeToConnect.blockPos());
                if (thisList.size() == otherList.size()) {
                    for (int i = 0; i < thisList.size(); i++) {
                        connectionManager.removeConnection(thisList.get(i).asConnectionNode(), otherList.get(i).asConnectionNode());
                    }
                }
            }

        }

        return super.handleNodeRemoval(thisNode, nodeToConnect, ctx, player);
    }

    private void pillarsBelow(List<VoltaicPillarBlockEntity> pillars, BlockPos pos) {
        scanPillars(pillars, pos.getX(), pos.getY() - 1, -1, pos.getZ());
    }

    private void scanPillars(List<VoltaicPillarBlockEntity> pillars, int x, int startY, int step, int z) {
        if (this.level == null) return;

        for (int currentY = startY; ; currentY += step) {
            BlockPos currentPos = new BlockPos(x, currentY, z);
            BlockEntity entity = level.getBlockEntity(currentPos);
            if (entity instanceof VoltaicPillarBlockEntity pillarEntity) {
                pillars.add(pillarEntity);
            } else {
                break;
            }

        }
    }

}
