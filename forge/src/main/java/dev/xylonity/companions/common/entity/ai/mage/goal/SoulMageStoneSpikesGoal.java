package dev.xylonity.companions.common.entity.ai.mage.goal;

import dev.xylonity.companions.common.entity.ai.mage.AbstractSoulMageAttackGoal;
import dev.xylonity.companions.common.entity.companion.SoulMageEntity;
import dev.xylonity.companions.common.entity.projectile.StoneSpikeProjectile;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsItems;
import dev.xylonity.knightlib.common.api.TickScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class SoulMageStoneSpikesGoal extends AbstractSoulMageAttackGoal {

    public SoulMageStoneSpikesGoal(SoulMageEntity soulMage, int minCd, int maxCd) {
        super(soulMage, minCd, maxCd, "STONE_SPIKES");
    }

    @Override
    protected void performAttack(LivingEntity target) {
        Vec3 baseDir = target.position().subtract(soulMage.position()).normalize();

        int spikes = 12;
        spawnSpikeRow(baseDir, spikes);
        spawnSpikeRow(Util.rotateHorizontalDirection(baseDir, -25), (int) (spikes * 0.7));
        spawnSpikeRow(Util.rotateHorizontalDirection(baseDir, 25), (int) (spikes * 0.7));
    }

    private void spawnSpikeRow(Vec3 direction, int count) {
        for (int i = 0; i < count; i++) {
            Vec3 pos = soulMage.position().add(direction.scale(1.5 + i * 1.5));
            int y = soulMage.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos((int) pos.x, 0, (int) pos.z)).getY();

            StoneSpikeProjectile spike = CompanionsEntities.STONE_SPIKE_PROJECTILE.get().create(soulMage.level());
            if (spike != null) {
                spike.moveTo(pos.x, Util.findValidSpawnPos(new BlockPos((int) pos.x, y, (int) pos.z), soulMage.level()).getY(), pos.z, soulMage.getYRot(), 0.0F);
                spike.setOwner(soulMage);

                if (i == 0) {
                    soulMage.level().addFreshEntity(spike);
                } else {
                    TickScheduler.scheduleBoth(soulMage.level(), () -> soulMage.level().addFreshEntity(spike), i * 2);
                }
            }
        }

    }

    @Override
    protected boolean hasRequiredBook() {
        for (int i = 0; i < soulMage.inventory.getContainerSize(); i++) {
            ItemStack stack = soulMage.inventory.getItem(i);
            if (stack.getItem() == CompanionsItems.BOOK_STONE_SPIKES.get()) {
                return true;
            }
        }

        return false;
    }

}