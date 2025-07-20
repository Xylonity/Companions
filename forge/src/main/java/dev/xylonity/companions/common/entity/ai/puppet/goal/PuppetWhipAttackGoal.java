package dev.xylonity.companions.common.entity.ai.puppet.goal;

import dev.xylonity.companions.common.entity.ai.puppet.AbstractPuppetAttackGoal;
import dev.xylonity.companions.common.entity.companion.PuppetEntity;
import dev.xylonity.companions.registry.CompanionsEffects;
import dev.xylonity.companions.registry.CompanionsItems;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class PuppetWhipAttackGoal extends AbstractPuppetAttackGoal {

    public PuppetWhipAttackGoal(PuppetEntity puppet, int minCd, int maxCd) {
        super(puppet, minCd, maxCd, "BLADE");
    }

    @Override
    protected void performAttack(LivingEntity target) {
        if (target != null) {
            puppet.doHurtTarget(target);
            target.addEffect(new MobEffectInstance(MobEffects.POISON, puppet.getRandom().nextInt(20, 200), 0, true, true, true));
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
    protected int hasRequiredArm() {
        for (int i = 0; i < puppet.inventory.getContainerSize(); i++) {
            ItemStack stack = puppet.inventory.getItem(i);
            if (stack.getItem() == CompanionsItems.WHIP_ARM.get()) {
                return i + 1;
            }
        }

        return 0;
    }

}