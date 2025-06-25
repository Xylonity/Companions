package dev.xylonity.companions.common.blockentity;

import dev.xylonity.companions.common.block.AbstractTeslaBlock;
import dev.xylonity.companions.common.particle.ElectricArcParticle;
import dev.xylonity.companions.common.tesla.behaviour.lamp.LampPulseBehaviour;
import dev.xylonity.companions.common.util.interfaces.ITeslaNodeBehaviour;
import dev.xylonity.companions.registry.CompanionsBlockEntities;
import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoBlockEntity;

import java.util.Random;

public class PlasmaLampBlockEntity extends AbstractTeslaBlockEntity implements GeoBlockEntity {

    private final ITeslaNodeBehaviour pulseBehaviour;
    private int whenToSpawnParticles;

    public PlasmaLampBlockEntity(BlockPos pos, BlockState state) {
        super(CompanionsBlockEntities.PLASMA_LAMP.get(), pos, state);
        this.pulseBehaviour = new LampPulseBehaviour();
        this.whenToSpawnParticles = new Random().nextInt(100, 360);
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, T t) {
        if (!(t instanceof PlasmaLampBlockEntity lamp)) return;

        lamp.pulseBehaviour.process(lamp, level, blockPos, blockState);

        if (lamp.isActive() && lamp.whenToSpawnParticles == 0) {
            lamp.spawnParticles(level, blockPos);
        }

        if (lamp.whenToSpawnParticles == 0) {
            lamp.whenToSpawnParticles = new Random().nextInt(100, 360);
        }

        lamp.whenToSpawnParticles--;
    }

    private void spawnParticles(Level level, BlockPos blockPos) {
        if (level.isClientSide()) {
            double radius1 = 0.42;
            double y = this.getBlockPos().getY() + 0.5;
            for (int i = 0; i < 360; i += 40) {
                double angleRadians = Math.toRadians(i);
                double x = this.getBlockPos().getCenter().add(electricalChargeOriginOffset()).x + radius1 * Math.cos(angleRadians);
                double z = this.getBlockPos().getCenter().add(electricalChargeOriginOffset()).z + radius1 * Math.sin(angleRadians);
                level.addParticle(CompanionsParticles.DINAMO_SPARK.get(), x, y + (level.random.nextDouble() * 0.7), z, 0d, 0.35d, 0d);
            }

            // electrical arc
            double dd = (level.random.nextDouble() * 2 - 1) * 2.5;

            Vec3 start = new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5).add(this.electricalChargeOriginOffset());
            Vec3 end = new Vec3(blockPos.getX() + Mth.floor(dd) + 0.5, blockPos.getY(), blockPos.getZ() + Mth.floor(dd) + 0.5);

            Minecraft.getInstance().particleEngine.add(new ElectricArcParticle((ClientLevel) level, start, end, Math.hypot(start.x - end.x, start.z - end.z) * 0.6, 0.35));
        }

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
