package dev.xylonity.companions.common.entity.ai.generic;

import dev.xylonity.companions.common.entity.CompanionEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class CompanionRandomStrollGoal extends Goal {
    protected final CompanionEntity mob;
    protected double wantedX;
    protected double wantedY;
    protected double wantedZ;
    protected final double speedModifier;
    protected int interval;
    protected boolean forceTrigger;
    private boolean checkNoActionTime;

    public CompanionRandomStrollGoal(CompanionEntity pMob, double pSpeedModifier) {
        this(pMob, pSpeedModifier, 120);
        this.checkNoActionTime = false;
    }

    public CompanionRandomStrollGoal(CompanionEntity pMob, double pSpeedModifier, int pInterval) {
        this(pMob, pSpeedModifier, pInterval, true);
    }

    public CompanionRandomStrollGoal(CompanionEntity pMob, double pSpeedModifier, int pInterval, boolean pCheckNoActionTime) {
        this.mob = pMob;
        this.speedModifier = pSpeedModifier;
        this.interval = pInterval;
        this.checkNoActionTime = pCheckNoActionTime;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean canUse() {
        if (this.mob.getMainAction() != 2) return false;

        if (this.mob.isVehicle()) {
            return false;
        } else {
            if (!this.forceTrigger) {
                if (this.checkNoActionTime && this.mob.getNoActionTime() >= 100) {
                    return false;
                }

                if (this.mob.getRandom().nextInt(reducedTickDelay(this.interval)) != 0) {
                    return false;
                }
            }

            Vec3 $$0 = this.getPosition();
            if ($$0 == null) {
                return false;
            } else {
                this.wantedX = $$0.x;
                this.wantedY = $$0.y;
                this.wantedZ = $$0.z;
                this.forceTrigger = false;
                return true;
            }
        }
    }

    protected Vec3 getPosition() {
        return DefaultRandomPos.getPos(this.mob, 10, 7);
    }

    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone() && !this.mob.isVehicle();
    }

    public void start() {
        this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
    }

    public void stop() {
        this.mob.getNavigation().stop();
        super.stop();
    }

    public void trigger() {
        this.forceTrigger = true;
    }

    public void setInterval(int pNewchance) {
        this.interval = pNewchance;
    }
}
