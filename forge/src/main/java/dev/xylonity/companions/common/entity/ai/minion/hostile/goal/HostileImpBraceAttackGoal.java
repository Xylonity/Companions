package dev.xylonity.companions.common.entity.ai.minion.hostile.goal;

import dev.xylonity.companions.common.entity.ai.minion.hostile.AbstractHostileImpAttackGoal;
import dev.xylonity.companions.common.entity.hostile.HostileImpEntity;
import dev.xylonity.companions.common.entity.projectile.BraceProjectile;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class HostileImpBraceAttackGoal extends AbstractHostileImpAttackGoal {

    public HostileImpBraceAttackGoal(HostileImpEntity imp, int minCd, int maxCd) {
        super(imp, 12, minCd, maxCd);
    }

    @Override
    public void start() {
        super.start();
        imp.setNoMovement(true);
    }

    @Override
    public void stop() {
        super.stop();
        imp.setNoMovement(false);
    }

    @Override
    public void tick() {
        LivingEntity target = imp.getTarget();
        if (target != null) {
            imp.getLookControl().setLookAt(target, 30.0F, 30.0F);

            double dx = target.getX() - imp.getX();
            double dz = target.getZ() - imp.getZ();

            float angle = (float)(Math.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0F;

            imp.setYRot(angle);

            imp.yBodyRot = angle;
            imp.yHeadRot = angle;
            imp.yRotO = angle;
            imp.yBodyRotO = angle;
            imp.yHeadRotO = angle;
        }

        super.tick();
    }

    @Override
    protected void performAttack(LivingEntity target) {
        if (imp.getTarget() != null) {
            BraceProjectile projectile = CompanionsEntities.BRACE_PROJECTILE.get().create(imp.level());
            if (projectile != null) {
                Vec3 spawnPos = imp.getEyePosition().add(imp.getLookAngle().scale(0.6));
                projectile.setPos(spawnPos.x, spawnPos.y, spawnPos.z);

                projectile.setOwner(imp);

                Vec3 initialVelocity = target.getEyePosition().subtract(spawnPos).normalize().scale(1.15).add(imp.getTarget().getDeltaMovement());
                projectile.setDeltaMovement(initialVelocity);

                imp.level().addFreshEntity(projectile);
            }
        }

    }

    @Override
    protected int attackDelay() {
        return 6;
    }

    @Override
    protected int attackType() {
        return 1;
    }

}