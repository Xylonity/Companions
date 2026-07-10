package dev.xylonity.companions.common.block;

import dev.xylonity.companions.common.blockentity.AbstractShadeAltarBlockEntity;
import dev.xylonity.companions.common.entity.ShadeEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractShadeAltarBlock extends Block implements EntityBlock {

    public AbstractShadeAltarBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onRemove(@NotNull BlockState pState, Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pNewState, boolean pMovedByPiston) {
        if (!pLevel.isClientSide && pLevel.getBlockEntity(pPos) instanceof AbstractShadeAltarBlockEntity altar) {
            if (altar.activeShadeUUID != null) {
                Entity e = ((ServerLevel) pLevel).getEntity(altar.activeShadeUUID);
                if (e instanceof ShadeEntity shade) {
                    shade.discard();
                    altar.activeShadeUUID = null;
                }
            }

        }

        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }
}
