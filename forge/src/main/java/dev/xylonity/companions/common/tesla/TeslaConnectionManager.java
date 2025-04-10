package dev.xylonity.companions.common.tesla;

import dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity;
import dev.xylonity.companions.config.CompanionsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

/**
 * Implements a hierarchical directed flow network for <i>Tesla Network</i> with
 * multi-layered propagation constraints. The graph model combines elements of
 * ADGs and Bipartite graphs as a whole.
 * <br><br>
 * The underlying graph is stored in two maps:
 * <br>
 * – {@code outgoing}: contains the edges where the node is the source.<br>
 * – {@code incoming}: contains the edges where the node is the target.
 * <br><br>
 * Propagation follows directional BFS from source nodes, enforcing a max-depth constraint
 * ({@code DINAMO_MAX_COIL_CONNECTIONS}). Unreachable nodes are pruned dynamically.
 *
 * @implNote For efficiency, distances are recomputed only on major structural changes (add/remove).
 *
 * @author Xylonity
 */
public class TeslaConnectionManager {
    private static TeslaConnectionManager instance;
    private final Map<ConnectionNode, Set<ConnectionNode>> outgoing = new HashMap<>();
    private final Map<ConnectionNode, Set<ConnectionNode>> incoming = new HashMap<>();
    private final Map<ConnectionNode, AbstractTeslaBlockEntity> blockEntities = new HashMap<>();

    public static TeslaConnectionManager getInstance() {
        if (instance == null) instance = new TeslaConnectionManager();
        return instance;
    }

    public void registerBlockEntity(AbstractTeslaBlockEntity blockEntity) {
        blockEntities.put(blockEntity.asConnectionNode(), blockEntity);
    }

    public void unregisterBlockEntity(AbstractTeslaBlockEntity blockEntity) {
        ConnectionNode node = blockEntity.asConnectionNode();
        blockEntities.remove(node);
        removeConnectionNode(node);
        recalculateDistances();
    }

    public AbstractTeslaBlockEntity getBlockEntity(ConnectionNode node) {
        return blockEntities.get(node);
    }

    public void addConnection(ConnectionNode source, ConnectionNode target) {
        addConnection(source, target, false);
    }

    public void addConnection(ConnectionNode source, ConnectionNode target, boolean bypassValidation) {
        if (!bypassValidation) {
            if (source.isBlock() && target.isBlock() && !canAddConnection(source, target)) {
                return;
            }
        }

        outgoing.computeIfAbsent(source, k -> new HashSet<>()).add(target);
        incoming.computeIfAbsent(target, k -> new HashSet<>()).add(source);
        recalculateDistances();
    }

