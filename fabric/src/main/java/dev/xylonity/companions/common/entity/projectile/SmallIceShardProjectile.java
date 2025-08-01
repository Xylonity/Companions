package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.config.CompanionsConfig;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
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

public class SmallIceShardProjectile extends AbstractArrow implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final int PHASE_1_DURATION = 20;
    private static final double PHASE_1_FRICTION = 0.95;
    private static final double PHASE_2_FRICTION = 0.88;
    private static final double PHASE_2_ACCELERATION = 0.04;
    private static final int LIFETIME = 200;

    private LivingEntity target;
    private boolean followOwnerLook = false;

    // For animation purposes, this handles the projectile lerping rotation
    private final Quaternionf prevRotation = new Quaternionf();
    private final Quaternionf currentRotation = new Quaternionf();

    public SmallIceShardProjectile(EntityType<? extends AbstractArrow> type, Level level) {
        super(type, level);

        Vec3 randomDir = new Vec3(random.nextDouble() * 2 - 1, random.nextDouble() * 2 - 1, random.nextDouble() * 2 - 1).normalize();
        this.setDeltaMovement(randomDir.scale(0.2));

        this.setNoGravity(true);
        this.pickup = Pickup.DISALLOWED;
    }

    public void setTarget(@Nullable LivingEntity target) {
        this.target = target;
    }

    @Nullable
    public LivingEntity getTarget() {
        return target;
    }

    @Override
    protected boolean canHitEntity(@NotNull Entity entity) {
        if (Util.areEntitiesLinked(entity, this)) return false;

        return super.canHitEntity(entity);
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    public void tick() {
        super.tick();

        // If the target exists throws the projectile towards it
        if (target != null) {
            if (tickCount < PHASE_1_DURATION) {
                setDeltaMovement(getDeltaMovement().scale(PHASE_1_FRICTION));
            } else {
                Vec3 targetPos = new Vec3(target.getX(), target.getY() + target.getBbHeight() * 0.7, target.getZ());
                Vec3 diff = targetPos.subtract(position());
                Vec3 newVel = getDeltaMovement().scale(PHASE_2_FRICTION).add(diff.normalize().scale(PHASE_2_ACCELERATION));
                setDeltaMovement(newVel);
            }
        }

        // If there is no real target, raytracing within the player's look vector is done
        // Also, the owner is the player that throws the spell if it uses the shards book, for more
        // proper owner usage since this is the real projectile that is thrown, not the bigger shard
        else if (followOwnerLook && getOwner() instanceof LivingEntity owner) {
            if (tickCount < PHASE_1_DURATION) {
                setDeltaMovement(getDeltaMovement().scale(PHASE_1_FRICTION));
            } else {
                Vec3 targetPos = owner.getLookAngle().normalize();
                Vec3 newVel = getDeltaMovement().scale(PHASE_2_FRICTION).add(targetPos.scale(PHASE_2_ACCELERATION));
                setDeltaMovement(newVel);
            }
        }

        updateRotations();

        if (tickCount == LIFETIME) {
            if (level().isClientSide) {
                spawnHitParticles();
            } else {
                level().broadcastEntityEvent(this, (byte) 3);
                playSound(SoundEvents.AMETHYST_BLOCK_RESONATE);
            }

            remove(RemovalReason.DISCARDED);
        }

        if (level().isClientSide) {
            if ((this.tickCount % 40 == 0 || this.tickCount == 1)) {
                float r = (190 + level().random.nextInt(30)) / 255f;
                float g = (240 + level().random.nextInt(10)) / 255f;
                float b = (247 + level().random.nextInt(5)) / 255f;
                Companions.PROXY.spawnBaseProjectileTrail(this, 0, getBbHeight() * 0.5f, r, g, b);
            }
        }

    }

    // Animation purposes too, lerping rotation
    private void updateRotations() {
        Vec3 vel = getDeltaMovement();
        if (vel.lengthSqr() <= 1e-7) return;

        prevRotation.set(currentRotation);

        Vector3f velVec = new Vector3f((float) vel.x, (float) vel.y, (float) vel.z).normalize();
        Vector3f forward = new Vector3f(0, 0, 1);

        Vector3f axis = forward.cross(velVec);
        if (axis.length() < 1e-4f) {
            axis.set(0, 1, 0);
        } else {
            axis.normalize();
        }

        float dot = Math.max(-1, Math.min(1, forward.dot(velVec)));
        Quaternionf targetRot = new Quaternionf().fromAxisAngleRad(axis, (float) Math.acos(dot));

        if (tickCount < PHASE_1_DURATION) {
            currentRotation.set(targetRot);
        } else {
            currentRotation.slerp(targetRot, 0.5f);
        }
    }

    @Override
    protected void onHit(@NotNull HitResult pResult) {
        if (pResult.getType().equals(HitResult.Type.ENTITY)) {
            Entity target = ((EntityHitResult) pResult).getEntity();
            if (target instanceof SmallIceShardProjectile) {
                return;
            }

            target.hurt(damageSources().magic(), (float) CompanionsConfig.SMALL_ICE_SHARD_DAMAGE);
            target.setTicksFrozen(target.getTicksFrozen() + CompanionsConfig.SMALL_ICE_SHARD_FREEZE_TICKS);
        }

        if (pResult.getType() != HitResult.Type.MISS) {
            if (level().isClientSide) {
                spawnHitParticles();
            } else {
                level().broadcastEntityEvent(this, (byte) 3);
                playSound(SoundEvents.AMETHYST_BLOCK_HIT);
            }

            remove(RemovalReason.DISCARDED);
        }

    }


    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 3) {
            spawnHitParticles();
        } else {
            super.handleEntityEvent(pId);
        }

    }

    public void setFollowOwnerLook(boolean ownerLook) {
        this.followOwnerLook = ownerLook;
    }

    public void shootTowards(Vec3 dir, double speed) {
        this.setDeltaMovement(dir.normalize().scale(speed));
    }

    public Quaternionf getPrevRotation() {
        return prevRotation;
    }

    public Quaternionf getCurrentRotation() {
        return currentRotation;
    }

    private void spawnHitParticles() {
        for (int i = 0; i < 5; i++) {
            double dx = (this.level().getRandom().nextDouble() - 0.5) * 0.1;
            double dy = (this.level().getRandom().nextDouble() - 0.5) * 0.1;
            double dz = (this.level().getRandom().nextDouble() - 0.5) * 0.1;
            this.level().addParticle(ParticleTypes.SNOWFLAKE, this.getX(), this.getY(), this.getZ(), dx, dy, dz);
            if (i % 2 == 0) this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.ICE)), this.getX(), this.getY() + getBbHeight() * 0.5, this.getZ(), dx, dy, dz);
        }
    }

    @Override
    protected boolean tryPickup(@NotNull Player p) {
        return false;
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public void playerTouch(@NotNull Player p) { ;; }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar r) { ;; }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

}
