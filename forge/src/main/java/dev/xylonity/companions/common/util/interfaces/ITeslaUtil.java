package dev.xylonity.companions.common.util.interfaces;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public interface ITeslaUtil {

    int MAX_LAPSUS = 14;
    int DINAMO_ATTACK_DELAY = 60;
    int ELECTRICAL_CHARGE_DURATION = 8;
    int TICKS_BEFORE_SENDING_PULSE = 5;

    static boolean isEntityNearLine(Vec3 start, Vec3 end, Entity entity, double threshold) {
        AABB bb = entity.getBoundingBox();
        Vec3 center = new Vec3((bb.minX + bb.maxX) * 0.5, (bb.minY + bb.maxY) * 0.5, (bb.minZ + bb.maxZ) * 0.5);

        Vec3 ab = end.subtract(start);
        double abLenSq = ab.lengthSqr();
        if (abLenSq < 1e-8) {
            return center.distanceToSqr(start) <= threshold * threshold;
        }

        double dist = threshold + (entity.getBbWidth() * 0.5);
        return start.add(ab.scale(Mth.clamp(center.subtract(start).dot(ab) / abLenSq, 0.0D, 1.0D))).distanceToSqr(center) <= dist * dist;
    }


}
