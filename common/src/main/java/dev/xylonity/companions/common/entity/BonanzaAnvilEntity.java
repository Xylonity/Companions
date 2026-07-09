package dev.xylonity.companions.common.entity;

import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.mixin.FallingBlockEntityAccessor;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

public class BonanzaAnvilEntity extends FallingBlockEntity {

    private static final float[] POP_TICKS = { 10, 20f };
    private static final float[] POP_SCALES = { 2f, 5f };
    private static final float[] POP_PITCHES = { 1.3f, 0.8f };

    private static final float POP_DURATION_TICKS = 3f;
    private static final float DAMAGE_PER_BLOCK = 2f;

    private int popsDone = 0;
    private double targetY = Double.MIN_VALUE;

    public BonanzaAnvilEntity(EntityType<? extends FallingBlockEntity> type, Level level) {
        super(type, level);
        this.blocksBuilding = true;
        this.noPhysics = true;
    }

    public static BonanzaAnvilEntity create(ServerLevel level, double x, double y, double z, double targetY) {
        final BonanzaAnvilEntity anvil = new BonanzaAnvilEntity(CompanionsEntities.BONANZA_ANVIL.get(), level);

        ((FallingBlockEntityAccessor) anvil).setBlockState(Blocks.ANVIL.defaultBlockState());
        anvil.moveTo(x, y, z, 0f, 0f);
        anvil.setStartPos(anvil.blockPosition());
        anvil.time = 1;
        anvil.targetY = targetY;

        return anvil;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.popsDone < POP_TICKS.length && this.time >= POP_TICKS[this.popsDone]) {
            if (!level().isClientSide) {
                level().playSound(null, this, CompanionsSounds.POP.get(), SoundSource.BLOCKS, 1f, POP_PITCHES[this.popsDone]);
            }

            this.popsDone++;
            this.refreshDimensions();
        }

        if (!level().isClientSide && !isRemoved() && getY() <= this.targetY) {
            smash();
        }

    }

    private void smash() {
        final int distance = Mth.ceil(getStartPos().getY() - getY() - 1.0F);
        final float damage = Math.min(Mth.floor(Math.max(distance, 0) * DAMAGE_PER_BLOCK), (float) CompanionsConfig.BONANZA_ANVIL_MAX_DAMAGE);
        if (damage > 0) {
            level().getEntities(this, getBoundingBox(), e -> e instanceof Player && e.isAlive())
                    .forEach(entity -> entity.hurt(damageSources().anvil(this), damage));
        }

        level().playSound(null, blockPosition(), SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 1f, 0.7f);
        if (level() instanceof ServerLevel server) {
            server.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, getBlockState()), getX(), getY() + 0.5, getZ(), 40, 0.65, 0.5, 0.65, 0.15);
        }

        discard();
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, @NotNull DamageSource pSource) {
        return false;
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pose) {
        return super.getDimensions(pose).scale(this.popsDone == 0 ? 1f : POP_SCALES[this.popsDone - 1]);
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putDouble("BonanzaTargetY", this.targetY);
        pCompound.putInt("BonanzaPopsDone", this.popsDone);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("BonanzaTargetY")) {
            this.targetY = pCompound.getDouble("BonanzaTargetY");
        }
        if (pCompound.contains("BonanzaPopsDone")) {
            this.popsDone = pCompound.getInt("BonanzaPopsDone");
        }

    }

    public float getScale(float partialTick) {
        final float scaledTime = this.time + partialTick;

        float scale = 1f;
        for (int i = 0; i < POP_TICKS.length; i++) {
            if (scaledTime < POP_TICKS[i]) {
                break;
            }

            final float from = i == 0 ? 1f : POP_SCALES[i - 1];
            final float pop = Math.min(1f, (scaledTime - POP_TICKS[i]) / POP_DURATION_TICKS);
            final float back = 1f + 2.70158f * (float) Math.pow(pop - 1, 3) + 1.70158f * (float) Math.pow(pop - 1, 2);

            scale = from + (POP_SCALES[i] - from) * back;
        }

        return scale;
    }

}