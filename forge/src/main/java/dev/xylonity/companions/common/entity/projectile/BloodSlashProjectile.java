package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.BaseProjectile;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.mixin.CompanionsProjectileAccessor;
import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BloodSlashProjectile extends FrogHealProjectile {

    public BloodSlashProjectile(EntityType<? extends BaseProjectile> type, Level level) {
        super(type, level);
    }

    public float getDefaultSpeed() {
        return (float) CompanionsConfig.BLOOD_SLASH_SPEED;
    }

    public float getDefaultLerp() {
        return 0f;
    }

    @Override
    public void spawnRibbon() {
        ;;
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide && (tickCount % 20 == 0 || tickCount == 1)) {
            spawnSidedRibbon();
        }

    }

    private void spawnSidedRibbon() {
        Vec3 dir = this.getDeltaMovement().normalize();

        if (dir.lengthSqr() < 1e-6) return;

        Vec3 perp = new Vec3(-dir.z, 0, dir.x).normalize().scale(1.5);
        double y = this.getY();

        Vec3 left = new Vec3(this.getX(), y, this.getZ()).subtract(perp);
        Companions.PROXY.spawnSidedRibbonTrail(this, level(), left.x, left.y, left.z, 145/255f, 20/255f, 20/255f, 0, 0.1725f, 0);

        Vec3 right = new Vec3(this.getX(), y, this.getZ()).add(perp);
        Companions.PROXY.spawnSidedRibbonTrail(this, level(), right.x, right.y, right.z, 145/255f, 20/255f, 20/255f, 0, 0.1725f, 1);
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

    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        Entity e = pResult.getEntity();
        if (e instanceof LivingEntity entity) {
            if (!Util.areEntitiesLinked(this, entity)) {
                entity.hurt(damageSources().magic(), (float) CompanionsConfig.BLOOD_SLASH_DAMAGE);
            }
        }

        this.remove(RemovalReason.KILLED);
    }

    @Override
    public void remove(@NotNull RemovalReason pReason) {
        if (!level().isClientSide) {
            spawnRemoveParticles();
            level().playSound(null, blockPosition(), SoundEvents.AMETHYST_BLOCK_HIT, SoundSource.NEUTRAL);
        }

        super.remove(pReason);
    }

    private void spawnRemoveParticles() {
        for (int i = 0; i < 15; i++) {
            double dx = (this.random.nextDouble() - 0.5) * 1.15;
            double dy = (this.random.nextDouble() - 0.5) * 1.15;
            double dz = (this.random.nextDouble() - 0.5) * 1.15;
            if (this.level() instanceof ServerLevel level) {
                level.sendParticles(ParticleTypes.POOF, this.getX(), this.getY() + getBbHeight() * Math.random(), this.getZ(), 1, dx, dy, dz, 0.1);
                if (level.random.nextFloat() < 0.35f) level.sendParticles(CompanionsParticles.SHADE_SUMMON.get(), this.getX(), this.getY() + getBbHeight() * Math.random(), this.getZ(), 1, dx, dy, dz, 0.1225);
            }
        }
    }

    @Nullable
    @Override
    protected EntityHitResult findHitEntity(Vec3 pStartVec, Vec3 pEndVec) {
        return ProjectileUtil.getEntityHitResult(this.level(), this, pStartVec, pEndVec, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(0.5), this::canHitEntity);
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult pResult) {
        super.onHitBlock(pResult);
        this.discard();
    }

}