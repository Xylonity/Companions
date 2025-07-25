package dev.xylonity.companions.common.blockentity;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.ShadeEntity;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsBlockEntities;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class ShadeSwordAltarBlockEntity extends AbstractShadeAltarBlockEntity {

    private int tickCount;
    private boolean shouldSpawnParticleExplosion;

    public ShadeSwordAltarBlockEntity(BlockPos pos, BlockState state) {
        super(CompanionsBlockEntities.SHADE_SWORD_ALTAR.get(), pos, state);
        this.tickCount = 0;
        this.shouldSpawnParticleExplosion = false;
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T F) {
        if (F instanceof ShadeSwordAltarBlockEntity altar) {
            if (altar.tickCount % 20 == 0 && altar.getCharges() >= altar.getMaxCharges() - altar.getBloodCharges()) {
                double dx = (new Random().nextDouble() - 0.5) * 0.5;
                double dy = (new Random().nextDouble() - 0.5) * 0.5;
                double dz = (new Random().nextDouble() - 0.5) * 0.5;
                if (level instanceof ServerLevel sv) {
                    sv.sendParticles(CompanionsParticles.SHADE_TRAIL.get(), altar.getBlockPos().getX() + 0.5, altar.getBlockPos().getY() + 0.25 * Math.random(), altar.getBlockPos().getZ() + 0.5, 1, dx, dy, dz, 0.1);
                }
            }

            if (altar.shouldSpawnParticleExplosion && altar.getCharges() == 1) {
                for (int i = 0; i < 10; i++) {
                    double dx = (new Random().nextDouble() - 0.5) * 0.5;
                    double dy = (new Random().nextDouble() - 0.5) * 0.5;
                    double dz = (new Random().nextDouble() - 0.5) * 0.5;
                    if (level instanceof ServerLevel sv) {
                        sv.sendParticles(CompanionsParticles.SHADE_TRAIL.get(), altar.getBlockPos().getX() + 0.5, altar.getBlockPos().getY() + 0.25 * Math.random(), altar.getBlockPos().getZ() + 0.5, 1, dx, dy, dz, 0.15);
                        if (i % 3 == 0)
                            sv.sendParticles(CompanionsParticles.SHADE_SUMMON.get(), altar.getBlockPos().getX() + 0.5, altar.getBlockPos().getY() + 0.25 * Math.random(), altar.getBlockPos().getZ() + 0.5, 1, dx, dy, dz, 0.35);
                    }
                }

                altar.shouldSpawnParticleExplosion = false;
            }

            if (altar.shouldSpawnParticleExplosion && altar.getCharges() == altar.getMaxCharges() - altar.getBloodCharges()) {
                for (int i = 0; i < 20; i++) {
                    double dx = (new Random().nextDouble() - 0.5) * 0.6;
                    double dy = (new Random().nextDouble() - 0.5) * 0.6;
                    double dz = (new Random().nextDouble() - 0.5) * 0.6;
                    if (level instanceof ServerLevel sv) {
                        sv.sendParticles(CompanionsParticles.SHADE_TRAIL.get(), altar.getBlockPos().getX() + 0.5, altar.getBlockPos().getY() + 0.35 * Math.random(), altar.getBlockPos().getZ() + 0.5, 1, dx, dy, dz, 0.15);
                        if (i % 3 == 0)
                            sv.sendParticles(CompanionsParticles.SHADE_SUMMON.get(), altar.getBlockPos().getX() + 0.5, altar.getBlockPos().getY() + 0.35 * Math.random(), altar.getBlockPos().getZ() + 0.5, 1, dx, dy, dz, 0.35);
                    }
                }

                altar.shouldSpawnParticleExplosion = false;
            }

            if (altar.getCharges() > 0 && altar.tickCount % 2 == 0 && level.random.nextFloat() < 0.01 * altar.getCharges() && altar.level != null && altar.level.isClientSide) {
                Companions.PROXY.spawnShadeAltarParticles(altar, level, 0, 0, 0, 5);
            }

            altar.tickCount++;
        }
    }

    @Override
    protected boolean hasIncreasedFromPrevInteraction() {
        boolean ret = super.hasIncreasedFromPrevInteraction();
        if (ret) this.shouldSpawnParticleExplosion = true;
        return ret;
    }

    @Override
    public ShadeEntity spawnShade(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ShadeEntity entity = CompanionsEntities.SHADE_SWORD.get().create(pLevel);
        if (entity != null) {
            entity.tame(pPlayer);

            double r1 = 2, r2 = 3;
            double u = pLevel.random.nextDouble();
            double r = Math.sqrt(u * (r2 * r2 - r1 * r1) + r1 * r1);
            float randYaw = pPlayer.getYRot() + (pLevel.random.nextFloat() * 2.0F - 1.0F) * 30f;
            float randPitch = pPlayer.getXRot() + (pLevel.random.nextFloat() * 2.0F - 1.0F) * 20f;

            Vec3 target = pPlayer.getEyePosition(1f).add(Vec3.directionFromRotation(randPitch, randYaw).scale(r));
            int blockX = Mth.floor(target.x);
            int blockZ = Mth.floor(target.z);

            double px = blockX + 0.5;
            double py = findPlaceToSpawn(pPlayer, pLevel, blockX, blockZ);
            double pz = blockZ + 0.5;
            entity.moveTo(px, py, pz);

            for (int i = 0; i < 20; i++) {
                double vx = (pLevel.random.nextDouble() - 0.5) * entity.getBbWidth();
                double vy = (pLevel.random.nextDouble() - 0.5) * entity.getBbHeight();
                double vz = (pLevel.random.nextDouble() - 0.5) * entity.getBbWidth();
                if (pLevel instanceof ServerLevel svlvl) {
                    svlvl.sendParticles(CompanionsParticles.SHADE_TRAIL.get(), px, py, pz, 1, vx, vy, vz, 0.15);
                    if (i % 3 == 0) svlvl.sendParticles(CompanionsParticles.SHADE_SUMMON.get(), px, py, pz, 1, vx, vy, vz, 0.35);
                }
            }

            if (isBloodUpgradeActive()) {
                entity.setIsBlood(true);

                AttributeInstance maxHealth = entity.getAttribute(Attributes.MAX_HEALTH);
                if (maxHealth != null) maxHealth.setBaseValue(maxHealth.getBaseValue() * CompanionsConfig.SHADOW_SWORD_BLOOD_MULTIPLIER);

                AttributeInstance dmg = entity.getAttribute(Attributes.ATTACK_DAMAGE);
                if (dmg != null) dmg.setBaseValue(dmg.getBaseValue() * CompanionsConfig.SHADOW_SWORD_BLOOD_MULTIPLIER);
            }

            double dx = pPlayer.getX() - px;
            double dy = (pPlayer.getY() + pPlayer.getEyeHeight()) - (py + entity.getEyeHeight());
            double dz = pPlayer.getZ() - pz;
            float yaw = (float) (Math.atan2(dz, dx) * (180F / Math.PI)) - 90F;
            float pitch = (float) (-(Math.atan2(dy,  Math.sqrt(dx * dx + dz * dz)) * (180F / Math.PI)));
            entity.setYRot(yaw);
            entity.yBodyRot = yaw;
            entity.yBodyRotO = yaw;
            entity.yHeadRot = yaw;
            entity.yHeadRotO = yaw;
            entity.setXRot(pitch);
            entity.xRotO = pitch;

            entity.setInvisible(true);
            pLevel.addFreshEntity(entity);
            this.activeShadeUUID = entity.getUUID();
            return entity;
        }

        return null;
    }

}