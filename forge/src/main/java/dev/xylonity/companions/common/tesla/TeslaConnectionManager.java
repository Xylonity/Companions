package dev.xylonity.companions.common.tesla;

import dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity;
import dev.xylonity.companions.common.blockentity.RecallPlatformBlockEntity;
import dev.xylonity.companions.common.blockentity.VoltaicPillarBlockEntity;
import dev.xylonity.companions.common.blockentity.VoltaicRelayBlockEntity;
import dev.xylonity.companions.config.CompanionsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Creates logical connections in a simulated graph, combining features of bipartite graphs and ADGs as a whole.
 * The algorithm assumes that components of type 'AbstractTeslaBlockEntity' have a default weight (distance)
 * assigned sequentially (i.e., 1 -> 2 -> · · · -> x) the further the weighted node is from a generator node
 * (where distance recalculation begins). For computing these distances when the graph is modified, a modified
 * impl of the BFS algorithm is used, which processes child nodes in clusters and reassigns new weights.
 *
 * A graph connection is only removed if the physical node (a block/entity) is deleted. When this happens,
 * the subsequent connections between the outgoing nodes of the affected node are preserved, generating new
 * graphs as a result (outgoing connections from a deleted node should NOT be purged entirely, as this would
 * disrupt the Tesla modules' pulse logic).
 *
 * Each node has two components: outgoing and incoming nodes (both memoized in maps), and a registry
 * of generic nodes (AbstractTeslaBlockEntity), which is populated from each node when the world loads.
 *
 * For efficiency, distances are only recomputed when there is a structural change in the graph (add/rem).
 *
 * @author Xylonity
 */
public class TeslaConnectionManager {
    private static TeslaConnectionManager instance;
    private final Map<ConnectionNode, Set<ConnectionNode>> outgoing = new ConcurrentHashMap<>();
    private final Map<ConnectionNode, Set<ConnectionNode>> incoming = new ConcurrentHashMap<>();
    private final Map<ConnectionNode, AbstractTeslaBlockEntity> blockEntities = new ConcurrentHashMap<>();

    public static TeslaConnectionManager getInstance() {
        if (instance == null) instance = new TeslaConnectionManager();
        return instance;
    }

    /**
     * Populated on world reload by each physical node, caches every node to access them directly.
     * @param blockEntity the module to register
     */
    public void registerBlockEntity(AbstractTeslaBlockEntity blockEntity) {
        ConnectionNode node = blockEntity.asConnectionNode();

        blockEntities.put(node, blockEntity);

        recalculateDistances();
        refreshRecallCachesAround(node);
    }

    /**
     * Unregisters and purges the blockEntity from the graph it belonged to.
     * @param blockEntity the module to unregister
     */
    public void unregisterBlockEntity(AbstractTeslaBlockEntity blockEntity) {
        ConnectionNode node = blockEntity.asConnectionNode();

        blockEntities.remove(node);

        removeConnectionNode(node);

        recalculateDistances();
        refreshRecallCachesAround(node);
    }

    public void addConnection(ConnectionNode source, ConnectionNode target) {
        addConnection(source, target, false);
        refreshRecallCachesAround(source, target);
    }

    /**
     * Adds a generic connection between a pair of nodes, thus creating a 'binary' graph that automatically
     * reconnects to existing outgoing nodes making a bigger graph per se.
     * @param source source node that handles the connection
     * @param target target node that receives the connection
     * @param bypassValidation should not recompute distances
     */
    public void addConnection(ConnectionNode source, ConnectionNode target, boolean bypassValidation) {
        if (!bypassValidation && source.isBlock()) {
            AbstractTeslaBlockEntity be = blockEntities.get(source);
            if (be != null && !be.canConnectToOtherModules()) {
                return;
            }

        }

        outgoing.computeIfAbsent(source, c -> new HashSet<>()).add(target);
        incoming.computeIfAbsent(target, c -> new HashSet<>()).add(source);

        if (!bypassValidation) {
            recalculateDistances();
        }

    }

    public @NotNull AbstractTeslaBlockEntity getBlockEntity(ConnectionNode node) {
        return blockEntities.get(node);
    }

    public Set<ConnectionNode> getOutgoing(ConnectionNode node) {
        return outgoing.getOrDefault(node, Collections.emptySet());
    }

    public Set<ConnectionNode> getIncoming(ConnectionNode node) {
        return incoming.getOrDefault(node, Collections.emptySet());
    }

    /**
     * Searches for existing recall platform nodes (RecallPlatformBlockEntity) inside multiple logical graphs. This is
     * just needed for that exact module because it needs to cache the blockpos of other recall platform nodes in the
     * same graph.
     * @param nodes nodes to view
     */
    private void refreshRecallCachesAround(ConnectionNode... nodes) {
        // Caches processed nodes
        Set<ConnectionNode> visitedComponents = new HashSet<>();

        for (ConnectionNode node : nodes) {
            if (visitedComponents.contains(node)) continue;

            // Get all nodes connected to this seed (node)
            Set<ConnectionNode> component = getConnectedComponent(node);
            visitedComponents.addAll(component);

            // Finds all recall platforms
            List<RecallPlatformBlockEntity> recalls = new ArrayList<>();
            for (ConnectionNode n : component) {
                AbstractTeslaBlockEntity be = blockEntities.get(n);
                if (be instanceof RecallPlatformBlockEntity rp) {
                    recalls.add(rp);
                }

            }

            // If there are not enough platforms to form pairs when pass the calculus
            if (recalls.size() < 2) {
                for (RecallPlatformBlockEntity rp : recalls) {
                    rp.updatePartners(Collections.emptySet());
                }

                continue;
            }

            List<BlockPos> allPos = recalls.stream().map(AbstractTeslaBlockEntity::getBlockPos).toList();
            for (RecallPlatformBlockEntity rp : recalls) {
                Set<BlockPos> partners = new HashSet<>(allPos);
                partners.remove(rp.getBlockPos());
                rp.updatePartners(partners);
            }
        }

    }

    /**
     * Finds all nodes connected to a starting node (BFS traversal)
     * @param start the node where we start
     * @return set of all connected nodes (undirected graph)
     */
    public Set<ConnectionNode> getConnectedComponent(ConnectionNode start) {
        Set<ConnectionNode> comp = new HashSet<>();
        Deque<ConnectionNode> queue = new ArrayDeque<>();
        comp.add(start);
        queue.add(start);

        while (!queue.isEmpty()) {
            ConnectionNode cur = queue.poll();
            comp.add(cur);

            // Checks both incoming/outgoing conns
            Set<ConnectionNode> neighbors = new HashSet<>();
            neighbors.addAll(outgoing.getOrDefault(cur, Collections.emptySet()));
            neighbors.addAll(incoming.getOrDefault(cur, Collections.emptySet()));

            for (ConnectionNode node : neighbors) {
                if (comp.add(node)) {
                    // Add new nodes to traversal
                    queue.add(node);
                }
            }

        }

        return comp;
    }

    /**
     * Removes a directed connection between two nodes
     * @param source origin node
     * @param target dest node
     */
    public void removeConnection(ConnectionNode source, ConnectionNode target) {
        Set<ConnectionNode> outSet = outgoing.get(source);
        if (outSet != null) {
            outSet.remove(target);
        }

        Set<ConnectionNode> inSet = incoming.get(target);
        if (inSet != null) {
            inSet.remove(source);
        }

        recalculateDistances();
        refreshRecallCachesAround(source, target);
    }

    /**
     * Completely removes a node and all its connections
     * @param node the node to remove
     */
    public void removeConnectionNode(ConnectionNode node) {
        outgoing.remove(node);
        incoming.remove(node);

        for (Set<ConnectionNode> set : outgoing.values()) {
            set.remove(node);
        }

        for (Set<ConnectionNode> set : incoming.values()) {
            set.remove(node);
        }
    }

    /**
     * Recomputes distances from generator nodes using a modified BFS implementation
     * Dinamos (generator nodes) start with a distance of 0, while voltaic relays act
     * as distance resets. Other blocks increment distance normally.
     *
     * Nodes beyond a max distance cap 'DINAMO_MAX_CHAIN_CONNECTIONS' or unreachable ones
     * are purged.
     */
    public void recalculateDistances() {
        // Resets distances
        for (AbstractTeslaBlockEntity be : blockEntities.values()) {
            be.setDistance(Integer.MAX_VALUE);
        }

        Set<ConnectionNode> allNodes = getAllNodes();
        Set<ConnectionNode> visited = new HashSet<>();

        // Process each connected node separately
        for (ConnectionNode start : allNodes) {
            if (!visited.add(start)) continue;

            Queue<ConnectionNode> queue = new LinkedList<>();
            Set<ConnectionNode> component = new HashSet<>();
            queue.add(start);
            component.add(start);
            while (!queue.isEmpty()) {
                ConnectionNode cur = queue.poll();
                for (ConnectionNode nb : outgoing.getOrDefault(cur, Collections.emptySet())) {
                    if (component.add(nb)) queue.add(nb);
                }

                for (ConnectionNode nb : incoming.getOrDefault(cur, Collections.emptySet())) {
                    if (component.add(nb)) queue.add(nb);
                }
            }
            visited.addAll(component);

            // Finds all dinamos
            List<ConnectionNode> seeds = component.stream().filter(ConnectionNode::isEntity).toList();
            if (seeds.isEmpty()) continue;

            // BFS from generators to compute distances
            Map<ConnectionNode,Integer> distances = new ConcurrentHashMap<>();
            Queue<ConnectionNode> bfsQueue = new LinkedList<>(seeds);
            seeds.forEach(n -> distances.put(n, 0));

            while (!bfsQueue.isEmpty()) {
                ConnectionNode cur = bfsQueue.poll();
                int currentDistance = distances.get(cur);
                AbstractTeslaBlockEntity currentBe = blockEntities.get(cur);

                // We only process outgoing conns (directed graph)
                for (ConnectionNode nb : outgoing.getOrDefault(cur, Collections.emptySet())) {
                    if (!nb.isBlock()) continue;

                    final int newDistance;
                    if (currentBe instanceof VoltaicRelayBlockEntity) {
                        newDistance = 1;
                    } else if (currentBe instanceof VoltaicPillarBlockEntity be && be.getLevel() != null && be.getLevel().getBlockEntity(be.getBlockPos().above()) instanceof VoltaicPillarBlockEntity) {
                        newDistance = currentDistance;
                    } else {
                        newDistance = currentDistance + 1;
                    }

                    // Respect the max distance cap
                    if (newDistance > CompanionsConfig.DINAMO_MAX_CHAIN_CONNECTIONS) continue;
                    if (!distances.containsKey(nb) || newDistance < distances.get(nb)) {
                        distances.put(nb, newDistance);
                        bfsQueue.add(nb);
                    }
                }
            }

            // Apply computed distances
            for (Map.Entry<ConnectionNode,Integer> e : distances.entrySet()) {
                AbstractTeslaBlockEntity be = blockEntities.get(e.getKey());
                if (be != null) be.setDistance(e.getValue());
            }

            // Purge unreachable nodes
            for (ConnectionNode node : component) {
                if (!node.isEntity() && !distances.containsKey(node)) {
                    removeConnectionNode(node);
                    AbstractTeslaBlockEntity be = blockEntities.get(node);
                    if (be != null) be.setDistance(0);
                }
            }

        }

    }

    /**
     * @return all nodes in every graph
     */
    private Set<ConnectionNode> getAllNodes() {
        Set<ConnectionNode> nodes = new HashSet<>();
        nodes.addAll(outgoing.keySet());
        nodes.addAll(incoming.keySet());

        for (Set<ConnectionNode> set : outgoing.values()) {
            nodes.addAll(set);
        }

        for (Set<ConnectionNode> set : incoming.values()) {
            nodes.addAll(set);
        }

        return nodes;
    }

    /**
     * This is used to serialize and deserialize node connection data on world reload.
     * @param entityId UUID of the entity node
     * @param blockPos pos of the block node
     * @param dimension dimension of the node
     */
    public record ConnectionNode(UUID entityId, BlockPos blockPos, ResourceLocation dimension) {

        public static ConnectionNode forEntity(UUID entityId, ResourceLocation dimension) {
            return new ConnectionNode(entityId, null, dimension);
        }

        public static ConnectionNode forBlock(BlockPos pos, ResourceLocation dimension) {
            return new ConnectionNode(null, pos, dimension);
        }

        public CompoundTag serialize() {
            CompoundTag tag = new CompoundTag();

            if (isEntity()) {
                tag.putString("Type", "entity");
                tag.putUUID("UUID", entityId);
            } else {
                tag.putString("Type", "block");
                tag.putInt("X", blockPos.getX());
                tag.putInt("Y", blockPos.getY());
                tag.putInt("Z", blockPos.getZ());
            }

            tag.putString("Dimension", dimension.toString());
            return tag;
        }

        public static ConnectionNode deserialize(CompoundTag tag) {
            ResourceLocation dimension = new ResourceLocation(tag.getString("Dimension"));

            if (tag.getString("Type").equals("entity")) {
                return forEntity(tag.getUUID("UUID"), dimension);
            } else {
                BlockPos pos = new BlockPos(tag.getInt("X"), tag.getInt("Y"), tag.getInt("Z"));
                return forBlock(pos, dimension);
            }
        }

        public boolean isEntity() {
            return entityId != null;
        }

        public boolean isBlock() {
            return blockPos != null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ConnectionNode that = (ConnectionNode) o;
            return Objects.equals(entityId, that.entityId) &&
                    Objects.equals(blockPos, that.blockPos) &&
                    Objects.equals(dimension, that.dimension);
        }

        @Override
        public int hashCode() {
            return Objects.hash(entityId, blockPos, dimension);
        }

    }

}