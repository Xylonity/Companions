package dev.xylonity.companions.common.util.interfaces;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public interface ITeslaUtil {

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

        Vec3 projection = start.add(ab.scale(t));

        double distance = entityPos.distanceTo(projection);

        return distance <= threshold;
    }

}
