package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.BaseProjectile;
import dev.xylonity.companions.common.util.Util;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.Random;

public class LaserRingProjectile extends BaseProjectile {

    public LaserRingProjectile(EntityType<? extends BaseProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void tick() {

        double px = getX();
        double py = getY();
        double pz = getZ();

        super.tick();

        this.setPos(px, py, pz);

        if (tickCount % 2 == 0 && !this.level().isClientSide) {
            int radius = tickCount / 2 + 1;
            double inner = radius - 1;
            double outer = radius + 1;
            AABB box = new AABB(
                    px - outer, py - 1.0, pz - outer,
                    px + outer, py + 1.0, pz + outer
            );

            for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class, box)) {
                double dx = e.getX() - px;
                double dz = e.getZ() - pz;
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist > inner && dist <= outer && getOwner() != null && !Util.areEntitiesLinked(getOwner(), e)) {
                    if (getOwner() != null && getOwner() instanceof LivingEntity owner) {
                        owner.doHurtTarget(e);
                    }

                    e.setSecondsOnFire(level().getRandom().nextInt(1, 15));
                }
            }
        }

        if (level().isClientSide && new Random().nextFloat() < 0.3f) {
            Companions.PROXY.spawnLaserRingElectricArc(this, level(), 7);
        }

    }

    @Override
    protected int baseLifetime() {
        return 20;
    }

}
