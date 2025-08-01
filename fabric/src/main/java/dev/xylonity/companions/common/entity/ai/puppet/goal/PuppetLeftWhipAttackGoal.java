package dev.xylonity.companions.common.entity.ai.puppet.goal;

import dev.xylonity.companions.common.entity.ai.puppet.AbstractPuppetLeftAttackGoal;
import dev.xylonity.companions.common.entity.companion.PuppetEntity;
import dev.xylonity.companions.registry.CompanionsItems;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class PuppetLeftWhipAttackGoal extends AbstractPuppetLeftAttackGoal {

    public PuppetLeftWhipAttackGoal(PuppetEntity puppet, int minCd, int maxCd) {
        super(puppet, minCd, maxCd, "BLADE");
    }

    @Override
    protected void performAttack(LivingEntity target) {
        if (target != null) {
            puppet.doHurtTarget(target);
            target.addEffect(new MobEffectInstance(MobEffects.POISON, puppet.getRandom().nextInt(20, 200), 0, true, true, true));
            target.knockback(0.75f, -(target.getX() - puppet.getX()), -(target.getZ() - puppet.getZ()));
        }

    }

    @Override
    public void start() {
        super.start();
        puppet.playSound(CompanionsSounds.PUPPET_ATTACK_WHIP.get());
    }

    @Override
    public boolean canUse() {
        return super.canUse() && this.puppet.getTarget() != null && puppet.distanceToSqr(puppet.getTarget()) <= 4;
    }

    @Override
    protected boolean hasRequiredArm() {
        return puppet.inventory.getItem(1).is(CompanionsItems.WHIP_ARM.get());
    }

}