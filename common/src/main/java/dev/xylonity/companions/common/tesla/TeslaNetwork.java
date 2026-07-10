package dev.xylonity.companions.common.tesla;

import dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity;
import dev.xylonity.companions.common.blockentity.RecallPlatformBlockEntity;
import dev.xylonity.companions.common.blockentity.VoltaicPillarBlockEntity;
import dev.xylonity.companions.common.blockentity.VoltaicRelayBlockEntity;
import dev.xylonity.companions.config.CompanionsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tesla network manager (server only). Contains:
 *  - An incoming-connection index (outgoing nodes are stored on each node directly).
 *  - A block-entity registry for quick look-ups and distance computation.
 *  - Recall-platform caching.
 *
 * Creates logical connections in a simulated graph, combining features of bipartite graphs and ADGs as a whole.
 * The algorithm assumes that components of type 'AbstractTeslaBlockEntity' have a default weight (distance)
 * assigned sequentially (i.e., 1 -> 2 -> · · · -> x) the further the weighted node is from a generator node
 * (where distance recalculation begins). For computing these distances when the graph is modified, a modified
 * impl of the BFS algorithm is used, which processes child nodes in clusters and reassigns new weights.
 *
 * One instance per dimension, created lazily and cleared on server stop.
 *
 * @author Xylonity
 */
public class TeslaNetwork {

    private static final Map<ResourceKey<Level>, TeslaNetwork> NETWORKS = new ConcurrentHashMap<>();

    private final Map<ConnectionTarget, Set<ConnectionTarget>> incoming = new ConcurrentHashMap<>();
    private final Map<BlockPos, AbstractTeslaBlockEntity> blockEntities = new ConcurrentHashMap<>();

    public static TeslaNetwork get(final Level level) {
        return NETWORKS.computeIfAbsent(level.dimension(), k -> new TeslaNetwork());
    }

    public static void clearAll() {
        NETWORKS.clear();
    }

    public void registerBlockEntity(AbstractTeslaBlockEntity blockEntity) {
        blockEntities.put(blockEntity.getBlockPos(), blockEntity);

        // Indexing existing outgoing connections into the incoming map
        final ConnectionTarget self = blockEntity.asConnectionTarget();
        for (ConnectionTarget target : blockEntity.getOutgoing()) {
            incoming.computeIfAbsent(target, connectionTarget -> ConcurrentHashMap.newKeySet()).add(self);
        }

        recalculateDistances();
        refreshRecallCaches(self);
    }

    public void unregisterBlockEntity(AbstractTeslaBlockEntity blockEntity) {
        final ConnectionTarget self = blockEntity.asConnectionTarget();
        blockEntities.remove(blockEntity.getBlockPos());

        // Removing this node from the incoming index entirely
        incoming.remove(self);
        for (Set<ConnectionTarget> set : incoming.values()) {
            set.remove(self);
        }

        recalculateDistances();
        refreshRecallCaches(self);
    }

    @Nullable
    public AbstractTeslaBlockEntity getBlockEntity(BlockPos pos) {
        return blockEntities.get(pos);
    }

    /**
     * Called after the source node has added the target to its own outgoing set
     * This updates the incoming index and recomputes distances
     */
    public void onConnectionAdded(ConnectionTarget source, ConnectionTarget target) {
        incoming.computeIfAbsent(target, connectionTarget -> ConcurrentHashMap.newKeySet()).add(source);
        recalculateDistances();
        refreshRecallCaches(source, target);
    }

    /**
     * Called after the source node has removed the target from its own outgoing set
     */
    public void onConnectionRemoved(ConnectionTarget source, ConnectionTarget target) {
        final Set<ConnectionTarget> inSet = incoming.get(target);
        if (inSet != null) {
            inSet.remove(source);
        }

        recalculateDistances();
        refreshRecallCaches(source, target);
    }

