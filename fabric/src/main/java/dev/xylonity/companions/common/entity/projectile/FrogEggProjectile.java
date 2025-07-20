package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.common.entity.BaseProjectile;
import dev.xylonity.companions.common.util.Util;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class FrogEggProjectile extends FrogLevitateProjectile {

    public FrogEggProjectile(EntityType<? extends BaseProjectile> type, Level level) {
        super(type, level);
    }

    public float getDefaultSpeed() {
        return 0.75f;
    }

    public float getDefaultLerp() {
        return 0.03f;
    }

    @Override
    public void spawnRibbon() {

    }

    @Override
    public void spawnParticles() {
        if (tickCount % new Random().nextInt(2, 4) == 0) {
            level().addParticle(ParticleTypes.END_ROD, getX(), getY() + getBbHeight() * 0.5, getZ(), 0, 0, 0);
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        Entity e = pResult.getEntity();
        if (e instanceof LivingEntity entity) {
            if (getOwner() != null && getOwner() instanceof LivingEntity owner && !Util.areEntitiesLinked(this, e)) {
                owner.doHurtTarget(entity);
            }
        }

        for (int i = 0; i < 6; i++) {
            double dx = (this.random.nextDouble() - 0.5) * 2.0;
            double dy = (this.random.nextDouble() - 0.5) * 2.0;
            double dz = (this.random.nextDouble() - 0.5) * 2.0;
            if (this.level() instanceof ServerLevel level) {
                if (level.random.nextFloat() < 0.45f) level.sendParticles(ParticleTypes.POOF, e.getX(), e.getY() + e.getBbHeight() * 0.5, e.getZ(), 1, dx, dy, dz, 0.1);
            }
        }

        this.discard();
    }

}