package dev.xylonity.companions.common.item;

import dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity;
import dev.xylonity.companions.common.tesla.TeslaConnectionManager;
import dev.xylonity.companions.common.entity.custom.DinamoEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
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
        if (!(target instanceof DinamoEntity dinamoEntity) || player.level().isClientSide()) return InteractionResult.PASS;
        if (dinamoEntity.getOwner() != null && !player.equals(dinamoEntity.getOwner())) return InteractionResult.PASS;

        if (dinamoEntity.isSitting()) {
            handleNodeSelection(player, TeslaConnectionManager.ConnectionNode.forEntity(target.getUUID(), player.level().dimension().location()));
        } else {
            handleDinamoAttackToggle(player, dinamoEntity);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide()) return InteractionResult.PASS;

        BlockPos pos = context.getClickedPos();
        BlockEntity blockEntity = context.getLevel().getBlockEntity(pos);

        if (blockEntity instanceof AbstractTeslaBlockEntity) {
            TeslaConnectionManager.ConnectionNode node = TeslaConnectionManager.ConnectionNode.forBlock(pos, context.getLevel().dimension().location());
            handleNodeSelection(context.getPlayer(), node, context);
        }

        return InteractionResult.SUCCESS;
    }

    private void handleNodeSelection(Player player, TeslaConnectionManager.ConnectionNode currentNode, UseOnContext context) {
        if (firstNode == null) {
            firstNode = currentNode;
            player.displayClientMessage(Component.literal("first node selected!").withStyle(ChatFormatting.GREEN), true);
        } else {
            if (firstNode.equals(currentNode)) {
                player.displayClientMessage(Component.literal("cannot select a node to itself!").withStyle(ChatFormatting.RED), true);
                firstNode = null;
                return;
            }

            TeslaConnectionManager manager = TeslaConnectionManager.getInstance();
            boolean connectionAtoB = manager.getOutgoing(firstNode).contains(currentNode);
            boolean connectionBtoA = manager.getOutgoing(currentNode).contains(firstNode);
            boolean anyConnection = connectionAtoB || connectionBtoA;

            if (anyConnection) {
                if (connectionAtoB) {
                    BlockEntity first = context.getLevel().getBlockEntity(firstNode.blockPos());
                    if (first instanceof AbstractTeslaBlockEntity be) {
                        be.handleNodeRemoval(firstNode, currentNode, context);
                    }
                }
                if (connectionBtoA) {
                    BlockEntity first = context.getLevel().getBlockEntity(firstNode.blockPos());
                    if (first instanceof AbstractTeslaBlockEntity be) {
                        be.handleNodeRemoval(currentNode, firstNode, context);
                    }
                }

                player.displayClientMessage(Component.literal("deleted!").withStyle(ChatFormatting.RED), true);
            } else {
                BlockEntity first = context.getLevel().getBlockEntity(firstNode.blockPos());
                if (first instanceof AbstractTeslaBlockEntity be) {
                    be.handleNodeSelection(firstNode, currentNode, context);
                }

                player.displayClientMessage(Component.literal("added!").withStyle(ChatFormatting.GREEN), true);
            }

            firstNode = null;
        }
    }

    private void handleNodeSelection(Player player, TeslaConnectionManager.ConnectionNode currentNode) {
        if (firstNode == null) {
            firstNode = currentNode;
            player.displayClientMessage(Component.literal("first node selected!").withStyle(ChatFormatting.GREEN), true);
        } else {
            if (firstNode.equals(currentNode)) {
                player.displayClientMessage(Component.literal("cannot select a node to itself!").withStyle(ChatFormatting.RED), true);
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
                player.displayClientMessage(Component.literal("deleted!").withStyle(ChatFormatting.RED), true);
            } else {
                manager.addConnection(firstNode, currentNode, false);
                player.displayClientMessage(Component.literal("added!").withStyle(ChatFormatting.GREEN), true);
            }

            firstNode = null;
        }
    }

    private void handleDinamoAttackToggle(Player player, DinamoEntity dinamoEntity) {
        if (!dinamoEntity.shouldAttack()) {
            player.displayClientMessage(Component.literal("activando ataque").withStyle(ChatFormatting.GREEN), true);
        } else {
            player.displayClientMessage(Component.literal("desactivando ataque").withStyle(ChatFormatting.GREEN), true);
        }

        dinamoEntity.setShouldAttack(!dinamoEntity.shouldAttack());
    }

}