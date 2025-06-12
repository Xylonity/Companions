package dev.xylonity.companions.common.blockentity;

import dev.xylonity.companions.common.block.AbstractTeslaBlock;
import dev.xylonity.companions.common.tesla.behaviour.lamp.LampPulseBehaviour;
import dev.xylonity.companions.common.util.interfaces.ITeslaNodeBehaviour;
import dev.xylonity.companions.registry.CompanionsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoBlockEntity;

public class PlasmaLampBlockEntity extends AbstractTeslaBlockEntity implements GeoBlockEntity {

    private final ITeslaNodeBehaviour pulseBehaviour;

    public PlasmaLampBlockEntity(BlockPos pos, BlockState state) {
        super(CompanionsBlockEntities.PLASMA_LAMP.get(), pos, state);
        this.pulseBehaviour = new LampPulseBehaviour();
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, T t) {
        if (!(t instanceof PlasmaLampBlockEntity lamp)) return;

        lamp.pulseBehaviour.process(lamp, level, blockPos, blockState);

    }

    @Override
    public @NotNull Vec3 electricalChargeOriginOffset() {
        return new Vec3(0, 0, 0);
    }

    @Override
    public @NotNull Vec3 electricalChargeEndOffset() {
        Direction dir = this.getBlockState().getValue(AbstractTeslaBlock.FACING);
        return switch (dir) {
            case DOWN -> new Vec3(0, 0.2, 0);
            case UP -> new Vec3(0, 0.75, 0);
            case NORTH -> new Vec3(0, 0.5, -0.45);
            case SOUTH -> new Vec3(0, 0.5, 0.45);
            case WEST -> new Vec3(-0.45, 0.5, 0);
            case EAST -> new Vec3(0.45, 0.5, 0);
        };
    }

}
