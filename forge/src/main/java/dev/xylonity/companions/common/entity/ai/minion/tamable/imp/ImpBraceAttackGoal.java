package dev.xylonity.companions.common.entity.ai.minion.tamable.imp;

import dev.xylonity.companions.common.entity.ai.minion.tamable.AbstractMinionAttackGoal;
import dev.xylonity.companions.common.entity.companion.MinionEntity;
import dev.xylonity.companions.common.entity.projectile.BraceProjectile;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class ImpBraceAttackGoal extends AbstractMinionAttackGoal {

    public ImpBraceAttackGoal(MinionEntity minion, int minCd, int maxCd) {
        super(minion, 12, minCd, maxCd);
    }

    @Override
    public void start() {
        super.start();
        minion.setNoMovement(true);
    }

    @Override
    public void stop() {
        super.stop();
        minion.setNoMovement(false);
    }

    @Override
    public void tick() {
        LivingEntity target = minion.getTarget();
        if (target != null) {
            minion.getLookControl().setLookAt(target, 30.0F, 30.0F);

            double dx = target.getX() - minion.getX();
            double dz = target.getZ() - minion.getZ();

            float angle = (float)(Math.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0F;

            minion.setYRot(angle);

            minion.yBodyRot = angle;
            minion.yHeadRot = angle;
            minion.yRotO = angle;
            minion.yBodyRotO = angle;
            minion.yHeadRotO = angle;
        }

        super.tick();
    }

    @Override
    protected void performAttack(LivingEntity target) {
        if (minion.getOwner() != null) {
            BraceProjectile projectile = CompanionsEntities.BRACE_PROJECTILE.get().create(minion.level());
            if (projectile != null) {
                Vec3 spawnPos = minion.getEyePosition().add(minion.getLookAngle().scale(0.6));
                projectile.setPos(spawnPos.x, spawnPos.y, spawnPos.z);

                projectile.setOwner(minion.getOwner());

                Vec3 toPos = target.getEyePosition().subtract(spawnPos).normalize();

                double speed = 1;
                Vec3 initialVelocity = toPos.scale(speed).add(minion.getOwner().getDeltaMovement());
                projectile.setDeltaMovement(initialVelocity);

                minion.level().addFreshEntity(projectile);
            }
        }
    }

    @Override
    protected int attackDelay() {
        return 6;
    }

    @Override
    protected String variant() {
        return MinionEntity.Variant.NETHER.getName();
    }

    @Override
    protected int attackType() {
        return 1;
    }

}