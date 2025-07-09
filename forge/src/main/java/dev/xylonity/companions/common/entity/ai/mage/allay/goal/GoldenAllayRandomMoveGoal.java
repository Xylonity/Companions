package dev.xylonity.companions.common.entity.ai.mage.allay.goal;

import dev.xylonity.companions.common.entity.companion.GoldenAllayEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;

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
            if (allay.level().isEmptyBlock(offset)) {
                allay.getMoveControl().setWantedPosition(offset.getX() + 0.5, offset.getY() + 0.5, offset.getZ() + 0.5, 0.25);
                if (allay.getTarget() == null) {
                    allay.getLookControl().setLookAt(offset.getX() + 0.5, offset.getY() + 0.5, offset.getZ() + 0.5, 180.0F, 20.0F);
                }

                break;
            }
        }

    }

}