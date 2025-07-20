package dev.xylonity.companions.common.entity.ai.cornelius.summon.goal;

import dev.xylonity.companions.common.entity.SummonFrogEntity;
import dev.xylonity.companions.common.entity.CompanionSummonEntity;
import dev.xylonity.companions.common.entity.ai.generic.CompanionSummonFollowOwnerGoal;
import dev.xylonity.companions.common.util.interfaces.IFrogJumpUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public class SummonHopToOwnerGoal<T extends CompanionSummonEntity & IFrogJumpUtil> extends CompanionSummonFollowOwnerGoal {

    private int cycleCounter;

    public SummonHopToOwnerGoal(T pTamable, double pSpeedModifier, float pStartDistance, float pStopDistance, boolean pCanFly) {
        super(pTamable, pSpeedModifier, pStartDistance, pStopDistance, pCanFly);
        this.cycleCounter = 0;
    }

    public boolean canUse() {
        LivingEntity owner = this.tamable.getOwner();
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
        this.oldWaterCost = this.tamable.getPathfindingMalus(BlockPathTypes.WATER);
        ((IFrogJumpUtil) tamable).setCanAttack(false);
    }

    public void stop() {
        this.owner = null;
        this.navigation.stop();
        this.tamable.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
        ((IFrogJumpUtil) tamable).setCanAttack(true);
    }

    public void tick() {
        this.tamable.getLookControl().setLookAt(this.owner, 10.0F, (float)this.tamable.getMaxHeadXRot());
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            if (this.tamable.distanceToSqr(this.owner) >= TELEPORT_WHEN_DISTANCE_IS * TELEPORT_WHEN_DISTANCE_IS) {
                this.teleportToOwner();
            } else {
                if (((SummonFrogEntity) tamable).getCycleCount() == -1) ((SummonFrogEntity) tamable).setCycleCount(((SummonFrogEntity) tamable).getCycleCount() + 1);
                this.navigation.moveTo(this.owner, this.speedModifier);
            }

        }

        cycleCounter++;

        if (cycleCounter >= 20) cycleCounter = 0;
    }

}
