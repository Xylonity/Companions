package dev.xylonity.companions.common.item;

import dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity;
import dev.xylonity.companions.common.event.CompanionsEntityTracker;
import dev.xylonity.companions.common.tesla.TeslaConnectionManager;
import dev.xylonity.companions.common.entity.companion.DinamoEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
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

        if (dinamoEntity.getMainAction() == 0) {
            handleNodeSelection(player, TeslaConnectionManager.ConnectionNode.forEntity(target.getUUID(), player.level().dimension().location()), null);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide()) return InteractionResult.PASS;

        BlockPos pos = context.getClickedPos();
        BlockEntity be = context.getLevel().getBlockEntity(pos);

        if (be instanceof AbstractTeslaBlockEntity) {
            TeslaConnectionManager.ConnectionNode node = TeslaConnectionManager.ConnectionNode.forBlock(pos, context.getLevel().dimension().location());
            handleNodeSelection(context.getPlayer(), node, context);
        }

        return InteractionResult.SUCCESS;
    }

    private void handleNodeSelection(Player player, TeslaConnectionManager.ConnectionNode currentNode, @Nullable UseOnContext context) {
        if (firstNode == null) {
            firstNode = currentNode;
            player.displayClientMessage(Component.translatable("wrench.companions.client_message.first_node_selection").withStyle(ChatFormatting.GREEN), true);
        } else {
            if (firstNode.equals(currentNode)) {
                player.displayClientMessage(Component.translatable("wrench.companions.client_message.invalid_selection").withStyle(ChatFormatting.RED), true);
                firstNode = null;
                return;
            }

            TeslaConnectionManager manager = TeslaConnectionManager.getInstance();
            boolean connectionAtoB = manager.getOutgoing(firstNode).contains(currentNode);
            boolean connectionBtoA = manager.getOutgoing(currentNode).contains(firstNode);
            boolean anyConnection = connectionAtoB || connectionBtoA;

            if (anyConnection) {
                // The context is null if the second node selected is a dinamo (and I assume so), so we just add a generic connection
                if (context == null) {
                    if (connectionAtoB) {
                        manager.removeConnection(firstNode, currentNode);
                    } else {
                        manager.removeConnection(currentNode, firstNode);
                    }
                } else {
                    // For example, dinamo -> tesla module
                    if (firstNode.isEntity()) {
                        manager.removeConnection(firstNode, currentNode);
                    } else {
                        BlockEntity first = context.getLevel().getBlockEntity(firstNode.blockPos());
                        if (first instanceof AbstractTeslaBlockEntity be) {
                            if (connectionAtoB) {
                                be.handleNodeRemoval(firstNode, currentNode, context);
                            } else {
                                be.handleNodeRemoval(currentNode, firstNode, context);
                            }

                            be.setOwnerUUID(player.getUUID());
                        }
                    }

                }

                player.displayClientMessage(Component.translatable("wrench.companions.client_message.connection_deleted").withStyle(ChatFormatting.RED), true);
            } else {
                // The context is null if the second node selected is a dinamo (and I assume so), so we just add a generic connection
                if (context == null) {
                    manager.addConnection(firstNode, currentNode, false);
                } else {
                    if (firstNode.isEntity()) {
                        Entity entity = CompanionsEntityTracker.getEntityByUUID(firstNode.entityId());
                        if (entity instanceof DinamoEntity dinamo) {
                            dinamo.handleNodeSelection(firstNode, currentNode);
                        }
                    } else {
                        BlockEntity first = context.getLevel().getBlockEntity(firstNode.blockPos());
                        if (first instanceof AbstractTeslaBlockEntity be) {
                            be.handleNodeSelection(firstNode, currentNode, context);
                            be.setOwnerUUID(player.getUUID());
                        }
                    }

                    if (currentNode.isBlock()) {
                        BlockEntity currentBe = context.getLevel().getBlockEntity(currentNode.blockPos());
                        if (currentBe instanceof AbstractTeslaBlockEntity currentTeslaBe) {
                            currentTeslaBe.setOwnerUUID(player.getUUID());
                        }
                    }

                }

                player.displayClientMessage(Component.translatable("wrench.companions.client_message.connection_established").withStyle(ChatFormatting.GREEN), true);
            }

            firstNode = null;
        }

    }

}