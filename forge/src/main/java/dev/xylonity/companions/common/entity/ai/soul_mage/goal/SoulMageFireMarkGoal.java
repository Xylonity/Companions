package dev.xylonity.companions.common.entity.ai.soul_mage.goal;

import dev.xylonity.companions.common.entity.ai.soul_mage.AbstractSoulMageAttackGoal;
import dev.xylonity.companions.common.entity.custom.SoulMageEntity;
import dev.xylonity.companions.common.entity.projectile.FireMarkRingProjectile;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;

public class SoulMageFireMarkGoal extends AbstractSoulMageAttackGoal {

    public SoulMageFireMarkGoal(SoulMageEntity soulMage, int minCd, int maxCd) {
        super(soulMage, minCd, maxCd, "FIRE_MARK");
    }

    @Override
    protected void performAttack(LivingEntity target) {
        if (!soulMage.level().isClientSide && soulMage.getOwner() != null) {
            FireMarkRingProjectile fireMarkRing = CompanionsEntities.FIRE_MARK_RING_PROJECTILE.get().create(soulMage.level());
            if (fireMarkRing != null) {
                fireMarkRing.moveTo(target.getX(), target.getY(), target.getZ());
                fireMarkRing.setOwner(target);
                fireMarkRing.setmayAffectOwner(true);
                fireMarkRing.setMageOwnerUUID(String.valueOf(this.soulMage.getOwnerUUID()));
                soulMage.level().addFreshEntity(fireMarkRing);
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