package dev.xylonity.companions.client;

import dev.xylonity.companions.common.blockentity.AbstractShadeAltarBlockEntity;
import dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity;
import dev.xylonity.companions.common.entity.custom.ShadeMawEntity;
import dev.xylonity.companions.common.entity.projectile.trigger.LaserTriggerProjectile;
import dev.xylonity.companions.common.event.CameraShakeManager;
import dev.xylonity.companions.common.particle.*;
import dev.xylonity.companions.proxy.IProxy;
import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class ClientProxy implements IProxy {

    @Override
    public void spawnElectricArc(Entity e, Entity target, Level level, float d) {
        if (target != null) {
            float distance = 1.0f + level.random.nextFloat() * d;
            float angle = (level.random.nextFloat() - 0.5f) * 60.0f;

            float finalAngle = e.getYRot() + angle;

            double radians = Math.toRadians(finalAngle);
            double offsetX = -Math.sin(radians) * distance;
            double offsetZ = Math.cos(radians) * distance;

            Vec3 start = new Vec3(e.position().x, e.position().y + e.getBbHeight() * 0.5f, e.position().z);
            Vec3 end = new Vec3(e.position().x + offsetX, e.blockPosition().getY(), e.position().z + offsetZ);

            Minecraft.getInstance().particleEngine.add(new RedElectricArcParticle((ClientLevel) level, start, end, 0.6, 0.35, true, 8));
        }
    }

    @Override
    public void shakePlayerCamera(Player player, int durationTicks, float intensityX, float intensityY, float intensityZ, int fadeStartTick) {
        CameraShakeManager.shakePlayer(player, durationTicks, intensityX, intensityY, intensityZ, fadeStartTick);
    }

    @Override
    public void spawnGenericRibbonTrail(Entity e, Level level, double x, double y, double z, float r, float g, float b, float radius, float height) {
        Minecraft.getInstance().particleEngine.add(new GenericRibbonTrailParticle((ClientLevel) level, x, y, z, r, g, b, radius, height, e.getId()));
    }

    @Override
    public void spawnShadeAltarParticles(AbstractShadeAltarBlockEntity e, Level level, float r, float g, float b, double radius) {
        Minecraft.getInstance().particleEngine.add(new ShadeAltarRibbonParticle((ClientLevel) level, e.getBlockPos().getCenter(), r, g, b, radius));
    }

    @Override
    public void spawnPlasmaLampElectricArc(AbstractTeslaBlockEntity e, Level level, BlockPos blockPos) {
        double radius1 = 0.42;
        double y = e.getBlockPos().getY() + 0.5;
        for (int i = 0; i < 360; i += 40) {
            double angleRadians = Math.toRadians(i);
            double x = e.getBlockPos().getCenter().add(e.electricalChargeOriginOffset()).x + radius1 * Math.cos(angleRadians);
            double z = e.getBlockPos().getCenter().add(e.electricalChargeOriginOffset()).z + radius1 * Math.sin(angleRadians);
            level.addParticle(CompanionsParticles.DINAMO_SPARK.get(), x, y + (level.random.nextDouble() * 0.7), z, 0d, 0.35d, 0d);
        }

        // electrical arc
        double dd = (level.random.nextDouble() * 2 - 1) * 2.5;

        Vec3 start = new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5).add(e.electricalChargeOriginOffset());
        Vec3 end = new Vec3(blockPos.getX() + Mth.floor(dd) + 0.5, blockPos.getY(), blockPos.getZ() + Mth.floor(dd) + 0.5);

        Minecraft.getInstance().particleEngine.add(new ElectricArcParticle((ClientLevel) level, start, end, Math.hypot(start.x - end.x, start.z - end.z) * 0.6, 0.35));
    }

    @Override
    public void tickLaserTriggerProjectile(LaserTriggerProjectile laser) {
        if (laser.target == null) {
            Entity e = laser.level().getEntity(laser.getTargetId());
            if (e instanceof LivingEntity) {
                laser.target = (LivingEntity) e;
            }
        }

        if (laser.target != null) {
            Vec3 start = laser.position();
            Vec3 targetPos = laser.target.getEyePosition();
            ClipContext ctx = new ClipContext(start, targetPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null);
            BlockHitResult hit = laser.level().clip(ctx);
            Vec3 end = (hit.getType() == BlockHitResult.Type.BLOCK && !laser.level().getBlockState(hit.getBlockPos()).getCollisionShape(laser.level(), hit.getBlockPos()).isEmpty()) ? hit.getLocation() : targetPos;
            ClientLevel cl = (ClientLevel) laser.level();

            if (laser.tickCount % 2 == 0) Minecraft.getInstance().particleEngine.add(new RedElectricArcParticle(cl, start, end, 0.15f, 0.4f, false, 16));
            if (laser.tickCount % 4 == 0 || laser.tickCount % 5 == 0) Minecraft.getInstance().particleEngine.add(new FuturisticLaserParticle(cl, start, end, 0.3f, 0.75f));
            if (new Random().nextFloat() < 0.2f) spawnElectricArc(laser, laser.target, laser.level(), (float) start.distanceTo(end));
            if (new Random().nextFloat() < 0.4f) laser.spawnSparks(laser.level());
        }
    }

    @Override
    public void spawnLaserRingElectricArc(Entity e, Level level, float distance) {
        float distStart = 1.0f + level.random.nextFloat() * distance;
        double radStart = Math.toRadians(level.random.nextFloat() * 360.0f);
        float distEnd = 1.0f + level.random.nextFloat() * distance;
        double radEnd = Math.toRadians(level.random.nextFloat() * 360.0f);

        Vec3 start = new Vec3(e.position().x + -Math.sin(radStart) * distStart, e.position().y + (level.random.nextFloat() - 0.5f) * 2.0f, e.position().z + Math.cos(radStart) * distStart);
        Vec3 end = new Vec3(e.position().x + -Math.sin(radEnd) * distEnd, e.position().y + (level.random.nextFloat() - 0.5f) * 2.0f, e.position().z + Math.cos(radEnd) * distEnd);

        Minecraft.getInstance().particleEngine.add(new RedElectricArcParticle((ClientLevel) level, start, end, 0.6, 0.35, true, 8));
    }

    @Override
    public void tickShadeMaw(ShadeMawEntity e) {
        Player player = Minecraft.getInstance().player;
        if (player != null && player.getVehicle() == e && Minecraft.getInstance().options.getCameraType() != CameraType.THIRD_PERSON_BACK) {
            Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_BACK);
        }
    }

}
