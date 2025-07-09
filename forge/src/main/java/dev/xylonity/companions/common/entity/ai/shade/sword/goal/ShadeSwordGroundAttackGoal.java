package dev.xylonity.companions.common.entity.ai.shade.sword.goal;

import dev.xylonity.companions.common.entity.ShadeEntity;
import dev.xylonity.companions.common.entity.ai.shade.AbstractShadeAttackGoal;
import dev.xylonity.companions.common.entity.companion.ShadeSwordEntity;
import dev.xylonity.companions.common.entity.projectile.ShadeSwordImpactProjectile;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class ShadeSwordGroundAttackGoal extends AbstractShadeAttackGoal {

    public ShadeSwordGroundAttackGoal(ShadeEntity boss, int minCd, int maxCd) {
        super(boss, 42, minCd, maxCd);
    }

    @Override
    public void start() {
        super.start();
        shade.setNoMovement(true);
    }

    @Override
    public void stop() {
        super.stop();
        shade.setNoMovement(false);
        shade.setDeltaMovement(Vec3.ZERO);
        shade.setShouldLookAtTarget(true);
    }

    @Override
    public boolean canUse() {
        if (!super.canUse()) return false;
        if (shade.getTarget() == null || shade.distanceTo(shade.getTarget()) >= 8.0D) return false;

        int groundY = shade.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, shade.blockPosition()).getY();
        return shade.getY() - groundY <= 1.5D;
    }

    @Override
    protected int getAttackType() {
        return 3;
    }

    @Override
    protected void performAttack(LivingEntity target) {
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            ShadeSwordImpactProjectile impact = CompanionsEntities.SHADE_SWORD_IMPACT_PROJECTILE.get().create(shade.level());
            if (impact != null) {
                BlockPos base = shade.blockPosition().relative(dir);

                impact.setOwner(shade);
                impact.moveTo(base.getX() + 0.5D, Math.min(shade.getY(), shade.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, base).getY() + 0.1D), base.getZ() + 0.5D, dir.toYRot(), 0.0F);
                impact.setDirection(dir);

                impact.setDeltaMovement(new Vec3(dir.getStepX(), 0, dir.getStepZ()).scale(0.375));

                shade.level().addFreshEntity(impact);
            }
        }
    }

    @Override
    protected int attackDelay() {
        return 14;
    }

    @Override
    protected Class<? extends ShadeEntity> shadeType() {
        return ShadeSwordEntity.class;
    }

}