package dev.xylonity.companions.common.entity.ai.minion.tamable.minion;

import dev.xylonity.companions.common.entity.ai.minion.tamable.AbstractMinionAttackGoal;
import dev.xylonity.companions.common.entity.companion.MinionEntity;
import dev.xylonity.companions.common.entity.projectile.TornadoProjectile;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class MinionTornadoAttackGoal extends AbstractMinionAttackGoal {

    public MinionTornadoAttackGoal(MinionEntity minion, int minCd, int maxCd) {
        super(minion, 50, minCd, maxCd);
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
        if (minion.getOwner() != null) {
            TornadoProjectile tornado = CompanionsEntities.TORNADO_PROJECTILE.get().create(this.minion.level());
            if (tornado != null) {
                Vec3 startPos = this.minion.getEyePosition(1f);
                Vec3 spawnPos = startPos.add(target.getEyePosition(1.0F).subtract(startPos).normalize());
                tornado.moveTo(spawnPos.x, spawnPos.y - 1, spawnPos.z);
                tornado.setOwner(this.minion);

                this.minion.level().addFreshEntity(tornado);
            }
        }

    }

    @Override
    protected int attackDelay() {
        return 12;
    }

    @Override
    protected String variant() {
        return MinionEntity.Variant.OVERWORLD.getName();
    }

    @Override
    protected int attackType() {
        return 1;
    }

}