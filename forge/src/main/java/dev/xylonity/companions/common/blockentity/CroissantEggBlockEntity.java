package dev.xylonity.companions.common.blockentity;

import dev.xylonity.companions.common.block.CroissantEggBlock;
import dev.xylonity.companions.registry.CompanionsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtils;

public class CroissantEggBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int tickCounter = 0;
    private static final int MAX_TIMEOUT = 60;

    private boolean spasm = false;

    public CroissantEggBlockEntity(BlockPos pos, BlockState state) {
        super(CompanionsBlockEntities.CROISSANT_EGG.get(), pos, state);
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T F) {
        if (F instanceof CroissantEggBlockEntity egg) {
            if (!level.isClientSide()) {
                egg.tickCounter++;

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
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) { ;; }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

}
