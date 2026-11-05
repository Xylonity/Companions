package dev.xylonity.companions.client.particle;

import dev.xylonity.knightlib.api.util.KnightLibMath;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class BloodVortexRibbonParticle extends BloodRibbonTrailParticle {

    private final float phase;

    public BloodVortexRibbonParticle(ClientLevel level, Entity target, int index, int count) {
        super(level, target.getX(), target.getY(), target.getZ(), 135 / 255f, 0, 12 / 255f, target.getId(),
                KnightLibMath.clamp(new Random().nextFloat(), 0.2f, 0.35f), 16);
        this.phase = index * ((float) Math.PI * 2f / count) + level.random.nextFloat() * 0.6f;
        init();
    }

    @Override
    protected Vec3 anchor(Entity target) {
        double y = 0.45 + 0.6 * Math.sin(age * 0.09 + phase * 1.7);
        double radius = (0.75 + 0.1 * Math.sin(age * 0.27 + phase)) * (1 + 0.5 * Math.max(0, y));
        double angle = phase - Math.toRadians(age * 14);
        return target.position().add(Math.cos(angle) * radius, y, Math.sin(angle) * radius);
    }

}
