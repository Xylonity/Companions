package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Random;

public class BigIceShardProjectile extends AbstractArrow implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final int LIFETIME = 25;
    private static final double RAY_DISTANCE = 64.0D;
    private static final double SMALL_SHARD_SPEED = 0.4D;

    private static final RawAnimation APPEAR = RawAnimation.begin().thenPlay("appear");

    private LivingEntity target;

    public BigIceShardProjectile(EntityType<? extends AbstractArrow> type, Level level) {
        super(type, level);

        double minElevation = Math.toRadians(30);
        double elevation = minElevation + random.nextDouble() * (Math.toRadians(90) - minElevation);
        double t = random.nextDouble() * Math.PI * 2;

        double dx = Math.cos(elevation) * Math.cos(t);
        double dy = Math.sin(elevation);
        double dz = Math.cos(elevation) * Math.sin(t);

        this.setDeltaMovement(new Vec3(dx, dy, dz).normalize().scale(4));
        this.pickup = Pickup.DISALLOWED;
    }

    @Override
    protected boolean canHitEntity(@NotNull Entity entity) {
        if (Util.areEntitiesLinked(entity, this)) return false;

        return super.canHitEntity(entity);
    }

    public void setTarget(@Nullable LivingEntity tgt) {
        this.target = tgt;
    }

    @Nullable
    public LivingEntity getTarget() {
        return target;
    }

    @Override
    public void tick() {

        super.tick();

        Entity owner = this.getOwner();
        if (owner != null) {
            this.setNoGravity(true);
            if (this.tickCount < LIFETIME) {
                this.setDeltaMovement(this.getDeltaMovement().scale(0.95));
            }

            if (this.tickCount == LIFETIME) {

                if (level().isClientSide) {
                    spawnHitParticles();
                } else {
                    level().broadcastEntityEvent(this, (byte) 3);
                    playSound(SoundEvents.AMETHYST_BLOCK_RESONATE);
                }

                // Generates small ice shards
                for (int i = 0; i < 5; i++) {
                    SmallIceShardProjectile shard = CompanionsEntities.SMALL_ICE_SHARD_PROJECTILE.create(level());

                    if (shard != null) {
                        shard.moveTo(getX(), getY(), getZ());
                        shard.setOwner(owner);

                        // Doing raytracing checks if there is any entity on the player's view vector
                        LivingEntity target1 = this.getTarget();
                        if (target1 == null && owner instanceof LivingEntity livingOwner) {
                            target1 = findEntityInCrosshair(livingOwner);
                        }

                        if (target1 != null) {
                            shard.setTarget(target1);
                        } else {
                            shard.setFollowOwnerLook(true);
                            // Gens shards pointing at a random conical dir
                            shard.shootTowards(genRandomShardDir(owner instanceof LivingEntity ? owner.getLookAngle() : this.getDeltaMovement().normalize()), SMALL_SHARD_SPEED);
                        }

                        // Yes
                        level().addFreshEntity(shard);
                    }

                }

                this.remove(RemovalReason.DISCARDED);
            }
        }

    }

    // Raytracing is my passion (no)
    @Nullable
    private static LivingEntity findEntityInCrosshair(LivingEntity player) {
        // Not much of a deal, just expand a bb to the player's view vector and id there is a hit result, I just return the entity selected
        AABB bb = player.getBoundingBox().expandTowards(player.getLookAngle().scale(RAY_DISTANCE)).inflate(1.0D);
        EntityHitResult hit = ProjectileUtil.getEntityHitResult(player.level(), player, player.getEyePosition(), player.getEyePosition().add(player.getLookAngle().scale(RAY_DISTANCE)), bb,
                e -> e instanceof LivingEntity && e != player);

        return hit != null ? (LivingEntity) hit.getEntity() : null;
    }

    // 'Generates' a cone around the forward vector (f) from the big shard, within a random dir is selected. If you
    // wanna copy this method make sure to change the aperture degrees (8) to whichever number you want
    private static Vec3 genRandomShardDir(Vec3 dir) {
        Random random = new Random();
        Vec3 forward = dir.normalize();

        // Orthonormal basis
        Vec3 up = Math.abs(forward.dot(new Vec3(0, 1, 0))) > 0.99 ? new Vec3(1, 0, 0) : new Vec3(0, 1, 0);
        Vec3 right = forward.cross(up).normalize();
        up = right.cross(forward).normalize();

        double maxRad = Math.toRadians(8);
        // Truncates the aperture angle
        double ct = random.nextDouble() * (1 - Math.cos(maxRad)) + Math.cos(maxRad);
        double st = Math.sqrt(1 - ct * ct);
        double ph = random.nextDouble() * Math.PI * 2;

        return forward.scale(ct).add(right.scale(st * Math.cos(ph))).add(up.scale(st * Math.sin(ph))).normalize();
    }

    private void spawnHitParticles() {
        for (int i = 0; i < 10; i++) {
            double dx = (this.level().getRandom().nextDouble() - 0.5) * 0.1;
            double dy = (this.level().getRandom().nextDouble() - 0.5) * 0.1;
            double dz = (this.level().getRandom().nextDouble() - 0.5) * 0.1;
            this.level().addParticle(ParticleTypes.SNOWFLAKE, this.getX(), this.getY(), this.getZ(), dx, dy, dz);
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

    @Override
    protected @NotNull SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.AMETHYST_BLOCK_HIT;
    }

    @Override
    protected boolean tryPickup(@NotNull Player pPlayer) {
        return false;
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public void playerTouch(@NotNull Player pPlayer) { ;; }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        if (tickCount <= 8) event.getController().setAnimation(APPEAR);

        return PlayState.CONTINUE;
    }

}
