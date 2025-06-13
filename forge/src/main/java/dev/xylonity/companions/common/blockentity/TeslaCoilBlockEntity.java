package dev.xylonity.companions.common.blockentity;

import dev.xylonity.companions.common.block.AbstractTeslaBlock;
import dev.xylonity.companions.common.tesla.behaviour.coil.CoilPulseBehaviour;
import dev.xylonity.companions.common.util.interfaces.ITeslaNodeBehaviour;
import dev.xylonity.companions.registry.CompanionsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class TeslaCoilBlockEntity extends AbstractTeslaBlockEntity {

    private final ITeslaNodeBehaviour pulseBehaviour;

    public TeslaCoilBlockEntity(BlockPos pos, BlockState state) {
        super(CompanionsBlockEntities.TESLA_COIL.get(), pos, state);
        this.pulseBehaviour = new CoilPulseBehaviour();
    }

    @Override
    public AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, T t) {
        if (level.isClientSide) return;
        if (!(t instanceof TeslaCoilBlockEntity coil)) return;

        coil.pulseBehaviour.process(coil, level, blockPos, blockState);
        coil.defaultAttackBehaviour.process(coil, level, blockPos, blockState);

        // We handle the animation counters in the server and send the data to the client. This is done because
        // if the dinamo's simulation distance is exceeded, the dinamo won't send client sided pulses to the outgoing
        // nodes, causing them to remain inactive on the client (even tho they are actually connected on the server). So
        // I just do the pulse things up there and sync the data to the client. The dinamo's chunk remains loaded
        // while it is sitting, as otherwise there is no pulsing behaviour when the entity is unloaded due to the chunk
        // not being loaded per se
        coil.sync();
    }

    @Override
    public @NotNull Vec3 electricalChargeOriginOffset() {
        Direction dir = this.getBlockState().getValue(AbstractTeslaBlock.FACING);
        return switch (dir) {
            case DOWN -> new Vec3(0, 0.1, 0);
            case UP -> new Vec3(0, 0.9, 0);
            case NORTH -> new Vec3(0, 0.5, -0.5);
            case SOUTH -> new Vec3(0, 0.5, 0.5);
            case WEST -> new Vec3(-0.5, 0.5, 0);
            case EAST -> new Vec3(0.5, 0.5, 0);
        };
    }

    @Override
    public @NotNull Vec3 electricalChargeEndOffset() {
        Direction dir = this.getBlockState().getValue(AbstractTeslaBlock.FACING);
        return switch (dir) {
            case DOWN -> new Vec3(0, 0.1, 0);
            case UP -> new Vec3(0, 0.9, 0);
            case NORTH -> new Vec3(0, 0.5, -0.5);
            case SOUTH -> new Vec3(0, 0.5, 0.5);
            case WEST -> new Vec3(-0.5, 0.5, 0);
            case EAST -> new Vec3(0.5, 0.5, 0);
        };
    }

}