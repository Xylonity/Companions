package dev.xylonity.companions.common.entity.ai.teddy.goal;

import dev.xylonity.companions.common.entity.companion.TeddyEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class MutatedTeddyRandomStrollGoal extends Goal {
    protected final TeddyEntity teddy;
    protected double wantedX, wantedY, wantedZ;
    protected final double speedModifier;
    protected int interval;
    protected boolean forceTrigger;
    private final boolean checkNoActionTime;
    private final float lerpFactor;

    private Vec3 targetPos;

    // adaptation of the CompanionRandomStroll goal
    public MutatedTeddyRandomStrollGoal(TeddyEntity teddy, double speedModifier, int interval, boolean checkNoActionTime, float lerpFactor) {
        this.teddy = teddy;
        this.speedModifier = speedModifier;
        this.interval = interval;
        this.checkNoActionTime = checkNoActionTime;
        this.lerpFactor = lerpFactor;
        this.forceTrigger = false;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public MutatedTeddyRandomStrollGoal(TeddyEntity teddy, double speedModifier) {
        this(teddy, speedModifier, 120, false, 0.1f);
    }

    @Override
    public boolean canUse() {
        if (teddy.getMainAction() != 2 || teddy.getPhase() != 2) return false;
        if (teddy.isVehicle()) return false;

        if (!forceTrigger) {
            if (checkNoActionTime && teddy.getNoActionTime() >= 100) return false;
            if (teddy.getRandom().nextInt(reducedTickDelay(interval)) != 0) return false;
        }

        Vec3 pos = getPosition();
        if (pos == null) return false;

        this.wantedX = pos.x;
        this.wantedY = pos.y;
        this.wantedZ = pos.z;
        this.forceTrigger = false;
        return true;
    }

    protected Vec3 getPosition() {
        Vec3 raw = DefaultRandomPos.getPos(teddy, 5, 1);
        if (raw == null) return null;

        BlockPos topP = teddy.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos((int) raw.x, (int) raw.y, (int) raw.z));

        return new Vec3(raw.x, topP.getY() + 1.0 + teddy.getRandom().nextDouble() * 2.0, raw.z);
    }

    @Override
    public void start() {
        this.targetPos = new Vec3(wantedX, wantedY, wantedZ);
        this.teddy.noPhysics = true;
    }

    @Override
    public boolean canContinueToUse() {
        if (teddy.getMainAction() != 2 || teddy.getPhase() != 2) return false;
        if (teddy.isVehicle() || targetPos == null) return false;
        double dx = targetPos.x - teddy.getX();
        double dy = targetPos.y - teddy.getY();
        double dz = targetPos.z - teddy.getZ();

        return ((dx * dx) + (dy * dy) + (dz * dz)) > 1.0;
    }

    @Override
    public void tick() {
        if (targetPos == null) return;

        teddy.lookAt(EntityAnchorArgument.Anchor.EYES, targetPos);

        Vec3 delta = targetPos.subtract(teddy.position());
        Vec3 dir = delta.scale(1.0 / delta.length());

        Vec3 vel = dir.scale(speedModifier);

        Vec3 cur = teddy.getDeltaMovement();
        double vx = net.minecraft.util.Mth.lerp(lerpFactor, cur.x, vel.x);
        double vy = net.minecraft.util.Mth.lerp(lerpFactor, cur.y, vel.y);
        double vz = net.minecraft.util.Mth.lerp(lerpFactor, cur.z, vel.z);

        teddy.setDeltaMovement(vx, vy, vz);
    }

    @Override
    public void stop() {
        teddy.noPhysics = false;
        this.targetPos = null;
    }

    public void trigger() {
        this.forceTrigger = true;
    }

}
