package dev.xylonity.companions.common.tesla;

import dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity;
import dev.xylonity.companions.common.blockentity.RecallPlatformBlockEntity;
import dev.xylonity.companions.config.CompanionsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implements a hierarchical directed flow network for Tesla Network with
 * multi-layered propagation constraints. The graph model combines elements of
 * ADGs and Bipartite graphs as a whole.
 *
 * The underlying graph is stored in two maps:
 * - outgoing: contains the edges where the node is the source.
 * - incoming: contains the edges where the node is the target.
 *
 * Propagation follows directional BFS from source nodes, enforcing a max-depth constraint
 * (DINAMO_MAX_COIL_CONNECTIONS). Unreachable nodes are pruned dynamically.
 *
 * For efficiency, distances are recomputed only on major structural changes (add/remove).
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

    public void registerBlockEntity(AbstractTeslaBlockEntity blockEntity) {
        blockEntities.put(blockEntity.asConnectionNode(), blockEntity);

        recalculateDistances();
        refreshRecallCachesAround(blockEntity.asConnectionNode());
    }

    public void unregisterBlockEntity(AbstractTeslaBlockEntity blockEntity) {
        ConnectionNode node = blockEntity.asConnectionNode();
        blockEntities.remove(node);
        removeConnectionNode(node);
        recalculateDistances();
        refreshRecallCachesAround(node);
    }

    public @NotNull AbstractTeslaBlockEntity getBlockEntity(ConnectionNode node) {
        return blockEntities.get(node);
    }

    public void addConnection(ConnectionNode source, ConnectionNode target) {
        addConnection(source, target, false);
        refreshRecallCachesAround(source, target);
    }

    public void addConnection(ConnectionNode source, ConnectionNode target, boolean bypassValidation) {
        if (!bypassValidation && source.isBlock()) {
            AbstractTeslaBlockEntity be = blockEntities.get(source);
            if (be != null && !be.canConnectToOtherModules()) {
                return;
            }
        }

        outgoing.computeIfAbsent(source, k -> new HashSet<>()).add(target);
        incoming.computeIfAbsent(target, k -> new HashSet<>()).add(source);

        if (!bypassValidation) {
            recalculateDistances();
        }
    }

    private void refreshRecallCachesAround(ConnectionNode... seeds) {
        Set<ConnectionNode> visitedComponents = new HashSet<>();

        for (ConnectionNode seed : seeds) {
            if (visitedComponents.contains(seed))
                continue;

            Set<ConnectionNode> component = getConnectedComponent(seed);
            visitedComponents.addAll(component);

            List<RecallPlatformBlockEntity> recalls = new ArrayList<>();
            for (ConnectionNode n : component) {
                AbstractTeslaBlockEntity be = blockEntities.get(n);
                if (be instanceof RecallPlatformBlockEntity rp) {
                    recalls.add(rp);
                }
            }

            if (recalls.size() < 2) {
                for (RecallPlatformBlockEntity rp : recalls) {
                    rp.updatePartners(Collections.emptySet());
                }
                continue;
            }

            List<BlockPos> allPos = recalls.stream()
                    .map(AbstractTeslaBlockEntity::getBlockPos)
                    .toList();
            for (RecallPlatformBlockEntity rp : recalls) {
                Set<BlockPos> partners = new HashSet<>(allPos);
                partners.remove(rp.getBlockPos());
                rp.updatePartners(partners);
            }
        }

    }

    public Set<ConnectionNode> getConnectedComponent(ConnectionNode start) {
        Set<ConnectionNode> comp = new HashSet<>();
        Deque<ConnectionNode> q   = new ArrayDeque<>();
        comp.add(start); q.add(start);

        while (!q.isEmpty()) {
            ConnectionNode cur = q.poll();
            comp.add(cur);

            Set<ConnectionNode> neigh = new HashSet<>();
            neigh.addAll(outgoing.getOrDefault(cur, Collections.emptySet()));
            neigh.addAll(incoming.getOrDefault(cur, Collections.emptySet()));

            for (ConnectionNode n : neigh)
                if (comp.add(n)) q.add(n);
        }
        return comp;
    }

    public void removeConnection(ConnectionNode source, ConnectionNode target) {
        Set<ConnectionNode> outSet = outgoing.get(source);
        if (outSet != null) outSet.remove(target);

        Set<ConnectionNode> inSet = incoming.get(target);
        if (inSet != null) inSet.remove(source);

        recalculateDistances();
        refreshRecallCachesAround(source, target);
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

    public void recalculateDistances() {
        for (AbstractTeslaBlockEntity be : blockEntities.values()) {
            be.setDistance(Integer.MAX_VALUE);
        }

        Set<ConnectionNode> all = getAllNodes();
        Set<ConnectionNode> visited = new HashSet<>();

        for (ConnectionNode start : all) {
            if (!visited.add(start)) continue;

            Queue<ConnectionNode> q = new LinkedList<>();
            Set<ConnectionNode> comp = new HashSet<>();
            q.add(start);
            comp.add(start);
            while (!q.isEmpty()) {
                ConnectionNode cur = q.poll();
                for (ConnectionNode nb : outgoing.getOrDefault(cur, Collections.emptySet())) {
                    if (comp.add(nb)) q.add(nb);
                }
                for (ConnectionNode nb : incoming.getOrDefault(cur, Collections.emptySet())) {
                    if (comp.add(nb)) q.add(nb);
                }
            }

            visited.addAll(comp);

            List<ConnectionNode> seeds = comp.stream().filter(ConnectionNode::isEntity).toList();
            if (seeds.isEmpty()) continue;

            Map<ConnectionNode,Integer> dist = new HashMap<>();
            Queue<ConnectionNode> q2 = new LinkedList<>(seeds);
            seeds.forEach(n -> dist.put(n, 0));

            int max = CompanionsConfig.DINAMO_MAX_CHAIN_CONNECTIONS;
            while (!q2.isEmpty()) {
                ConnectionNode cur = q2.poll();
                int d = dist.get(cur);

                AbstractTeslaBlockEntity currentBe = blockEntities.get(cur);

                for (ConnectionNode nb : outgoing.getOrDefault(cur, Collections.emptySet())) {
                    if (!nb.isBlock()) continue;

                    final int nd;
                    //if (currentBe instanceof TeslaRepeaterBlockEntity) {
                    //    nd = 1;
                    //} else {
                    //    nd = d + 1;
                    //}
                    nd = d + 1;

                    if (nd > max) continue;
                    if (!dist.containsKey(nb) || nd < dist.get(nb)) {
                        dist.put(nb, nd);
                        q2.add(nb);
                    }
                }
            }

            for (Map.Entry<ConnectionNode,Integer> e : dist.entrySet()) {
                AbstractTeslaBlockEntity be = blockEntities.get(e.getKey());
                if (be != null) be.setDistance(e.getValue());
            }

            for (ConnectionNode node : comp) {
                if (!node.isEntity() && !dist.containsKey(node)) {
                    removeConnectionNode(node);
                    AbstractTeslaBlockEntity be = blockEntities.get(node);
                    if (be != null) be.setDistance(0);
                }
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

    private boolean canAddConnection(ConnectionNode source, ConnectionNode target) {
        int maxAllowed = CompanionsConfig.DINAMO_MAX_CHAIN_CONNECTIONS;

        Set<ConnectionNode> comp = getConnectedComponentIncluding(source, target);
        Map<ConnectionNode, Integer> simDistance = new ConcurrentHashMap<>();
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