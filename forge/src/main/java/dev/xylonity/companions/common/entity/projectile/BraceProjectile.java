package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.BaseProjectile;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BraceProjectile extends BaseProjectile {
    private static final double BOUNCE_DAMPING_VERTICAL = 0.6;
    private static final double BOUNCE_DAMPING_HORIZONTAL = 0.7;
    private static final int MAX_BOUNCES = 5;
    private static final double MIN_UPWARD = 0.4;

    private final List<Integer> hitEntities = new ArrayList<>();

    private int entityBounces = 0;
    private int blockBounces = 0;

    public BraceProjectile(EntityType<? extends BaseProjectile> type, Level level) {
        super(type, level);
        this.noPhysics = false;
    }

    @Override
    public void tick() {
        super.tick();

        if (tickCount > getLifetime() || entityBounces >= MAX_BOUNCES) {
            if (!this.level().isClientSide)
                this.level().broadcastEntityEvent(this, (byte) 3);

            this.remove(RemovalReason.DISCARDED);
        }

        // We update the velocity here (simulating gravity and friction)
        Vec3 vel = getDeltaMovement().add(0, -0.03, 0).scale(0.98);
        Vec3 start = position();
        Vec3 end = start.add(vel);

        // And detect collisions from both entities and blocks
        HitResult entityHit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        // TODO: block hit are not always detected
        BlockHitResult blockHit = level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));

        double entityDist = entityHit.getType() == HitResult.Type.ENTITY ? entityHit.getLocation().distanceToSqr(start) : Double.MAX_VALUE;
        double blockDist = blockHit.getType() != HitResult.Type.MISS ? blockHit.getLocation().distanceToSqr(start) : Double.MAX_VALUE;

        // We check the distances between collisions to arbitrary select one of each (or just normally move)
        if (entityDist < blockDist) {
            handleEntityHit((EntityHitResult) entityHit);
        } else if (blockDist < Double.MAX_VALUE) {
            handleBlockHit(blockHit);
        } else {
            move(vel);
        }

        // Trail
        if (tickCount % 3 == 0) {
            level().addParticle(CompanionsParticles.EMBER.get(), getX(), getY() - getBbHeight() * 0.5, getZ(), 0, 0, 0);
        }

        if (level().isClientSide) {
            if ((this.tickCount % 50 == 0 || this.tickCount == 1)) {
                float r = 250f / 255f;
                float g = (203 + level().random.nextInt(10)) / 255f;
                float b = (138 + level().random.nextInt(10)) / 255f;
                Companions.PROXY.spawnGenericRibbonTrail(this, level(), getX(), getY(), getZ(), r, g, b, 0, 0.35f);
            }
        }

    }

    private void handleEntityHit(EntityHitResult hit) {
        Entity entity = hit.getEntity();
        if (!(entity instanceof LivingEntity target) || Util.areEntitiesLinked(target, this) || hitEntities.contains(entity.getId())) {
            move(getDeltaMovement());
            return;
        }

        // Hurts the trigger entity and caches its id so the projectile doesn't attack it again and continues bouncing
        target.hurt(damageSources().thrown(this, getOwner()), (float) CompanionsConfig.BRACE_PROJECTILE_DAMAGE);
        target.setRemainingFireTicks(random.nextInt(1, 10) * 20);
        hitEntities.add(target.getId());
        entityBounces++;

        // We should stop the projectile before rethrowing it again
        setPos(hit.getLocation());

        Vec3 bouncePos = new Vec3(target.getX(), target.getEyeY(), target.getZ());
        setPos(bouncePos);

        setDeltaMovement(Vec3.ZERO);

        if (entityBounces >= MAX_BOUNCES) {
            spawnParticles();
            discard();
            return;
        }

        // If we couldn't find another, the projectile will disappear
        LivingEntity next = findNextTarget();
        if (next == null) {
            spawnParticles();
            discard();
            return;
        }

        // Throws the projectile towards the nearest entity
        Vec3 direction = next.position().add(0, next.getBbHeight() * 0.5, 0).subtract(position()).normalize();

        if (direction.y < MIN_UPWARD) {
            direction = new Vec3(direction.x, MIN_UPWARD, direction.z).normalize();
        }

        setDeltaMovement(direction.scale(0.6));
    }

    private void handleBlockHit(BlockHitResult hit) {
        // We simulate a 'bounce' reflection on block hit
        Direction face = hit.getDirection();
        Vec3 normal = new Vec3(face.getStepX(), face.getStepY(), face.getStepZ());
        Vec3 reflected = this.getDeltaMovement().subtract(normal.scale(2 * this.getDeltaMovement().dot(normal)));

        // Extra damping
        if (face.getAxis().isVertical()) {
            reflected = new Vec3(reflected.x, reflected.y * BOUNCE_DAMPING_VERTICAL, reflected.z);
        } else {
            reflected = new Vec3(reflected.x * BOUNCE_DAMPING_HORIZONTAL, reflected.y, reflected.z * BOUNCE_DAMPING_HORIZONTAL);
        }

        blockBounces++;
        if (blockBounces >= MAX_BOUNCES) reflected = Vec3.ZERO;

        // We prevent the projectile getting stuck
        setPos(hit.getLocation().add(normal.scale(0.02)));
        move(reflected);
    }

    private void spawnSmokeParticles() {
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
            spawnSmokeParticles();
        } else {
            super.handleEntityEvent(pId);
        }
    }

    private void spawnParticles() {
        if (!level().isClientSide) return;

        for (int i = 0; i < 6; i++) {
            float speed = 0.1f;
            level().addParticle(CompanionsParticles.EMBER.get(), getX(), getY(), getZ(), (random.nextDouble() - 0.5) * speed, (random.nextDouble() - 0.5) * speed, (random.nextDouble() - 0.5) * speed);
        }

    }

    private LivingEntity findNextTarget() {
        // We should find the nearest entity from the one that triggered the entity hit
        return level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(10),
                        e -> e.isAlive() && !Util.areEntitiesLinked(this, e) && !hitEntities.contains(e.getId()))
                .stream()
                .min(Comparator.comparingDouble(e -> e.distanceToSqr(this)))
                .orElse(null);
    }

    private void move(Vec3 vel) {
        if (onGround() && Math.abs(vel.y) < 0.001 && vel.horizontalDistanceSqr() < 0.0009) {
            vel = Vec3.ZERO;
        }

        setDeltaMovement(vel);
        move(MoverType.SELF, vel);
    }

    protected boolean canHitEntity(@NotNull Entity e) {
        if (Util.areEntitiesLinked(this, e)) return false;
        if (e == this.getOwner() || e == this) return false;
        if (hitEntities.contains(e.getId())) return false;
        return e instanceof LivingEntity;
    }

    @Override
    public void playerTouch(@NotNull Player player) { ;; }

    @Override
    protected int baseLifetime() {
        return 200;
    }

}
