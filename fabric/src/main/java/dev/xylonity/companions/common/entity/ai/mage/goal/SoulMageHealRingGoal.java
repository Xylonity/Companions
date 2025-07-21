package dev.xylonity.companions.common.entity.ai.mage.goal;

import dev.xylonity.companions.common.entity.ai.mage.AbstractSoulMageAttackGoal;
import dev.xylonity.companions.common.entity.companion.SoulMageEntity;
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
    public boolean canUse() {
        if (soulMage.isAttacking()) return false;
        if (soulMage.tickCount < nextUseTick) return false;
        if (!hasRequiredBook()) return false;
        if (this.soulMage.getOwner() == null) return false;
        if (soulMage.getOwner().getHealth() >= soulMage.getOwner().getMaxHealth() * 0.7) return false;

        return true;
    }

    @Override
    protected void performAttack(LivingEntity target) {
        if (soulMage.getOwner() != null) {
            Projectile ring = CompanionsEntities.HEAL_RING_PROJECTILE.create(soulMage.level());
            if (ring != null) {
                ring.moveTo(soulMage.getOwner().getX(), soulMage.getOwner().getY(), soulMage.getOwner().getZ());
                ring.setOwner(soulMage.getOwner());
                soulMage.level().addFreshEntity(ring);
            }
        }

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