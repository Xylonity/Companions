package dev.xylonity.companions.common.entity.ai.teddy;

import dev.xylonity.companions.common.entity.custom.TeddyEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class MutatedTeddyFollowOwnerGoal extends Goal {
    // Distancia mínima al dueño
    private final double minDistance;
    // Distancia a partir de la cual se activa el seguimiento
    private final double startDistance;
    // Velocidad máxima base
    private final double maxSpeedModifier;

    private final TeddyEntity teddy;
    private final PathNavigation navigation;

    private LivingEntity owner;

    public MutatedTeddyFollowOwnerGoal(TeddyEntity teddy, double maxSpeedModifier, double minDistance, double startDistance) {
        this.teddy = teddy;
        this.maxSpeedModifier = maxSpeedModifier;
        this.minDistance = minDistance;
        this.startDistance = startDistance;

        this.navigation = teddy.getNavigation();

        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        // Solo fase 2
        if (this.teddy.getPhase() != 2) {
            return false;
        }
        LivingEntity possibleOwner = this.teddy.getOwner();
        if (possibleOwner == null || !possibleOwner.isAlive()) {
            return false;
        }
        double distSqr = this.teddy.distanceToSqr(possibleOwner);
        if (distSqr < (this.startDistance * this.startDistance)) {
            return false;
        }
        this.owner = possibleOwner;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.teddy.getPhase() != 2 || this.owner == null || !this.owner.isAlive()) {
            return false;
        }
        double distSqr = this.teddy.distanceToSqr(this.owner);
        return distSqr > (this.minDistance * this.minDistance);
    }

    @Override
    public void start() {
        this.navigation.stop();
    }

    @Override
    public void stop() {
        this.owner = null;
        this.navigation.stop();
        // Asegurarse de dejar la colisión activa al salir
        this.teddy.noPhysics = false;
    }

    @Override
    public void tick() {
        if (this.owner == null) return;

        this.teddy.getLookControl().setLookAt(this.owner, 10.0F, this.teddy.getMaxHeadXRot());

        double dx = this.owner.getX() - this.teddy.getX();
        double dy = (this.owner.getY() + 2.0) - this.teddy.getY();
        double dz = this.owner.getZ() - this.teddy.getZ();
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (dist <= this.minDistance) {
            Vec3 currentMovement = this.teddy.getDeltaMovement();
            this.teddy.setDeltaMovement(currentMovement.scale(0.9));
            if (currentMovement.lengthSqr() < 0.01) {
                this.teddy.getMoveControl().setWantedPosition(
                        this.teddy.getX(),
                        this.teddy.getY(),
                        this.teddy.getZ(),
                        0.0
                );
                this.navigation.stop();
            }
            // Cuando está cerca, que no atraviese paredes
            this.teddy.noPhysics = false;
            return;
        }

        double targetX = this.owner.getX() - (dx / dist) * this.minDistance;
        double targetY = (this.owner.getY() + 2.0) - (dy / dist) * this.minDistance;
        double targetZ = this.owner.getZ() - (dz / dist) * this.minDistance;

        double factor;
        if (dist >= this.startDistance) {
            factor = 1.0;
        } else {
            double range = this.startDistance - this.minDistance;
            double offset = dist - this.minDistance;
            factor = Mth.clamp(offset / range, 0.0, 1.0);
        }
        double effectiveSpeed = this.maxSpeedModifier * factor;

        boolean blocked = isPathBlocked(
                this.teddy.level(),
                this.teddy.getEyePosition(),
                new Vec3(targetX, targetY, targetZ)
        );

        if (!blocked) {
            // SIN obstáculo => movimiento directo con colisión normal
            this.teddy.noPhysics = false;
            this.navigation.stop();
            this.teddy.getMoveControl().setWantedPosition(
                    targetX, targetY, targetZ, effectiveSpeed
            );
        } else {
            // BLOQUEADO => atravesar el bloque
            // 1) Ponemos noPhysics = true (puede atravesar)
            this.teddy.noPhysics = true;

            // 2) Hacemos movimiento directo sin pathfinding
            this.navigation.stop();
            this.teddy.getMoveControl().setWantedPosition(
                    targetX, targetY, targetZ, effectiveSpeed
            );
        }
    }

    private boolean isPathBlocked(Level level, Vec3 from, Vec3 to) {
        BlockHitResult hitResult = level.clip(new ClipContext(
                from,
                to,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                this.teddy
        ));
        return (hitResult.getType() != HitResult.Type.MISS);
    }
}
