package dev.xylonity.companions.common.entity.ai.shade.bat.goal;

import dev.xylonity.companions.common.entity.ShadeEntity;
import dev.xylonity.companions.common.entity.ai.shade.AbstractShadeAttackGoal;
import dev.xylonity.companions.common.entity.companion.ShadeBatEntity;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

public class ShadeBatBloodAttackGoal extends AbstractShadeAttackGoal {

    // Permutes between 2 different attack types in the same goal
    public static final int TYPE_ATTACK = 1;
    public static final int TYPE_STING = 2;

    private static final double SWOOP_HIT_RADIUS = 1.6;
    private static final double SWOOP_OVERSHOOT = 3.0;
    private static final int SWOOP_DIVE_START = 9;

    private Vec3 swoopStart;
    private Vec3 swoopFocus;
    private Vec3 swoopEnd;
    private boolean swoopHit;
    private Vec3 retreatDirection;

    private final ShadeBatEntity bat;
    private final int type;

    public ShadeBatBloodAttackGoal(ShadeBatEntity bat, int type, int minCd, int maxCd) {
        super(bat, type == TYPE_STING ? 23 : 20, minCd, maxCd);
        this.bat = bat;
        this.type = type;
    }

    @Override
    public boolean canUse() {
        if (!bat.isBlood() || !super.canUse() || bat.getTarget() == null) {
            return false;
        }

        return bat.distanceTo(bat.getTarget()) < (type == TYPE_STING ? 4.5 : 9);
    }

    @Override
    public void start() {
        super.start();
        swoopStart = null;
        swoopFocus = null;
        swoopEnd = null;
        swoopHit = false;
        retreatDirection = null;
        if (type == TYPE_STING) {
            bat.playSound(CompanionsSounds.SHADE_IDLE.get());
        }

    }

    @Override
    public void stop() {
        super.stop();
        bat.setDeltaMovement(Vec3.ZERO);
    }

    @Override
    public void tick() {
        super.tick();

        final LivingEntity target = bat.getTarget();

        if (type == TYPE_STING) {
            tickSting(target);
        }
        else {
            tickSwoop(target);
        }

        bat.setDeltaMovement(Vec3.ZERO);
    }

    private void tickSwoop(LivingEntity target) {
        if (attackTicks < SWOOP_DIVE_START) {
            if (target == null) return;

            final Vec3 away = horizontalAway(target);
            final Vec3 perch = target.position().add(away.scale(3)).add(0, target.getBbHeight() + 2.5, 0);
            final Vec3 next = bat.keepAboveTerrain(bat.position().lerp(perch, 0.25));

            bat.setPos(next.x, next.y, next.z);
            faceTarget(target);

            return;
        }

        if (swoopStart == null) {
            swoopStart = bat.position();
            swoopFocus = swoopFocus(target);

            Vec3 flat = new Vec3(swoopFocus.x - swoopStart.x, 0, swoopFocus.z - swoopStart.z);
            flat = flat.lengthSqr() < 1.0E-4 ? horizontalAwayFromYaw().scale(-1) : flat.normalize();
            swoopEnd = swoopFocus.add(flat.scale(SWOOP_OVERSHOOT)).add(0, 1.2, 0);
        }

        // Reaims the target
        if (target != null && target.isAlive()) {
            swoopFocus = swoopFocus(target);
        }

        // Arc so the swoop passes through the target
        final Vec3 control = swoopFocus.scale(2.0).subtract(swoopStart.add(swoopEnd).scale(0.5));

        final double raw = Math.min(1.0, (attackTicks - SWOOP_DIVE_START + 1) / (double) (attackDuration - SWOOP_DIVE_START + 1));
        final double time = 1.0 - (1.0 - raw) * (1.0 - raw);
        final Vec3 next = bat.keepAboveTerrain(swoopStart.lerp(control, time).lerp(control.lerp(swoopEnd, time), time));
        final Vec3 movement = next.subtract(bat.position());

        bat.setPos(next.x, next.y, next.z);
        faceMovement(movement);

        if (!swoopHit && target != null && target.isAlive() && bat.distanceTo(target) < SWOOP_HIT_RADIUS) {
            performSwoopHit(target);
        }

    }

