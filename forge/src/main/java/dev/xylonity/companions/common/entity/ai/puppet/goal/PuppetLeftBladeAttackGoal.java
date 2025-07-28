package dev.xylonity.companions.common.entity.ai.puppet.goal;

import dev.xylonity.companions.common.entity.ai.puppet.AbstractPuppetLeftAttackGoal;
import dev.xylonity.companions.common.entity.ai.puppet.AbstractPuppetRightAttackGoal;
import dev.xylonity.companions.common.entity.companion.PuppetEntity;
import dev.xylonity.companions.registry.CompanionsItems;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.world.entity.LivingEntity;

public class PuppetLeftBladeAttackGoal extends AbstractPuppetLeftAttackGoal {

    public PuppetLeftBladeAttackGoal(PuppetEntity puppet, int minCd, int maxCd) {
        super(puppet, minCd, maxCd, "BLADE");
    }

    @Override
    protected void performAttack(LivingEntity target) {
        if (target != null) {
            puppet.doHurtTarget(target);
        }

    }

    @Override
    public void start() {
        super.start();
        puppet.playSound(CompanionsSounds.PUPPET_ATTACK_BLADE.get());
    }

    @Override
    public boolean canUse() {
        return super.canUse() && this.puppet.getTarget() != null && puppet.distanceToSqr(puppet.getTarget()) <= 4;
    }

    @Override
    protected boolean hasRequiredArm() {
        return puppet.inventory.getItem(1).is(CompanionsItems.BLADE_ARM.get());
    }

}