package dev.xylonity.companions.common.entity.ai.antlion.tamable.goal;

import dev.xylonity.companions.common.entity.companion.AntlionEntity;
import dev.xylonity.companions.config.CompanionsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.pathfinder.FlyNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class AntlionAdultFollowOwnerGoal extends Goal {
    public static final int TELEPORT_WHEN_DISTANCE_IS = CompanionsConfig.COMPANIONS_FOLLOW_OWNER_TELEPORT_DISTANCE;

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

        if (this.antlion.distanceToSqr(this.owner) >= TELEPORT_WHEN_DISTANCE_IS * TELEPORT_WHEN_DISTANCE_IS) {
            this.teleportToOwner();
        }

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
            this.antlion.moveTo(pX + 0.5F, pY, pZ + 0.5F, this.antlion.getYRot(), this.antlion.getXRot());
            this.navigation.stop();
            return true;
        }

    }

    private boolean canTeleportTo(BlockPos pPos) {
        if (FlyNodeEvaluator.getPathTypeStatic(this.antlion, pPos.mutable()) != PathType.WALKABLE) {
            return false;
        } else {
            if ( this.antlion.level().getBlockState(pPos.below()).getBlock() instanceof LeavesBlock) {
                return false;
            } else {
                return antlion.level().noCollision(this.antlion, this.antlion.getBoundingBox().move(pPos.subtract(this.antlion.blockPosition())));
            }
        }

    }

    private int randomIntInclusive(int pMin, int pMax) {
        return this.antlion.getRandom().nextInt(pMax - pMin + 1) + pMin;
    }

}
