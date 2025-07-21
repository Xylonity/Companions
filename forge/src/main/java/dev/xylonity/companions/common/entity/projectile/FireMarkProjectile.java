package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.registry.CompanionsEffects;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class FireMarkProjectile extends Projectile implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");

    public FireMarkProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    public void tick() {
        super.tick();

        Entity owner = this.getOwner();

        if (owner == null) this.remove(RemovalReason.DISCARDED);

        if (!level().isClientSide && owner != null && !((LivingEntity) owner).hasEffect(CompanionsEffects.FIRE_MARK)) this.remove(RemovalReason.DISCARDED);

        if (owner != null && !owner.isRemoved()) {
            Vec3 targetPos = new Vec3(owner.getX(), owner.getY() + owner.getBbHeight() + 0.5, owner.getZ());
            Vec3 offset = targetPos.subtract(this.position());
            Vec3 velocity = this.getDeltaMovement();

            double K = 0.06;
            double C = 2.0 * Math.sqrt(K);

            Vec3 accel = offset.scale(K).subtract(velocity.scale(C));
            velocity = velocity.add(accel);

            this.setDeltaMovement(velocity);
            this.move(MoverType.SELF, velocity);
        }
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
        event.getController().setAnimation(IDLE);
        return PlayState.CONTINUE;
    }

}
