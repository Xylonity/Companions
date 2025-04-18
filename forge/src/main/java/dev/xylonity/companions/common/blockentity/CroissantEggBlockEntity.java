package dev.xylonity.companions.common.blockentity;

import dev.xylonity.companions.common.entity.custom.CroissantDragonEntity;
import dev.xylonity.companions.common.tick.TickScheduler;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsBlockEntities;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtils;

public class CroissantEggBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private final RawAnimation SPASM = RawAnimation.begin().thenPlay("spasms");

    private int tickCounter = 0;

    private boolean spasm = false;

    public CroissantEggBlockEntity(BlockPos pos, BlockState state) {
        super(CompanionsBlockEntities.CROISSANT_EGG.get(), pos, state);
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T F) {
        if (F instanceof CroissantEggBlockEntity egg) {
            egg.tickCounter++;

            if (egg.tickCounter >= CompanionsConfig.CROISSANT_EGG_LIFETIME) {
                level.destroyBlock(pos, false);

                Entity croissantDragonEntity = CompanionsEntities.CROISSANT_DRAGON.get().create(level);
                if (croissantDragonEntity instanceof CroissantDragonEntity croissantDragon) {
                    croissantDragon.moveTo(egg.getBlockPos().getCenter());
                    level.addFreshEntity(croissantDragon);
                }
            }

            if (egg.tickCounter % 200 == 0) {
                egg.spasm = true;
                TickScheduler.scheduleBoth(level, () -> egg.spasm = false, 40);
            }

            if (egg.tickCounter % 25 == 0) {
                level.addParticle(ParticleTypes.CLOUD, egg.getBlockPos().getX() + 0.5, egg.getBlockPos().getY(), egg.getBlockPos().getZ() + 0.5, 0.0015, 0.1, 0.0015);
            }

        }

    }

    public int getTickCounter() {
        return this.tickCounter;
    }

    @Override
    public double getTick(Object o) {
        return RenderUtils.getCurrentTick();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 5, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        if (spasm) {
            event.getController().setAnimation(SPASM);
        } else {
            event.getController().setAnimation(IDLE);
        }

        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

}