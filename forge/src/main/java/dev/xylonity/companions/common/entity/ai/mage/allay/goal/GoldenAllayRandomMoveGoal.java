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
        BlockPos $$0 = allay.getOnPos();

        for (int $$1 = 0; $$1 < 3; ++$$1) {
            BlockPos $$2 = $$0.offset(allay.getRandom().nextInt(15) - 7, allay.getRandom().nextInt(7) - 3, allay.getRandom().nextInt(15) - 7);
            if (allay.level().isEmptyBlock($$2)) {
                allay.getMoveControl().setWantedPosition((double)$$2.getX() + 0.5, (double)$$2.getY() + 0.5, (double)$$2.getZ() + 0.5, 0.25);
                if (allay.getTarget() == null) {
                    allay.getLookControl().setLookAt((double)$$2.getX() + 0.5, (double)$$2.getY() + 0.5, (double)$$2.getZ() + 0.5, 180.0F, 20.0F);
                }

                break;
            }
        }

    }
}