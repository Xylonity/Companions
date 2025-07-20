package dev.xylonity.companions.common.ai.navigator;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.*;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * Customized ground navigator that adds a node stimulation cache to speed up the repeated path checking
 *
 * A chunk of the implementation is derived from the work of BobMowzie:
 * https://github.com/BobMowzie/MowziesMobs/blob/master/src/main/java/com/bobmowzie/mowziesmobs/server/ai/MMPathNavigateGround.java
 */
public class GroundNavigator extends GroundPathNavigation {

    private static final float EPSILON = 1.0E-8F;

    private Vec3 lastPos = Vec3.ZERO;
    private int noMoveTicks = 0;

    // Cache of block positions to a bool indicating whether that block is pathfindable or not
    private final Cache<BlockPos, Boolean> cache =
            CacheBuilder.newBuilder()
                    .maximumSize(10000)
                    .expireAfterAccess(5, TimeUnit.SECONDS)
                    .build();

    public GroundNavigator(Mob entity, Level world) {
        super(entity, world);
    }

    @Override
    protected @NotNull PathFinder createPathFinder(int maxVisitedNodes) {
        this.nodeEvaluator = new WalkNodeEvaluator();
        this.nodeEvaluator.setCanPassDoors(true);
        return new BonusPathFinder(this.nodeEvaluator, maxVisitedNodes);
    }

    /**
     * Core loop for following the current path. Attempts shortcuts, moves towards the next node and handles
     * the entity jumping (the latter killed me ngl)
     */
    @Override
    protected void followThePath() {
        // If there is no path
        if (this.path == null || this.path.isDone()) return;

        Vec3 entityPos = this.getTempMobPos();
        int nextIdx = this.path.getNextNodeIndex();
        double yFloor = Math.floor(entityPos.y);

        // Checks if there are any more nodes remaining on the same Y
        int lastIdx = nextIdx;
        while (lastIdx < this.path.getNodeCount() && this.path.getNode(lastIdx).y == yFloor) {
            ++lastIdx;
        }

        // Shortcut raycast
        Vec3 base = entityPos.subtract(this.mob.getBbWidth(), 0, this.mob.getBbWidth());

        this.attemptShortcut(this.path, entityPos, lastIdx, base);
        // If the entity is very close to the next node (or on an elevation change), advance the path index
        if (this.hasReached(this.path, 1f) || (this.isAtElevationChange(this.path) && this.hasReached(this.path, 1f))) {
            this.path.advance();
        }

        // If we still have a node to reach, instruct the mob to move over there
        if (!this.path.isDone()) {
            Vec3 target = this.path.getNextEntityPos(this.mob);

            // Move the entity
            this.mob.getMoveControl().setWantedPosition(target.x, target.y, target.z, this.speedModifier);

            // The jump detection depends completely on the azimuth where the entity's direction is prominent (and this
            // is specified on the coordinates the entity is moving to). When the entity is moving towards the west/south,
            // the coordinates compute a 'negative' offset (where the center of the block the entity is) that is left behind
            // towards the next node calculated, so the entity would get stuck in front of an obstacle without jumping (because
            // the offset point became unusable). My alternative solution (that should work :skull:) is to use the real Y node,
            // and this may fix the problem of entities with big hitboxes dancing macarena when there is an elevation change
            if (this.mob.horizontalCollision && this.mob.onGround()) {
                Direction dir = this.mob.getDirection();
                BlockPos frontPos = this.mob.blockPosition().relative(dir);
                BlockState state = this.level.getBlockState(frontPos);
                VoxelShape shape = state.getCollisionShape(this.level, frontPos);
                double shapeHeight = shape.max(Direction.Axis.Y);

                if (shapeHeight > 0 && shapeHeight <= 1.0D) {
                    BlockPos abovePos = frontPos.above();
                    PathType aboveType = this.nodeEvaluator.getPathType(new PathfindingContext(this.level, this.mob), abovePos.getX(), abovePos.getY(), abovePos.getZ());
                    float malus = this.mob.getPathfindingMalus(aboveType);

                    if (malus >= 0.0F && malus < 8.0F) {
                        this.mob.getJumpControl().jump();
                    }
                }
            }

        }

        Vec3 curr = Vec3.atLowerCornerOf(this.mob.blockPosition()).add(0.0, this.mob.getBbHeight()/2, 0.0);
        if (curr.distanceTo(lastPos) < 0.01) {
            noMoveTicks++;
        } else {
            noMoveTicks = 0;
        }

        lastPos = curr;

        if (noMoveTicks > 10) {
            cache.invalidateAll();

            this.path = null;
            this.mob.getMoveControl().setWantedPosition(this.mob.getX(), this.mob.getY(), this.mob.getZ(), 0);

            if (this.getTargetPos() != null) {
                this.createPath(this.getTargetPos(), (int) this.mob.getAttributeValue(Attributes.FOLLOW_RANGE));
            }

            noMoveTicks = 0;
        }

        this.doStuckDetection(entityPos);
    }

    private boolean hasReached(Path path, float threshold) {
        Vec3 pathPos = path.getNextEntityPos(this.mob);

        if (Math.abs(this.mob.getX() - pathPos.x) >= threshold) return false;
        if (Math.abs(this.mob.getZ() - pathPos.z) >= threshold) return false;

        return Math.abs(this.mob.getY() - pathPos.y) <= 1.001D;
    }

