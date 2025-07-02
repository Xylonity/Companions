package dev.xylonity.companions.common.entity.ai.cornelius.summon.goal;

import dev.xylonity.companions.common.entity.custom.TeddyEntity;
import dev.xylonity.companions.common.entity.summon.EnderFrogEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class EnderFrogFollowOwnerGoal extends Goal {

    private final double minDistance;
    private final double startDistance;
    private final double maxSpeedModifier;
    private final float lerpFactor;

    private final EnderFrogEntity frog;
    private final PathNavigation navigation;

    private LivingEntity owner;

    public EnderFrogFollowOwnerGoal(EnderFrogEntity frog, double maxSpeedModifier, double minDistance, double startDistance, float lerpFactor) {
        this.frog = frog;
        this.maxSpeedModifier = maxSpeedModifier;
        this.minDistance = minDistance;
        this.startDistance = startDistance;
        this.lerpFactor = lerpFactor;

        this.navigation = this.frog.getNavigation();
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity poss = this.frog.getOwner();
        if (poss == null || !poss.isAlive()) return false;
        if (this.frog.distanceToSqr(poss) < this.startDistance * this.startDistance) return false;
        this.owner = poss;

        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.owner == null) return false;
        if (!this.owner.isAlive()) return false;

        return this.frog.distanceToSqr(this.owner) > this.minDistance * this.minDistance;
    }

    @Override
    public void tick() {

        if (this.owner == null) return;

        frog.lookAt(this.owner, 30f, 30f);

        double dx = this.owner.getX() - this.frog.getX();
        double dy = (this.owner.getY() + 2.0) - this.frog.getY();
        double dz = this.owner.getZ() - this.frog.getZ();

        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (dist <= this.minDistance) {
            Vec3 slowed = this.frog.getDeltaMovement().scale(0.9);
            this.frog.setDeltaMovement(slowed);

            if (slowed.lengthSqr() < 0.01) {
                this.navigation.stop();
                this.frog.getMoveControl().setWantedPosition(this.frog.getX(), this.frog.getY(), this.frog.getZ(), 0);
            }

            return;
        }

        double theta = dist >= this.startDistance ? 1.0 : Mth.clamp((dist - this.minDistance) / (this.startDistance - this.minDistance), 0.0, 1.0);

        Vec3 target = new Vec3(this.owner.getX() - (dx/dist) * this.minDistance, (this.owner.getY() + 2) - (dy/dist) * this.minDistance, this.owner.getZ() - (dz/dist) * this.minDistance);
        Vec3 vel = target.subtract(this.frog.position()).normalize().scale(this.maxSpeedModifier * theta);

        Vec3 cVel = this.frog.getDeltaMovement();
        this.frog.setDeltaMovement(new Vec3(Mth.lerp(this.lerpFactor, cVel.x, vel.x), Mth.lerp(this.lerpFactor, cVel.y, vel.y), Mth.lerp(this.lerpFactor, cVel.z, vel.z)));
    }

    private boolean isPathBlocked(Level level, Vec3 from, Vec3 to) {
        return level.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.frog)).getType() != HitResult.Type.MISS;
    }

}
