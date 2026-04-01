package dev.xylonity.companions.common.tesla;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a node in the Tesla network, either a block (by position) or an entity (by UUID)
 */
public record ConnectionTarget(
        UUID entityId,
        BlockPos blockPos,
        ResourceLocation dimension)
{

    public static ConnectionTarget forEntity(UUID entityId, ResourceLocation dimension) {
        return new ConnectionTarget(entityId, null, dimension);
    }

    public static ConnectionTarget forBlock(BlockPos pos, ResourceLocation dimension) {
        return new ConnectionTarget(null, pos, dimension);
    }

    public boolean isEntity() {
        return entityId != null;
    }

    public boolean isBlock() {
        return blockPos != null;
    }

    public CompoundTag serialize() {
        final CompoundTag tag = new CompoundTag();
        if (isEntity()) {
            tag.putString("Type", "entity");
            tag.putUUID("UUID", entityId);
        }
        else {
            tag.putString("Type", "block");
            tag.putInt("X", blockPos.getX());
            tag.putInt("Y", blockPos.getY());
            tag.putInt("Z", blockPos.getZ());
        }

        tag.putString("Dimension", dimension.toString());

        return tag;
    }

    public static ConnectionTarget deserialize(CompoundTag tag) {
        final ResourceLocation dimensionRL = new ResourceLocation(tag.getString("Dimension"));
        if ("entity".equals(tag.getString("Type"))) {
            return forEntity(tag.getUUID("UUID"), dimensionRL);
        }
        else {
            return forBlock(new BlockPos(tag.getInt("X"), tag.getInt("Y"), tag.getInt("Z")), dimensionRL);
        }

    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final ConnectionTarget otherObject = (ConnectionTarget) object;
        return Objects.equals(entityId, otherObject.entityId)
                && Objects.equals(blockPos, otherObject.blockPos)
                && Objects.equals(dimension, otherObject.dimension);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityId, blockPos, dimension);
    }

}