package dev.xylonity.companions.common.entity.ai.pontiff.goal;

import dev.xylonity.companions.common.entity.HostileEntity;
import dev.xylonity.companions.common.entity.ai.pontiff.AbstractSacredPontiffAttackGoal;
import dev.xylonity.companions.common.entity.hostile.SacredPontiffEntity;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class PontiffDashAttackGoal extends AbstractSacredPontiffAttackGoal {

    private Vec3 dashVelocity = Vec3.ZERO;
    private boolean isDashing = false;

    public PontiffDashAttackGoal(SacredPontiffEntity boss, int minCd, int maxCd) {
        super(boss, 50, minCd, maxCd);
    }

    @Override
    public void start() {
        super.start();
        pontiff.getNavigation().stop();
        isDashing = false;
        pontiff.playSound(CompanionsSounds.PONTIFF_AREA_ATTACK.get());
    }

    @Override
    public void stop() {
        super.stop();
        pontiff.setDeltaMovement(Vec3.ZERO);
        pontiff.hasImpulse = true;
        pontiff.setNoGravity(false);
        isDashing = false;
    }

    @Override
    public boolean canUse() {
        return super.canUse() && pontiff.getTarget() != null && pontiff.distanceTo(pontiff.getTarget()) < 10 && pontiff.hasLineOfSight(pontiff.getTarget());
    }

    @Override
    protected int getAttackType() {
        return 4;
    }

    @Override
    public void tick() {
        super.tick();
        if (attackTicks == 20 && !isDashing) {
            LivingEntity tgt = pontiff.getTarget();
            if (tgt != null) {
                Vec3 dashTargetPos = tgt.position();
                Vec3 dir = dashTargetPos.subtract(pontiff.position());
                dir = new Vec3(dir.x, 0, dir.z);
                dashVelocity = dir.normalize().scale(8.0 / 30.0);
                isDashing = true;
                pontiff.setNoGravity(true);
            }
        }

        if (isDashing && attackTicks >= 20 && attackTicks <= 50) {
            double groundY = pontiff.getY();
            BlockPos base = new BlockPos((int) pontiff.getX(), (int) (pontiff.getY() - 0.05), (int) pontiff.getZ());
            for (int i = 0; i < 3; i++) {
                BlockPos check = base.below(i);
                if (!pontiff.level().getBlockState(check).getCollisionShape(pontiff.level(), check).isEmpty()) {
                    groundY = check.getY() + 1.0;
                    break;
                }
            }

            pontiff.setPos(pontiff.getX(), groundY, pontiff.getZ());

            pontiff.setDeltaMovement(dashVelocity.x, 0, dashVelocity.z);
            pontiff.hasImpulse = true;

            float yaw = (float)(Math.toDegrees(Math.atan2(dashVelocity.z, dashVelocity.x)) - 90f);
            pontiff.setYRot(yaw);
            pontiff.yBodyRot = yaw;
            pontiff.yHeadRot = yaw;
        }

        if (isDashing && attackTicks > 50) {
            pontiff.setNoGravity(false);
            isDashing = false;
        }

    }

    @Override
    protected void performAttack(LivingEntity target) {
        for (Entity e : pontiff.level().getEntitiesOfClass(Entity.class,
                new AABB(new BlockPos((int) pontiff.getX(), (int) pontiff.getY(), (int) pontiff.getZ())).inflate(5),
                e -> !(e instanceof HostileEntity) )) {
            if (e instanceof LivingEntity livingEntity && pontiff.hasLineOfSight(livingEntity) && isEntityInFront(pontiff, livingEntity, 220)) {
                pontiff.doHurtTarget(livingEntity);
                livingEntity.knockback(0.75f, pontiff.getX() - livingEntity.getX(), pontiff.getZ() - livingEntity.getZ());
            }
        }

    }

    @Override
    protected int attackDelay() {
        return 32;
    }

    @Override
    protected int phase() {
        return 1;
    }

}