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

}