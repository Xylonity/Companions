package dev.xylonity.companions.common.entity.ai.antlion.tamable.goal;

import dev.xylonity.companions.common.entity.companion.AntlionEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class AntlionAdultFollowOwnerGoal extends Goal {

    private final double minDistance;
    private final double startDistance;
    private final double maxSpeedModifier;
    private final float lerpFactor;

    private final AntlionEntity antlion;
    private final PathNavigation navigation;

    private LivingEntity owner;

    public AntlionAdultFollowOwnerGoal(AntlionEntity antlion, double maxSpeedModifier, double minDistance, double startDistance, float lerpFactor) {
        this.antlion = antlion;
        this.maxSpeedModifier = maxSpeedModifier;
        this.minDistance = minDistance;
        this.startDistance = startDistance;
        this.lerpFactor = lerpFactor;

        this.navigation = this.antlion.getNavigation();
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (antlion.getVariant() != 2) return false;
        if (antlion.getMainAction() != 1) return false;
        LivingEntity poss = this.antlion.getOwner();
        if (poss == null || !poss.isAlive()) return false;
        if (this.antlion.distanceToSqr(poss) < this.startDistance * this.startDistance) return false;
        this.owner = poss;

        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.owner == null) return false;
        if (!this.owner.isAlive()) return false;

        return this.antlion.distanceToSqr(this.owner) > this.minDistance * this.minDistance;
    }

    @Override
    public void tick() {

        if (this.owner == null) return;

        antlion.lookAt(this.owner, 30f, 30f);

        double dx = this.owner.getX() - this.antlion.getX();
        double dy = (this.owner.getY() + 2.0) - this.antlion.getY();
        double dz = this.owner.getZ() - this.antlion.getZ();

        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (dist <= this.minDistance) {
            Vec3 slowed = this.antlion.getDeltaMovement().scale(0.9);
            this.antlion.setDeltaMovement(slowed);

            if (slowed.lengthSqr() < 0.01) {
                this.navigation.stop();
                this.antlion.getMoveControl().setWantedPosition(this.antlion.getX(), this.antlion.getY(), this.antlion.getZ(), 0);
            }

            return;
        }

        double theta = dist >= this.startDistance ? 1.0 : Mth.clamp((dist - this.minDistance) / (this.startDistance - this.minDistance), 0.0, 1.0);

        Vec3 target = new Vec3(this.owner.getX() - (dx/dist) * this.minDistance, (this.owner.getY() + 2) - (dy/dist) * this.minDistance, this.owner.getZ() - (dz/dist) * this.minDistance);
        Vec3 vel = target.subtract(this.antlion.position()).normalize().scale(this.maxSpeedModifier * theta);

        Vec3 cVel = this.antlion.getDeltaMovement();
        this.antlion.setDeltaMovement(new Vec3(Mth.lerp(this.lerpFactor, cVel.x, vel.x), Mth.lerp(this.lerpFactor, cVel.y, vel.y), Mth.lerp(this.lerpFactor, cVel.z, vel.z)));

        this.antlion.noPhysics = isPathBlocked(this.antlion.level(), this.antlion.getEyePosition(), target);
    }

    private boolean isPathBlocked(Level level, Vec3 from, Vec3 to) {
        return level.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.antlion)).getType() != HitResult.Type.MISS;
    }

}
