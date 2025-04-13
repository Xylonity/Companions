package dev.xylonity.companions.common.blockentity;

import dev.xylonity.companions.common.block.PlasmaLampBlock;
import dev.xylonity.companions.common.tesla.TeslaConnectionManager;
import dev.xylonity.companions.common.tesla.behaviour.coil.CoilPulseBehaviour;
import dev.xylonity.companions.common.tesla.behaviour.lamp.LampPulseBehaviour;
import dev.xylonity.companions.common.util.interfaces.ITeslaNodeBehaviour;
import dev.xylonity.companions.common.util.interfaces.ITeslaUtil;
import dev.xylonity.companions.registry.CompanionsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtils;

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
