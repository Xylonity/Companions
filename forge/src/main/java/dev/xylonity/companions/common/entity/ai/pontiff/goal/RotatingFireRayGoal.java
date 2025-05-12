package dev.xylonity.companions.common.entity.ai.pontiff.goal;

import dev.xylonity.companions.common.entity.ai.pontiff.AbstractSacredPontiffAttackGoal;
import dev.xylonity.companions.common.entity.hostile.SacredPontiffEntity;
import dev.xylonity.companions.common.entity.projectile.trigger.FireRayBeamEntity;
import dev.xylonity.companions.common.tick.TickScheduler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.Random;

public class RotatingFireRayGoal extends AbstractSacredPontiffAttackGoal {

    public RotatingFireRayGoal(SacredPontiffEntity boss, int minCd, int maxCd) {
        super(boss, 171, minCd, maxCd);
    }

    @Override
    public void start() {
        super.start();
        pontiff.setNoMovement(true);
    }

    @Override
    public void stop() {
        super.stop();
        pontiff.setNoMovement(false);
    }

    private void doAttack() {
        if (!(pontiff.level() instanceof ServerLevel s)) return;

        Vec3 origin = pontiff.position().add(0, pontiff.getBbHeight() + 3.5, 0);
        float yaw0   = pontiff.getRandom().nextFloat() * 360f;
        float pitch0 = -pontiff.getRandom().nextFloat() * 60f;

        FireRayBeamEntity beam = new FireRayBeamEntity(s, origin, yaw0, pitch0, 150, 30, 1.0f, pontiff);
        s.addFreshEntity(beam);
    }

    @Override
    protected int getAttackType() {
        return 3;
    }

    @Override
    protected void performAttack(LivingEntity target) {
        for (int i = 0; i < 10; i++) {
            TickScheduler.scheduleServer(pontiff.level(), this::doAttack, new Random().nextInt(0, 10));
        }
    }

    @Override
    protected int attackDelay() {
        return 10;
    }

}