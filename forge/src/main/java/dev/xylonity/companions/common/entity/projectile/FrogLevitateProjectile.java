package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.common.entity.BaseProjectile;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.mixin.CompanionsProjectileAccessor;
import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class FrogLevitateProjectile extends FrogHealProjectile {

    public FrogLevitateProjectile(EntityType<? extends BaseProjectile> type, Level level) {
        super(type, level);
    }

    public float getDefaultSpeed() {
        return 0.4f;
    }

    public float getDefaultLerp() {
        return 0.2f;
    }

    @Override
    protected boolean canHitEntity(@NotNull Entity pTarget) {
        if (Util.areEntitiesLinked(this, pTarget)) {
            return false;
        }

        if (!pTarget.canBeHitByProjectile()) {
            return false;
        } else {
            Entity entity = this.getOwner();
            return entity == null || ((CompanionsProjectileAccessor) this).getLeftOwner() || !entity.isPassengerOfSameVehicle(pTarget);
        }
    }

    @Override
    public void spawnParticles() {
        level().addParticle(ParticleTypes.END_ROD, getX(), getY() + getBbHeight() * 0.5, getZ(), 0, 0, 0);
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        Entity e = pResult.getEntity();
        if (e instanceof LivingEntity entity) {
            if (!entity.hasEffect(MobEffects.LEVITATION)) {
                entity.addEffect(new MobEffectInstance(MobEffects.LEVITATION, new Random().nextInt(100, 200), 1, true, true, true));
            }
            if (getOwner() != null && getOwner() instanceof LivingEntity owner) {
                owner.doHurtTarget(entity);
            }
        }

        for (int i = 0; i < 20; i++) {
            double dx = (this.random.nextDouble() - 0.5) * 2.0;
            double dy = (this.random.nextDouble() - 0.5) * 2.0;
            double dz = (this.random.nextDouble() - 0.5) * 2.0;
            if (this.level() instanceof ServerLevel level) {
                if (level.random.nextFloat() < 0.35f) level.sendParticles(ParticleTypes.POOF, e.getX(), e.getY() + e.getBbHeight() * 0.5, e.getZ(), 1, dx, dy, dz, 0.1);
                if (level.random.nextFloat() < 0.3f) level.sendParticles(CompanionsParticles.TEDDY_TRANSFORMATION.get(), e.getX(), e.getY() + e.getBbHeight() * 0.5, e.getZ(), 1, dx, dy, dz, 0.2);
            }
        }

        this.discard();
    }

    @Nullable
    @Override
    protected EntityHitResult findHitEntity(Vec3 pStartVec, Vec3 pEndVec) {
        return ProjectileUtil.getEntityHitResult(this.level(), this, pStartVec, pEndVec, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(0.5), this::canHitEntity);
    }

}