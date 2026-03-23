package dev.xylonity.companions.common.entity.ai.mage.goal;

import dev.xylonity.companions.common.entity.ai.mage.AbstractSoulMageAttackGoal;
import dev.xylonity.companions.common.entity.companion.SoulMageEntity;
import dev.xylonity.companions.common.entity.projectile.TornadoProjectile;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class SoulMageTornadoGoal extends AbstractSoulMageAttackGoal {

    public SoulMageTornadoGoal(SoulMageEntity soulMage, int minCd, int maxCd) {
        super(soulMage, minCd, maxCd, "TORNADO");
    }

    @Override
    protected void performAttack(LivingEntity target) {
        if (target != null) {
            TornadoProjectile tornado = CompanionsEntities.TORNADO_PROJECTILE.create(this.soulMage.level());
            if (tornado != null) {
                Vec3 startPos = this.soulMage.getEyePosition(1f);
                Vec3 spawnPos = startPos.add(target.getEyePosition(1f).subtract(startPos).normalize());

                tornado.moveTo(spawnPos.x, spawnPos.y - 1, spawnPos.z);
                tornado.setOwner(this.soulMage);

                this.soulMage.level().addFreshEntity(tornado);
            }
        }

    }

    @Override
    protected boolean hasRequiredBook() {
        for (int i = 0; i < soulMage.inventory.getContainerSize(); i++) {
            ItemStack stack = soulMage.inventory.getItem(i);
            if (stack.getItem() == CompanionsItems.BOOK_ICE_TORNADO.get()) {
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