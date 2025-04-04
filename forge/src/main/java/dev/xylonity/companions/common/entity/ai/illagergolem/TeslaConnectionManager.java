package dev.xylonity.companions.common.entity.ai.illagergolem;

import dev.xylonity.companions.common.blockentity.TeslaReceiverBlockEntity;
import dev.xylonity.companions.config.CompanionsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import java.util.*;

/**
 * Dynamic bidirectional algorithm (based on BFS) with memoized generic node tracking (for both UUIDs and block positions), for graphs under a
 * depth constraint (preconfigured with a default cap, which is a config value, `DINAMO_MAX_RECEIVER_CONNECTIONS`).
 *
 * @author Xylonity
 */
public class TeslaConnectionManager {
    private static TeslaConnectionManager instance;
    private final Map<ConnectionNode, Set<ConnectionNode>> outgoing = new HashMap<>();
    private final Map<ConnectionNode, Set<ConnectionNode>> incoming = new HashMap<>();
    private final Map<ConnectionNode, TeslaReceiverBlockEntity> blockEntities = new HashMap<>();

    public static TeslaConnectionManager getInstance() {
        if (instance == null) instance = new TeslaConnectionManager();
        return instance;
    }

    public void registerBlockEntity(TeslaReceiverBlockEntity blockEntity) {
        blockEntities.put(blockEntity.asConnectionNode(), blockEntity);
    }

    public void unregisterBlockEntity(TeslaReceiverBlockEntity blockEntity) {
        ConnectionNode node = blockEntity.asConnectionNode();
        blockEntities.remove(node);
        removeConnectionNode(node);
        recalculateDistances();
    }

    public void addConnection(ConnectionNode source, ConnectionNode target) {
        addConnection(source, target, false);
    }

    public void addConnection(ConnectionNode source, ConnectionNode target, boolean bypassValidation) {
        if (!bypassValidation) {
            if (source.isBlock() && target.isBlock()) {
                if (!canAddConnection(source, target)) return;
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

    public void recalculateDistances() {
        for (TeslaReceiverBlockEntity be : blockEntities.values()) {
            be.setDistance(Integer.MAX_VALUE);
        }

        Queue<ConnectionNode> queue = new LinkedList<>();
        Map<ConnectionNode, Integer> distances = new HashMap<>();

        for (ConnectionNode node : getAllNodes()) {
            if (node.isEntity()) {
                distances.put(node, 0);
                queue.add(node);
            }
        }

        while (!queue.isEmpty()) {
            ConnectionNode current = queue.poll();
            int currentDistance = distances.get(current);

            Set<ConnectionNode> neighbors = new HashSet<>();

            neighbors.addAll(outgoing.getOrDefault(current, Collections.emptySet()));
            neighbors.addAll(incoming.getOrDefault(current, Collections.emptySet()));

            for (ConnectionNode neighbor : neighbors) {
                if (!neighbor.isBlock()) continue;

                int newDistance = current.isBlock() ? currentDistance + 1 : 1;

                if (newDistance > CompanionsConfig.DINAMO_MAX_RECEIVER_CONNECTIONS.get()) continue;
                if (!distances.containsKey(neighbor) || newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    queue.add(neighbor);
                    TeslaReceiverBlockEntity be = blockEntities.get(neighbor);
                    if (be != null) be.setDistance(newDistance);
                }
            }

        }

        for (TeslaReceiverBlockEntity be : blockEntities.values()) {
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

        if (!simDistance.containsKey(source) || !simDistance.containsKey(target)) return false;

        return simDistance.get(source) <= maxAllowed && simDistance.get(target) <= maxAllowed;
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

        public UUID getEntityId() {
            return this.entityId;
        }

        public BlockPos getBlockPos() {
            return blockPos;
        }

        public ResourceLocation getDimension() {
            return dimension;
        }

    }

}