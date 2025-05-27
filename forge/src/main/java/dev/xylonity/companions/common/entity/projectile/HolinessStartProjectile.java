package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.common.entity.BaseProjectile;
import dev.xylonity.companions.common.entity.hostile.SacredPontiffEntity;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;

public class HolinessStartProjectile extends BaseProjectile {

    public static final float SPEED = 0.34f;
    private LivingEntity homingTarget;

    public HolinessStartProjectile(EntityType<? extends BaseProjectile> type, Level level) {
        super(type, level);
    }

    public void setTarget(LivingEntity target) {
        this.homingTarget = target;
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide && homingTarget != null && homingTarget.isAlive()) {

            Vec3 desiredVel = homingTarget.getEyePosition().subtract(position()).normalize().scale(SPEED);
            Vec3 newVel = getDeltaMovement().lerp(desiredVel, 0.115);
            setDeltaMovement(newVel.normalize().scale(SPEED));
            hasImpulse = true;
        }

        move(MoverType.SELF, getDeltaMovement());

        if (!level().isClientSide) {
            Vec3 v = getDeltaMovement();
            float yRot = (float)(Mth.atan2(v.x, v.z) * Mth.RAD_TO_DEG);
            float xRot = (float)(Mth.atan2(v.y, v.horizontalDistance()) * Mth.RAD_TO_DEG);
            setYRot(yRot);
            setXRot(xRot);
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

    private void spawnHitParticles() {
        for (int i = 0; i < 5; i++) {
            double dx = (this.level().getRandom().nextDouble() - 0.5) * 0.1;
            double dy = (this.level().getRandom().nextDouble() - 0.5) * 0.1;
            double dz = (this.level().getRandom().nextDouble() - 0.5) * 0.1;
            this.level().addParticle(ParticleTypes.SNOWFLAKE, this.getX(), this.getY(), this.getZ(), dx, dy, dz);
            if (i % 2 == 0) this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.STONE.defaultBlockState()), this.getX(), this.getY() + 1, this.getZ(), dx, dy, dz);
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    public void playerTouch(@NotNull Player pEntity) { ;; }

    @Override
    protected int baseLifetime() {
        return 200;
    }

}