    private boolean isAtElevationChange(Path path) {
        int currIndex = path.getNextNodeIndex();
        int endIndex = Math.min(path.getNodeCount(), currIndex + Mth.ceil(this.mob.getBbWidth() * 0.5F) + 1);
        int currY = path.getNode(currIndex).y;

        for (int i = currIndex + 1; i < endIndex; i++) {
            if (path.getNode(i).y != currY) {
                return true;
            }
        }

        return false;
    }

    private boolean attemptShortcut(Path path, Vec3 entityPos, int pathLength, Vec3 base) {
        int nextNodeIndex = path.getNextNodeIndex();
        for (int i = pathLength - 1; i > nextNodeIndex; i--) {
            Vec3 vec = path.getEntityPosAtNode(this.mob, i).subtract(entityPos);
            if (this.catchF(vec, base)) {
                path.setNextNodeIndex(i);
                return false;
            }
        }

        return true;
    }

    /**
     * 3D DDA algorithm to check if a straight line to a node is clear
     */
    private boolean catchF(Vec3 vec, Vec3 base) {
        float maxT = (float) vec.length();
        if (maxT < EPSILON) return true; // too close to worry about

        // Normalize direction
        float dx = (float) vec.x / maxT;
        float dy = (float) vec.y / maxT;
        float dz = (float) vec.z / maxT;

        // Compute step for each axis
        float tNextX, tNextY, tNextZ;
        float tDeltaX, tDeltaY, tDeltaZ;
        int stepX, stepY, stepZ;
        int currentX, currentY, currentZ;

        // X axis
        if (Math.abs(dx) < EPSILON) {
            tDeltaX = Float.POSITIVE_INFINITY;
            tNextX = Float.POSITIVE_INFINITY;
            stepX = 0;
        } else {
            stepX = dx > 0 ? 1 : -1;
            float voxelBoundaryX = stepX > 0 ? Mth.floor(base.x) + 1 : Mth.floor(base.x);
            tDeltaX = 1.0F / Math.abs(dx);
            tNextX = (float) ((voxelBoundaryX - base.x) / dx);
        }
        currentX = Mth.floor(base.x);

        // Y axis
        if (Math.abs(dy) < EPSILON) {
            tDeltaY = Float.POSITIVE_INFINITY;
            tNextY = Float.POSITIVE_INFINITY;
            stepY = 0;
        } else {
            stepY = dy > 0 ? 1 : -1;
            float voxelBoundaryY = stepY > 0 ? Mth.floor(base.y) + 1 : Mth.floor(base.y);
            tDeltaY = 1.0F / Math.abs(dy);
            tNextY = (float) ((voxelBoundaryY - base.y) / dy);
        }
        currentY = Mth.floor(base.y);

        // Z axis
        if (Math.abs(dz) < EPSILON) {
            tDeltaZ = Float.POSITIVE_INFINITY;
            tNextZ = Float.POSITIVE_INFINITY;
            stepZ = 0;
        } else {
            stepZ = dz > 0 ? 1 : -1;
            float voxelBoundaryZ = stepZ > 0 ? Mth.floor(base.z) + 1 : Mth.floor(base.z);
            tDeltaZ = 1.0F / Math.abs(dz);
            tNextZ = (float) ((voxelBoundaryZ - base.z) / dz);
        }
        currentZ = Mth.floor(base.z);

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        float t = 0.0F;

        // March along the ray until we exceed the target distance
        while (t <= maxT) {
            if (tNextX < tNextY) {
                if (tNextX < tNextZ) {
                    currentX += stepX;
                    t = tNextX;
                    tNextX += tDeltaX;
                } else {
                    currentZ += stepZ;
                    t = tNextZ;
                    tNextZ += tDeltaZ;
                }
            } else {
                if (tNextY < tNextZ) {
                    currentY += stepY;
                    t = tNextY;
                    tNextY += tDeltaY;
                } else {
                    currentZ += stepZ;
                    t = tNextZ;
                    tNextZ += tDeltaZ;
                }
            }

            pos.set(currentX, currentY, currentZ);
            BlockPos immutablePos = pos.immutable();

            // Caches nodes to avoid recomputing them again
            Boolean isPathfindable = cache.getIfPresent(immutablePos);
            if (isPathfindable == null) {
                BlockState blockState = this.level.getBlockState(pos);
                isPathfindable = blockState.isSolidRender(this.level, pos) || blockState.isAir();
                cache.put(immutablePos, isPathfindable);
            }

            if (!isPathfindable)
                return false;

            // Also rejects if the block's path type is not okie dokie
            PathType pathType = this.nodeEvaluator.getPathType(new PathfindingContext(this.level, this.mob), currentX, currentY, currentZ);
            float malus = this.mob.getPathfindingMalus(pathType);

            if (malus < 0.0F || malus >= 8.0F || pathType == PathType.DAMAGE_FIRE || pathType == PathType.DANGER_FIRE || pathType == PathType.DAMAGE_OTHER) {
                return false;
            }

        }

        return true;
    }

}