    /**
     * Punches the target and then flies back
     */
    private void tickSting(LivingEntity target) {
        if (attackTicks <= attackDelay()) {
            if (target == null) {
                return;
            }

            final Vec3 nextPosition = bat.keepAboveTerrain(bat.position().lerp(target.position().add(0, target.getBbHeight() * 0.6, 0), 0.25));
            bat.setPos(nextPosition.x, nextPosition.y, nextPosition.z);
            faceTarget(target);

            return;
        }

        if (retreatDirection == null) {
            retreatDirection = target != null ? computeRetreatDirection(target) : bat.getLookAngle().scale(-1).add(0, 0.55, 0).normalize();
        }

        final double progress = Math.min(1.0, (attackTicks - attackDelay()) / (double) (attackDuration - attackDelay()));
        final Vec3 nextPosition = bat.keepAboveTerrain(bat.position().add(retreatDirection.scale(0.55 * (1.0 - 0.6 * progress))));

        bat.setPos(nextPosition.x, nextPosition.y, nextPosition.z);

        if (target != null) {
            faceTarget(target);
        }

    }

    @Override
    protected void performAttack(LivingEntity target) {
        if (type == TYPE_STING) {
            if (bat.distanceTo(target) <= 3) {
                bat.playSound(CompanionsSounds.SHADE_MAW_BITE.get());

                // Stings the target
                final float damage = (float) bat.getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.25f;
                if (target.hurt(bat.damageSources().mobAttack(bat), damage)) {
                    bat.heal(damage * 0.5f);
                    target.addEffect(new MobEffectInstance(MobEffects.WITHER, bat.getRandom().nextInt(40, 120), 0), bat);
                    bat.startFrenzy();
                }

            }

            retreatDirection = computeRetreatDirection(target);
        }
        else if (!swoopHit && bat.distanceTo(target) < SWOOP_HIT_RADIUS + 1.0) {
            // fallback in case the contact fails
            performSwoopHit(target);
        }

    }

    private void performSwoopHit(LivingEntity target) {
        swoopHit = true;
        bat.playSound(CompanionsSounds.SHADE_MAW_BITE.get());

        float damage = (float) bat.getAttributeValue(Attributes.ATTACK_DAMAGE);
        if (bat.isFrenzied()) {
            damage *= 1.25f;
        }

        if (target.hurt(bat.damageSources().mobAttack(bat), damage)) {
            final Vec3 direction = swoopEnd != null ? swoopEnd.subtract(swoopStart) : target.position().subtract(bat.position());
            target.knockback(0.45f, -direction.x, -direction.z);
        }

    }

    private Vec3 computeRetreatDirection(LivingEntity target) {
        final Vec3 away = horizontalAway(target);
        return new Vec3(away.x, 0.55, away.z).normalize();
    }

    private Vec3 swoopFocus(LivingEntity target) {
        if (target == null) {
            return swoopStart.add(bat.getLookAngle().scale(SWOOP_OVERSHOOT));
        }

        return target.getBoundingBox().getCenter().subtract(0, bat.getBbHeight() * 0.5, 0);
    }

    private Vec3 horizontalAway(LivingEntity target) {
        final Vec3 away = new Vec3(bat.getX() - target.getX(), 0, bat.getZ() - target.getZ());
        return away.lengthSqr() < 1.0E-4 ? horizontalAwayFromYaw() : away.normalize();
    }

    private Vec3 horizontalAwayFromYaw() {
        final float yaw = (float) Math.toRadians(bat.getYRot());
        return new Vec3(Math.sin(yaw), 0, -Math.cos(yaw));
    }

    private void faceTarget(LivingEntity target) {
        final Vec3 toTarget = target.getBoundingBox().getCenter().subtract(bat.getEyePosition());
        final double horizontal = Math.sqrt(toTarget.x * toTarget.x + toTarget.z * toTarget.z);
        final float yaw = (float) Math.toDegrees(Math.atan2(toTarget.z, toTarget.x)) - 90F;
        final float pitch = (float) -Math.toDegrees(Math.atan2(toTarget.y, horizontal));

        bat.smoothRotate(yaw, pitch, 20.0F);
    }

    private void faceMovement(Vec3 movement) {
        if (movement.lengthSqr() < 1.0E-6) {
            return;
        }

        final double horizontal = Math.sqrt(movement.x * movement.x + movement.z * movement.z);
        final float yaw = (float) Math.toDegrees(Math.atan2(movement.z, movement.x)) - 90F;
        final float pitch = (float) -Math.toDegrees(Math.atan2(movement.y, horizontal));

        bat.smoothRotate(yaw, pitch, 35.0F);
    }

    @Override
    protected int attackDelay() {
        // the sting pierces at 0.54
        return type == TYPE_STING ? 11 : 12;
    }

    @Override
    protected int getAttackType() {
        return type;
    }

    @Override
    protected Class<? extends ShadeEntity> shadeType() {
        return ShadeBatEntity.class;
    }

}