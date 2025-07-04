package dev.xylonity.companions.common.entity.ai.antlion.tamable.goal;

import dev.xylonity.companions.common.entity.ai.antlion.tamable.AbstractAntlionAttackGoal;
import dev.xylonity.companions.common.entity.ai.cloak.AbstractCloakAttackGoal;
import dev.xylonity.companions.common.entity.custom.AntlionEntity;
import dev.xylonity.companions.common.entity.custom.CloakEntity;
import dev.xylonity.companions.registry.CompanionsEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class AntlionBaseAttackGoal extends AbstractAntlionAttackGoal {

    public AntlionBaseAttackGoal(AntlionEntity antlion, int minCd, int maxCd) {
        super(antlion, 15, minCd, maxCd);
    }

    @Override
    public void start() {
        super.start();
        antlion.setNoMovement(true);
    }

    @Override
    public void stop() {
        super.stop();
        antlion.setNoMovement(false);
    }

    @Override
    public boolean canUse() {
        LivingEntity target = antlion.getTarget();
        if (target != null && antlion.distanceToSqr(target) > 9) {
            return false;
        }

        if (antlion.getVariant() == 0 && antlion.getState() == 0 && target != null) {
            antlion.cycleState();
            return false;
        }

        if (!antlion.isUnderground()) {
            return false;
        }

        return super.canUse();
    }

    @Override
    protected void performAttack(LivingEntity target) {
        antlion.doHurtTarget(target);
    }

    @Override
    protected int attackDelay() {
        return 7;
    }

    @Override
    protected int attackType() {
        return 1;
    }

    @Override
    protected int variant() {
        return 0;
    }

}