package dev.xylonity.companions.common.entity.projectile.trigger;

import dev.xylonity.companions.common.entity.BaseProjectile;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;

public class GenericTriggerProjectile extends BaseProjectile implements GeoEntity {

    public GenericTriggerProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData() {
        ;;
    }

    @Override
    public void playerTouch(@NotNull Player pEntity) {
        ;;
    }

    @Override
    protected int baseLifetime() {
        return 100;
    }

}