    /**
     * Bulk index incoming for an entity node (called when a Dinamo registers its saved connections)
     */
    public void indexEntityOutgoing(ConnectionTarget entityNode, Set<ConnectionTarget> outgoing) {
        for (final ConnectionTarget target : outgoing) {
            incoming.computeIfAbsent(target, connectionTarget -> ConcurrentHashMap.newKeySet()).add(entityNode);
        }

        recalculateDistances();
        refreshRecallCaches(entityNode);
    }

    /**
     * Removes all incoming references for a given entity node
     */
    public void removeEntityNode(ConnectionTarget entityNode) {
        incoming.remove(entityNode);
        for (final Set<ConnectionTarget> set : incoming.values()) {
            set.remove(entityNode);
        }

        recalculateDistances();
    }

    public Set<ConnectionTarget> getIncoming(ConnectionTarget node) {
        return incoming.getOrDefault(node, Collections.emptySet());
    }

    /**
     * Finds every node reachable (in either direction) from {@code start}
     */
    public Set<ConnectionTarget> getConnectedComponent(ConnectionTarget start) {
        final Set<ConnectionTarget> component = ConcurrentHashMap.newKeySet();
        final Deque<ConnectionTarget> queue = new ArrayDeque<>();
        component.add(start);
        queue.add(start);

        while (!queue.isEmpty()) {
            final ConnectionTarget currentTarget = queue.poll();

            // Outgoing neighbours
            final Set<ConnectionTarget> outgoing = getOutgoingOf(currentTarget);
            for (final ConnectionTarget node : outgoing) {
                if (component.add(node)) {
                    queue.add(node);
                }

            }

            // Incoming neighbours
            for (final ConnectionTarget node : incoming.getOrDefault(currentTarget, Collections.emptySet())) {
                if (component.add(node)) {
                    queue.add(node);
                }

            }

        }

        return component;
    }

    /**
     * Returns the outgoing set for a node by reading it from the actual blockentity
     * For entity nodes, returns empty (they manage their own outgoing nodes)
     */
    private Set<ConnectionTarget> getOutgoingOf(ConnectionTarget node) {
        if (node.isBlock()) {
            final AbstractTeslaBlockEntity blockEntity = blockEntities.get(node.blockPos());
            if (blockEntity != null) {
                return blockEntity.getOutgoing();
            }

        }

        // Entity outgoing isn't tracked here as I look at the incoming index to infer edges.
        // For component search, outgoing nodes from entities are indexed in the incoming nodes of targets,
        // so the BFS incoming branch will cover it
        return Collections.emptySet();
    }

