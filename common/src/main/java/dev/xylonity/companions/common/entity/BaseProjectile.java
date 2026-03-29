package dev.xylonity.companions.common.entity;

import dev.xylonity.knightlib.common.entity.AbstractProjectile;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

public abstract class BaseProjectile extends AbstractProjectile {

    public BaseProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

}
