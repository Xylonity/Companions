package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Random;

public class BigIceShardProjectile extends AbstractArrow implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private LivingEntity target;

    private static final RawAnimation APPEAR = RawAnimation.begin().thenPlay("appear");

    private static final int LIFETIME = 25;

    public BigIceShardProjectile(EntityType<? extends AbstractArrow> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

        Random random = new Random();
        double initialSpeed = 4;

        double minElevation = Math.toRadians(30);
        double elevation = minElevation + random.nextDouble() * (Math.toRadians(90) - minElevation);

        double theta = random.nextDouble() * 2 * Math.PI;

        double dx = Math.cos(elevation) * Math.cos(theta);
        double dy = Math.sin(elevation);
        double dz = Math.cos(elevation) * Math.sin(theta);

        Vec3 upwardDir = new Vec3(dx, dy, dz).normalize();
        this.setDeltaMovement(upwardDir.scale(initialSpeed));

        this.pickup = Pickup.DISALLOWED;
    }

    @Override
    protected @NotNull SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.AMETHYST_BLOCK_HIT;
    }

    public void setTarget(@Nullable LivingEntity target) {
        this.target = target;
    }

    @Nullable
    public LivingEntity getTarget() {
        return this.target;
    }

    @Override
    protected boolean tryPickup(@NotNull Player pPlayer) {
        return false;
    }

    @Override
    public void tick() {
        Entity owner = this.getOwner();
        if (owner != null) {
            this.setNoGravity(true);
            if (this.tickCount < LIFETIME) {
                this.setDeltaMovement(this.getDeltaMovement().scale(0.95));
            }

            if (tickCount == LIFETIME) {
                if (this.level().isClientSide) {
                    spawnHitParticles();
                } else {
                    this.level().broadcastEntityEvent(this, (byte)3);
                    this.playSound(SoundEvents.AMETHYST_BLOCK_RESONATE);
                }

                for (int i = 0; i < 5; i++) {
                    SmallIceShardProjectile projectile = CompanionsEntities.SMALL_ICE_SHARD_PROJECTILE.get().create(level());

                    if (projectile != null) {
                        projectile.moveTo(getX(), getY(), getZ());
                        projectile.setOwner(owner);

                        projectile.setTarget(this.getTarget());

                        level().addFreshEntity(projectile);
                    }
                }

                this.remove(RemovalReason.DISCARDED);
            }

        }

        super.tick();
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
        for (int i = 0; i < 10; i++) {
            double dx = (this.level().getRandom().nextDouble() - 0.5) * 0.1;
            double dy = (this.level().getRandom().nextDouble() - 0.5) * 0.1;
            double dz = (this.level().getRandom().nextDouble() - 0.5) * 0.1;
            this.level().addParticle(ParticleTypes.SNOWFLAKE, this.getX(), this.getY(), this.getZ(), dx, dy, dz);
        }
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public void playerTouch(@NotNull Player pEntity) {

    }

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
