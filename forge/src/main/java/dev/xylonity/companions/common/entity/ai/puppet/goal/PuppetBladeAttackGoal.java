package dev.xylonity.companions.common.entity.ai.puppet.goal;

import dev.xylonity.companions.common.entity.ai.puppet.AbstractPuppetAttackGoal;
import dev.xylonity.companions.common.entity.custom.PuppetEntity;
import dev.xylonity.companions.registry.CompanionsItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class PuppetBladeAttackGoal extends AbstractPuppetAttackGoal {

    public PuppetBladeAttackGoal(PuppetEntity puppet, int minCd, int maxCd) {
        super(puppet, minCd, maxCd, "BLADE");
    }

    @Override
    protected void performAttack(LivingEntity target) {


    }

    @Override
    public boolean canUse() {
        return super.canUse() && this.puppet.getOwner() != null;
    }

    @Override
    protected boolean hasRequiredArm() {
        for (int i = 0; i < puppet.inventory.getContainerSize(); i++) {
            ItemStack stack = puppet.inventory.getItem(i);
            if (stack.getItem() == CompanionsItems.BLADE_ARM.get()) {
                return true;
            }
        }

        return false;
    }
}