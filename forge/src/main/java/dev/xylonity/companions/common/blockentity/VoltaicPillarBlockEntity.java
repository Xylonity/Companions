package dev.xylonity.companions.common.blockentity;

import dev.xylonity.companions.common.tesla.TeslaConnectionManager;
import dev.xylonity.companions.common.tesla.behaviour.pillar.PillarPulseBehaviour;
import dev.xylonity.companions.common.util.interfaces.ITeslaNodeBehaviour;
import dev.xylonity.companions.registry.CompanionsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoBlockEntity;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class VoltaicPillarBlockEntity extends AbstractTeslaBlockEntity implements GeoBlockEntity {

    private final ITeslaNodeBehaviour pulseBehaviour;

    public VoltaicPillarBlockEntity(BlockPos pos, BlockState state) {
        super(CompanionsBlockEntities.VOLTAIC_PILLAR.get(), pos, state);
        this.pulseBehaviour = new PillarPulseBehaviour();
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, T t) {
        if (!(t instanceof VoltaicPillarBlockEntity pillar)) return;

        pillar.pulseBehaviour.process(pillar, level, blockPos, blockState);
        pillar.defaultAttackBehaviour.process(pillar, level, blockPos, blockState);

    }

    @Override
    public @NotNull Vec3 electricalChargeOriginOffset() {
        return new Vec3(0, 0.5, 0);
    }

    @Override
    public @NotNull Vec3 electricalChargeEndOffset() {
        return new Vec3(0, 0.5, 0);
    }

    //@Override
    //public void handleNodeSelection(TeslaConnectionManager.ConnectionNode thisNode, TeslaConnectionManager.ConnectionNode nodeToConnect, UseOnContext ctx) {
//
    //    // We defer the call to the coil node removal method if a pillar -> coil interaction is being made
    //    if (nodeToConnect.isBlock()
    //            && ctx.getLevel().getBlockEntity(nodeToConnect.blockPos()) instanceof TeslaCoilBlockEntity dest) {
//
    //        List<VoltaicPillarBlockEntity> pillars = new LinkedList<>();
//
    //        pillarsBelowAndAbove(pillars, thisNode.blockPos());
    //        pillars.sort(Comparator.comparing(e -> e.getBlockPos().getY()));
//
    //        AbstractTeslaBlockEntity be = pillars.stream().reduce((f, s) -> s).orElse(null);
//
    //        if (be != null) {
    //            if (ctx.getLevel().getBlockEntity(be.getBlockPos().above()) instanceof TeslaCoilBlockEntity coil) {
    //                dest.handleNodeRemoval(nodeToConnect, coil.asConnectionNode(), ctx);
    //            }
    //        }
//
    //        return;
    //    }
//
    //    // Connects every single pillar and the coil on top to the nodeToConnect's column if a correct connection between
    //    // voltaic pillars if made
    //    if (nodeToConnect.isBlock()
    //            && ctx.getLevel().getBlockEntity(nodeToConnect.blockPos()) instanceof VoltaicPillarBlockEntity) {
//
    //        List<VoltaicPillarBlockEntity> pillars = new LinkedList<>();
    //        List<VoltaicPillarBlockEntity> pillarsDestiny = new LinkedList<>();
//
    //        // We filter and sort both column1 (from thisNode) and column2 (from nodeToConnect)
    //        pillarsBelowAndAbove(pillars, thisNode.blockPos());
    //        pillarsBelowAndAbove(pillarsDestiny, nodeToConnect.blockPos());
    //        pillars.sort(Comparator.comparing(e -> e.getBlockPos().getY()));
    //        pillarsDestiny.sort(Comparator.comparing(e -> e.getBlockPos().getY()));
//
    //        AbstractTeslaBlockEntity be = pillars.stream().reduce((f, s) -> s).orElse(null);
    //        AbstractTeslaBlockEntity beDestiny = pillarsDestiny.stream().reduce((f, s) -> s).orElse(null);
//
    //        if (be != null && beDestiny != null) {
//
    //            // We check if both columns have a coil on top
    //            if (ctx.getLevel().getBlockEntity(be.getBlockPos().above()) instanceof TeslaCoilBlockEntity coil1
    //                    && ctx.getLevel().getBlockEntity(beDestiny.getBlockPos().above()) instanceof TeslaCoilBlockEntity coil2) {
//
    //                if (pillars.size() == pillarsDestiny.size()) {
//
    //                    // Let's add the connection between coils
    //                    connectionManager.addConnection(coil1.asConnectionNode(), coil2.asConnectionNode());
//
    //                    // And here we connect every pillar in order, that's why we sorted the lists before tho
    //                    for (int i = 0; i < pillars.size(); i++) {
    //                        connectionManager.addConnection(pillars.get(i).asConnectionNode(), pillarsDestiny.get(i).asConnectionNode(), false);
    //                    }
//
    //                } else {
    //                    System.out.println("diff pillar sizes");
    //                }
//
    //            } else {
    //                System.out.println("one of both pillars doesn't have the coil on top");
    //            }
//
    //        }
//
    //    }
//
    //}
//
    //private void pillarsBelowAndAbove(List<VoltaicPillarBlockEntity> pillars, BlockPos pos) {
    //    scanPillars(pillars, pos.getX(), pos.getY() - 1, -1, pos.getZ());
    //    scanPillars(pillars, pos.getX(), pos.getY(), 1, pos.getZ());
    //}
//
    //private void scanPillars(List<VoltaicPillarBlockEntity> pillars, int x, int startY, int step, int z) {
    //    if (this.level == null) return;
//
    //    for (int currentY = startY; ; currentY += step) {
    //        BlockPos currentPos = new BlockPos(x, currentY, z);
    //        BlockEntity entity = level.getBlockEntity(currentPos);
    //        if (entity instanceof VoltaicPillarBlockEntity pillarEntity) {
    //            pillars.add(pillarEntity);
    //        } else {
    //            break;
    //        }
//
    //    }
//
    //}

}
