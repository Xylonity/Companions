package dev.xylonity.companions.common.entity.ai.antlion.tamable.goal;

import dev.xylonity.companions.common.entity.ai.antlion.tamable.AbstractAntlionAttackGoal;
import dev.xylonity.companions.common.entity.custom.AntlionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class AntlionAdultAttackGoal extends AbstractAntlionAttackGoal {

    private static final int TOTAL_DURATION = 80;
    private static final int ASCEND_DURATION = 65;
    private static final int DIVE_DURATION = TOTAL_DURATION - ASCEND_DURATION;

    private Vec3 hoverPoint;
    private double ascendSpeed;
    private double diveSpeed;
    private double groundLevel;
    private boolean hasStartedDive;

    public AntlionAdultAttackGoal(AntlionEntity antlion, int minCd, int maxCd) {
        super(antlion, TOTAL_DURATION, minCd, maxCd);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && antlion.getVariant() == 2 && antlion.getTarget() != null && antlion.distanceToSqr(antlion.getTarget()) <= 16 * 16;
    }

    @Override
    public void start() {
        super.start();
        hasStartedDive = false;

        Vec3 targetPos;
        if (antlion.getTarget() != null) {
            targetPos = antlion.getTarget().position();
        } else {
            targetPos = antlion.position();
        }

        hoverPoint = targetPos.add(0, 10, 0);

        ascendSpeed = antlion.position().distanceTo(hoverPoint) / ASCEND_DURATION;

        antlion.setAttackType(1);
    }

    @Override
    public void tick() {
        super.tick();

        LivingEntity target = antlion.getTarget();
        if (target == null || !target.isAlive()) {
            stop();
            return;
        }

        if (!hasStartedDive) {
            Vec3 delta = hoverPoint.subtract(antlion.position());
            double remaining = delta.length();

            if (remaining <= 0.3) {
                startDive();
            } else {
                Vec3 motion = delta.normalize().scale(Math.min(ascendSpeed, remaining));
                antlion.setDeltaMovement(motion);
                antlion.move(MoverType.SELF, motion);
            }
        } else {
            antlion.getNavigation().stop();
            antlion.setDeltaMovement(0, diveSpeed, 0);

            if (antlion.onGround() || antlion.getY() <= groundLevel) {
                stop();
                return;
            }

        }

        if (attackTicks >= TOTAL_DURATION) {
            stop();
        }

    }

    @Override
    public void stop() {
        super.stop();
        antlion.setAttackType(0);
        hasStartedDive = false;
    }

    private void startDive() {
        antlion.cycleState();

        groundLevel = getGroundLevel();
        double distanceToGround = antlion.getY() - groundLevel;

        diveSpeed = -(distanceToGround / DIVE_DURATION);

        hasStartedDive = true;
    }

    private double getGroundLevel() {
        BlockPos.MutableBlockPos pos = antlion.blockPosition().mutable();
        int y = pos.getY();
        while (y > antlion.level().getMinBuildHeight()) {
            pos.setY(--y);
            BlockState state = antlion.level().getBlockState(pos);
            if (!state.getCollisionShape(antlion.level(), pos).isEmpty()) {
                return y + 1;
            }
        }

        return antlion.level().getMinBuildHeight();
    }

    @Override
    protected int attackDelay() {
        return -1;
    }

    @Override
    protected int attackType() {
        return 1;
    }

    @Override
    protected int variant() {
        return 2;
    }

    @Override
    protected void performAttack(LivingEntity target) {

    }

}
