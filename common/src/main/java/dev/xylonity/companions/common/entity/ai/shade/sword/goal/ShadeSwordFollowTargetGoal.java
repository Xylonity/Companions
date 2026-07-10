package dev.xylonity.companions.common.entity.ai.shade.sword.goal;

import dev.xylonity.companions.common.entity.ShadeEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class ShadeSwordFollowTargetGoal extends Goal {

    private static final double MIN_RADIUS = 3.0;
    private static final double AMP_RADIUS = 2.0;
    private static final double RADIUS_FREQ = 0.45;
    private static final double ANG_SPEED = 0.12;

    private final ShadeEntity sword;
    private LivingEntity owner;
    private LivingEntity target;
    private double angle = 0.0;
    private double time  = 0.0;
    private final int dir;

    public ShadeSwordFollowTargetGoal(ShadeEntity sword) {
        this.sword = sword;
        this.dir = sword.getRandom().nextBoolean() ? 1 : -1;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean canUse() {
        if (sword.isSpawning()) return false;
        owner = sword.getOwner();
        target = (sword.getTarget() != null) ? sword.getTarget() : null;
        return owner != null && target != null && target.isAlive();
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void tick() {
        if (sword.shouldLookAtTarget()) sword.lookAt(target, 30f, 30f);

        Vec3 c = new Vec3(target.getX(),target.getY() + target.getBbHeight() * 0.5, target.getZ());

        time += 0.04;
        double r = MIN_RADIUS + AMP_RADIUS * 0.5 * (1 + Math.sin(time * RADIUS_FREQ * 2 * Math.PI));

        angle = (angle + ANG_SPEED * dir) % (Math.PI * 2);
        Vec3 lerped = c.add(r * Math.cos(angle), 0, r * Math.sin(angle));

        // Handles a lerping rotating movement around a certain target
        Vec3 nextPos = sword.position().lerp(lerped, 0.10);
        sword.setPos(nextPos.x, nextPos.y, nextPos.z);
        sword.setDeltaMovement(Vec3.ZERO);
    }

    @Override
    public void stop() {
        sword.setDeltaMovement(Vec3.ZERO);
        super.stop();
    }

}