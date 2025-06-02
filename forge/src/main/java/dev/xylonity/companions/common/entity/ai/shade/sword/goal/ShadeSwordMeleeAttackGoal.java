package dev.xylonity.companions.common.entity.ai.shade.sword.goal;

import dev.xylonity.companions.common.entity.ShadeEntity;
import dev.xylonity.companions.common.entity.ai.shade.AbstractShadeAttackGoal;
import dev.xylonity.companions.common.entity.custom.ShadeSwordEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ShadeSwordMeleeAttackGoal extends AbstractShadeAttackGoal {

    private double anchorY;

    public ShadeSwordMeleeAttackGoal(ShadeEntity boss, int minCd, int maxCd) {
        super(boss, 40, minCd, maxCd);
    }

    @Override
    public void start() {
        super.start();
        shade.setNoMovement(true);
        anchorY = shade.getY();
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
        super.tick();

        if (attackTicks < 4) shade.setShouldLookAtTarget(false);
        if (attackTicks > attackDuration - 5) shade.setShouldLookAtTarget(true);

        LivingEntity tgt = shade.getTarget();
        if (tgt != null) {
            shade.setPos(shade.getX(), anchorY, shade.getZ());
            shade.setDeltaMovement(Vec3.ZERO);

            float yaw = (float)(180 + Math.toDegrees(Math.atan2(tgt.getX()-shade.getX(), tgt.getZ()-shade.getZ())));
            shade.setYRot(yaw);
            shade.setYBodyRot(yaw);
        }
    }

    @Override
    public boolean canUse() {
        return super.canUse() && shade.getTarget() != null && shade.distanceTo(shade.getTarget()) < 3 && isEntityInFront(shade, shade.getTarget(), 200);
    }

    @Override
    protected int getAttackType() {
        return 1;
    }

    @Override
    protected void performAttack(LivingEntity target) {
        for (Entity e : shade.level().getEntitiesOfClass(Entity.class, new AABB(shade.blockPosition()).inflate(3))) {
            if (e instanceof LivingEntity livingEntity && shade.hasLineOfSight(livingEntity) && isEntityInFront(shade, livingEntity, 150) && livingEntity != shade) {
                shade.doHurtTarget(livingEntity);
                livingEntity.knockback(0.5f, shade.getX() - target.getX(), shade.getZ() - target.getZ());
            }
        }

    }

    @Override
    protected int attackDelay() {
        return 10;
    }

    @Override
    protected Class<? extends ShadeEntity> shadeType() {
        return ShadeSwordEntity.class;
    }

}