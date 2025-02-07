package dev.xylonity.companions.common.entity.ai.teddy;

import dev.xylonity.companions.common.entity.custom.TeddyEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class TeddySitWhenOrderedToGoal extends Goal {
    private final TeddyEntity mob;

    public TeddySitWhenOrderedToGoal(TeddyEntity pMob) {
        this.mob = pMob;
        this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!this.mob.isTame()) {
            return false;
        }
        if (this.mob.isInWaterOrBubble()) {
            return false;
        }

        // Si NO está en fase 2, usamos la lógica vanilla (requiere onGround)
        TeddyEntity teddy = this.mob;
        if (teddy.getPhase() != 2) {
            // Lógica normal
            if (!this.mob.onGround()) {
                return false;
            }
        }

        LivingEntity owner = this.mob.getOwner();
        if (owner == null) {
            return this.mob.isOrderedToSit();
        }
        // Evitar sentarse si el dueño está en combate cerca
        if (this.mob.distanceToSqr(owner) < 144.0 && owner.getLastHurtByMob() != null) {
            return false;
        }
        // Finalmente, si todo lo anterior da OK, verificamos si está ordenada a sentarse
        return this.mob.isOrderedToSit();
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.isOrderedToSit();
    }

    @Override
    public void start() {
        // Si estamos en fase 2 y volando, buscamos un bloque donde posarnos
        if (mob.getPhase() == 2) {
            // Encuentra el bloque más cercano debajo
            BlockPos groundPos = findClosestGroundBelow(mob);
            if (groundPos != null) {
                // Teletransportamos (o ajustamos) la posición del Teddy justo encima de ese bloque
                double x = groundPos.getX() + 0.5;
                double y = groundPos.getY() + 1.0;
                double z = groundPos.getZ() + 0.5;
                mob.teleportTo(x, y, z);
            }
        }

        // Una vez en tierra, paramos cualquier navegación
        this.mob.getNavigation().stop();
        // Y ahora sí, sentarse
        this.mob.setInSittingPose(true);
    }

    @Override
    public void stop() {
        this.mob.setInSittingPose(false);
    }

    /**
     * Busca el bloque sólido más cercano hacia abajo (hasta un límite de altura).
     */
    private BlockPos findClosestGroundBelow(TeddyEntity entity) {
        // Partimos de la posición actual
        BlockPos start = entity.blockPosition();

        // Buscamos hasta 20 bloques hacia abajo, por ejemplo
        for (int i = 0; i < 20; i++) {
            BlockPos checkPos = start.below(i);
            if (entity.level().getBlockState(checkPos).blocksMotion()) {
                // Este es un bloque sólido, devolvemos su posición
                return checkPos;
            }
        }
        // Si no encontramos nada, devolvemos null
        return null;
    }
}
