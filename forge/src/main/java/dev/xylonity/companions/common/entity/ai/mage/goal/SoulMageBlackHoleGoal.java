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
        if (!soulMage.level().isClientSide) {
            BlackHoleProjectile blackHole = CompanionsEntities.BLACK_HOLE_PROJECTILE.get().create(soulMage.level());
            if (blackHole != null) {
                Vec3 look = soulMage.getLookAngle();
                Vec3 spawnPos = soulMage.getEyePosition(1.0F).add(look.scale(0.5D));
                blackHole.setPos(spawnPos.x, spawnPos.y, spawnPos.z);

                blackHole.setOwner(soulMage);

                float verticalOffset = 1.0F + soulMage.getRandom().nextFloat();
                Vec3 targetAdjusted = target.getEyePosition(1.0F).add(0, verticalOffset, 0);

                Vec3 direction = targetAdjusted.subtract(spawnPos).normalize();

                float yaw = (float) (Math.atan2(direction.z, direction.x) * (180.0F / Math.PI)) - 90.0F;
                float pitch = (float) (-(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)) * (180.0F / Math.PI)));

                float velocity = 0.80F;
                blackHole.shootFromRotation(soulMage, pitch, yaw, 0.0F, velocity, 0.0F);
                soulMage.level().addFreshEntity(blackHole);
            }
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