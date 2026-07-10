package dev.xylonity.companions.common.item;

import dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity;
import dev.xylonity.companions.common.blockentity.VoltaicPillarBlockEntity;
import dev.xylonity.companions.common.blockentity.VoltaicRelayBlockEntity;
import dev.xylonity.companions.common.entity.companion.DinamoEntity;
import dev.xylonity.companions.common.event.CompanionsEntityTracker;
import dev.xylonity.companions.common.tesla.ConnectionTarget;
import dev.xylonity.companions.common.tesla.TeslaNetwork;
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

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class WrenchItem extends TooltipItem {

    // I hate statics
    private final Map<UUID, ConnectionTarget> firstNodes = new WeakHashMap<>();

    private final int maxConnectionDistanceSqr;

    public WrenchItem(Properties properties) {
        super(properties);
        this.maxConnectionDistanceSqr = CompanionsConfig.DINAMO_MAX_CONNECTION_DISTANCE * CompanionsConfig.DINAMO_MAX_CONNECTION_DISTANCE;
    }

    @Override
    protected String tooltipName() {
        return "wrench";
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, @NotNull Player player, @NotNull LivingEntity target, @NotNull InteractionHand hand) {
        if (!(target instanceof DinamoEntity dinamo) || player.level().isClientSide()) {
            return InteractionResult.PASS;
        }
        if (dinamo.getOwner() != null && !player.equals(dinamo.getOwner())) {
            return InteractionResult.PASS;
        }

        if (dinamo.getMainAction() == 0) {
            handleNodeSelection(player,
                    ConnectionTarget.forEntity(target.getUUID(), player.level().dimension().location()),
                    null);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide()) {
            return InteractionResult.PASS;
        }

        final BlockPos clickedPos = context.getClickedPos();
        final BlockEntity blockEntity = context.getLevel().getBlockEntity(clickedPos);

        if (blockEntity instanceof AbstractTeslaBlockEntity && context.getPlayer() != null) {
            handleNodeSelection(context.getPlayer(),
                    ConnectionTarget.forBlock(clickedPos, context.getLevel().dimension().location()),
                    context);
        }

        return InteractionResult.SUCCESS;
    }

    private void handleNodeSelection(Player player, ConnectionTarget currentNode, @Nullable UseOnContext context) {
        final UUID playerId = player.getUUID();
        final ConnectionTarget firstNode = firstNodes.get(playerId);

        if (firstNode == null) {
            // First node selection
            if (currentNode.isBlock()) {
                if (player.level().getBlockEntity(currentNode.blockPos()) instanceof VoltaicPillarBlockEntity vp && !vp.isTop()) {
                    player.displayClientMessage(Component.translatable("wrench.companions.client_message.connection_non_top_voltaic_pillar").withStyle(ChatFormatting.RED), true);
                    return;
                }

            }

            firstNodes.put(playerId, currentNode);
            showFirstNodeMessage(player, currentNode);

            return;
        }

        // Second node selection
        if (firstNode.equals(currentNode)) {
            player.displayClientMessage(Component.translatable("wrench.companions.client_message.same_node").withStyle(ChatFormatting.RED), true);
            firstNodes.remove(playerId);
            return;
        }

        final boolean aToBExists = nodeHasOutgoingTo(firstNode, currentNode, player);
        final boolean bToAExists = nodeHasOutgoingTo(currentNode, firstNode, player);
        final boolean anyConnection = aToBExists || bToAExists;

        if (anyConnection) {
            // Removes existing connection
            removeExistingConnection(player, firstNode, currentNode, aToBExists, bToAExists, context);
            player.displayClientMessage(Component.translatable("wrench.companions.client_message.connection_deleted").withStyle(ChatFormatting.RED), true);
        }
        else {
            // Creates a new connection
            if (!validateNewConnection(player, firstNode, currentNode, context)) {
                firstNodes.remove(playerId);
                return;
            }

            final boolean messageFlag = createConnection(player, firstNode, currentNode, context);
            if (messageFlag) {
                player.displayClientMessage(Component.translatable("wrench.companions.client_message.connection_established").withStyle(ChatFormatting.GREEN), true);
            }

        }

        if (context != null) {
            context.getLevel().playSound(null, context.getClickedPos(), CompanionsSounds.WRENCH_CONNECTION.get(), SoundSource.BLOCKS, 0.35f, 1);
        }

        firstNodes.remove(playerId);
    }

    private boolean nodeHasOutgoingTo(ConnectionTarget source, ConnectionTarget target, Player player) {
        if (source.isBlock()) {
            if (player.level().getBlockEntity(source.blockPos()) instanceof AbstractTeslaBlockEntity blockEntity) {
                return blockEntity.getOutgoing().contains(target);
            }

        }
        else if (source.isEntity()) {
            final Entity entity = CompanionsEntityTracker.getEntityByUUID(source.entityId());
            if (entity instanceof DinamoEntity dinamo) {
                return dinamo.getOutgoing().contains(target);
            }

        }

        return false;
    }

    private void removeExistingConnection(Player player, ConnectionTarget first, ConnectionTarget current, boolean aToBExists, boolean bToAExists, @Nullable UseOnContext context) {
        final ConnectionTarget source = aToBExists ? first : current;
        final ConnectionTarget target = aToBExists ? current : first;

        if (source.isEntity()) {
            final Entity entity = CompanionsEntityTracker.getEntityByUUID(source.entityId());
            if (entity instanceof DinamoEntity dinamo) {
                dinamo.removeOutgoingConnection(target);
                TeslaNetwork.get(player.level()).onConnectionRemoved(source, target);
            }
        }
        else if (source.isBlock()) {
            if (player.level().getBlockEntity(source.blockPos()) instanceof AbstractTeslaBlockEntity blockEntity) {
                if (context != null) {
                    blockEntity.handleNodeRemoval(source, target, context, player);
                }
                else {
                    blockEntity.removeOutgoing(target);
                    TeslaNetwork.get(player.level()).onConnectionRemoved(source, target);
                }

                blockEntity.setOwnerUUID(player.getUUID());
                blockEntity.sync();
            }

        }

    }

    private boolean validateNewConnection(Player player, ConnectionTarget first, ConnectionTarget current, @Nullable UseOnContext context) {
        // Distance check
        final Vec3 posFirst = getNodePosition(first);
        final Vec3 posCurrent = getNodePosition(current);
        if (posFirst == null || posCurrent == null) {
            return false;
        }

        if (posFirst.distanceToSqr(posCurrent) > maxConnectionDistanceSqr) {
            player.displayClientMessage(Component.translatable("wrench.companions.client_message.connection_distance",
                    CompanionsConfig.DINAMO_MAX_CONNECTION_DISTANCE).withStyle(ChatFormatting.RED), true);
            return false;
        }

        if (context != null && current.isBlock()) {
            final BlockEntity currentBlockEntity = context.getLevel().getBlockEntity(current.blockPos());
            if (currentBlockEntity instanceof VoltaicPillarBlockEntity pillar && !pillar.isTop()) {
                player.displayClientMessage(Component.translatable("wrench.companions.client_message.connection_non_top_voltaic_pillar").withStyle(ChatFormatting.RED), true);
                return false;
            }

            if (first.isBlock()) {
                final BlockEntity firstBlockEntity = context.getLevel().getBlockEntity(first.blockPos());
                if (firstBlockEntity instanceof VoltaicPillarBlockEntity && !(currentBlockEntity instanceof VoltaicPillarBlockEntity)) {
                    player.displayClientMessage(Component.translatable("wrench.companions.client_message.connection_non_voltaic_pillar").withStyle(ChatFormatting.RED), true);
                    return false;
                }
                if (firstBlockEntity instanceof AbstractTeslaBlockEntity blockEntity && blockEntity.getDistance() == CompanionsConfig.DINAMO_MAX_CHAIN_CONNECTIONS && !(currentBlockEntity instanceof VoltaicRelayBlockEntity)) {
                    player.displayClientMessage(Component.translatable("wrench.companions.client_message.max_chain_connections").withStyle(ChatFormatting.RED), true);
                    return false;
                }

            }

        }

        return true;
    }

    private boolean createConnection(Player player, ConnectionTarget first, ConnectionTarget current, @Nullable UseOnContext context) {
        final TeslaNetwork network = TeslaNetwork.get(player.level());
        boolean messageFlag = false;

        if (first.isEntity()) {
            final Entity entity = CompanionsEntityTracker.getEntityByUUID(first.entityId());
            if (entity instanceof DinamoEntity dinamo) {
                dinamo.addOutgoingConnection(current);
                network.onConnectionAdded(first, current);
                messageFlag = true;
            }

        }
        else if (first.isBlock() && context != null) {
            final BlockEntity blockEntity = context.getLevel().getBlockEntity(first.blockPos());
            if (blockEntity instanceof AbstractTeslaBlockEntity teslaBlockEntity) {
                messageFlag = teslaBlockEntity.handleNodeSelection(first, current, context, player);
                teslaBlockEntity.setOwnerUUID(player.getUUID());
                teslaBlockEntity.sync();
            }

        }
        else {
            // If the context is null but the first one is a block
            if (first.isEntity()) {
                final Entity entity = CompanionsEntityTracker.getEntityByUUID(first.entityId());
                if (entity instanceof DinamoEntity dinamo) {
                    dinamo.addOutgoingConnection(current);
                    network.onConnectionAdded(first, current);
                }

            }

        }

        // Sets the owner on the second node if it's a block
        if (current.isBlock() && context != null) {
            final BlockEntity currentBlockEntity = context.getLevel().getBlockEntity(current.blockPos());
            if (currentBlockEntity instanceof AbstractTeslaBlockEntity blockEntity) {
                blockEntity.setOwnerUUID(player.getUUID());
            }

        }

        return messageFlag;
    }

    private void showFirstNodeMessage(Player player, ConnectionTarget node) {
        String name = "";
        if (node.isEntity()) {
            if (CompanionsEntityTracker.getEntityByUUID(node.entityId()) instanceof DinamoEntity dinamo) {
                name = dinamo.getName().getString();
            }

            player.displayClientMessage(Component.translatable("wrench.companions.client_message.first_node_selection_entity", name).withStyle(ChatFormatting.GREEN), true);
        }
        else if (node.isBlock()) {
            if (player.level().getBlockEntity(node.blockPos()) instanceof AbstractTeslaBlockEntity be) {
                name = new ItemStack(be.getBlockState().getBlock()).getHoverName().getString();
            }

            player.displayClientMessage(Component.translatable("wrench.companions.client_message.first_node_selection_block", name).withStyle(ChatFormatting.GREEN), true);
        }

    }

    @Nullable
    private Vec3 getNodePosition(ConnectionTarget node) {
        if (node.isEntity()) {
            final Entity entity = CompanionsEntityTracker.getEntityByUUID(node.entityId());
            return entity != null ? entity.position() : null;
        }
        else {
            final BlockPos blockPos = node.blockPos();
            return new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        }

    }

}