package dev.xylonity.companions.common.entity.ai.cornelius.goal;

import dev.xylonity.companions.common.entity.CompanionEntity;
import dev.xylonity.companions.common.entity.ai.generic.CompanionFollowOwnerGoal;
import dev.xylonity.companions.common.entity.companion.CorneliusEntity;
import dev.xylonity.companions.common.util.interfaces.IFrogJumpUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.pathfinder.PathType;

public class HopToOwnerGoal<T extends CompanionEntity & IFrogJumpUtil> extends CompanionFollowOwnerGoal {

    private int cycleCounter;

    public HopToOwnerGoal(T pTamable, double pSpeedModifier, float pStartDistance, float pStopDistance, boolean pCanFly) {
        super(pTamable, pSpeedModifier, pStartDistance, pStopDistance, pCanFly);
        this.cycleCounter = 0;
    }

    public boolean canUse() {
        LivingEntity owner = this.tamable.getOwner();
        if (this.tamable.getMainAction() != 1) return false;
        if (((IFrogJumpUtil) tamable).getAttackType() != 0) return false;
        if (owner == null) return false;
        if (owner.isSpectator()) return false;
        if (this.unableToMove()) return false;
        if (this.tamable.distanceToSqr(owner) < (double)(this.startDistance * this.startDistance)) return false;

        this.owner = owner;
        return true;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.tamable.getPathfindingMalus(PathType.WATER);
        ((IFrogJumpUtil) tamable).setCanAttack(false);
    }

    public void stop() {
        this.owner = null;
        this.navigation.stop();
        this.tamable.setPathfindingMalus(PathType.WATER, this.oldWaterCost);
        ((IFrogJumpUtil) tamable).setCanAttack(true);
    }

    public void tick() {
        this.tamable.getLookControl().setLookAt(this.owner, 10.0F, (float)this.tamable.getMaxHeadXRot());
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            if (this.tamable.distanceToSqr(this.owner) >= TELEPORT_WHEN_DISTANCE_IS * TELEPORT_WHEN_DISTANCE_IS) {
                this.teleportToOwner();
            } else {
                if (((CorneliusEntity) tamable).getCycleCount() == -1) ((CorneliusEntity) tamable).setCycleCount(((CorneliusEntity) tamable).getCycleCount() + 1);
                this.navigation.moveTo(this.owner, this.speedModifier);
            }

        }

    }

}