package dev.xylonity.companions.common.util;

import dev.xylonity.companions.common.entity.CompanionEntity;
import dev.xylonity.companions.common.entity.CompanionSummonEntity;
import dev.xylonity.companions.mixin.CompanionsLevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class Util {

    private Util() { ;; }

    public static BlockPos findClosestGroundBelow(CompanionEntity entity, float y) {
        Vec3 start = new Vec3(entity.getX(), entity.getBoundingBox().minY + 0.01, entity.getZ());
        BlockHitResult trace = entity.level().clip(new ClipContext(start, start.subtract(0, y, 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));

        if (trace.getType() == HitResult.Type.BLOCK) {
            return trace.getBlockPos();
        } else {
            return null;
        }
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
     * Estimate length between a pair of nodes under a bezier parabola
     */
    public static double estimateLengthBezier(Vec3 a, Vec3 b, Vec3 c) {
        double len = 0;
        Vec3 prev = a;
        for (int i = 1; i <= 24; i++) {
            double t = i / (double) 24;
            Vec3 cur = Util.bezier(a, b, c, t);
            len += cur.distanceTo(prev);
            prev = cur;
        }

        return len;
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
     * Check if two entities belong to the same owner chain (cluster)
     */
    public static boolean areEntitiesLinked(Entity e1, Entity e2) {
        if (e1 == null || e2 == null) return false;
        if (e1 == e2) return true;

        Set<UUID> ownersA = collectOwners(e1, new HashSet<>(), new HashSet<>());
        Set<UUID> ownersB = collectOwners(e2, new HashSet<>(), new HashSet<>());

        ownersA.retainAll(ownersB);
        return !ownersA.isEmpty();
    }

    private static Set<UUID> collectOwners(Entity current, Set<UUID> result, Set<UUID> visited) {
        if (current == null) return result;

        if (!visited.add(current.getUUID())) return result;

        result.add(current.getUUID());

        if (current instanceof TamableAnimal tamable) {
            UUID ownerUuid = tamable.getOwnerUUID();
            if (ownerUuid != null) result.add(ownerUuid);

            if (current instanceof CompanionSummonEntity summon) {
                UUID secondUuid = summon.getSecondOwnerUUID();
                if (secondUuid != null) result.add(secondUuid);
            }

            collectOwners(tamable.getOwner(), result, visited);
        }

        if (current instanceof Projectile projectile) {
            collectOwners(projectile.getOwner(), result, visited);
        }

        if (current instanceof OwnableEntity owE) {
            UUID ownerUuid = owE.getOwnerUUID();
            if (ownerUuid != null) {
                result.add(ownerUuid);

                Entity owner = current.level().getPlayerByUUID(ownerUuid);
                if (owner == null && current.level() instanceof CompanionsLevelAccessor acc) {
                    owner = acc.companions$getEntities().get(ownerUuid);
                }
                collectOwners(owner, result, visited);
            }
        }

        return result;
    }

    public static Vec3 randomVectorInCone(Vec3 base, double maxAngleDegrees, Random random) {
        Vec3 baseNorm = base.normalize();
        Vec3 u = baseNorm.cross(new Vec3(0, 1, 0));

        if (u.lengthSqr() < 1e-6) {
            u = baseNorm.cross(new Vec3(1, 0, 0));
        }

        u = u.normalize();
        Vec3 v = baseNorm.cross(u).normalize();

        double minCos = Math.cos(Math.toRadians(maxAngleDegrees));
        double cos = minCos + random.nextDouble() * (1.0 - minCos);
        double sin = Math.sqrt(1.0 - cos * cos);
        double phi = random.nextDouble() * 2 * Math.PI;
        return baseNorm.scale(cos).add(u.scale(sin * Math.cos(phi))).add(v.scale(sin * Math.sin(phi))).scale(base.length());
    }

    /**
     * Checks how many pieces of an armor set are equipped
     */
    public static int hasFullSetOn(Player player, Holder<ArmorMaterial> material) {
        int amount = 0;
        for (ItemStack armorStack : player.getInventory().armor) {
            if (!armorStack.isEmpty() && armorStack.getItem() instanceof ArmorItem armorItem) {
                if (armorItem.getMaterial() == material) {
                    amount++;
                }
            }
        }

        return amount;
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