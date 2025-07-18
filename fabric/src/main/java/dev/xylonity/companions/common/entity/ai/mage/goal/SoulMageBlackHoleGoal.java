package dev.xylonity.companions.common.entity.ai.mage.goal;

import dev.xylonity.companions.common.entity.ai.mage.AbstractSoulMageAttackGoal;
import dev.xylonity.companions.common.entity.companion.SoulMageEntity;
import dev.xylonity.companions.common.entity.projectile.BlackHoleProjectile;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class SoulMageBlackHoleGoal extends AbstractSoulMageAttackGoal {

    public SoulMageBlackHoleGoal(SoulMageEntity soulMage, int minCd, int maxCd) {
        super(soulMage, minCd, maxCd, "BLACK_HOLE");
    }

    @Override
    protected void performAttack(LivingEntity target) {
        BlackHoleProjectile blackHole = CompanionsEntities.BLACK_HOLE_PROJECTILE.create(soulMage.level());
        if (blackHole != null) {
            Vec3 spawnPos = soulMage.getEyePosition(1f).add(soulMage.getLookAngle().scale(0.5D));
            blackHole.setPos(spawnPos.x, spawnPos.y, spawnPos.z);

            blackHole.setOwner(soulMage);

            Vec3 dir = target.getEyePosition(1f).add(0, 1f + soulMage.getRandom().nextFloat(), 0).subtract(spawnPos).normalize();
            float yaw = (float) (Math.atan2(dir.z, dir.x) * (180f / Math.PI)) - 90f;
            float pitch = (float) (-(Math.atan2(dir.y, Math.sqrt(dir.x * dir.x + dir.z * dir.z)) * (180.0F / Math.PI)));

            blackHole.shootFromRotation(soulMage, pitch, yaw, 0f, 0.8f, 0f);

            soulMage.level().addFreshEntity(blackHole);
        }

    }

    @Override
    protected boolean hasRequiredBook() {
        for (int i = 0; i < soulMage.inventory.getContainerSize(); i++) {
            ItemStack stack = soulMage.inventory.getItem(i);
            if (stack.getItem() == CompanionsItems.BOOK_BLACK_HOLE.get()) {
                return true;
            }
        }

        return false;
    }

}