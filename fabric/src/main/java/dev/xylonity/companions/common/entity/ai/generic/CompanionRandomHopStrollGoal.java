package dev.xylonity.companions.common.entity.ai.generic;

import dev.xylonity.companions.common.entity.companion.CorneliusEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class CompanionRandomHopStrollGoal extends Goal {
    protected final CorneliusEntity mob;
    protected double wantedX;
    protected double wantedY;
    protected double wantedZ;
    protected final double speedModifier;
    protected int interval;
    protected boolean forceTrigger;
    private boolean checkNoActionTime;

    private int cycleCounter;
    private static final int MOVE_DURATION = 11;
    private static final int CYCLE_DURATION = 20;

    public CompanionRandomHopStrollGoal(CorneliusEntity pMob, double pSpeedModifier) {
        this(pMob, pSpeedModifier, 120);
        this.checkNoActionTime = false;
    }

    public CompanionRandomHopStrollGoal(CorneliusEntity pMob, double pSpeedModifier, int pInterval) {
        this(pMob, pSpeedModifier, pInterval, true);
    }

    public CompanionRandomHopStrollGoal(CorneliusEntity pMob, double pSpeedModifier, int pInterval, boolean pCheckNoActionTime) {
        this.mob = pMob;
        this.speedModifier = pSpeedModifier;
        this.interval = pInterval;
        this.checkNoActionTime = pCheckNoActionTime;
        this.setFlags(EnumSet.of(Flag.MOVE));
        this.cycleCounter = 0;
    }

    @Override
    public boolean canUse() {
        if (this.mob.getMainAction() != 2) return false;
        if (this.mob.isVehicle()) return false;

        if (!this.forceTrigger) {
            if (this.checkNoActionTime && this.mob.getNoActionTime() >= 100) {
                return false;
            }
            if (this.mob.getRandom().nextInt(reducedTickDelay(this.interval)) != 0) {
                return false;
            }
        }

        Vec3 pos = this.getPosition();
        if (pos == null) {
            return false;
        } else {
            this.wantedX = pos.x;
            this.wantedY = pos.y;
            this.wantedZ = pos.z;
            this.forceTrigger = false;
            return true;
        }
    }

    protected Vec3 getPosition() {
        return DefaultRandomPos.getPos(this.mob, 10, 7);
    }

    @Override
    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone() && !this.mob.isVehicle();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
        this.cycleCounter = 0;
    }

    @Override
    public void tick() {
        if (mob.getCycleCount() == -1) mob.setCycleCount(mob.getCycleCount() + 1);
        this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
    }

    @Override
    public void stop() {
        this.mob.getNavigation().stop();
        super.stop();
    }

    public void trigger() {
        this.forceTrigger = true;
    }

    public void setInterval(int pNewInterval) {
        this.interval = pNewInterval;
    }

}
