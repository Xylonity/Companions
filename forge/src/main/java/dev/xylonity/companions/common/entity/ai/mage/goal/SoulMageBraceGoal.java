package dev.xylonity.companions.common.entity.ai.mage.goal;

import dev.xylonity.companions.common.entity.ai.mage.AbstractSoulMageAttackGoal;
import dev.xylonity.companions.common.entity.companion.SoulMageEntity;
import dev.xylonity.companions.common.entity.projectile.BraceProjectile;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class SoulMageBraceGoal extends AbstractSoulMageAttackGoal {

    public SoulMageBraceGoal(SoulMageEntity soulMage, int minCd, int maxCd) {
        super(soulMage, minCd, maxCd, "BRACE");
    }

    @Override
    protected void performAttack(LivingEntity target) {
        if (!soulMage.level().isClientSide && soulMage.getOwner() != null) {
            BraceProjectile projectile = CompanionsEntities.BRACE_PROJECTILE.get().create(this.soulMage.level());
            if (projectile != null) {
                Vec3 eyePos = this.soulMage.getEyePosition();
                Vec3 targetPos = target.getEyePosition();

                Vec3 direction = targetPos.subtract(eyePos).normalize();

                Vec3 spawnPos = eyePos.add(direction.scale(0.3));
                projectile.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
                projectile.setOwner(this.soulMage);

                double speed = 0.85;
                projectile.setDeltaMovement(direction.scale(speed));

                this.soulMage.level().addFreshEntity(projectile);
            }
        }
    }

    @Override
    protected boolean hasRequiredBook() {
        for (int i = 0; i < soulMage.inventory.getContainerSize(); i++) {
            ItemStack stack = soulMage.inventory.getItem(i);
            if (stack.getItem() == CompanionsItems.BOOK_BRACE.get()) {
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