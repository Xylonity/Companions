package dev.xylonity.companions.common.item;

import dev.xylonity.companions.common.block.RespawnTotemBlock;
import dev.xylonity.companions.common.blockentity.RespawnTotemBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class HourglassItem extends Item {

    public HourglassItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {

        BlockPos pos = context.getClickedPos();
        BlockEntity be = context.getLevel().getBlockEntity(pos);
        BlockState state = context.getLevel().getBlockState(pos);

        if (be instanceof RespawnTotemBlockEntity totem) {
            if (!state.getValue(RespawnTotemBlock.LIT)) {
                context.getLevel().setBlockAndUpdate(pos, state.setValue(RespawnTotemBlock.LIT, true));
            }
        }

        return InteractionResult.SUCCESS;
    }

}