package dev.xylonity.companions.common.blockentity;

import dev.xylonity.companions.common.tesla.behaviour.lamp.LampPulseBehaviour;
import dev.xylonity.companions.common.util.interfaces.ITeslaNodeBehaviour;
import dev.xylonity.companions.registry.CompanionsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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

}
