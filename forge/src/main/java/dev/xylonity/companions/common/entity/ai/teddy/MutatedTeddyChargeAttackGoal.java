package dev.xylonity.companions.common.entity.ai.teddy;

import dev.xylonity.companions.common.entity.custom.TeddyEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class MutatedTeddyChargeAttackGoal extends Goal {
    private enum AttackState {
        WIND_UP,
        CHARGING,
        RETREAT
    }

    private final TeddyEntity teddy;
    private LivingEntity target;

    // Parámetros de comportamiento
    private final double chargeSpeed;       // Velocidad al embestir
    private final double retreatSpeed;      // Velocidad al retirarse
    private final int windUpDuration;       // Ticks de “preparación”
    private final int retreatDuration;      // Ticks de “retirada”
    private final double hitRange;          // Distancia para hacer daño
    private final double retreatDistance;   // Bloques que se aleja tras golpear

    // Contadores / estados internos
    private int stateTicks;                 // Cuenta ticks en cada estado
    private AttackState state = AttackState.WIND_UP;

    public MutatedTeddyChargeAttackGoal(
            TeddyEntity teddy,
            double chargeSpeed,
            double retreatSpeed,
            int windUpDuration,
            int retreatDuration,
            double hitRange,
            double retreatDistance
    ) {
        this.teddy = teddy;
        this.chargeSpeed = chargeSpeed;
        this.retreatSpeed = retreatSpeed;
        this.windUpDuration = windUpDuration;
        this.retreatDuration = retreatDuration;
        this.hitRange = hitRange;
        this.retreatDistance = retreatDistance;

        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        // Activar si:
        // 1) Fase 2
        if (this.teddy.getPhase() != 2 || this.teddy.isSitting()) {
            return false;
        }
        // 2) Tenemos target vivo
        LivingEntity possibleTarget = this.teddy.getTarget();
        if (possibleTarget == null || !possibleTarget.isAlive()) {
            return false;
        }
        this.target = possibleTarget;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        // Continuar mientras:
        // 1) Fase 2
        // 2) Target existe y está vivo
        if (this.teddy.getPhase() != 2 || this.teddy.isSitting()) {
            return false;
        }
        return this.target != null && this.target.isAlive();
    }

    @Override
    public void start() {
        this.state = AttackState.WIND_UP;
        this.stateTicks = 0;

        // Aseguramos colisiones (no atraviesa bloques)
        //this.teddy.noPhysics = false;
        // Si “flota” en fase 2
        this.teddy.setNoGravity(true);
    }

    @Override
    public void stop() {
        // Limpiamos
        this.target = null;
        this.state = AttackState.WIND_UP;
        this.stateTicks = 0;
    }

    @Override
    public void tick() {
        if (this.target == null) {
            return;
        }

        // Si en algún momento el target muere, paramos
        if (!this.target.isAlive()) {
            // Esto forzará la IA a volver a FollowOwnerGoal
            this.teddy.setTarget(null);
            return;
        }

        // Mira al target
        this.teddy.getLookControl().setLookAt(this.target, 30.0F, 30.0F);

        switch (this.state) {
            case WIND_UP:
                doWindUp();
                break;
            case CHARGING:
                doCharging();
                break;
            case RETREAT:
                doRetreat();
                break;
        }
    }

    private void doWindUp() {
        // Mantente quieto o muévete muy poco
        this.teddy.getMoveControl().setWantedPosition(
                this.teddy.getX(),
                this.teddy.getY(),
                this.teddy.getZ(),
                0.0
        );

        this.stateTicks++;
        // Pasar a CHARGING tras windUpDuration
        if (this.stateTicks >= this.windUpDuration) {
            this.state = AttackState.CHARGING;
            this.stateTicks = 0;
        }
    }

    private void doCharging() {
        // Moverse hacia el target con "deslizamiento"
        double distSqr = this.teddy.distanceToSqr(this.target);
        double hitRangeSqr = this.hitRange * this.hitRange;

        if (distSqr > hitRangeSqr) {
            // Aún lejos => acércate con chargeSpeed
            this.teddy.getMoveControl().setWantedPosition(
                    this.target.getX(),
                    this.target.getY(),
                    this.target.getZ(),
                    this.chargeSpeed
            );
        } else {
            // En rango de golpe => daña
            this.teddy.swing(InteractionHand.MAIN_HAND);
            this.teddy.doHurtTarget(this.target);
            // Pasamos a RETREAT
            this.state = AttackState.RETREAT;
            this.stateTicks = 0;
        }
    }

    private void doRetreat() {
        // Alejarse "retreatDistance" bloques en línea Teddy->Target invertida
        double dx = this.teddy.getX() - this.target.getX();
        double dy = this.teddy.getY() - this.target.getY();
        double dz = this.teddy.getZ() - this.target.getZ();
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dist < 0.1) {
            dist = 0.1;
        }

        double rx = this.teddy.getX() + (dx / dist) * this.retreatDistance;
        double ry = this.teddy.getY() + (dy / dist) * this.retreatDistance;
        double rz = this.teddy.getZ() + (dz / dist) * this.retreatDistance;

        // Muévete rápido al punto de retirada
        this.teddy.getMoveControl().setWantedPosition(
                rx, ry, rz, this.retreatSpeed
        );

        // Contamos ticks de retirada
        this.stateTicks++;
        // Al cabo de retreatDuration ticks, volvemos a wind-up para otro ataque
        if (this.stateTicks >= this.retreatDuration) {
            this.state = AttackState.WIND_UP;
            this.stateTicks = 0;
        }
    }

}