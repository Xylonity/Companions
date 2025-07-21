package dev.xylonity.companions.common.entity.projectile.trigger;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.BaseProjectile;
import dev.xylonity.companions.common.entity.companion.MankhEntity;
import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LaserTriggerProjectile extends BaseProjectile {

    private static final EntityDataAccessor<Integer> TARGET_ID = SynchedEntityData.defineId(LaserTriggerProjectile.class, EntityDataSerializers.INT);
    public LivingEntity target;

    public LaserTriggerProjectile(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
    }

    public void setTarget(LivingEntity target) {
        this.target = target;
        this.entityData.set(TARGET_ID, target.getId());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TARGET_ID, -1);
    }

    public int getTargetId() {
        return this.entityData.get(TARGET_ID);
    }

    @Override
    protected int baseLifetime() {
        return 60;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getOwner() instanceof MankhEntity mankh) {
            if (target == null && !level().isClientSide) {
                int id = this.entityData.get(TARGET_ID);
                Entity e = level().getEntity(id);
                if (e instanceof LivingEntity) {
                    target = (LivingEntity) e;
                }
            }

            if (target != null) {
                Vec3 mankhPos = mankh.getEyePosition();
                Vec3 targetPos = target.getEyePosition();
                Vec3 directionToTarget = targetPos.subtract(mankhPos).normalize();

                double dx = directionToTarget.x;
                double dy = directionToTarget.y;
                double dz = directionToTarget.z;

                float yaw = (float) (Math.atan2(-dx, dz) * 180.0 / Math.PI);
                float pitch = (float) (Math.asin(-dy) * 180.0 / Math.PI);

                mankh.setYRot(yaw);
                mankh.setXRot(pitch);

                Vec3 alpha = mankh.position().add(0, mankh.getBbHeight() * 0.5, 0).add(directionToTarget.scale(0.05));
                this.moveTo(alpha.x, alpha.y, alpha.z);
                this.setYRot(yaw);
                this.setXRot(pitch);
            }
        }

        if (!level().isClientSide && target != null) {
            Vec3 start = this.position();
            Vec3 targetPos = target.getEyePosition();

            BlockHitResult hit = level().clip(new ClipContext(start, targetPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            Vec3 end = (hit.getType() == BlockHitResult.Type.BLOCK && !level().getBlockState(hit.getBlockPos()).getCollisionShape(level(), hit.getBlockPos()).isEmpty()) ? hit.getLocation() : targetPos;

            AABB bb = new AABB(start, end).inflate(0.25);
            List<LivingEntity> entities = level().getEntitiesOfClass(LivingEntity.class, bb, e -> e != getOwner());

            for (LivingEntity e : entities) {
                if (getOwner() instanceof LivingEntity le) {
                    le.doHurtTarget(e);
                    e.setDeltaMovement(e.getDeltaMovement().multiply(0.05, 0.5, 0.05));
                }
            }
        }

        if (level().isClientSide) {
            Companions.PROXY.tickLaserTriggerProjectile(this);
        }

    }

    public void spawnSparks(Level level) {
        double y = this.position().y + getBbHeight() * 0.5f;
        for (int i = 0; i < 360; i += 120) {
            double angleRadians = Math.toRadians(i);
            double x = this.position().x + 0.35 * Math.cos(angleRadians);
            double z = this.position().z + 0.35 * Math.sin(angleRadians);
            level.addParticle(CompanionsParticles.LASER_SPARK.get(), x, y, z, 0d, 0.15d, 0d);
        }
    }

}