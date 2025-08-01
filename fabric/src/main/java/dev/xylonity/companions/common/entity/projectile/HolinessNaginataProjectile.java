package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.config.CompanionsConfig;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.Random;

public class HolinessNaginataProjectile extends ThrownTrident implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final Quaternionf prevRot = new Quaternionf();
    private final Quaternionf currentRot = new Quaternionf();

    protected boolean dealtDamage;
    protected int timeToDespawn;

    public HolinessNaginataProjectile(EntityType<? extends ThrownTrident> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.pickup = Pickup.DISALLOWED;
        if (!level().isClientSide) timeToDespawn = new Random().nextInt(120, 300);
    }

    @Override
    protected boolean canHitEntity(@NotNull Entity entity) {
        if (Util.areEntitiesLinked(this, entity)) return false;

        return super.canHitEntity(entity);
    }

    @Override
    public void tickDespawn() {
        if (this.inGroundTime >= timeToDespawn) {
            this.discard();
        }
    }

    @Override
    protected boolean tryPickup(@NotNull Player pPlayer) {
        return false;
    }

    @Override
    public @NotNull AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling().inflate(10);
    }

    public void refreshOrientation() {
        Vec3 v = getDeltaMovement();
        if (v.lengthSqr() < 1.0E-7) return;

        float yaw = (float) Math.atan2(v.x, v.z);
        float pitch = (float) Math.atan2(v.y, Math.hypot(v.x, v.z));

        setYRot(yaw * Mth.RAD_TO_DEG);
        setXRot(pitch * Mth.RAD_TO_DEG);
        yRotO = getYRot();
        xRotO = getXRot();

        Quaternionf quaternionf = new Quaternionf().rotateY(yaw + (float) Math.PI).rotateX(pitch);

        prevRot.set(currentRot);
        currentRot.set(quaternionf);
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult pResult) {
        super.onHitBlock(pResult);
        doShake();
    }

    protected void doShake() {
        if (level().isClientSide) {
            for (Player player : level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(30))) {
                Companions.PROXY.shakePlayerCamera(player, 5, 0.1f, 0.1f, 0.1f, 10);
            }
        }
    }

    @Override
    public void tick() {
        if (tickCount < 2) {
            setInvisible(true);
        } else if (tickCount < 5) {
            setInvisible(false);
        }

        if (getDeltaMovement().lengthSqr() > 1.0E-7) refreshOrientation();

        super.tick();
    }

    @Nullable
    @Override
    protected EntityHitResult findHitEntity(@NotNull Vec3 pStartVec, @NotNull Vec3 pEndVec) {
        return this.dealtDamage ? null : super.findHitEntity(pStartVec, pEndVec);
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        Entity entity = pResult.getEntity();
        Entity owner = this.getOwner();
        DamageSource damageSource = this.damageSources().trident(this, (owner == null ? this : owner));
        this.dealtDamage = true;
        entity.hurt(damageSource, (float) CompanionsConfig.NAGINATA_DAMAGE);
        this.playSound(SoundEvents.TRIDENT_HIT, 5.0f, 1.0F);
    }

    public Quaternionf getPrevRotation() {
        return prevRot;
    }

    public Quaternionf getCurrentRotation() {
        return currentRot;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) { }
}