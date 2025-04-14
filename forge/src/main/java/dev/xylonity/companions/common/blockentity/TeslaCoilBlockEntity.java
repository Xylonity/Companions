package dev.xylonity.companions.common.blockentity;

import dev.xylonity.companions.common.tesla.behaviour.coil.CoilPulseBehaviour;
import dev.xylonity.companions.common.util.interfaces.ITeslaNodeBehaviour;
import dev.xylonity.companions.registry.CompanionsBlockEntities;
import net.minecraft.core.BlockPos;
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
        return new AABB(getBlockPos()).inflate(10.0);
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, T t) {
        if (!(t instanceof TeslaCoilBlockEntity coil)) return;

        coil.pulseBehaviour.process(coil, level, blockPos, blockState);

    }

    @Override
    public @NotNull Vec3 electricalChargeOriginOffset() {
        return new Vec3(0, 0, 0);
    }

    @Override
    public @NotNull Vec3 electricalChargeEndOffset() {
        return new Vec3(0, 0, 0);
    }

}