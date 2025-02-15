package dev.xylonity.companions.common.entity.projectile;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Random;

public class SmallIceShardProjectile extends AbstractArrow implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final int PHASE_1_DURATION = 20;
    private static final double INITIAL_SPEED = 0.2;
    private static final double PHASE_1_FRICTION = 0.95;
    private static final double PHASE_2_FRICTION = 0.98;
    private static final double PHASE_2_ACCELERATION = 0.04;

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
    protected boolean tryPickup(Player pPlayer) {
        return false;
    }

    @Override
    public void tick() {
        Entity owner = this.getOwner();
        if (owner instanceof Player playerOwner) {
            LivingEntity target = playerOwner.getLastHurtByMob();
            Vec3 targetPos;
            if (target != null) {
                targetPos = new Vec3(
                        target.getX(),
                        target.getY() + target.getBbHeight() * 0.7,
                        target.getZ()
                );
            } else {
                targetPos = new Vec3(
                        owner.getX(),
                        owner.getY() + owner.getBbHeight() * 0.7,
                        owner.getZ()
                );
            }

            this.setNoGravity(true);

            if (this.tickCount < PHASE_1_DURATION) {
                this.setDeltaMovement(this.getDeltaMovement().scale(PHASE_1_FRICTION));
            }

            else {
                Vec3 diff = targetPos.subtract(this.position());

                this.setPosRaw(this.getX(), this.getY() + diff.y * 0.015, this.getZ());

                Vec3 newVel = this.getDeltaMovement()
                        .scale(PHASE_2_FRICTION)
                        .add(diff.normalize().scale(PHASE_2_ACCELERATION));

                this.setDeltaMovement(newVel);
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
