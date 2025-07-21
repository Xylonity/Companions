package dev.xylonity.companions.common.entity.ai.mage.goal;

import dev.xylonity.companions.common.entity.ai.mage.AbstractSoulMageAttackGoal;
import dev.xylonity.companions.common.entity.companion.SoulMageEntity;
import dev.xylonity.companions.common.entity.projectile.HolinessNaginataProjectile;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class SoulMageNaginataGoal extends AbstractSoulMageAttackGoal {

    public SoulMageNaginataGoal(SoulMageEntity soulMage, int minCd, int maxCd) {
        super(soulMage, minCd, maxCd, "FIRE_MARK");
    }

    @Override
    protected void performAttack(LivingEntity target) {
        if (target != null) {
            Vec3 targetPos = target.position();
            for (int i = 0; i < 5; i++) {
                double dx = (new Random().nextDouble() - 0.5) * 25;
                double dz = (new Random().nextDouble() - 0.5) * 25;
                double x = targetPos.x + dx;
                double z = targetPos.z + dz;
                Vec3 spawnPos = new Vec3(x, 20 + targetPos.y + new Random().nextDouble() * 10, z);
                spawnNaginata(soulMage, spawnPos, targetPos, soulMage.level(), 1.5 + new Random().nextDouble() * 0.5);
            }
        }

    }

    private void spawnNaginata(SoulMageEntity mage, Vec3 spawnPos, Vec3 targetPos, Level level, double speed) {
        HolinessNaginataProjectile naginata = CompanionsEntities.HOLINESS_NAGINATA.create(level);
        if (naginata != null) {
            naginata.setOwner(mage);
            naginata.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
            naginata.setDeltaMovement(targetPos.subtract(spawnPos).normalize().scale(speed));
            naginata.refreshOrientation();
            naginata.setInvisible(true);
            level.addFreshEntity(naginata);
        }
    }

    @Override
    protected boolean hasRequiredBook() {
        for (int i = 0; i < soulMage.inventory.getContainerSize(); i++) {
            ItemStack stack = soulMage.inventory.getItem(i);
            if (stack.getItem() == CompanionsItems.BOOK_NAGINATA.get()) {
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