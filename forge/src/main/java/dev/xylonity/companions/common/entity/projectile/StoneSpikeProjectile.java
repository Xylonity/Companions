package dev.xylonity.companions.common.entity.projectile;

import dev.xylonity.companions.common.entity.BaseProjectile;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.config.CompanionsConfig;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.*;

import java.util.List;
import java.util.Random;

public class StoneSpikeProjectile extends BaseProjectile {
    private final RawAnimation APPEAR = RawAnimation.begin().thenPlay("appear");

    public StoneSpikeProjectile(EntityType<? extends BaseProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.tickCount >= getLifetime()) {
            if (!this.level().isClientSide)
                this.level().broadcastEntityEvent(this, (byte) 3);

            this.remove(RemovalReason.DISCARDED);
        }

        if (this.tickCount == 5) {
            if (!this.level().isClientSide)
                spawnParticles();

            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.STONE_HIT, this.getSoundSource(), 1.0F, 1.0F);
        }

        List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.3), e -> !e.equals(getOwner()));
        for (LivingEntity e : entities) {
            if (!Util.areEntitiesLinked(e, this)) {
                e.hurt(damageSources().magic(), (float) CompanionsConfig.STONE_SPIKE_DAMAGE);
                if (new Random().nextFloat() < 0.25f) {
                    if (!e.hasEffect(MobEffects.POISON)) {
                        e.addEffect(new MobEffectInstance(MobEffects.POISON, new Random().nextInt(20, 100), 0, true, true, true));
                    }
                }
            }

        }

    }

    private void spawnParticles() {
        if (this.level() instanceof ServerLevel sv) {
            for (int i = 0; i < 5; i++) {
                double dx = (sv.getRandom().nextDouble() - 0.5) * 0.1;
                double dy = (sv.getRandom().nextDouble() - 0.5) * 0.1;
                double dz = (sv.getRandom().nextDouble() - 0.5) * 0.1;
                sv.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.STONE.defaultBlockState()), this.getX(), this.getY() + 1, this.getZ(), 1, dx, dy, dz, 0.0);
            }
        }

    }

    @Override
    public void playerTouch(@NotNull Player pEntity) { ;; }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", this::predicate));
    }

    @Override
    protected int baseLifetime() {
        return 36;
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        event.getController().setAnimation(APPEAR);
        return PlayState.CONTINUE;
    }

}
