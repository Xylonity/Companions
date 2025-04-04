package dev.xylonity.companions.common.entity.projectile.trigger;

import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult;

public class CakeCreamTriggerProjectile extends GenericTriggerProjectile {
    private int groundTicks = 0;
    private final float GRAVITY = 0.015f;

    public CakeCreamTriggerProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.onGround()) {
            Vec3 motion = this.getDeltaMovement();
            this.setDeltaMovement(motion.x * 0.98, motion.y - GRAVITY, motion.z * 0.98);
            this.move(MoverType.SELF, this.getDeltaMovement());
        } else {
            Projectile cakeCreamProjectile = CompanionsEntities.FLOOR_CAKE_CREAM.get().create(level());
            if (cakeCreamProjectile != null) {
                cakeCreamProjectile.moveTo(getX(), getY(), getZ());
                level().addFreshEntity(cakeCreamProjectile);
            }

            this.discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
    }
}