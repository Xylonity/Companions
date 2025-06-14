package dev.xylonity.companions.common.entity.ai.minion.minion;

import dev.xylonity.companions.common.entity.ai.minion.AbstractMinionAttackGoal;
import dev.xylonity.companions.common.entity.custom.MinionEntity;
import dev.xylonity.companions.common.entity.projectile.TornadoProjectile;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class MinionAttackGoal extends AbstractMinionAttackGoal {

    public MinionAttackGoal(MinionEntity minion, int minCd, int maxCd) {
        super(minion, 10, minCd, maxCd);
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

            //if (tickCount <= 5) {
            double dx = target.getX() - minion.getX();
            double dz = target.getZ() - minion.getZ();

            float angle = (float)(Math.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0F;

            minion.setYRot(angle);

            minion.yBodyRot = angle;
            minion.yHeadRot = angle;
            minion.yRotO = angle;
            minion.yBodyRotO = angle;
            minion.yHeadRotO = angle;
            //}
        }

        if (attackTicks == attackDelay() && target != null && target.isAlive()) {
            performAttack(target);
        }

        attackTicks++;
    }

    @Override
    protected void performAttack(LivingEntity target) {
        if (minion.getOwner() != null) {
            TornadoProjectile tornadoProjectile = CompanionsEntities.TORNADO_PROJECTILE.get().create(this.minion.level());
            if (tornadoProjectile != null) {

                Vec3 startPos = this.minion.getEyePosition(1f);
                Vec3 targetPos = target.getEyePosition(1.0F);
                Vec3 direction = targetPos.subtract(startPos).normalize();

                Vec3 spawnPos = startPos.add(direction);

                tornadoProjectile.moveTo(spawnPos.x, spawnPos.y - 1, spawnPos.z);
                tornadoProjectile.setOwner(this.minion);
                this.minion.level().addFreshEntity(tornadoProjectile);
            }
        }
    }

    @Override
    protected int attackDelay() {
        return 5;
    }

    @Override
    protected String variant() {
        return MinionEntity.Variant.OVERWORLD.getName();
    }

}