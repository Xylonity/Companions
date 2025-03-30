package dev.xylonity.companions.common.blockentity;

import dev.xylonity.companions.common.block.CroissantEggBlock;
import dev.xylonity.companions.registry.CompanionsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
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

    private int tickCounter = 0;
    private static final int MAX_TIMEOUT = 100;

    public CroissantEggBlockEntity(BlockPos pos, BlockState state) {
        super(CompanionsBlockEntities.CROISSANT_EGG.get(), pos, state);
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T F) {
        if (F instanceof CroissantEggBlockEntity egg) {
            egg.tickCounter++;

            if (!level.isClientSide()) {
                if (egg.tickCounter >= MAX_TIMEOUT) {
                    level.removeBlock(pos, false);

                    for (int i = 0; i < 10; i++) {
                        double offsetX = 0.5 + (level.random.nextDouble() - 0.5);
                        double offsetY = 0.5 + (level.random.nextDouble() - 0.5);
                        double offsetZ = 0.5 + (level.random.nextDouble() - 0.5);
                        double velX = (level.random.nextDouble() - 0.5) * 0.2;
                        double velY = (level.random.nextDouble() - 0.5) * 0.2;
                        double velZ = (level.random.nextDouble() - 0.5) * 0.2;
                        level.addParticle(ParticleTypes.SMOKE,
                                pos.getX() + offsetX,
                                pos.getY() + offsetY,
                                pos.getZ() + offsetZ,
                                velX, velY, velZ);
                    }
                }
            }

            if (egg.tickCounter % 40 == 0) {

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
        controllerRegistrar.add(new AnimationController<>(this, "controller", this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        event.getController().setAnimation(IDLE);
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

}
