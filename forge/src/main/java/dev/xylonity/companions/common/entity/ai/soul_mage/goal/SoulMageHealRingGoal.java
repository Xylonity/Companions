package dev.xylonity.companions.common.entity.ai.soul_mage.goal;

import dev.xylonity.companions.common.entity.ai.soul_mage.AbstractSoulMageAttackGoal;
import dev.xylonity.companions.common.entity.custom.SoulMageEntity;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;

public class SoulMageHealRingGoal extends AbstractSoulMageAttackGoal {

    public SoulMageHealRingGoal(SoulMageEntity soulMage, int minCd, int maxCd) {
        super(soulMage, minCd, maxCd, "HEAL_RING");
    }

    @Override
    protected void performAttack(LivingEntity target) {
        if (!soulMage.level().isClientSide && soulMage.getOwner() != null) {
            Projectile healRing = CompanionsEntities.HEAL_RING_PROJECTILE.get().create(soulMage.level());
            if (healRing != null) {
                healRing.moveTo(soulMage.getOwner().getX(), soulMage.getOwner().getY(), soulMage.getOwner().getZ());
                healRing.setOwner(soulMage.getOwner());
                soulMage.level().addFreshEntity(healRing);
            }
        }
    }

    @Override
    public boolean canUse() {
        if (soulMage.isAttacking()) return false;
        if (soulMage.tickCount < nextUseTick) return false;
        if (!hasRequiredBook()) return false;
        if (this.soulMage.getOwner() == null) return false;
        if (soulMage.getOwner().getHealth() >= soulMage.getOwner().getMaxHealth() * 0.7) return false;
        return true;
    }

    @Override
    protected boolean hasRequiredBook() {
        for (int i = 0; i < soulMage.inventory.getContainerSize(); i++) {
            ItemStack stack = soulMage.inventory.getItem(i);
            if (stack.getItem() == CompanionsItems.BOOK_HEAL_RING.get()) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected boolean shouldPerformAttack(LivingEntity target) {
        return attackTicks == 6;
    }

}