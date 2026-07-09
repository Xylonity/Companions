package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.BaseProjectile;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.config.CompanionsConfig;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class AntlionSandProjectile extends FrogLevitateProjectile {

    public AntlionSandProjectile(EntityType<? extends BaseProjectile> type, Level level) {
        super(type, level);
    }

    public float getDefaultSpeed() {
        return 0.8225f;
    }

    public float getDefaultLerp() {
        return 0.03f;
    }

    @Override
    public void spawnRibbon() {
        Companions.PROXY.spawnGenericRibbonTrail(this, level(), getX(), getY(), getZ(), 255/255f, 217/255f, 144/255f, 0, 0.35f);
    }

    @Override
    public void spawnParticles() {
        if (tickCount % new Random().nextInt(2, 4) == 0) {
            level().addParticle(ParticleTypes.END_ROD, getX(), getY() + getBbHeight() * 0.5, getZ(), 0, 0, 0);
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        for (LivingEntity e : this.level().getEntitiesOfClass(LivingEntity.class, new AABB(this.blockPosition()).inflate(3))) {
            if (!Util.areEntitiesLinked(e, this)) {
                e.hurt(damageSources().magic(), (float) CompanionsConfig.ANTLION_SOLDIER_PROJECTILE_DAMAGE);
            }
        }

        if (!level().isClientSide && level() instanceof ServerLevel sv) {
            sv.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SAND.defaultBlockState()), position().x, position().y, position().z, 10, 0.25, 0.25, 0.25, 0.0225);
        }

        this.remove(RemovalReason.KILLED);
    }

}