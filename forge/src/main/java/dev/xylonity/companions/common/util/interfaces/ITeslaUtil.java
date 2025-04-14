package dev.xylonity.companions.common.util.interfaces;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public interface ITeslaUtil {

    int MAX_LAPSUS = 14;
    int DINAMO_ATTACK_DELAY = 60;
    int ELECTRICAL_CHARGE_DURATION = 8;
    int TICKS_BEFORE_SENDING_PULSE = 5;

    static boolean isEntityNearLine(Vec3 start, Vec3 end, Entity entity, double threshold) {
        Vec3 entityPos = entity.position();

        Vec3 ab = end.subtract(start);
        Vec3 ac = entityPos.subtract(start);

        double abLengthSq = ab.lengthSqr();
        if (abLengthSq < 1e-8) {
            return entityPos.distanceTo(start) <= threshold;
        }

        double t = ac.dot(ab) / abLengthSq;
        t = Mth.clamp(t, 0.0D, 1.0D);

        return entityPos.distanceTo(start.add(ab.scale(t))) <= threshold;
    }

}
