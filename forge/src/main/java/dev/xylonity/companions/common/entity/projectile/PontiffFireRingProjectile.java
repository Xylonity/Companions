package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.common.entity.BaseProjectile;
import dev.xylonity.companions.common.entity.HostileEntity;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.config.CompanionsConfig;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoEntity;

public class PontiffFireRingProjectile extends BaseProjectile implements GeoEntity {

    public PontiffFireRingProjectile(EntityType<? extends BaseProjectile> pEntityType, Level pLevel) {
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

            for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class, box, e -> e.isAlive() && !Util.areEntitiesLinked(e, getOwner()))) {
                double dx = e.getX() - px;
                double dz = e.getZ() - pz;
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist > inner && dist <= outer) {
                    e.hurt(damageSources().magic(), (float) CompanionsConfig.FIRE_RING_DAMAGE);
                    e.setSecondsOnFire(level().getRandom().nextInt(1, 15));
                }
            }
        }

    }

    @Override
    protected int baseLifetime() {
        return 20;
    }

}
