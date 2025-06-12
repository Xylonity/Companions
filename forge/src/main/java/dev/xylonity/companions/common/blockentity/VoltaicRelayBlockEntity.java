package dev.xylonity.companions.common.blockentity;

import dev.xylonity.companions.common.block.AbstractTeslaBlock;
import dev.xylonity.companions.common.tesla.behaviour.coil.CoilPulseBehaviour;
import dev.xylonity.companions.common.util.interfaces.ITeslaNodeBehaviour;
import dev.xylonity.companions.registry.CompanionsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class VoltaicRelayBlockEntity extends AbstractTeslaBlockEntity {

    private final ITeslaNodeBehaviour pulseBehaviour;

    public VoltaicRelayBlockEntity(BlockPos pos, BlockState state) {
        super(CompanionsBlockEntities.VOLTAIC_RELAY.get(), pos, state);
        this.pulseBehaviour = new CoilPulseBehaviour();
    }

    @Override
    public AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, T t) {
        if (level.isClientSide) return;
        if (!(t instanceof VoltaicRelayBlockEntity relay)) return;

        relay.pulseBehaviour.process(relay, level, blockPos, blockState);
        relay.defaultAttackBehaviour.process(relay, level, blockPos, blockState);

        relay.sync();
    }

    @Override
    public @NotNull Vec3 electricalChargeOriginOffset() {
        Direction dir = this.getBlockState().getValue(AbstractTeslaBlock.FACING);
        return switch (dir) {
            case DOWN -> new Vec3(0, 0, 0);
            case UP -> new Vec3(0, 1, 0);
            case NORTH -> new Vec3(0, 0.5, -0.65);
            case SOUTH -> new Vec3(0, 0.5, 0.65);
            case WEST -> new Vec3(-0.65, 0.5, 0);
            case EAST -> new Vec3(0.65, 0.5, 0);
        };
    }

    @Override
    public @NotNull Vec3 electricalChargeEndOffset() {
        Direction dir = this.getBlockState().getValue(AbstractTeslaBlock.FACING);
        return switch (dir) {
            case DOWN -> new Vec3(0, 0, 0);
            case UP -> new Vec3(0, 1, 0);
            case NORTH -> new Vec3(0, 0.5, -0.65);
            case SOUTH -> new Vec3(0, 0.5, 0.65);
            case WEST -> new Vec3(-0.65, 0.5, 0);
            case EAST -> new Vec3(0.65, 0.5, 0);
        };
    }

