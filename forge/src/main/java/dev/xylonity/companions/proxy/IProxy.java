package dev.xylonity.companions.proxy;

import dev.xylonity.companions.common.blockentity.AbstractShadeAltarBlockEntity;
import dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity;
import dev.xylonity.companions.common.entity.custom.ShadeMawEntity;
import dev.xylonity.companions.common.entity.projectile.trigger.LaserTriggerProjectile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface IProxy {

    default void spawnGenericRibbonTrail(Entity e, Level level, double x, double y, double z, float r, float g, float b, float radius, float height) { ;; }
    default void spawnElectricArc(Entity e, Entity target, Level level, float distance) { ;; }
    default void spawnLaserRingElectricArc(Entity e, Level level, float distance) { ;; }
    default void spawnPlasmaLampElectricArc(AbstractTeslaBlockEntity e, Level level, BlockPos pos) { ;; }
    default void spawnShadeAltarParticles(AbstractShadeAltarBlockEntity e, Level level, float r, float g, float b, double radius) { ;; }
    default void tickLaserTriggerProjectile(LaserTriggerProjectile e) { ;; }
    default void tickShadeMaw(ShadeMawEntity e) { ;; }
    default void shakePlayerCamera(Player player, int durationTicks, float intensityX, float intensityY, float intensityZ, int fadeStartTick) { ;; }

}