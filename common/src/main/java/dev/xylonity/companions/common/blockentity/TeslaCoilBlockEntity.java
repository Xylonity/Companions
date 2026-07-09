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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class TeslaCoilBlockEntity extends AbstractTeslaBlockEntity {

    private final ITeslaNodeBehaviour pulseBehaviour;

    public TeslaCoilBlockEntity(BlockPos pos, BlockState state) {
        super(CompanionsBlockEntities.TESLA_COIL.get(), pos, state);
        this.pulseBehaviour = new CoilPulseBehaviour();
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, T t) {
        if (level.isClientSide) {
            return;
        }
        if (!(t instanceof TeslaCoilBlockEntity coil)) {
            return;
        }

        coil.pulseBehaviour.process(coil, level, blockPos, blockState);
        coil.defaultAttackBehaviour.process(coil, level, blockPos, blockState);

        coil.sync();
    }

    @Override
    public @NotNull Vec3 electricalChargeOriginOffset() {
        final Direction direction = this.getBlockState().getValue(AbstractTeslaBlock.FACING);
        return switch (direction) {
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
        return electricalChargeOriginOffset();
    }

}