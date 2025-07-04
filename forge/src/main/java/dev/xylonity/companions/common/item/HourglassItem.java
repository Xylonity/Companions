package dev.xylonity.companions.common.item;

import dev.xylonity.companions.common.block.RespawnTotemBlock;
import dev.xylonity.companions.common.blockentity.RespawnTotemBlockEntity;
import dev.xylonity.companions.common.entity.custom.AntlionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class HourglassItem extends Item {

    public HourglassItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player = ctx.getPlayer();

        if (!(state.getBlock() instanceof RespawnTotemBlock respawnTotemBlock))
            return InteractionResult.PASS;

        if (!(respawnTotemBlock.getMultiblockBlockEntity(level, pos, state) instanceof RespawnTotemBlockEntity totem))
            return InteractionResult.PASS;

        if (!level.isClientSide && player != null) {
            if (totem.getCaptureCooldown() > 0) {
                return InteractionResult.SUCCESS;
            }

            totem.setCapturing(true);
            respawnTotemBlock.updateLitState(level, pos, state, true);
            level.scheduleTick(pos, respawnTotemBlock, 60);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

}