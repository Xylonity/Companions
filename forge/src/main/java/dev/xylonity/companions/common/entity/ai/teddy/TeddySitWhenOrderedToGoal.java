package dev.xylonity.companions.common.entity.ai.teddy;

import dev.xylonity.companions.common.entity.custom.TeddyEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class TeddySitWhenOrderedToGoal extends Goal {
    private final TeddyEntity mob;

    public TeddySitWhenOrderedToGoal(TeddyEntity pMob) {
        this.mob = pMob;
        this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!this.mob.isTame()) {
            return false;
        }
        if (this.mob.isInWaterOrBubble()) {
            return false;
        }

        TeddyEntity teddy = this.mob;
        if (teddy.getPhase() != 2) {
            if (!this.mob.onGround()) {
                return false;
            }
        }

        LivingEntity owner = this.mob.getOwner();
        if (owner == null) {
            return this.mob.isOrderedToSit();
        }

        if (this.mob.distanceToSqr(owner) < 144.0 && owner.getLastHurtByMob() != null) {
            return false;
        }

        return this.mob.isOrderedToSit();
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.isOrderedToSit();
    }

    @Override
    public void start() {
        if (mob.getPhase() == 2) {
            double currentX = mob.getX();
            double currentZ = mob.getZ();

            BlockPos groundPos = findClosestGroundBelow(mob);
            if (groundPos != null) {
                double y = groundPos.getY() + 1.0;
                mob.teleportTo(currentX, y, currentZ);
            }
        }

        this.mob.getNavigation().stop();
        this.mob.setInSittingPose(true);
    }

    @Override
    public void stop() {
        this.mob.setInSittingPose(false);
    }

    private BlockPos findClosestGroundBelow(TeddyEntity entity) {
        BlockPos start = entity.blockPosition();

        for (int i = 0; i < 20; i++) {
            BlockPos checkPos = start.below(i);
            if (entity.level().getBlockState(checkPos).blocksMotion()) {
                return checkPos;
            }
        }

        return null;
    }
}