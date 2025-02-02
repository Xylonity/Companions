package dev.xylonity.companions.common.entity.ai.illagergolem;

import dev.xylonity.companions.common.blockentity.TeslaReceiverBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import java.util.*;

/**
 * Dynamic bidirectional BFS algorithm with memoized generic node tracking (for both UUIDs and block positions),
 * for graphs under a depth constraint (preconfigured with a cap of 3).
 *
 * @Author Xylonity
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
        blockEntities.remove(blockEntity.asConnectionNode());
    }

    public void addConnection(ConnectionNode source, ConnectionNode target) {
        outgoing.computeIfAbsent(source, k -> new HashSet<>()).add(target);
        incoming.computeIfAbsent(target, k -> new HashSet<>()).add(source);
        recalculateDistances();
    }

    public void removeConnection(ConnectionNode source, ConnectionNode target) {
        Optional.ofNullable(outgoing.get(source)).ifPresent(s -> s.remove(target));
        Optional.ofNullable(incoming.get(target)).ifPresent(s -> s.remove(source));
        recalculateDistances();
    }

    private void recalculateDistances() {
        blockEntities.values().forEach(be -> be.setDistance(Integer.MAX_VALUE));
        Queue<ConnectionNode> queue = new LinkedList<>();
        Map<ConnectionNode, Integer> distances = new HashMap<>();
        for (ConnectionNode node : outgoing.keySet()) {
            if (!node.isBlock()) {
                queue.add(node);
                distances.put(node, 0);
            }
        }
        while (!queue.isEmpty()) {
            ConnectionNode current = queue.poll();
            int currentDistance = distances.get(current);
            for (ConnectionNode neighbor : outgoing.getOrDefault(current, Collections.emptySet())) {
                if (neighbor.isBlock()) {
                    int newDistance = current.isBlock() ? currentDistance + 1 : 1;
                    if (newDistance > 3) continue;
                    if (!distances.containsKey(neighbor) || newDistance < distances.get(neighbor)) {
                        distances.put(neighbor, newDistance);
                        queue.add(neighbor);
                        TeslaReceiverBlockEntity be = blockEntities.get(neighbor);
                        if (be != null) be.setDistance(newDistance);
                    }
                }
            }
        }
        blockEntities.values().forEach(be -> {
            if (be.getDistance() == Integer.MAX_VALUE) be.setDistance(0);
        });
        for (Map.Entry<ConnectionNode, Integer> entry : distances.entrySet()) {
            ConnectionNode node = entry.getKey();
            int distance = entry.getValue();
            if (node.isBlock() && distance == 3) outgoing.getOrDefault(node, Collections.emptySet()).clear();
        }
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
            return Objects.equals(entityId, that.entityId) && Objects.equals(blockPos, that.blockPos) && Objects.equals(dimension, that.dimension);
        }

        @Override
        public int hashCode() {
            return Objects.hash(entityId, blockPos, dimension);
        }

        public UUID getEntityId() { return this.entityId; }
        public BlockPos getBlockPos() { return blockPos; }
        public ResourceLocation getDimension() { return dimension; }

    }
}