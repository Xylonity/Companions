package dev.xylonity.companions.common.item;

import dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity;
import dev.xylonity.companions.common.blockentity.VoltaicPillarBlockEntity;
import dev.xylonity.companions.common.blockentity.VoltaicRelayBlockEntity;
import dev.xylonity.companions.common.entity.companion.DinamoEntity;
import dev.xylonity.companions.common.event.CompanionsEntityTracker;
import dev.xylonity.companions.common.tesla.TeslaConnectionManager;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WrenchItem extends TooltipItem {

    @Nullable
    private TeslaConnectionManager.ConnectionNode firstNode = null;
    private final int maxConnDist;

    public WrenchItem(Properties properties) {
        super(properties);
        this.maxConnDist = CompanionsConfig.DINAMO_MAX_CONNECTION_DISTANCE * CompanionsConfig.DINAMO_MAX_CONNECTION_DISTANCE;
    }

    @Override
    protected String tooltipName() {
        return "wrench";
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

            if (currentNode.isBlock()) {
                if (player.level().getBlockEntity(currentNode.blockPos()) instanceof VoltaicPillarBlockEntity be && !be.isTop()) {
                    player.displayClientMessage(Component.translatable("wrench.companions.client_message.connection_non_top_voltaic_pillar").withStyle(ChatFormatting.RED), true);
                    return;
                }
            }

            firstNode = currentNode;
            handleFirstNodeMessage(player, currentNode, context);
        } else {
            if (firstNode.equals(currentNode)) {
                player.displayClientMessage(Component.translatable("wrench.companions.client_message.same_node").withStyle(ChatFormatting.RED), true);
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
                                be.handleNodeRemoval(firstNode, currentNode, context, player);
                            } else {
                                be.handleNodeRemoval(currentNode, firstNode, context, player);
                            }

                            be.setOwnerUUID(player.getUUID());
                        }
                    }

                }

                player.displayClientMessage(Component.translatable("wrench.companions.client_message.connection_deleted").withStyle(ChatFormatting.RED), true);
            } else {
                // caps conn max distance
                Vec3 posFirst = getNodePosition(firstNode);
                Vec3 posCurrent = getNodePosition(currentNode);

                if (posFirst == null) return;
                if (posCurrent == null) return;

                if (posFirst.distanceToSqr(posCurrent) > maxConnDist) {
                    player.displayClientMessage(Component.translatable("wrench.companions.client_message.connection_distance", CompanionsConfig.DINAMO_MAX_CONNECTION_DISTANCE).withStyle(ChatFormatting.RED), true);
                    this.firstNode = null;
                    return;
                }

                boolean msgFlag = false;

                // The context is null if the second node selected is a dinamo (and I assume so), so we just add a generic connection
                if (context == null) {
                    manager.addConnection(firstNode, currentNode, false);
                } else {
                    if (firstNode.isEntity()) {
                        Entity entity = CompanionsEntityTracker.getEntityByUUID(firstNode.entityId());
                        if (entity instanceof DinamoEntity dinamo) {
                            dinamo.handleNodeSelection(firstNode, currentNode);
                            msgFlag = true;
                        }
                    } else {
                        BlockEntity first = context.getLevel().getBlockEntity(firstNode.blockPos());
                        if (first instanceof AbstractTeslaBlockEntity be) {

                            if (currentNode.isBlock()) {
                                BlockEntity curr = context.getLevel().getBlockEntity(currentNode.blockPos());
                                if (curr instanceof VoltaicPillarBlockEntity pillar && !pillar.isTop()) {
                                    player.displayClientMessage(Component.translatable("wrench.companions.client_message.connection_non_top_voltaic_pillar").withStyle(ChatFormatting.RED), true);
                                    this.firstNode = null;
                                    return;
                                }
                                if (be.getDistance() == CompanionsConfig.DINAMO_MAX_CHAIN_CONNECTIONS && !(curr instanceof VoltaicRelayBlockEntity)) {
                                    player.displayClientMessage(Component.translatable("wrench.companions.client_message.max_chain_connections").withStyle(ChatFormatting.RED), true);
                                    this.firstNode = null;
                                    return;
                                }
                            }

                            msgFlag = be.handleNodeSelection(firstNode, currentNode, context, player);
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

                if (msgFlag) {
                    player.displayClientMessage(Component.translatable("wrench.companions.client_message.connection_established").withStyle(ChatFormatting.GREEN), true);
                }

            }

            if (context != null) {
                context.getLevel().playSound(null, context.getClickedPos(), CompanionsSounds.WRENCH_CONNECTION.get(), SoundSource.BLOCKS, 0.35f, 1);
            }

            firstNode = null;
        }

    }

    private void handleFirstNodeMessage(Player player, TeslaConnectionManager.ConnectionNode currentNode, @Nullable UseOnContext context) {
        if (firstNode != null) {
            String name = "";
            if (firstNode.isEntity()) {
                if (CompanionsEntityTracker.getEntityByUUID(firstNode.entityId()) instanceof DinamoEntity dinamo) {
                    name = dinamo.getName().getString();
                }

                player.displayClientMessage(Component.translatable("wrench.companions.client_message.first_node_selection_entity", name).withStyle(ChatFormatting.GREEN), true);
            } else {
                if (player.level().getBlockEntity(currentNode.blockPos()) instanceof AbstractTeslaBlockEntity be) {
                    name = new ItemStack(be.getBlockState().getBlock()).getHoverName().getString();
                }

                player.displayClientMessage(Component.translatable("wrench.companions.client_message.first_node_selection_block", name).withStyle(ChatFormatting.GREEN), true);
            }
        }

    }

    @Nullable
    private Vec3 getNodePosition(TeslaConnectionManager.ConnectionNode node) {
        if (node.isEntity()) {
            Entity e = CompanionsEntityTracker.getEntityByUUID(node.entityId());
            return (e != null) ? e.position() : null;
        } else {
            BlockPos p = node.blockPos();
            return new Vec3(p.getX(), p.getY(), p.getZ());
        }
    }

}