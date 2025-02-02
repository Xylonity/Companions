package dev.xylonity.companions.common.entity.ai.teddy;

import dev.xylonity.companions.common.entity.custom.TeddyEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class TeddyAttackGoal extends Goal {
    private final TeddyEntity teddy;

    // Velocidad con la que nos movemos hacia el objetivo
    private final double speedModifier;
    // Distancia a la que se puede golpear
    private final double attackRange;
    // Cooldown en ticks entre golpes
    private final int attackCooldown;

    // Contador para limitar la frecuencia de golpes
    private int attackTime;

    public TeddyAttackGoal(TeddyEntity teddy, double speedModifier, double attackRange, int attackCooldown) {
        this.teddy = teddy;
        this.speedModifier = speedModifier;
        this.attackRange = attackRange;
        this.attackCooldown = attackCooldown;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        // Solo ataca si está en fase 2
        if (this.teddy.getPhase() != 2) {
            return false;
        }
        // Ver si hay un target ya asignado (por OwnerHurtTargetGoal u otro)
        LivingEntity target = this.teddy.getTarget();
        if (target == null) {
            return false;
        }
        // Comprobar si está vivo
        return target.isAlive();
    }

    @Override
    public boolean canContinueToUse() {
        // Seguimos mientras:
        //   - Fase 2
        //   - Siga existiendo un target vivo
        if (this.teddy.getPhase() != 2) {
            return false;
        }
        LivingEntity target = this.teddy.getTarget();
        return (target != null && target.isAlive());
    }

    @Override
    public void start() {
        // Opcional: aseguramos colisiones en fase 2
        this.teddy.noPhysics = false;
        // Si tu Teddy debe volar en fase 2
        this.teddy.setNoGravity(true);

        this.attackTime = 0;
    }

    @Override
    public void stop() {
        // Al terminar, podemos limpiar el movimiento
        this.teddy.getMoveControl().setWantedPosition(
                this.teddy.getX(),
                this.teddy.getY(),
                this.teddy.getZ(),
                0.0
        );
    }

    @Override
    public void tick() {
        LivingEntity target = this.teddy.getTarget();
        if (target == null) {
            return;
        }

        // Reducimos el contador de cooldown de ataque
        if (this.attackTime > 0) {
            this.attackTime--;
        }

        // Mira al objetivo
        this.teddy.getLookControl().setLookAt(target, 30.0F, 30.0F);

        // Calculamos la distancia (al cuadrado) al target
        double distSqr = this.teddy.distanceToSqr(target);
        double rangeSqr = this.attackRange * this.attackRange;

        // Si estamos FUERA de rango de ataque, nos acercamos
        if (distSqr > rangeSqr) {
            this.teddy.getMoveControl().setWantedPosition(
                    target.getX(),
                    target.getY(),
                    target.getZ(),
                    this.speedModifier
            );
        } else {
            // En rango de ataque => detenemos avance
            this.teddy.getMoveControl().setWantedPosition(
                    this.teddy.getX(),
                    this.teddy.getY(),
                    this.teddy.getZ(),
                    0.0
            );

            // Intentamos golpear si cooldown llegó a 0
            if (this.attackTime <= 0) {
                // Golpear
                this.teddy.doHurtTarget(target);
                // Reiniciamos cooldown
                this.attackTime = this.attackCooldown;
            }
        }
    }
}