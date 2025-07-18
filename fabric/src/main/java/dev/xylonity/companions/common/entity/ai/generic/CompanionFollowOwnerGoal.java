package dev.xylonity.companions.common.entity.ai.generic;

import dev.xylonity.companions.common.entity.CompanionEntity;
import dev.xylonity.companions.config.CompanionsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

import java.util.EnumSet;

public class CompanionFollowOwnerGoal extends Goal {
    public static final int TELEPORT_WHEN_DISTANCE_IS = CompanionsConfig.COMPANIONS_FOLLOW_OWNER_TELEPORT_DISTANCE;
    protected final CompanionEntity tamable;
    protected LivingEntity owner;
    protected final LevelReader level;
    protected final double speedModifier;
    protected final PathNavigation navigation;
    protected int timeToRecalcPath;
    protected final float stopDistance;
    protected final float startDistance;
    protected float oldWaterCost;
    protected final boolean canFly;

    public CompanionFollowOwnerGoal(CompanionEntity pTamable, double pSpeedModifier, float pStartDistance, float pStopDistance, boolean pCanFly) {
        this.tamable = pTamable;
        this.level = pTamable.level();
        this.speedModifier = pSpeedModifier;
        this.navigation = pTamable.getNavigation();
        this.startDistance = pStartDistance;
        this.stopDistance = pStopDistance;
        this.canFly = pCanFly;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public boolean canUse() {
        LivingEntity owner = this.tamable.getOwner();
        if (this.tamable.getMainAction() != 1) return false;
        if (owner == null) return false;
        if (owner.isSpectator()) return false;
        if (this.unableToMove()) return false;
        if (this.tamable.distanceToSqr(owner) < (double)(this.startDistance * this.startDistance)) return false;

        this.owner = owner;

        return true;
    }

    public boolean canContinueToUse() {
        if (this.navigation.isDone()) {
            return false;
        } else if (this.unableToMove()) {
            return false;
        } else {
            return !(this.tamable.distanceToSqr(this.owner) <= (double)(this.stopDistance * this.stopDistance));
        }
    }

    protected boolean unableToMove() {
        return this.tamable.isOrderedToSit() || this.tamable.isPassenger() || this.tamable.isLeashed();
    }

    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.tamable.getPathfindingMalus(BlockPathTypes.WATER);
        this.tamable.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    public void stop() {
        this.owner = null;
        this.navigation.stop();
        this.tamable.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
    }

    public void tick() {
        this.tamable.getLookControl().setLookAt(this.owner, 10.0f, (float)this.tamable.getMaxHeadXRot());
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            if (this.tamable.distanceToSqr(this.owner) >= TELEPORT_WHEN_DISTANCE_IS * TELEPORT_WHEN_DISTANCE_IS) {
                this.teleportToOwner();
            } else {
                this.navigation.moveTo(this.owner, this.speedModifier);
            }

        }
    }

    protected void teleportToOwner() {
        BlockPos pos = this.owner.blockPosition();

        for(int i = 0; i < 10; ++i) {
            int x = this.randomIntInclusive(-3, 3);
            int y = this.randomIntInclusive(-1, 1);
            int z = this.randomIntInclusive(-3, 3);
            if (this.maybeTeleportTo(pos.getX() + x, pos.getY() + y, pos.getZ() + z)) {
                return;
            }
        }

    }

    private boolean maybeTeleportTo(int pX, int pY, int pZ) {
        if (Math.abs(pX - this.owner.getX()) < 2.0F && Math.abs(pZ - this.owner.getZ()) < 2.0f) {
            return false;
        } else if (!this.canTeleportTo(new BlockPos(pX, pY, pZ))) {
            return false;
        } else {
            this.tamable.moveTo(pX + 0.5F, pY, pZ + 0.5F, this.tamable.getYRot(), this.tamable.getXRot());
            this.navigation.stop();
            return true;
        }

    }

    private boolean canTeleportTo(BlockPos pPos) {
        if (WalkNodeEvaluator.getBlockPathTypeStatic(this.level, pPos.mutable()) != BlockPathTypes.WALKABLE) {
            return false;
        } else {
            if (!this.canFly && this.level.getBlockState(pPos.below()).getBlock() instanceof LeavesBlock) {
                return false;
            } else {
                return this.level.noCollision(this.tamable, this.tamable.getBoundingBox().move(pPos.subtract(this.tamable.blockPosition())));
            }
        }

    }

    private int randomIntInclusive(int pMin, int pMax) {
        return this.tamable.getRandom().nextInt(pMax - pMin + 1) + pMin;
    }

}
