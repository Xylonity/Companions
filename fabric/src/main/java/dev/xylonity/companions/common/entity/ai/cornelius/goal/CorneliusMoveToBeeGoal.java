package dev.xylonity.companions.common.entity.ai.cornelius.goal;

import dev.xylonity.companions.common.entity.CompanionEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.animal.Bee;

import java.util.EnumSet;
import java.util.List;

public class CorneliusMoveToBeeGoal extends Goal {
    private final CompanionEntity cornelius;
    private Bee bee;
    private final double speed;

    private int cycleCounter;
    private static final int MOVE_DURATION = 11;
    private static final int CYCLE_DURATION = 20;

    public CorneliusMoveToBeeGoal(CompanionEntity cornelius, double speed) {
        this.cornelius = cornelius;
        this.speed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE));
        this.cycleCounter = 0;
    }

    @Override
    public boolean canUse() {
        if (cornelius.isTame()) return false;
        List<Bee> bees = cornelius.level().getEntitiesOfClass(Bee.class, cornelius.getBoundingBox().inflate(20), EntitySelector.NO_SPECTATORS);
        if (bees.isEmpty()) return false;
        this.bee = bees.get(0);
        return this.bee.isAlive();
    }

    @Override
    public boolean canContinueToUse() {
        return this.bee != null && this.bee.isAlive() && !cornelius.isVehicle();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        this.cycleCounter = 0;
    }

    @Override
    public void tick() {
        if (this.bee == null || !this.bee.isAlive()) return;

        cornelius.setTarget(bee);

        this.cornelius.getLookControl().setLookAt(this.bee, 30.0F, 30.0F);

        if (this.cycleCounter < MOVE_DURATION) {
            this.cornelius.getNavigation().moveTo(this.bee, this.speed);
        } else {
            this.cornelius.getNavigation().stop();
        }

        this.cycleCounter++;
        if (this.cycleCounter >= CYCLE_DURATION) {
            this.cycleCounter = 0;
        }

    }

    @Override
    public void stop() {
        this.bee = null;
        this.cornelius.getNavigation().stop();
        super.stop();
    }

}