//    @Override
//    public void handleNodeSelection(TeslaConnectionManager.ConnectionNode thisNode, TeslaConnectionManager.ConnectionNode nodeToConnect, UseOnContext ctx) {
//
//        // If both coils are on top of voltaic pillars, we defer to the pillar connection handler
//        if (nodeToConnect.isBlock()
//                && ctx.getLevel().getBlockEntity(thisNode.blockPos().below()) instanceof VoltaicPillarBlockEntity pillar1
//                && ctx.getLevel().getBlockEntity(nodeToConnect.blockPos().below()) instanceof VoltaicPillarBlockEntity pillar2) {
//
//            pillar1.handleNodeSelection(pillar1.asConnectionNode(), pillar2.asConnectionNode(), ctx);
//
//            return;
//        }
//
//        // If the block above of a bunch of pillars is an instance of a TeslaCoil, we defer the call to this same
//        // method but with that coil as the nodeToConnect, not the actual voltaic pillar, as we shouldn't be
//        // able to connect anything directly to voltaic pillars
//        if (nodeToConnect.isBlock()
//                && ctx.getLevel().getBlockEntity(nodeToConnect.blockPos()) instanceof VoltaicPillarBlockEntity) {
//
//            List<VoltaicPillarBlockEntity> pillars = new LinkedList<>();
//
//            pillarsBelowAndAbove(pillars, nodeToConnect.blockPos());
//            pillars.sort(Comparator.comparing(e -> e.getBlockPos().getY()));
//
//            AbstractTeslaBlockEntity be = pillars.stream().reduce((f, s) -> s).orElse(null);
//
//            if (be != null) {
//                if (ctx.getLevel().getBlockEntity(be.getBlockPos().above()) instanceof TeslaCoilBlockEntity coil) {
//                    this.handleNodeSelection(thisNode, coil.asConnectionNode(), ctx);
//                }
//            }
//
//            return;
//        }
//
//        // Connects the destination coil node to each pillar below it if any is present
//        if (nodeToConnect.isBlock()
//                && ctx.getLevel().getBlockEntity(nodeToConnect.blockPos().below()) instanceof VoltaicPillarBlockEntity
//                && ctx.getLevel().getBlockEntity(nodeToConnect.blockPos()) instanceof TeslaCoilBlockEntity) {
//
//            // It is needed to call the removal method if the connection between the both coils already exists, just
//            // in case the selection of coil -> pillar is done (because we don't connect coils to pillars directly)
//            boolean connectionAtoB = connectionManager.getOutgoing(thisNode).contains(nodeToConnect);
//            boolean connectionBtoA = connectionManager.getOutgoing(nodeToConnect).contains(thisNode);
//            boolean anyConnection = connectionAtoB || connectionBtoA;
//
//            if (anyConnection) {
//                this.handleNodeRemoval(thisNode, nodeToConnect, ctx);
//                return;
//            }
//
//            connectionManager.addConnection(thisNode, nodeToConnect);
//
//            List<VoltaicPillarBlockEntity> pillars = new LinkedList<>();
//
//            pillarsBelow(pillars, nodeToConnect.blockPos());
//            pillars.sort(Comparator.comparing(e -> e.getBlockPos().getY()));
//
//            // Connects the coil on top to every single pillar below
//            for (VoltaicPillarBlockEntity pillar : pillars) {
//                connectionManager.addConnection(nodeToConnect, pillar.asConnectionNode());
//            }
//
//            return;
//        }
//
//        super.handleNodeSelection(thisNode, nodeToConnect, ctx);
//    }
//
//    @Override
//    public void handleNodeRemoval(TeslaConnectionManager.ConnectionNode thisNode, TeslaConnectionManager.ConnectionNode nodeToConnect, UseOnContext ctx) {
//
//        // If the block above of a bunch of pillars is an instance of a TeslaCoil, we defer the call to this same
//        // method but with that coil as the nodeToConnect, not the actual voltaic pillar, because the pillars
//        // aren't really connected to a single coil as is
//        if (nodeToConnect.isBlock()
//                && ctx.getLevel().getBlockEntity(nodeToConnect.blockPos()) instanceof VoltaicPillarBlockEntity) {
//
//            List<VoltaicPillarBlockEntity> pillars = new LinkedList<>();
//
//            pillarsBelowAndAbove(pillars, nodeToConnect.blockPos());
//            pillars.sort(Comparator.comparing(e -> e.getBlockPos().getY()));
//
//            AbstractTeslaBlockEntity be = pillars.stream().reduce((f, s) -> s).orElse(null);
//
//            if (be != null) {
//                if (ctx.getLevel().getBlockEntity(be.getBlockPos().above()) instanceof TeslaCoilBlockEntity coil) {
//                    super.handleNodeRemoval(thisNode, coil.asConnectionNode(), ctx);
//                }
//            }
//
//            return;
//        }
//
//        super.handleNodeRemoval(thisNode, nodeToConnect, ctx);
//    }
//
//    private void pillarsBelow(List<VoltaicPillarBlockEntity> pillars, BlockPos pos) {
//        scanPillars(pillars, pos.getX(), pos.getY() - 1, -1, pos.getZ());
//    }
//
//    private void pillarsBelowAndAbove(List<VoltaicPillarBlockEntity> pillars, BlockPos pos) {
//        scanPillars(pillars, pos.getX(), pos.getY() - 1, -1, pos.getZ());
//        scanPillars(pillars, pos.getX(), pos.getY(), 1, pos.getZ());
//    }
//
//    private void scanPillars(List<VoltaicPillarBlockEntity> pillars, int x, int startY, int step, int z) {
//        if (this.level == null) return;
//
//        for (int currentY = startY; ; currentY += step) {
//            BlockPos currentPos = new BlockPos(x, currentY, z);
//            BlockEntity entity = level.getBlockEntity(currentPos);
//            if (entity instanceof VoltaicPillarBlockEntity pillarEntity) {
//                pillars.add(pillarEntity);
//            } else {
//                break;
//            }
//
//        }
//
//    }

}