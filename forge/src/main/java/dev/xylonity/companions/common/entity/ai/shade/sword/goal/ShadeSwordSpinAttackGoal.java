package dev.xylonity.companions.common.entity.ai.shade.sword.goal;

import dev.xylonity.companions.common.entity.ShadeEntity;
import dev.xylonity.companions.common.entity.ai.shade.AbstractShadeAttackGoal;
import dev.xylonity.companions.common.entity.companion.ShadeSwordEntity;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ShadeSwordSpinAttackGoal extends AbstractShadeAttackGoal {

    private double anchorY;

    public ShadeSwordSpinAttackGoal(ShadeEntity boss, int minCd, int maxCd) {
        super(boss, 30, minCd, maxCd);
    }

    @Override
    public void start() {
        super.start();
        shade.setNoMovement(true);
        anchorY = shade.getY();
        shade.playSound(CompanionsSounds.SHADE_SWORD_SPIN_SLASH.get());
    }

    @Override
    public void stop() {
        super.stop();
        shade.setNoMovement(false);
        shade.setDeltaMovement(Vec3.ZERO);
        shade.setShouldLookAtTarget(true);
    }

    @Override
    public void tick() {
        if (attackTicks < 4) shade.setShouldLookAtTarget(false);

        LivingEntity target = shade.getTarget();
        if (target != null) {
            shade.setPos(shade.getX(), anchorY, shade.getZ());
            shade.setDeltaMovement(Vec3.ZERO);

            float yaw = (float) (180 + Math.toDegrees(Math.atan2(target.getX() - shade.getX(), target.getZ() - shade.getZ())));
            shade.setYRot(yaw);
            shade.setYBodyRot(yaw);
        }

        if (target != null) {
            shade.getLookControl().setLookAt(target, 30F, 30F);
        }

        if (attackTicks >= 5 && attackTicks <= 26 && attackTicks % 5 == 0 && target != null && target.isAlive()) {
            performAttack(target);
        }

        attackTicks++;
    }

    @Override
    public boolean canUse() {
        return super.canUse() && shade.getTarget() != null && shade.distanceTo(shade.getTarget()) < 3;
    }

    @Override
    protected int getAttackType() {
        return 4;
    }

    @Override
    protected void performAttack(LivingEntity target) {
        for (Entity e : shade.level().getEntitiesOfClass(Entity.class, shade.getBoundingBox().inflate(3.25))) {
            if (e instanceof LivingEntity livingEntity && shade.hasLineOfSight(livingEntity) && !Util.areEntitiesLinked(e, shade)) {
                shade.doHurtTarget(livingEntity);
                livingEntity.knockback(0.5f, shade.getX() - target.getX(), shade.getZ() - target.getZ());
            }
        }

    }

    @Override
    protected int attackDelay() {
        return 0;
    }

    @Override
    protected Class<? extends ShadeEntity> shadeType() {
        return ShadeSwordEntity.class;
    }

}