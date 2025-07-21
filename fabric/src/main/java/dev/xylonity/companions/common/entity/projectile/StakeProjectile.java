package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class StakeProjectile extends AbstractArrow implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final Quaternionf prevRot = new Quaternionf();
    private final Quaternionf currentRot = new Quaternionf();

    public StakeProjectile(EntityType<? extends AbstractArrow> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.noPhysics = false;
    }

    public StakeProjectile(Level pLevel, LivingEntity pShooter) {
        super(CompanionsEntities.STAKE_PROJECTILE, pShooter, pLevel, ItemStack.EMPTY, null);
        this.noPhysics = false;
    }

    @Override
    protected boolean canHitEntity(@NotNull Entity entity) {
        if (Util.areEntitiesLinked(entity, this)) return false;

        return super.canHitEntity(entity);
    }

    @Override
    public void tick() {
        Vec3 velocity = this.getDeltaMovement();

        if (velocity.lengthSqr() > 1e-7) {
            prevRot.set(currentRot);

            Vector3f velVec = new Vector3f((float) velocity.x, (float) velocity.y, (float) velocity.z);
            velVec.normalize();

            Vector3f defaultForward = new Vector3f(0.0F, 0.0F, -1.0F);

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

            currentRot.set(targetRotation);
        }

        super.tick();
    }

    public Quaternionf getPrevRotation() {
        return prevRot;
    }

    public Quaternionf getCurrentRotation() {
        return currentRot;
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
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) { }
}