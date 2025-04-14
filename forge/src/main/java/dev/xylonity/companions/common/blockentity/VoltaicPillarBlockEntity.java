package dev.xylonity.companions.common.blockentity;

import dev.xylonity.companions.common.tesla.behaviour.pillar.PillarPulseBehaviour;
import dev.xylonity.companions.common.util.interfaces.ITeslaNodeBehaviour;
import dev.xylonity.companions.registry.CompanionsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoBlockEntity;

public class VoltaicPillarBlockEntity extends AbstractTeslaBlockEntity implements GeoBlockEntity {

    private final ITeslaNodeBehaviour pulseBehaviour;

    public VoltaicPillarBlockEntity(BlockPos pos, BlockState state) {
        super(CompanionsBlockEntities.VOLTAIC_PILLAR.get(), pos, state);
        this.pulseBehaviour = new PillarPulseBehaviour();
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, T t) {
        if (!(t instanceof VoltaicPillarBlockEntity pillar)) return;

        pillar.pulseBehaviour.process(pillar, level, blockPos, blockState);

    }

    @Override
    public @NotNull Vec3 electricalChargeOriginOffset() {
        return new Vec3(0, 0.5, 0);
    }

    @Override
    public @NotNull Vec3 electricalChargeEndOffset() {
        return new Vec3(0, 0.5, 0);
    }

}
