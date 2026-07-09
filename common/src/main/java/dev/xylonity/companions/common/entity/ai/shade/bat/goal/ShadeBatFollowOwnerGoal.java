package dev.xylonity.companions.common.entity.ai.shade.bat.goal;

import dev.xylonity.companions.common.entity.companion.ShadeBatEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class ShadeBatFollowOwnerGoal extends Goal {

    private static final double COMBAT_BEHIND_MARGIN = 0.045;

    private final ShadeBatEntity shade;
    private LivingEntity owner;
    private LivingEntity combatTarget;
    private double orbitAngle;
    private int orbitDirection = 1;

    public ShadeBatFollowOwnerGoal(ShadeBatEntity shade) {
        this.shade = shade;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (shade.isSpawning()) {
            return false;
        }

        owner = shade.getOwner();
        return owner != null && !owner.isSpectator();
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        final LivingEntity target = shade.getShadeBatTarget();

        if (shade.isBlood()) {
            if (target != null) {
                tickOrbit(target);
            }
            else {
                combatTarget = null;
                tickOrbitWithTarget(owner);
            }

            return;
        }

        final LivingEntity anchor = target != null ? target : owner;
        final Vec3 targetPos = anchor.getEyePosition(1.0F).subtract(0, shade.getEyeHeight(), 0);
        final Vec3 movement = targetPos.subtract(shade.position());

        shade.setPos(targetPos.x, targetPos.y, targetPos.z);
        shade.setDeltaMovement(Vec3.ZERO);

        rotateToward(movement);
    }

    private void tickOrbit(LivingEntity target) {
        if (combatTarget != target) {
            combatTarget = target;
            orbitAngle = Math.atan2(shade.getZ() - target.getZ(), shade.getX() - target.getX());
            orbitDirection = shade.getRandom().nextBoolean() ? 1 : -1;
        }

        if (shade.getRandom().nextFloat() < 0.008f) {
            orbitDirection = -orbitDirection;
        }

        final Vec3 look = target.getLookAngle();
        final double behindAngle = Math.atan2(-look.z, -look.x);
        final double behindDrift = Math.atan2(Math.sin(behindAngle - orbitAngle), Math.cos(behindAngle - orbitAngle));

        orbitAngle += 0.07 * orbitDirection + Mth.clamp(behindDrift * 0.05, -COMBAT_BEHIND_MARGIN, COMBAT_BEHIND_MARGIN);

        final double radius = 2.4 + 1.2 * 0.5 * (1 + Math.sin(shade.tickCount * 0.045));
        final double y = target.getEyeY() - shade.getEyeHeight() + Math.sin(shade.tickCount * 0.09) * 0.4;

        final Vec3 desired = new Vec3(target.getX() + Math.cos(orbitAngle) * radius, y, target.getZ() + Math.sin(orbitAngle) * radius);
        final Vec3 nextPosition = shade.keepAboveTerrain(shade.position().lerp(desired, 0.1));

        shade.setPos(nextPosition.x, nextPosition.y, nextPosition.z);
        shade.setDeltaMovement(Vec3.ZERO);
        lookAtTarget(target);
    }

    private void tickOrbitWithTarget(LivingEntity anchor) {
        final Vec3 target = anchor.getEyePosition(1.0F).add(shade.batPartOffset(0, 0.0F)).subtract(0, shade.getEyeHeight(), 0);
        final Vec3 nextPosition = shade.keepAboveTerrain(shade.position().lerp(target, 0.14));
        final Vec3 movement = nextPosition.subtract(shade.position());

        shade.setPos(nextPosition.x, nextPosition.y, nextPosition.z);
        shade.setDeltaMovement(Vec3.ZERO);
        rotateToward(movement);
    }

    private void lookAtTarget(LivingEntity target) {
        final Vec3 toTarget = target.getBoundingBox().getCenter().subtract(shade.getEyePosition());
        final double horizontal = Math.sqrt(toTarget.x * toTarget.x + toTarget.z * toTarget.z);
        final float yaw = (float) Math.toDegrees(Math.atan2(toTarget.z, toTarget.x)) - 90F;
        final float pitch = (float) -Math.toDegrees(Math.atan2(toTarget.y, horizontal));

        shade.smoothRotate(yaw, pitch, 12.0F);
    }

    private void rotateToward(Vec3 movement) {
        if (movement.lengthSqr() > 1.0E-6) {
            final double horizontal = Math.sqrt(movement.x * movement.x + movement.z * movement.z);
            final float yaw = (float) (Math.atan2(movement.z, movement.x) * (180F / Math.PI)) - 90F;
            final float pitch = (float) (-(Math.atan2(movement.y, horizontal) * (180F / Math.PI)));

            shade.smoothRotate(yaw, pitch, 12.0F);
        }

    }

    @Override
    public void stop() {
        combatTarget = null;
        shade.setDeltaMovement(Vec3.ZERO);
        super.stop();
    }

}