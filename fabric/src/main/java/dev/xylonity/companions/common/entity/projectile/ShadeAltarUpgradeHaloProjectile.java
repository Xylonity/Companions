package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.common.entity.BaseProjectile;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;

public class ShadeAltarUpgradeHaloProjectile extends BaseProjectile implements GeoEntity {

    public ShadeAltarUpgradeHaloProjectile(EntityType<? extends BaseProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void tick() {

        double px = getX();
        double py = getY();
        double pz = getZ();

        super.tick();

        this.setPos(px, py, pz);

    }

    @Override
    protected int baseLifetime() {
        return 16;
    }

}
