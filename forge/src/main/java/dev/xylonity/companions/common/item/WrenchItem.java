package dev.xylonity.companions.common.item;

import dev.xylonity.companions.common.blockentity.TeslaReceiverBlockEntity;
import dev.xylonity.companions.common.entity.ai.illagergolem.TeslaConnectionManager;
import dev.xylonity.companions.common.entity.custom.TamedIllagerGolemEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WrenchItem extends Item {

    @Nullable
    private TeslaConnectionManager.ConnectionNode firstNode = null;

    public WrenchItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, @NotNull Player player, @NotNull LivingEntity target, @NotNull InteractionHand hand) {
        if (!(target instanceof TamedIllagerGolemEntity) || player.level().isClientSide()) {
            return InteractionResult.PASS;
        }

        handleNodeSelection(player, TeslaConnectionManager.ConnectionNode.forEntity(target.getUUID(), player.level().dimension().location()));
        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockPos pos = context.getClickedPos();
        BlockEntity blockEntity = context.getLevel().getBlockEntity(pos);

        if (blockEntity instanceof TeslaReceiverBlockEntity) {
            TeslaConnectionManager.ConnectionNode node = TeslaConnectionManager.ConnectionNode.forBlock(pos, context.getLevel().dimension().location());
            handleNodeSelection(context.getPlayer(), node);
        }

        return InteractionResult.SUCCESS;
    }

    private void handleNodeSelection(Player player, TeslaConnectionManager.ConnectionNode currentNode) {
        if (firstNode == null) {
            firstNode = currentNode;
            player.displayClientMessage(
                    Component.literal("first node selected!").withStyle(ChatFormatting.GREEN),
                    true
            );
        } else {
            if (firstNode.equals(currentNode)) {
                player.displayClientMessage(
                        Component.literal("cannot select a node to itself!").withStyle(ChatFormatting.RED),
                        true
                );
                firstNode = null;
                return;
            }

            TeslaConnectionManager manager = TeslaConnectionManager.getInstance();
            boolean connectionAtoB = manager.getOutgoing(firstNode).contains(currentNode);
            boolean connectionBtoA = manager.getOutgoing(currentNode).contains(firstNode);
            boolean anyConnection = connectionAtoB || connectionBtoA;

            if (anyConnection) {
                if (connectionAtoB) {
                    manager.removeConnection(firstNode, currentNode);
                }
                if (connectionBtoA) {
                    manager.removeConnection(currentNode, firstNode);
                }
                player.displayClientMessage(
                        Component.literal("deleted!").withStyle(ChatFormatting.RED),
                        true
                );
            } else {
                manager.addConnection(firstNode, currentNode);
                player.displayClientMessage(
                        Component.literal("added!").withStyle(ChatFormatting.GREEN),
                        true
                );
            }

            firstNode = null;
        }
    }

}