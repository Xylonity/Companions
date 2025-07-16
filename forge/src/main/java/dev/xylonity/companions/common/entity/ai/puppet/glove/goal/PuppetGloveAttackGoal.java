package dev.xylonity.companions.common.entity.ai.puppet.glove.goal;

import dev.xylonity.companions.common.entity.ai.puppet.glove.AbstractPuppetGloveAttackGoal;
import dev.xylonity.companions.common.entity.companion.PuppetGloveEntity;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.world.entity.LivingEntity;

public class PuppetGloveAttackGoal extends AbstractPuppetGloveAttackGoal {

    public PuppetGloveAttackGoal(PuppetGloveEntity glove, int minCd, int maxCd) {
        super(glove, 15, minCd, maxCd);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && !glove.isTransforming();
    }

    @Override
    public void start() {
        super.start();
        glove.setNoMovement(true);
        glove.playSound(CompanionsSounds.PUPPET_GLOVE_SHOOT.get());
    }

    @Override
    public void stop() {
        super.stop();
        glove.setNoMovement(false);
    }

    @Override
    protected void performAttack(LivingEntity target) {
        if (glove.hasLineOfSight(target) && !Util.areEntitiesLinked(glove, target))
            glove.doHurtTarget(target);
    }

    @Override
    protected int attackDelay() {
        return 7;
    }

}