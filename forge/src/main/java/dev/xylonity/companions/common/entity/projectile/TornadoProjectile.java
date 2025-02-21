package dev.xylonity.companions.common.entity.projectile;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class TornadoProjectile extends AbstractArrow implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final RawAnimation SPIN = RawAnimation.begin().thenPlay("spin");

    private static final int LIFETIME = 120;

    private static final double PARAM_SCALE = 0.2;

    private double groundY = 0;
    private double startX, startZ;

    private double alpha = 0;
    private boolean initialized = false;

    public TornadoProjectile(EntityType<? extends AbstractArrow> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.pickup = Pickup.DISALLOWED;
    }

    @Override
    protected boolean tryPickup(@NotNull Player pPlayer) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        Entity owner = this.getOwner();
        if (!initialized) {
            initialized = true;

            this.groundY = this.getY();

            this.startX = this.getX();
            this.startZ = this.getZ();

            if (owner != null) {
                Vec3 look = owner.getLookAngle();
                double forwardX = look.x;
                double forwardZ = look.z;
                this.alpha = Math.atan2(forwardZ, forwardX);
            } else {
                this.alpha = 0;
            }

            this.setNoGravity(true);
        }

        if (this.tickCount >= LIFETIME) {
            onExpire();
            return;
        }

        double t = this.tickCount * PARAM_SCALE;

        //double xLocal = Math.sin(-2.0 * t) + t;
        //double zLocal = Math.cos(-2.0 * t) + 1.0;

        double xLocal = t + Math.pow(Math.E, 0.1 * t) * ((Math.cos(5*t)-1)/2);
        double zLocal = Math.pow(Math.E, 0.1*t)*Math.sin(5*t);

        double cosA = Math.cos(this.alpha);
        double sinA = Math.sin(this.alpha);
        double rotX = xLocal * cosA - zLocal * sinA;
        double rotZ = xLocal * sinA + zLocal * cosA;

        double finalX = this.startX + rotX;
        double finalZ = this.startZ + rotZ;

        this.setPosRaw(finalX, this.groundY, finalZ);
    }

    private void onExpire() {
        if (this.level().isClientSide) {
            spawnHitParticles();
        } else {
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.playSound(SoundEvents.AMETHYST_BLOCK_CHIME);
        }

        this.remove(RemovalReason.DISCARDED);
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
    protected void onHit(@NotNull HitResult pResult) {
        if (pResult instanceof BlockHitResult blockHit) {
            Direction hitDirection = blockHit.getDirection();
            if (hitDirection == Direction.UP || hitDirection == Direction.DOWN) {
                return;
            }
        }

        if (this.level().isClientSide) {
            spawnHitParticles();
        } else {
            this.level().broadcastEntityEvent(this, (byte) 3);
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
        controllerRegistrar.add(new AnimationController<>(this, "controller", this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        event.getController().setAnimation(SPIN);

        return PlayState.CONTINUE;
    }

}
