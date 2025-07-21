package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.BaseProjectile;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.mixin.CompanionsProjectileAccessor;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.*;

import javax.annotation.Nullable;
import java.util.Random;

public class HolinessStartProjectile extends BaseProjectile {

    private static final EntityDataAccessor<Boolean> RED = SynchedEntityData.defineId(HolinessStartProjectile.class, EntityDataSerializers.BOOLEAN);

    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");

    public static float SPEED = 0.6f;

    private LivingEntity target;

    public HolinessStartProjectile(EntityType<? extends BaseProjectile> type, Level level) {
        super(type, level);
    }

    public void setTarget(LivingEntity target) {
        this.target = target;
    }

    @Nullable
    protected EntityHitResult findHitEntity(Vec3 pStartVec, Vec3 pEndVec) {
        return ProjectileUtil.getEntityHitResult(this.level(), this, pStartVec, pEndVec, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1), this::canHitEntity);
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
    public void tick() {
        super.tick();

        if (!level().isClientSide && target != null && target.isAlive()) {
            Vec3 v = target.getEyePosition().subtract(position()).normalize().scale(SPEED);
            setDeltaMovement(getDeltaMovement().lerp(v, 0.095).normalize().scale(SPEED));
            hasImpulse = true;
        }

        this.move(MoverType.SELF, getDeltaMovement());

        if (!level().isClientSide) {
            Vec3 v = getDeltaMovement();
            setYRot((float)(Mth.atan2(v.x, v.z) * Mth.RAD_TO_DEG));
            setXRot((float)(Mth.atan2(v.y, v.horizontalDistance()) * Mth.RAD_TO_DEG));
        }

        if (tickCount % 8 == 0) {
            level().addParticle(isRed() ? CompanionsParticles.HOLINESS_RED_STAR_TRAIL.get() : CompanionsParticles.HOLINESS_BLUE_STAR_TRAIL.get(), getX(), getY() - getBbHeight() * 0.5, getZ(), 0, 0, 0);
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

        if (level().isClientSide && (tickCount % 20 == 0 || tickCount == 1)) {
            Companions.PROXY.spawnGenericRibbonTrail(this, level(), getX(), getY(), getZ(), isRed() ? 204/255f : 25/255f, isRed() ? 50/255f : 139/255f, isRed() ? 50/255f : 86/255f, 0, 0.35f);
        }

    }

    @Override
    public void remove(@NotNull RemovalReason pReason) {
        if (!level().isClientSide) {
            if (isRed()) {
                redExplosion();
            } else {
                blueExplosion();
            }
        } else {
            for (Player player : level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(30))) {
                Companions.PROXY.shakePlayerCamera(player, 20, 0.1f, 0.1f, 0.1f, 10);
            }
        }

        super.remove(pReason);
    }

    private void redExplosion() {
        RedStarExplosion explosion = CompanionsEntities.RED_STAR_EXPLOSION.get().create(level());
        if (explosion != null) {
            explosion.moveTo(position());
            level().addFreshEntity(explosion);
        }
        RedStarExplosionCenter explosion2 = CompanionsEntities.RED_STAR_EXPLOSION_CENTER.get().create(level());
        if (explosion2 != null) {
            explosion2.moveTo(position());
            level().addFreshEntity(explosion2);
        }

        hurtNearby();
    }

    private void blueExplosion() {
        BlueStarExplosion explosion = CompanionsEntities.BLUE_STAR_EXPLOSION.get().create(level());
        if (explosion != null) {
            explosion.moveTo(position());
            level().addFreshEntity(explosion);
        }
        BlueStarExplosionCenter explosion2 = CompanionsEntities.BLUE_STAR_EXPLOSION_CENTER.get().create(level());
        if (explosion2 != null) {
            explosion2.moveTo(position());
            level().addFreshEntity(explosion2);
        }

        hurtNearby();
    }

    private void hurtNearby() {
        for (LivingEntity e : this.level().getEntitiesOfClass(LivingEntity.class, new AABB(this.blockPosition()).inflate(3))) {
            if (!Util.areEntitiesLinked(e, this)) {
                e.hurt(this.damageSources().magic(), 7f);
                if (isRed()) {
                    e.setRemainingFireTicks(new Random().nextInt(2, 10) * 20);
                } else {
                    e.setTicksFrozen(e.getTicksFrozen() + new Random().nextInt(60, 200));
                }
            }
        }

    }

    public boolean isRed() {
        return this.entityData.get(RED);
    }

    public void setRed(boolean isRed) {
        this.entityData.set(RED, isRed);
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        Entity entity = pResult.getEntity();
        Entity owner = this.getOwner();
        DamageSource damageSource = this.damageSources().trident(this, (owner == null ? this : owner));
        entity.hurt(damageSource, (float) CompanionsConfig.HOLINESS_STAR_DAMAGE);
        this.playSound(SoundEvents.DRAGON_FIREBALL_EXPLODE, 5.0f, 1.0F);
        this.remove(RemovalReason.KILLED);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(RED, false);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("Red")) {
            this.setRed(pCompound.getBoolean("Red"));
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("Red", this.isRed());
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