    public void removeConnection(ConnectionNode source, ConnectionNode target) {
        Set<ConnectionNode> outSet = outgoing.get(source);
        if (outSet != null) outSet.remove(target);

        Set<ConnectionNode> inSet = incoming.get(target);
        if (inSet != null) inSet.remove(source);

        recalculateDistances();
    }

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
     * Recalculate node “distances” based on valid directional propagation from generator nodes.
     * <br>
     * Performs a directional BFS (only follow outgoing edges) starting from every entity node.
     * All block entities that are unreachable (distance remains Integer.MAX_VALUE) are removed
     * from their independent graph.
     */
    public void recalculateDistances() {
        /* Reset distances */
        for (AbstractTeslaBlockEntity be : blockEntities.values()) {
            be.setDistance(Integer.MAX_VALUE);
        }

        Queue<ConnectionNode> queue = new LinkedList<>();
        Map<ConnectionNode, Integer> distances = new HashMap<>();
        int maxAllowed = CompanionsConfig.DINAMO_MAX_RECEIVER_CONNECTIONS.get();

        /* Start only from generator nodes (entity nodes) */
        for (ConnectionNode node : getAllNodes()) {
            if (node.isEntity()) {
                distances.put(node, 0);
                queue.add(node);
            }
        }

        /* Follow only outgoing edges */
        while (!queue.isEmpty()) {
            ConnectionNode current = queue.poll();
            int currentDistance = distances.get(current);

            /* Get only the outgoing neighbors */
            Set<ConnectionNode> neighbors = outgoing.getOrDefault(current, Collections.emptySet());

            for (ConnectionNode neighbor : neighbors) {
                /* We pass generator nodes */
                if (!neighbor.isBlock()) continue;

                int newDistance = currentDistance + 1;

                if (newDistance > maxAllowed) continue;
                if (!distances.containsKey(neighbor) || newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    queue.add(neighbor);
                    AbstractTeslaBlockEntity be = blockEntities.get(neighbor);
                    if (be != null) {
                        be.setDistance(newDistance);
                    }
                }
            }
        }

        /* Clean nodes that could not be reached via a valid directional chain */
        for (AbstractTeslaBlockEntity be : new ArrayList<>(blockEntities.values())) {
            if (be.getDistance() == Integer.MAX_VALUE) {
                removeConnectionNode(be.asConnectionNode());
                be.setDistance(0);
            }
        }
    }

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
     * Checks if the directed connection from source to target can be added.
     * <br>
     * This method uses a simulated BFS within the connected component (considering bidirectional
     * connectivity) to enforce that both source and target are within the allowed maximum distance.
     *
     * @param source source node
     * @param target target node
     * @return true if the connection is allowed.
     */
    private boolean canAddConnection(ConnectionNode source, ConnectionNode target) {
        int maxAllowed = CompanionsConfig.DINAMO_MAX_RECEIVER_CONNECTIONS.get();

        Set<ConnectionNode> comp = getConnectedComponentIncluding(source, target);
        Map<ConnectionNode, Integer> simDistance = new HashMap<>();
        Queue<ConnectionNode> queue = new LinkedList<>();

        for (ConnectionNode node : comp) {
            if (node.isEntity()) {
                simDistance.put(node, 0);
                queue.add(node);
            }
        }

        if (simDistance.isEmpty()) return false;

        while (!queue.isEmpty()) {
            ConnectionNode cur = queue.poll();
            int d = simDistance.get(cur);

            Set<ConnectionNode> neighbors = new HashSet<>();
            neighbors.addAll(outgoing.getOrDefault(cur, Collections.emptySet()));
            neighbors.addAll(incoming.getOrDefault(cur, Collections.emptySet()));

            /* Ensure that source and target are connected in at least one direction */
            if (cur.equals(source)) neighbors.add(target);
            if (cur.equals(target)) neighbors.add(source);

            for (ConnectionNode neighbor : neighbors) {
                if (!comp.contains(neighbor)) continue;

                int nd = cur.isBlock() ? d + 1 : 1;

                if (nd > maxAllowed) continue;
                if (!simDistance.containsKey(neighbor) || nd < simDistance.get(neighbor)) {
                    simDistance.put(neighbor, nd);
                    queue.add(neighbor);
                }
            }
        }

        return simDistance.containsKey(source) && simDistance.containsKey(target)
                && simDistance.get(source) <= maxAllowed && simDistance.get(target) <= maxAllowed;
    }

    private Set<ConnectionNode> getConnectedComponentIncluding(ConnectionNode source, ConnectionNode target) {
        Set<ConnectionNode> comp = new HashSet<>();
        Queue<ConnectionNode> queue = new LinkedList<>();

        comp.add(source);
        comp.add(target);
        queue.add(source);
        queue.add(target);

        while (!queue.isEmpty()) {
            ConnectionNode cur = queue.poll();
            Set<ConnectionNode> neighbors = new HashSet<>();
            neighbors.addAll(outgoing.getOrDefault(cur, Collections.emptySet()));
            neighbors.addAll(incoming.getOrDefault(cur, Collections.emptySet()));

            if (cur.equals(source)) neighbors.add(target);
            if (cur.equals(target)) neighbors.add(source);

            for (ConnectionNode nb : neighbors) {
                if (comp.add(nb)) queue.add(nb);
            }
        }

        return comp;
    }

    public Set<ConnectionNode> getOutgoing(ConnectionNode node) {
        return outgoing.getOrDefault(node, Collections.emptySet());
    }

    public Set<ConnectionNode> getIncoming(ConnectionNode node) {
        return incoming.getOrDefault(node, Collections.emptySet());
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