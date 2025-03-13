package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Random;

public class SmallIceShardProjectile extends AbstractArrow implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private LivingEntity target;

    private static final int PHASE_1_DURATION = 20;
    private static final float ROTATION_LERP_FACTOR = 0.5f;
    private static final double INITIAL_SPEED = 0.2;
    private static final double PHASE_1_FRICTION = 0.95;
    private static final double PHASE_2_FRICTION = 0.98;
    private static final double PHASE_2_ACCELERATION = 0.04;
    private static final int LIFETIME = 100;

    public SmallIceShardProjectile(EntityType<? extends AbstractArrow> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

        Random random = new Random();
        double dx = random.nextDouble() * 2 - 1;
        double dy = random.nextDouble() * 2 - 1;
        double dz = random.nextDouble() * 2 - 1;
        Vec3 randomDir = new Vec3(dx, dy, dz).normalize();
        this.setDeltaMovement(randomDir.scale(INITIAL_SPEED));

        this.pickup = Pickup.DISALLOWED;
    }

    @Override
    protected boolean tryPickup(@NotNull Player pPlayer) {
        return false;
    }

    public void setTarget(@Nullable LivingEntity target) {
        this.target = target;
    }

    @Nullable
    public LivingEntity getTarget() {
        return this.target;
    }

    private final Quaternionf prevRotation = new Quaternionf();
    private final Quaternionf currentRotation = new Quaternionf();

    @Override
    public void tick() {
        if (this.getTarget() != null) {
            this.setNoGravity(true);
            if (this.tickCount < PHASE_1_DURATION) {
                this.setDeltaMovement(this.getDeltaMovement().scale(PHASE_1_FRICTION));
            } else {
                Vec3 targetPos = new Vec3(this.getTarget().getX(), this.getTarget().getY() + this.getTarget().getBbHeight() * 0.7, this.getTarget().getZ());
                Vec3 diff = targetPos.subtract(this.position());
                Vec3 newVelocity = this.getDeltaMovement().scale(PHASE_2_FRICTION).add(diff.normalize().scale(PHASE_2_ACCELERATION));

                this.setDeltaMovement(newVelocity);
            }
        }

        Vec3 velocity = this.getDeltaMovement();
        if (velocity.lengthSqr() > 1e-7) {
            prevRotation.set(currentRotation);

            Vector3f velVec = new Vector3f((float) velocity.x, (float) velocity.y, (float) velocity.z);
            velVec.normalize();

            Vector3f defaultForward = new Vector3f(0.0F, 0.0F, 1.0F);
            float dot = defaultForward.dot(velVec);
            dot = Math.max(-1.0F, Math.min(1.0F, dot));
            float angle = (float) Math.acos(dot);

            Vector3f axis = defaultForward.cross(velVec);
            if (axis.length() < 1e-4F) {
                axis.set(0.0F, 1.0F, 0.0F);
            } else {
                axis.normalize();
            }

            Quaternionf targetRotation = new Quaternionf().fromAxisAngleRad(axis, angle);

            if (this.tickCount < PHASE_1_DURATION) {
                currentRotation.set(targetRotation);
            } else {
                currentRotation.slerp(targetRotation, ROTATION_LERP_FACTOR);
            }
        }

        if (tickCount == LIFETIME) {
            if (this.level().isClientSide) {
                spawnHitParticles();
            } else {
                this.level().broadcastEntityEvent(this, (byte)3);
                this.playSound(SoundEvents.AMETHYST_BLOCK_RESONATE);
            }

            this.remove(RemovalReason.DISCARDED);
        }

        super.tick();
    }

    public Quaternionf getPrevRotation() {
        return prevRotation;
    }

    public Quaternionf getCurrentRotation() {
        return currentRotation;
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 3) {
            spawnHitParticles();
        } else {
            super.handleEntityEvent(pId);
        }
    }

    private void spawnHitParticles() {
        for (int i = 0; i < 5; i++) {
            double dx = (this.level().getRandom().nextDouble() - 0.5) * 0.1;
            double dy = (this.level().getRandom().nextDouble() - 0.5) * 0.1;
            double dz = (this.level().getRandom().nextDouble() - 0.5) * 0.1;
            this.level().addParticle(ParticleTypes.SNOWFLAKE, this.getX(), this.getY(), this.getZ(), dx, dy, dz);
            if (i % 2 == 0) this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.ICE)), this.getX(), this.getY(), this.getZ(), dx, dy, dz);
        }
    }

    @Override
    protected void onHit(@NotNull HitResult pResult) {
        if (pResult.getType().equals(HitResult.Type.ENTITY)) {
            Entity target = ((EntityHitResult) pResult).getEntity();
            if (target instanceof OwnableEntity ownable && this.getOwner() != null) {
                if ((ownable.getOwner() != null && ownable.getOwner().equals(this.getOwner()) || target.equals(getOwner()))) {
                    return;
                }
            }

            target.hurt(damageSources().magic(), 1);
            target.setTicksFrozen(target.getTicksFrozen() + 100);
        }

        if (this.level().isClientSide) {
            spawnHitParticles();
        } else {
            this.level().broadcastEntityEvent(this, (byte)3);
            this.playSound(SoundEvents.AMETHYST_BLOCK_HIT);
        }

        this.remove(RemovalReason.DISCARDED);

    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public void playerTouch(@NotNull Player pEntity) { ;; }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
    }

}
