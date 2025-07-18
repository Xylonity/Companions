package dev.xylonity.companions.common.entity.ai.mage.goal;

import dev.xylonity.companions.common.entity.ai.mage.AbstractSoulMageAttackGoal;
import dev.xylonity.companions.common.entity.companion.SoulMageEntity;
import dev.xylonity.companions.common.entity.projectile.FireMarkRingProjectile;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class SoulMageFireMarkGoal extends AbstractSoulMageAttackGoal {

    public SoulMageFireMarkGoal(SoulMageEntity soulMage, int minCd, int maxCd) {
        super(soulMage, minCd, maxCd, "FIRE_MARK");
    }

    @Override
    protected void performAttack(LivingEntity target) {
        if (soulMage.getOwner() != null) {
            FireMarkRingProjectile ring = CompanionsEntities.FIRE_MARK_RING_PROJECTILE.create(soulMage.level());
            if (ring != null) {
                ring.moveTo(target.getX(), target.getY(), target.getZ());
                ring.setOwner(soulMage);
                soulMage.level().addFreshEntity(ring);
            }
        }

    }

    @Override
    protected boolean hasRequiredBook() {
        for (int i = 0; i < soulMage.inventory.getContainerSize(); i++) {
            ItemStack stack = soulMage.inventory.getItem(i);
            if (stack.getItem() == CompanionsItems.BOOK_FIRE_MARK.get()) {
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