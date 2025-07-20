package dev.xylonity.companions.common.item;

import dev.xylonity.companions.common.block.RespawnTotemBlock;
import dev.xylonity.companions.common.blockentity.RespawnTotemBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HourglassItem extends TooltipItem {

    public HourglassItem(Properties properties) {
        super(properties);
    }

    @Override
    protected String tooltipName() {
        return "";
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("tooltip.item.companions.hourglass_1").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            tooltipComponents.add(Component.translatable("tooltip.item.companions.hourglass_2").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.item.companions.hourglass_shift"));
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
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
            if ((ctx.getItemInHand().getMaxDamage() - ctx.getItemInHand().getDamageValue()) < 12) {
                player.displayClientMessage(Component.translatable("hourglass.companions.client_message.required_durability"), true);
                return InteractionResult.PASS;
            }

            if (totem.getCaptureCooldown() > 0) {
                return InteractionResult.SUCCESS;
            }

            totem.setCapturing(true);
            respawnTotemBlock.updateLitState(level, pos, state, true);
            level.scheduleTick(pos, respawnTotemBlock, 60);
            ctx.getItemInHand().hurtAndBreak(12, player, LivingEntity.getSlotForHand(ctx.getHand()));
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

}