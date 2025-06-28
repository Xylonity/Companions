package dev.xylonity.companions.common.util;

import dev.xylonity.companions.common.particle.BaseRibbonTrailParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class Util {

    private Util() { ;; }

    /**
     * Spawns a base trail particle
     */
    public static void spawnBaseProjectileTrail(Entity entity, float radius, float height, float r, float g, float b) {
        if (!(entity.level() instanceof ClientLevel level)) return;

        double x = entity.getX() + (level.random.nextDouble() - 0.5) * entity.getBbWidth();
        double y = entity.getY() + entity.getBbHeight() * level.random.nextDouble();
        double z = entity.getZ() + (level.random.nextDouble() - 0.5) * entity.getBbWidth();

        Minecraft.getInstance().particleEngine.add(new BaseRibbonTrailParticle(level, x, y, z, r, g, b, radius, height, entity.getId()));
    }

    public static void spawnBaseProjectileTrail(Entity entity, float radius, float height, float r, float g, float b, float trailHeight) {
        if (!(entity.level() instanceof ClientLevel level)) return;

        double x = entity.getX() + (level.random.nextDouble() - 0.5) * entity.getBbWidth();
        double y = entity.getY() + entity.getBbHeight() * level.random.nextDouble();
        double z = entity.getZ() + (level.random.nextDouble() - 0.5) * entity.getBbWidth();

        Minecraft.getInstance().particleEngine.add(new BaseRibbonTrailParticle(level, x, y, z, r, g, b, radius, height, entity.getId(), trailHeight));
    }

    public static void spawnBaseProjectileTrail(Entity entity, double x, double y, double z, float radius, float height, float r, float g, float b, float trailHeight) {
        if (!(entity.level() instanceof ClientLevel level)) return;
        Minecraft.getInstance().particleEngine.add(new BaseRibbonTrailParticle(level, x, y, z, r, g, b, radius, height, entity.getId(), trailHeight));
    }

    /**
     * Normalize degrees within a 360 degree cap
     */
    public static float normalizeDeg(float deg) {
        deg %= 360f;
        return deg < 0 ? deg + 360f : deg;
    }

    /**
     * Degrees to radians fast parser
     */
    public static float degToRad(float deg) {
        return (float) Math.toRadians(deg);
    }

    /**
     * Cubical smoothstep
      */
    public static float smoothStep(float t) {
        return t * t * (3 - 2 * t);
    }

    /**
     * Bezier parabola
     */
    public static Vec3 bezier(Vec3 a, Vec3 b, Vec3 c, double t) {
        double u = 1 - t;
        double tt = t * t;
        double uu = u * u;
        return new Vec3(uu * a.x + 2 * u * t * b.x + tt * c.x, uu * a.y + 2 * u * t * b.y + tt * c.y, uu * a.z + 2 * u * t * b.z + tt * c.z);
    }

    /**
     * Horizontal direction rotator within a specific direction
     */
    public static Vec3 rotateHorizontalDirection(Vec3 direction, double degrees) {
        double rad = Math.toRadians(degrees);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        return new Vec3(direction.x * cos - direction.z * sin, direction.y, direction.x * sin + direction.z * cos);
    }

    /**
     * Finds a valid (empty) position on the vertical axis checking the top face of each block within a specific pos
     */
    public static BlockPos findValidSpawnPos(BlockPos pos, Level level) {
        if (isValidSpawnPos(pos, level)) return pos;

        for (int d = 1; d <= 6; d++) {
            BlockPos below = pos.below(d);
            if (isValidSpawnPos(below, level)) return below;
        }

        for (int u = 1; u <= 3; u++) {
            BlockPos above = pos.above(u);
            if (isValidSpawnPos(above, level)) return above;
        }

        return pos;
    }

    private static boolean isValidSpawnPos(BlockPos pos, Level level) {
        return level.isInWorldBounds(pos) && level.getBlockState(pos).isAir() && level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP);
    }

    /**
     * NBT float array to listTag parser
     */
    public static ListTag floatsToList(float[] arr) {
        ListTag list = new ListTag();
        for (float v : arr) {
            list.add(FloatTag.valueOf(v));
        }

        return list;
    }

    /**
     * NBT int array to listTag parser
     */
    public static ListTag intsToList(int[] arr) {
        ListTag list = new ListTag();
        for (int v : arr) {
            list.add(IntTag.valueOf(v));
        }

        return list;
    }

    /**
     * NBT listTag floatTag to float array parser
     */
    public static void listToFloats(ListTag list, float[] o) {
        for (int i = 0; i < o.length && i < list.size(); i++) {
            o[i] = ((FloatTag) list.get(i)).getAsFloat();
        }

    }

    /**
     * NBT listTag intTag to int array parser
     */
    public static void listToInts(ListTag list, int[] o) {
        for (int i = 0; i < o.length && i < list.size(); i++) {
            o[i] = ((IntTag) list.get(i)).getAsInt();
        }

    }

}
