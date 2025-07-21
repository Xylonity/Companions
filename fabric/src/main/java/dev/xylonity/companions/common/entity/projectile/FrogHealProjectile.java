package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.BaseProjectile;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsSounds;
import dev.xylonity.knightlib.registry.KnightLibParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.*;

public class FrogHealProjectile extends BaseProjectile {

    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");

    private LivingEntity target;

    public FrogHealProjectile(EntityType<? extends BaseProjectile> type, Level level) {
        super(type, level);
    }

    public void setTarget(LivingEntity target) {
        this.target = target;
    }

    protected EntityHitResult findHitEntity(Vec3 pStartVec, Vec3 pEndVec) {
        return ProjectileUtil.getEntityHitResult(this.level(), this, pStartVec, pEndVec, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(0.5), this::canHitEntity);
    }

    @Override
    protected boolean canHitEntity(@NotNull Entity pTarget) {
        if (pTarget == this.getOwner()) return false;

        return Util.areEntitiesLinked(this, pTarget);
    }

    public float getDefaultSpeed() {
        return 0.425f;
    }

    public float getDefaultLerp() {
        return 0.28f;
    }

    public void spawnRibbon() {
        Companions.PROXY.spawnGenericRibbonTrail(this, level(), getX(), getY(), getZ(), 225/255f, 1, 218/255f, 0, 0.35f);
    }

    public void spawnParticles() {
        if (tickCount % 8 == 0) {
            level().addParticle(KnightLibParticles.STARSET.get(), getX(), getY() + getBbHeight() * 0.5, getZ(), 0, 0, 0);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide && target != null && target.isAlive()) {
            Vec3 v = target.getEyePosition().subtract(position()).normalize().scale(getDefaultSpeed());
            setDeltaMovement(getDeltaMovement().lerp(v, getDefaultLerp()).normalize().scale(getDefaultSpeed()));
            hasImpulse = true;
        }

        this.move(MoverType.SELF, getDeltaMovement());

        if (!level().isClientSide) {
            Vec3 v = getDeltaMovement();
            setYRot((float) (Mth.atan2(v.x, v.z) * Mth.RAD_TO_DEG));
            setXRot((float) (Mth.atan2(v.y, v.horizontalDistance()) * Mth.RAD_TO_DEG));
        }

        spawnParticles();

        if (!this.onGround()) {
            Vec3 pos = this.position();
            Vec3 vec33 = pos.add(this.getDeltaMovement());
            HitResult hitresult = this.level().clip(new ClipContext(pos, vec33, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));

            if (hitresult.getType() != HitResult.Type.MISS) vec33 = hitresult.getLocation();

            while (!this.isRemoved()) {
                EntityHitResult entityhitresult = this.findHitEntity(pos, vec33);
                if (entityhitresult != null) {
                    hitresult = entityhitresult;
                }

                if (hitresult != null && hitresult.getType() == HitResult.Type.ENTITY) {
                    Entity entity = ((EntityHitResult) hitresult).getEntity();
                    Entity entity1 = this.getOwner();
                    if (entity instanceof Player p1 && entity1 instanceof Player p2 && !p1.canHarmPlayer(p2)) {
                        hitresult = null;
                        entityhitresult = null;
                    }
                }

                if (hitresult != null && hitresult.getType() != HitResult.Type.MISS) {
                    this.onHit(hitresult);
                    this.hasImpulse = true;
                }

                if (entityhitresult == null) break;

                hitresult = null;
            }

            this.checkInsideBlocks();
        }

        if (this.onGround()) discard();

        if (level().isClientSide && (tickCount % 20 == 0 || tickCount == 1)) {
            spawnRibbon();
        }

    }

    @Override
    public void remove(@NotNull RemovalReason pReason) {
        super.remove(pReason);
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        Entity e = pResult.getEntity();
        for (int i = 0; i < 20; i++) {
            double dx = (this.random.nextDouble() - 0.5) * 1.25;
            double dy = (this.random.nextDouble() - 0.5) * 1.25;
            double dz = (this.random.nextDouble() - 0.5) * 1.25;
            if (this.level() instanceof ServerLevel level) {
                if (level.random.nextFloat() < 0.35f) level.sendParticles(ParticleTypes.POOF, e.getX(), e.getY() + 0.15, e.getZ(), 1, dx, dy, dz, 0.1);
                if (level.random.nextFloat() < 0.65f) level.sendParticles(KnightLibParticles.STARSET.get(), e.getX(), e.getY() + 0.15, e.getZ(), 1, dx, dy, dz, 0.1);
            }
        }

        if (e instanceof LivingEntity entity) entity.heal((float) CompanionsConfig.ENDER_FROG_HEAL_PROJECTILE_HEAL_AMOUNT);

        playSound(CompanionsSounds.END_FROG_HEAL.get());

        this.discard();
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
    }

    @Override
    public void playerTouch(@NotNull Player pEntity) { ;; }

    @Override
    protected int baseLifetime() {
        return 200;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 2, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        event.setAnimation(IDLE);
        return PlayState.CONTINUE;
    }

    @Override
    public boolean shouldRender(double pX, double pY, double pZ) {
        return true;
    }

}