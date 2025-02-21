package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;

public class BraceProjectile extends Projectile implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final int LIFETIME = 200;
    private static final double GRAVITY = 0.03D;
    private static final double AIR_FRICTION = 0.98D;
    private static final double BOUNCE_DAMPING_VERTICAL = 0.6D;
    private static final double BOUNCE_DAMPING_HORIZONTAL = 0.7D;
    private static final int MAX_ENTITY_BOUNCES = 5;
    private static final int MAX_BLOCK_BOUNCES = 5;
    private static final float DAMAGE_AMOUNT = 3.0F;
    private static final double SEARCH_RADIUS = 10D;
    private static final double BASE_SPEED = 0.6D;
    private static final double MIN_UPWARD = 0.4D;
    private int entityBounceCount = 0;
    private int blockBounceCount = 0;

    private final List<Integer> hitEntityIds = new ArrayList<>();

    public BraceProjectile(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
        this.noPhysics = false;
    }

    @Override
    public void tick() {
        super.tick();

        if (tickCount > LIFETIME || entityBounceCount >= MAX_ENTITY_BOUNCES) {
            if (!this.level().isClientSide)
                this.level().broadcastEntityEvent(this, (byte) 3);

            this.remove(RemovalReason.DISCARDED);
        }

        Vec3 vel = this.getDeltaMovement();
        vel = vel.add(0, -GRAVITY, 0);
        vel = vel.multiply(AIR_FRICTION, AIR_FRICTION, AIR_FRICTION);

        Vec3 startPos = this.position();
        Vec3 endPos = startPos.add(vel);

        HitResult entityResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);

        ClipContext ctx = new ClipContext(startPos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this);
        BlockHitResult blockResult = level().clip(ctx);

        double blockDist = (blockResult != null && blockResult.getType() != HitResult.Type.MISS)
                ? blockResult.getLocation().distanceToSqr(startPos) : Double.MAX_VALUE;
        double entityDist = (entityResult != null && entityResult.getType() == HitResult.Type.ENTITY)
                ? entityResult.getLocation().distanceToSqr(startPos) : Double.MAX_VALUE;

        if (entityDist < blockDist) {
            EntityHitResult ehr = (EntityHitResult) entityResult;
            Entity hit = ehr.getEntity();
            if (hit instanceof LivingEntity living && living != this.getOwner() && !hitEntityIds.contains(living.getId())) {

                living.hurt(damageSources().thrown(this, this.getOwner()), DAMAGE_AMOUNT);
                hitEntityIds.add(living.getId());
                entityBounceCount++;

                Vec3 cPoint = ehr.getLocation();

                if (!this.level().isClientSide)
                    this.level().broadcastEntityEvent(this, (byte) 3);

                this.setPos(cPoint.x, cPoint.y, cPoint.z);
                this.setDeltaMovement(Vec3.ZERO);

                if (entityBounceCount >= MAX_ENTITY_BOUNCES) {
                    this.discard();
                    return;
                }

                LivingEntity next = findNextTarget();
                if (next != null) {
                    Vec3 center = next.position().add(0, next.getBbHeight() * 0.5, 0);
                    Vec3 dir = center.subtract(cPoint).normalize();

                    if (dir.y < MIN_UPWARD) {
                        dir = new Vec3(dir.x, MIN_UPWARD, dir.z).normalize();
                    }

                    Vec3 newVel = dir.scale(BASE_SPEED);
                    this.setDeltaMovement(newVel);

                } else {
                    if (!this.level().isClientSide)
                        this.level().broadcastEntityEvent(this, (byte) 3);

                    this.discard();
                }
            } else {
                doMove(vel);
            }
        } else if (blockDist < Double.MAX_VALUE) {
            Direction face = blockResult.getDirection();
            Vec3 normal = new Vec3(face.getStepX(), face.getStepY(), face.getStepZ());

            double dot = vel.dot(normal);
            Vec3 reflected = vel.subtract(normal.scale(2 * dot));

            if (face == Direction.UP || face == Direction.DOWN) {
                reflected = new Vec3(reflected.x, reflected.y * BOUNCE_DAMPING_VERTICAL, reflected.z);
            } else {
                reflected = new Vec3(reflected.x * BOUNCE_DAMPING_HORIZONTAL, reflected.y, reflected.z * BOUNCE_DAMPING_HORIZONTAL);
            }

            blockBounceCount++;
            if (blockBounceCount >= MAX_BLOCK_BOUNCES) {
                reflected = Vec3.ZERO;
            }

            Vec3 impactPos = blockResult.getLocation().add(normal.scale(0.02));
            this.setPos(impactPos.x, impactPos.y, impactPos.z);

            doMove(reflected);
        } else {
            doMove(vel);
        }

        if (tickCount % 3 == 0) this.level().addParticle(CompanionsParticles.EMBER.get(), getX() + getDeltaMovement().x, getY() - getBbHeight() * 0.5, getZ() + getDeltaMovement().z, 0.0, 0.0, 0.0);
    }

    private void spawnSmokeParticles(Vec3 spawnPos) {
        for (int i = 0; i < 6; i++) {
            double speedX = (this.random.nextDouble() - 0.5) * 0.1;
            double speedY = (this.random.nextDouble() - 0.5) * 0.1;
            double speedZ = (this.random.nextDouble() - 0.5) * 0.1;

            this.level().addParticle(CompanionsParticles.EMBER.get(), this.getX(), this.getY(), this.getZ(), speedX, speedY, speedZ);
        }
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 3) {
            spawnSmokeParticles(null);
        } else {
            super.handleEntityEvent(pId);
        }
    }

    private void doMove(Vec3 vel) {
        if (this.onGround() && Math.abs(vel.y) < 0.001 && vel.horizontalDistanceSqr() < 0.0009) vel = Vec3.ZERO;

        this.setDeltaMovement(vel);
        this.move(MoverType.SELF, vel);
    }

    private LivingEntity findNextTarget() {
        AABB area = new AABB(
                this.getX() - SEARCH_RADIUS, this.getY() - SEARCH_RADIUS, this.getZ() - SEARCH_RADIUS,
                this.getX() + SEARCH_RADIUS, this.getY() + SEARCH_RADIUS, this.getZ() + SEARCH_RADIUS
        );

        List<LivingEntity> candi = level().getEntitiesOfClass(LivingEntity.class, area, e -> e.isAlive() && e != this.getOwner() && !hitEntityIds.contains(e.getId()));

        if (candi.isEmpty()) return null;

        double minDist = Double.MAX_VALUE;
        LivingEntity nearest = null;

        for (LivingEntity c : candi) {
            double dist = c.distanceToSqr(this);
            if (dist < minDist) {
                minDist = dist;
                nearest = c;
            }
        }

        return nearest;
    }

    protected boolean canHitEntity(@NotNull Entity e) {
        if (e == this.getOwner()) return false;
        if (hitEntityIds.contains(e.getId())) return false;
        return e instanceof LivingEntity;
    }

    @Override
    protected void defineSynchedData() { ;; }

    @Override
    public void playerTouch(@NotNull Player player) { ;; }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) { ;; }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return super.getAddEntityPacket();
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

}
