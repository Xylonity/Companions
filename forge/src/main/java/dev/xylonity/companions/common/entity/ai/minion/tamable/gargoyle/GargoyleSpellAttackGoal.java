package dev.xylonity.companions.common.entity.ai.minion.tamable.gargoyle;

import dev.xylonity.companions.common.entity.ai.minion.tamable.AbstractMinionAttackGoal;
import dev.xylonity.companions.common.entity.companion.MinionEntity;
import dev.xylonity.companions.common.entity.projectile.StoneSpikeProjectile;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.knightlib.common.api.TickScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class GargoyleSpellAttackGoal extends AbstractMinionAttackGoal {

    public GargoyleSpellAttackGoal(MinionEntity minion, int minCd, int maxCd) {
        super(minion, 21, minCd, maxCd);
    }

    @Override
    public void start() {
        super.start();
        minion.setNoMovement(true);
    }

    @Override
    public void stop() {
        super.stop();
        minion.setNoMovement(false);
    }

    @Override
    protected void performAttack(LivingEntity target) {
        Vec3 baseDir = target.position().subtract(minion.position()).normalize();

        int spikes = 12;
        spawnSpikeRow(baseDir, spikes);
        spawnSpikeRow(Util.rotateHorizontalDirection(baseDir, -25), (int) (spikes * 0.7));
        spawnSpikeRow(Util.rotateHorizontalDirection(baseDir, 25), (int) (spikes * 0.7));
    }

    private void spawnSpikeRow(Vec3 direction, int count) {
        for (int i = 0; i < count; i++) {
            Vec3 pos = minion.position().add(direction.scale(1.5 + i * 1.5));
            int y = minion.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos((int) pos.x, 0, (int) pos.z)).getY();

            StoneSpikeProjectile spike = CompanionsEntities.STONE_SPIKE_PROJECTILE.get().create(minion.level());
            if (spike != null) {
                spike.moveTo(pos.x, Util.findValidSpawnPos(new BlockPos((int) pos.x, y, (int) pos.z), minion.level()).getY(), pos.z, minion.getYRot(), 0.0F);
                spike.setOwner(minion);

                if (i == 0) {
                    minion.level().addFreshEntity(spike);
                } else {
                    TickScheduler.scheduleBoth(minion.level(), () -> minion.level().addFreshEntity(spike), i * 2);
                }
            }
        }

    }

    @Override
    protected int attackDelay() {
        return 12;
    }

    @Override
    protected String variant() {
        return MinionEntity.Variant.END.getName();
    }

    @Override
    protected int attackType() {
        return 1;
    }

}