package dev.xylonity.companions.common.entity.ai.pontiff.goal;

import dev.xylonity.companions.common.entity.HostileEntity;
import dev.xylonity.companions.common.entity.ai.pontiff.AbstractSacredPontiffAttackGoal;
import dev.xylonity.companions.common.entity.hostile.SacredPontiffEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class PontiffDashAttackGoal extends AbstractSacredPontiffAttackGoal {
    private Vec3 dashVelocity = Vec3.ZERO;
    public PontiffDashAttackGoal(SacredPontiffEntity boss, int minCd, int maxCd) {
        super(boss, 50, minCd, maxCd);
    }

    @Override
    public void start() {
        super.start();
        pontiff.getNavigation().stop();
    }

    @Override
    public void stop() {
        super.stop();
        pontiff.setDeltaMovement(Vec3.ZERO);
        pontiff.hasImpulse = true;
        pontiff.setNoGravity(false);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && pontiff.getTarget() != null && pontiff.distanceTo(pontiff.getTarget()) < 12;
    }

    @Override
    protected int getAttackType() {
        return 4;
    }

    @Override
    public void tick() {
        super.tick();
        if (attackTicks == 20) {
            LivingEntity tgt = pontiff.getTarget();
            if (tgt != null) {
                Vec3 dir = tgt.position().subtract(pontiff.position());
                dir = new Vec3(dir.x, 0, dir.z);

                dashVelocity = dir.normalize().scale(8.0 / 30.0);
            }
        }

        if (attackTicks >= 20) {
            pontiff.setDeltaMovement(dashVelocity);
            pontiff.hasImpulse = true;
            pontiff.setNoGravity(true);
        }
    }

    @Override
    protected void performAttack(LivingEntity target) {
        for (Entity e : pontiff.level().getEntitiesOfClass(Entity.class,
                new AABB(new BlockPos((int) pontiff.getX(), (int) pontiff.getY(), (int) pontiff.getZ())).inflate(4),
                e -> !(e instanceof HostileEntity) )) {
            if (e instanceof LivingEntity livingEntity) {
                pontiff.doHurtTarget(livingEntity);
                livingEntity.knockback(0.75f, pontiff.getX() - livingEntity.getX(), pontiff.getZ() - livingEntity.getZ());
            }
        }

    }

    @Override
    protected int attackDelay() {
        return 32;
    }

}