package dev.xylonity.companions.common.entity.ai.cornelius.goal;

import dev.xylonity.companions.common.entity.companion.CorneliusEntity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;
import java.util.List;

public class CorneliusMoveToBeeGoal extends Goal {
    private final CorneliusEntity cornelius;
    private Bee bee;
    private final double speed;

    public CorneliusMoveToBeeGoal(CorneliusEntity cornelius, double speed) {
        this.cornelius = cornelius;
        this.speed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (cornelius.isTame()) return false;
        if (cornelius.level().getEntitiesOfClass(Player.class, cornelius.getBoundingBox().inflate(10)).isEmpty()) return false;
        List<Bee> bees = cornelius.level().getEntitiesOfClass(Bee.class, cornelius.getBoundingBox().inflate(10), EntitySelector.NO_SPECTATORS);
        if (bees.isEmpty()) return false;
        this.bee = bees.get(0);
        return this.bee.isAlive();
    }

    @Override
    public boolean canContinueToUse() {
        if (cornelius.level().getEntitiesOfClass(Player.class, cornelius.getBoundingBox().inflate(10)).isEmpty()) return false;
        return this.bee != null && this.bee.isAlive() && !cornelius.isVehicle();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (this.bee == null || !this.bee.isAlive()) return;

        cornelius.setTarget(bee);

        this.cornelius.getLookControl().setLookAt(this.bee, 30.0F, 30.0F);

        if (cornelius.getCycleCount() == -1) cornelius.setCycleCount(cornelius.getCycleCount() + 1);
        this.cornelius.getNavigation().moveTo(this.bee, this.speed);
    }

    @Override
    public void stop() {
        this.bee = null;
        this.cornelius.getNavigation().stop();
        super.stop();
    }

}
