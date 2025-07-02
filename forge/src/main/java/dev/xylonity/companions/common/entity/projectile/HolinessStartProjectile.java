package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.common.entity.BaseProjectile;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;
import java.util.Random;

public class HolinessStartProjectile extends BaseProjectile {

    private static final EntityDataAccessor<Boolean> IS_FIRE = SynchedEntityData.defineId(HolinessStartProjectile.class, EntityDataSerializers.BOOLEAN);

    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");

    public static final float SPEED = 0.425f;
    private LivingEntity target;

    public HolinessStartProjectile(EntityType<? extends BaseProjectile> type, Level level) {
        super(type, level);
    }

    public void setTarget(LivingEntity target) {
        this.target = target;
    }

    @Nullable
    protected EntityHitResult findHitEntity(Vec3 pStartVec, Vec3 pEndVec) {
        return ProjectileUtil.getEntityHitResult(this.level(), this, pStartVec, pEndVec, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0), this::canHitEntity);
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide && target != null && target.isAlive()) {
            Vec3 v = target.getEyePosition().subtract(position()).normalize().scale(SPEED);
            setDeltaMovement(getDeltaMovement().lerp(v, 0.04).normalize().scale(SPEED));
            hasImpulse = true;
        }

        this.move(MoverType.SELF, getDeltaMovement());

        if (!level().isClientSide) {
            Vec3 v = getDeltaMovement();
            setYRot((float)(Mth.atan2(v.x, v.z) * Mth.RAD_TO_DEG));
            setXRot((float)(Mth.atan2(v.y, v.horizontalDistance()) * Mth.RAD_TO_DEG));
        }

        if (tickCount % 8 == 0) {
            level().addParticle(CompanionsParticles.HOLINESS_STAR_TRAIL.get(), getX(), getY() - getBbHeight() * 0.5, getZ(), 0, 0, 0);
        }

        if (!this.onGround()) {
            Vec3 pos = this.position();
            Vec3 vec33 = pos.add(this.getDeltaMovement());
            HitResult hitresult = this.level().clip(new ClipContext(pos, vec33, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));

            if (hitresult.getType() != HitResult.Type.MISS) vec33 = hitresult.getLocation();

            while (!this.isRemoved()) {
                EntityHitResult entityhitresult = this.findHitEntity(pos, vec33);
                if (entityhitresult != null) {
                    hitresult = entityhitresult;
                }

                if (hitresult != null && hitresult.getType() == HitResult.Type.ENTITY) {
                    Entity entity = ((EntityHitResult) hitresult).getEntity();
                    Entity entity1 = this.getOwner();
                    if (entity instanceof Player && entity1 instanceof Player && !((Player)entity1).canHarmPlayer((Player)entity)) {
                        hitresult = null;
                        entityhitresult = null;
                    }
                }

                if (hitresult != null && hitresult.getType() != HitResult.Type.MISS) {
                    this.onHit(hitresult);
                    this.hasImpulse = true;
                }

                if (entityhitresult == null) break;

                hitresult = null;
            }

            this.checkInsideBlocks();
        }

        if (this.onGround()) discard();

        if (level().isClientSide) {
            if ((this.tickCount % 15 == 0 || this.tickCount == 1)) {
                for (int i = 0; i < 3; i++) {
                    float r = (190 + level().random.nextInt(30)) / 255f;
                    float g = (240 + level().random.nextInt(10)) / 255f;
                    float b = (247 + level().random.nextInt(5)) / 255f;
                    Util.spawnBaseProjectileTrail(
                            this,
                            this.getBbWidth() + level().random.nextFloat() * 0.4f,
                            (float) ((this.getY() + this.getBbHeight() * Math.random()) * 0.15f), r, g, b);
                }

            }
        }

    }

    @Override
    public void remove(@NotNull RemovalReason pReason) {
        if (!level().isClientSide) {
            RedStarExplosion explosion = CompanionsEntities.RED_STAR_EXPLOSION.get().create(level());
            if (explosion != null) {
                explosion.moveTo(position());
                level().addFreshEntity(explosion);
            }
        }

        super.remove(pReason);
    }

    public boolean isFire() {
        return this.entityData.get(IS_FIRE);
    }

    public void setIsFire(boolean isFire) {
        this.entityData.set(IS_FIRE, isFire);
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        Entity entity = pResult.getEntity();
        Entity owner = this.getOwner();
        DamageSource damageSource = this.damageSources().trident(this, (owner == null ? this : owner));
        entity.hurt(damageSource, 8.0F);
        this.playSound(SoundEvents.DRAGON_FIREBALL_EXPLODE, 5.0f, 1.0F);
        this.discard();
    }

    private void fireParticles() {
        for (int i = 0; i < 20; i++) {
            double vx = (level().random.nextDouble() - 0.5) * this.getBbWidth();
            double vy = (level().random.nextDouble() - 0.5) * this.getBbHeight();
            double vz = (level().random.nextDouble() - 0.5) * this.getBbWidth();
            if (level() instanceof ServerLevel level) {
                if (level.random.nextFloat() < 0.35)
                    level.sendParticles(ParticleTypes.POOF, getX(), getY(), getZ(), 1, vx, vy, vz, 0.15);
                if (level.random.nextFloat() < 0.8)
                    level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, getX(), getY(), getZ(), 1, vx, vy, vz, 0.15);
                level.sendParticles(ParticleTypes.LARGE_SMOKE, getX(), getY(), getZ(), 1, vx, vy, vz, 0.15);
                if (level.random.nextFloat() < 0.5)
                    level.sendParticles(ParticleTypes.LAVA, getX(), getY(), getZ(), 1, vx, vy, vz, 0.15);
            }
        }

        for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class, new AABB(blockPosition()).inflate(3))) {
            e.setSecondsOnFire(new Random().nextInt(4, 15));
        }
    }

    private void iceParticles() {
        for (int i = 0; i < 20; i++) {
            double vx = (level().random.nextDouble() - 0.5) * this.getBbWidth();
            double vy = (level().random.nextDouble() - 0.5) * this.getBbHeight();
            double vz = (level().random.nextDouble() - 0.5) * this.getBbWidth();
            if (level() instanceof ServerLevel level) {
                if (level.random.nextFloat() < 0.35)
                    level.sendParticles(ParticleTypes.POOF, getX(), getY(), getZ(), 1, vx, vy, vz, 0.2);
                if (level.random.nextFloat() < 0.8)
                    level.sendParticles(ParticleTypes.ITEM_SNOWBALL, getX(), getY(), getZ(), 1, vx, vy, vz, 0.15);
                level.sendParticles(ParticleTypes.CLOUD, getX(), getY(), getZ(), 1, vx, vy, vz, 0.2);
                level.sendParticles(ParticleTypes.SNOWFLAKE, getX(), getY(), getZ(), 1, vx, vy, vz, 0.25);
            }
        }

        for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class, new AABB(blockPosition()).inflate(3))) {
            e.setTicksFrozen(e.getTicksFrozen() + 400);
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_FIRE, level().random.nextBoolean());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("IsFire")) {
            this.setIsFire(pCompound.getBoolean("IsFire"));
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("IsFire", this.isFire());
    }

    @Override
    public void playerTouch(@NotNull Player pEntity) { ;; }

    @Override
    protected int baseLifetime() {
        return 200;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 2, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        event.setAnimation(IDLE);
        return PlayState.CONTINUE;
    }

    @Override
    public boolean shouldRender(double pX, double pY, double pZ) {
        return true;
    }

}