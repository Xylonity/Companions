package dev.xylonity.companions.common.entity.ai.mage.allay.goal;

import dev.xylonity.companions.common.entity.companion.GoldenAllayEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class GoldenAllayRandomMoveGoal extends Goal {

    private final GoldenAllayEntity allay;

    public GoldenAllayRandomMoveGoal(GoldenAllayEntity allay) {
        this.setFlags(EnumSet.of(Flag.MOVE));
        this.allay = allay;
    }

    public boolean canUse() {
        if (allay.getState() == 1 || allay.getState() == 3 || allay.getState() == 5) return false;
        return !allay.getMoveControl().hasWanted() && allay.getRandom().nextInt(reducedTickDelay(7)) == 0;
    }

    public boolean canContinueToUse() {
        return false;
    }

    public void tick() {
        BlockPos pos = allay.getOnPos();

        for (int i = 0; i < 3; ++i) {
            BlockPos offset = pos.offset(allay.getRandom().nextInt(15) - 7, allay.getRandom().nextInt(7) - 3, allay.getRandom().nextInt(15) - 7);
            Vec3 destination = Vec3.atCenterOf(offset);
            if (canMoveTo(offset, destination)) {
                allay.getMoveControl().setWantedPosition(destination.x, destination.y, destination.z, 0.25);
                if (allay.getTarget() == null) {
                    allay.getLookControl().setLookAt(destination.x, destination.y, destination.z, 180.0F, 20.0F);
                }

                break;
            }
        }

    }

    private boolean canMoveTo(BlockPos destinationPos, Vec3 destination) {
        if (!allay.level().isInWorldBounds(destinationPos) || !allay.level().isEmptyBlock(destinationPos)) {
            return false;
        }

        Vec3 movement = destination.subtract(allay.position());
        if (!allay.level().noCollision(allay, allay.getBoundingBox().move(movement))) {
            return false;
        }

        return allay.level().clip(new ClipContext(allay.getBoundingBox().getCenter(), destination, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, allay))
                .getType() == HitResult.Type.MISS;
    }

}