    public void recalculateDistances() {
        // Resets all distances
        for (final AbstractTeslaBlockEntity blockEntity : blockEntities.values()) {
            blockEntity.setDistance(Integer.MAX_VALUE);
        }

        // Collects all nodes that appear in any edge
        final Set<ConnectionTarget> allNodes = new HashSet<>(incoming.keySet());
        for (final Set<ConnectionTarget> set : incoming.values()) {
            allNodes.addAll(set);
        }

        for (final AbstractTeslaBlockEntity blockEntity : blockEntities.values()) {
            allNodes.add(blockEntity.asConnectionTarget());
            allNodes.addAll(blockEntity.getOutgoing());
        }

        final Set<ConnectionTarget> visited = new HashSet<>();

        for (final ConnectionTarget start : allNodes) {
            if (!visited.add(start)) {
                continue;
            }

            // Discovers connected component
            final Set<ConnectionTarget> component = getConnectedComponent(start);
            visited.addAll(component);

            // Finds generators (dinamos)
            final List<ConnectionTarget> generators = component.stream().filter(ConnectionTarget::isEntity).toList();
            if (generators.isEmpty()) {
                continue;
            }

            // BFS
            final Map<ConnectionTarget, Integer> distances = new HashMap<>();
            final Queue<ConnectionTarget> bfsQueue = new LinkedList<>(generators);
            generators.forEach(connectionTarget -> distances.put(connectionTarget, 0));

            while (!bfsQueue.isEmpty()) {
                final ConnectionTarget currentTarget = bfsQueue.poll();
                final int currentDistance = distances.get(currentTarget);
                final AbstractTeslaBlockEntity currentBlockEntity = currentTarget.isBlock() ? blockEntities.get(currentTarget.blockPos()) : null;

                // Follow outgoing edges
                final Set<ConnectionTarget> outgoing = currentTarget.isBlock() && currentBlockEntity != null
                        ? currentBlockEntity.getOutgoing()
                        : getOutgoingFromIncoming(currentTarget);

                for (final ConnectionTarget connectionTarget : outgoing) {
                    if (!connectionTarget.isBlock()) {
                        continue;
                    }

                    final AbstractTeslaBlockEntity childBlockEntity = blockEntities.get(connectionTarget.blockPos());
                    if (childBlockEntity == null) {
                        continue;
                    }

                    int newDist;
                    final boolean samePillarColumn = currentBlockEntity instanceof VoltaicPillarBlockEntity
                            && childBlockEntity instanceof VoltaicPillarBlockEntity
                            && currentBlockEntity.getBlockPos().getX() == childBlockEntity.getBlockPos().getX()
                            && currentBlockEntity.getBlockPos().getZ() == childBlockEntity.getBlockPos().getZ();

                    // Resets the distance with a voltaic relay
                    if (currentBlockEntity instanceof VoltaicRelayBlockEntity) {
                        newDist = 1;
                    }
                    // Same distance for each pillar in the same column
                    else if (samePillarColumn) {
                        newDist = currentDistance;
                    }
                    // Normal connection
                    else {
                        newDist = currentDistance + 1;
                    }

                    if (newDist > CompanionsConfig.DINAMO_MAX_CHAIN_CONNECTIONS) {
                        continue;
                    }

                    if (!distances.containsKey(connectionTarget) || newDist < distances.get(connectionTarget)) {
                        distances.put(connectionTarget, newDist);
                        bfsQueue.add(connectionTarget);
                    }

                }

            }

            // Applies distances
            for (final Map.Entry<ConnectionTarget, Integer> entry : distances.entrySet()) {
                if (entry.getKey().isBlock()) {
                    final AbstractTeslaBlockEntity blockEntity = blockEntities.get(entry.getKey().blockPos());
                    if (blockEntity != null) {
                        blockEntity.setDistance(entry.getValue());
                    }
                }

            }

        }

    }

    private Set<ConnectionTarget> getOutgoingFromIncoming(ConnectionTarget entityNode) {
        final Set<ConnectionTarget> result = new HashSet<>();
        for (final Map.Entry<ConnectionTarget, Set<ConnectionTarget>> entry : incoming.entrySet()) {
            if (entry.getValue().contains(entityNode)) {
                result.add(entry.getKey());
            }

        }

        return result;
    }

    private void refreshRecallCaches(ConnectionTarget... seeds) {
        final Set<ConnectionTarget> visitedComponents = new HashSet<>();
        for (final ConnectionTarget seed : seeds) {
            if (visitedComponents.contains(seed)) {
                continue;
            }

            final Set<ConnectionTarget> component = getConnectedComponent(seed);
            visitedComponents.addAll(component);

            final List<RecallPlatformBlockEntity> recalls = new ArrayList<>();
            for (final ConnectionTarget connectionTarget : component) {
                if (connectionTarget.isBlock()) {
                    final AbstractTeslaBlockEntity blockEntity = blockEntities.get(connectionTarget.blockPos());
                    if (blockEntity instanceof RecallPlatformBlockEntity recallPlatformBlock) {
                        recalls.add(recallPlatformBlock);
                    }
                }

            }

            if (recalls.size() < 2) {
                for (final RecallPlatformBlockEntity recallPlatformBlock : recalls) {
                    recallPlatformBlock.updatePartners(Collections.emptySet());
                }

                continue;
            }

            final List<BlockPos> allPositions = recalls.stream().map(AbstractTeslaBlockEntity::getBlockPos).toList();
            for (final RecallPlatformBlockEntity recallPlatformBlock : recalls) {
                final Set<BlockPos> partners = new HashSet<>(allPositions);
                partners.remove(recallPlatformBlock.getBlockPos());
                recallPlatformBlock.updatePartners(partners);
            }

        }

    }

}