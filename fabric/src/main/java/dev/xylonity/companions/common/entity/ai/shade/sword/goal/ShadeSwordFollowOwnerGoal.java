package dev.xylonity.companions.common.entity.ai.shade.sword.goal;

import dev.xylonity.companions.common.entity.ShadeEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class ShadeSwordFollowOwnerGoal extends Goal {

    private static final double ORBIT_RADIUS = 3.5;
    private static final float FLOAT_Y = 0.70f;

    private final ShadeEntity shade;
    private LivingEntity owner;
    private Vec3 center = null;
    private double angle = 0.0;

    public ShadeSwordFollowOwnerGoal(ShadeEntity shade) {
        this.shade = shade;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (shade.isSpawning()) return false;
        owner = shade.getOwner();
        return owner != null && !owner.isSpectator();
    }

    @Override
    public boolean canContinueToUse() { return canUse() && owner.getLastHurtMob() == null; }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override public void tick() {
        //if (sword.distanceToSqr(owner) > TELEPORT_DIST * TELEPORT_DIST) {
        //    //CompanionGoalUtil.teleportNear(owner, sword);
        //    center = null;
        //}

        if (center == null) {
            center = shade.position();
            angle = Math.atan2(shade.getZ() - center.z, shade.getX() - center.x);
        } else {
            center = center.lerp(new Vec3(owner.getX(),owner.getY()+owner.getBbHeight() * FLOAT_Y, owner.getZ()), 0.08);
        }

        // Kinda the same as the target follow goal but adapted to the player movement
        angle = (angle + (2 * Math.PI)/(7.5f * 20f)) % (Math.PI * 2);
        Vec3 nextPos = shade.position().lerp(center.add(ORBIT_RADIUS * Math.cos(angle), 0, ORBIT_RADIUS * Math.sin(angle)), 0.15);

        shade.setPos(nextPos.x, nextPos.y, nextPos.z);
        shade.setDeltaMovement(Vec3.ZERO);

        // The shade doesn't look at the owner per se, just at the center of the circle its rotating around
        float yaw = (float) Math.toDegrees(angle + Math.PI / 2);
        shade.setYRot(yaw);
        shade.setYBodyRot(yaw);
    }
}