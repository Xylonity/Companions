package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.config.CompanionsConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.Random;

public class NeedleProjectile extends HolinessNaginataProjectile implements GeoEntity {

    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");

    private static final EntityDataAccessor<Integer> LIFETIME = SynchedEntityData.defineId(NeedleProjectile.class, EntityDataSerializers.INT);

    public NeedleProjectile(EntityType<? extends ThrownTrident> type, Level level) {
        super(type, level);
        this.pickup = Pickup.DISALLOWED;
        if (!level().isClientSide) timeToDespawn = new Random().nextInt(80, 160);
    }

    @Override
    public void remove(@NotNull RemovalReason pReason) {
        super.remove(pReason);
        for (int i = 0; i < 8; i++) {
            double vx = (level().random.nextDouble() - 0.5) * this.getBbWidth();
            double vy = (level().random.nextDouble() - 0.5) * this.getBbHeight();
            double vz = (level().random.nextDouble() - 0.5) * this.getBbWidth();
            if (level() instanceof ServerLevel level) {
                level.sendParticles(ParticleTypes.POOF, getX(), getY(), getZ(), 1, vx, vy, vz, 0.15);
            }
        }

    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            if (getLifetime() >= timeToDespawn) {
                this.discard();
            }

            setLifetime(getLifetime() + 1);
        }

    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        Entity entity = pResult.getEntity();
        Entity owner = this.getOwner();
        DamageSource damageSource = this.damageSources().trident(this, (owner == null ? this : owner));
        this.dealtDamage = true;
        entity.hurt(damageSource, (float) CompanionsConfig.NEEDLE_PROJECTILE_DAMAGE);
        this.playSound(SoundEvents.TRIDENT_HIT, 5.0f, 1.0F);
    }

    @Override
    protected void doShake() {
        if (level().isClientSide) {
            for (Player player : level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(30))) {
                Companions.PROXY.shakePlayerCamera(player, 5, 0.02f, 0.02f, 0.02f, 10);
            }
        }

    }

    public int getLifetime() {
        return this.entityData.get(LIFETIME);
    }

    public void setLifetime(int tick) {
        this.entityData.set(LIFETIME, tick);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Lifetime", this.getLifetime());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("Lifetime")) {
            setLifetime(pCompound.getInt("Lifetime"));
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LIFETIME, 80);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar reg) {
        reg.add(new AnimationController<>(this, "controller", 2, this::predicate));
    }

    private <T extends GeoEntity> PlayState predicate(AnimationState<T> state) {
        state.setAnimation(IDLE);
        return PlayState.CONTINUE;
    }

    @Override
    public @NotNull AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling().inflate(10);
    }

}
