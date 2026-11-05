package dev.xylonity.companions.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class SidedRibbonTrailParticle extends BloodRibbonTrailParticle {

    protected final int side;

    public SidedRibbonTrailParticle(ClientLevel level, double x, double y, double z, float r, float g, float b, float radius, float height, int targetId, int side) {
        super(level, x, y, z, r, g, b, targetId, 0.28f, 6);
        this.side = side;
        init();
    }

    @Override
    protected Vec3 anchor(Entity target) {
        final Vec3 dir = target.getDeltaMovement().normalize();
        if (dir.lengthSqr() < 1e-6) {
            return target.position();
        }

        final Vec3 perp = new Vec3(-dir.z, 0, dir.x).normalize().scale(side == 0 ? -1.5 : 1.5);
        return target.position().add(perp);
    }

}
