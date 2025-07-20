package dev.xylonity.companions.common.entity.ai.teddy.goal;

import dev.xylonity.companions.common.entity.companion.TeddyEntity;
import dev.xylonity.companions.config.CompanionsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.FlyNodeEvaluator;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class MutatedTeddyFollowOwnerGoal extends Goal {
    public static final int TELEPORT_WHEN_DISTANCE_IS = CompanionsConfig.COMPANIONS_FOLLOW_OWNER_TELEPORT_DISTANCE;

    private final double minDistance;
    private final double startDistance;
    private final double maxSpeedModifier;
    private final float lerpFactor;

    private final TeddyEntity teddy;
    private final PathNavigation navigation;

    private LivingEntity owner;

    public MutatedTeddyFollowOwnerGoal(TeddyEntity teddy, double maxSpeedModifier, double minDistance, double startDistance, float lerpFactor) {
        this.teddy = teddy;
        this.maxSpeedModifier = maxSpeedModifier;
        this.minDistance = minDistance;
        this.startDistance = startDistance;
        this.lerpFactor = lerpFactor;

        this.navigation = teddy.getNavigation();
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.teddy.getMainAction() != 1) return false;
        if (this.teddy.getPhase() != 2) return false;

        LivingEntity poss = this.teddy.getOwner();
        if (poss == null || !poss.isAlive()) return false;
        if (this.teddy.distanceToSqr(poss) < this.startDistance * this.startDistance) return false;
        this.owner = poss;

        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.teddy.getPhase() != 2) return false;
        if (this.owner == null) return false;
        if (!this.owner.isAlive()) return false;

        return this.teddy.distanceToSqr(this.owner) > this.minDistance * this.minDistance;
    }

    @Override
    public void tick() {
        if (this.owner == null) return;

        if (this.teddy.distanceToSqr(this.owner) >= TELEPORT_WHEN_DISTANCE_IS * TELEPORT_WHEN_DISTANCE_IS) {
            this.teleportToOwner();
        }

        teddy.lookAt(this.owner, 30f, 30f);

        double dx = this.owner.getX() - this.teddy.getX();
        double dy = (this.owner.getY() + 2.0) - this.teddy.getY();
        double dz = this.owner.getZ() - this.teddy.getZ();

        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (dist <= this.minDistance) {
            Vec3 slowed = this.teddy.getDeltaMovement().scale(0.9);
            this.teddy.setDeltaMovement(slowed);

            if (slowed.lengthSqr() < 0.01) {
                this.navigation.stop();
                this.teddy.getMoveControl().setWantedPosition(this.teddy.getX(), this.teddy.getY(), this.teddy.getZ(), 0);
            }

            this.teddy.noPhysics = false;
            return;
        }

        double theta = dist >= this.startDistance ? 1.0 : Mth.clamp((dist - this.minDistance) / (this.startDistance - this.minDistance), 0.0, 1.0);

        Vec3 target = new Vec3(this.owner.getX() - (dx/dist) * this.minDistance, (this.owner.getY() + 2) - (dy/dist) * this.minDistance, this.owner.getZ() - (dz/dist) * this.minDistance);
        Vec3 vel = target.subtract(this.teddy.position()).normalize().scale(this.maxSpeedModifier * theta);

        Vec3 cVel = this.teddy.getDeltaMovement();
        this.teddy.setDeltaMovement(new Vec3(Mth.lerp(this.lerpFactor, cVel.x, vel.x), Mth.lerp(this.lerpFactor, cVel.y, vel.y), Mth.lerp(this.lerpFactor, cVel.z, vel.z)));

        this.teddy.noPhysics = isPathBlocked(this.teddy.level(), this.teddy.getEyePosition(), target);
    }

    private boolean isPathBlocked(Level level, Vec3 from, Vec3 to) {
        return level.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.teddy)).getType() != HitResult.Type.MISS;
    }

    protected void teleportToOwner() {
        BlockPos pos = this.owner.blockPosition();

        for(int i = 0; i < 10; ++i) {
            int x = this.randomIntInclusive(-3, 3);
            int y = this.randomIntInclusive(-1, 1);
            int z = this.randomIntInclusive(-3, 3);
            if (this.maybeTeleportTo(pos.getX() + x, pos.getY() + y, pos.getZ() + z)) {
                return;
            }
        }

    }

    private boolean maybeTeleportTo(int pX, int pY, int pZ) {
        if (Math.abs(pX - this.owner.getX()) < 2.0F && Math.abs(pZ - this.owner.getZ()) < 2.0f) {
            return false;
        } else if (!this.canTeleportTo(new BlockPos(pX, pY, pZ))) {
            return false;
        } else {
            this.teddy.moveTo(pX + 0.5F, pY, pZ + 0.5F, this.teddy.getYRot(), this.teddy.getXRot());
            this.navigation.stop();
            return true;
        }

    }

    private boolean canTeleportTo(BlockPos pPos) {
        if (FlyNodeEvaluator.getBlockPathTypeStatic(this.teddy.level(), pPos.mutable()) != BlockPathTypes.WALKABLE) {
            return false;
        } else {
            if ( this.teddy.level().getBlockState(pPos.below()).getBlock() instanceof LeavesBlock) {
                return false;
            } else {
                return teddy.level().noCollision(this.teddy, this.teddy.getBoundingBox().move(pPos.subtract(this.teddy.blockPosition())));
            }
        }

    }

    private int randomIntInclusive(int pMin, int pMax) {
        return this.teddy.getRandom().nextInt(pMax - pMin + 1) + pMin;
    }

}
