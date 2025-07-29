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
        if (target != null) {
            BraceProjectile projectile = CompanionsEntities.BRACE_PROJECTILE.get().create(this.soulMage.level());
            if (projectile != null) {
                Vec3 eyePos = this.soulMage.getEyePosition();

                Vec3 dir = target.getEyePosition().subtract(eyePos).normalize();
                Vec3 spawnPos = eyePos.add(dir.scale(0.3));

                projectile.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
                projectile.setOwner(this.soulMage);

                projectile.setDeltaMovement(dir.scale(0.8225));

                soulMage.level().addFreshEntity(projectile);
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