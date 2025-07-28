package dev.xylonity.companions.common.item.book.books;

import dev.xylonity.companions.common.entity.projectile.StoneSpikeProjectile;
import dev.xylonity.companions.common.item.book.AbstractMagicBook;
import dev.xylonity.companions.common.material.ArmorMaterials;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.knightlib.api.TickScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiPredicate;

public class StoneSpikesBook extends AbstractMagicBook {

    public StoneSpikesBook(Properties properties) {
        super(properties);
    }

    @Override
    protected String tooltipName() {
        return "stone_spikes_book";
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        double yaw = Math.toRadians(player.yRotO);
        Vec3 baseDir = new Vec3(-Math.sin(yaw), 0, Math.cos(yaw)).normalize();

        int spikes = 10;
        spawnSpikeRow(baseDir, spikes, player);
        spawnSpikeRow(Util.rotateHorizontalDirection(baseDir, -30), (int) (spikes * 0.3), player);
        spawnSpikeRow(Util.rotateHorizontalDirection(baseDir, 30), (int) (spikes * 0.3), player);

        if (!level.isClientSide) {
            player.getCooldowns().addCooldown(this, (int)(CompanionsConfig.STONE_SPIKES_COOLDOWN * (1 - (Util.hasFullSetOn(player, ArmorMaterials.MAGE) * CompanionsConfig.MAGE_SET_COOLDOWN_REDUCTION))));
        }

        return super.use(level, player, usedHand);
    }

    @Override
    protected void playSound(Player player) {
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, player.getSoundSource(), 1.0F, 1.0F);
    }

    private void spawnSpikeRow(Vec3 direction, int count, Player player) {
        for (int i = 0; i < count; i++) {
            Vec3 line = player.position().add(direction.scale(1.5 + i * 1.5));

            int ix = Mth.floor(line.x);
            int iz = Mth.floor(line.z);
            int iy = Mth.floor(line.y);

            StoneSpikeProjectile spike = CompanionsEntities.STONE_SPIKE_PROJECTILE.create(player.level());
            if (spike != null) {
                spike.moveTo(ix + 0.5, findSpikeH(player.level(), ix, iy, iz, Math.toRadians(player.getXRot()) < 0) + 1, iz + 0.5, player.getYRot(), 0.0F);
                spike.setOwner(player);

                if (i == 0) {
                    player.level().addFreshEntity(spike);
                } else {
                    TickScheduler.scheduleBoth(player.level(), () -> player.level().addFreshEntity(spike), i * 2);
                }

            }
        }

    }

    private int findSpikeH(Level level, int x, int yStart, int z, boolean shouldCheckUp) {
        BiPredicate<Level, BlockPos> solid = (level1, pos) -> !level1.getBlockState(pos).getCollisionShape(level1, pos).isEmpty();
        if (shouldCheckUp) {
            for (int y = yStart; y <= level.getMaxBuildHeight(); y++) {
                BlockPos pos = new BlockPos(x, y, z);
                if (solid.test(level, pos)) return y;
            }
            for (int y = yStart; y >= 0; y--) {
                BlockPos pos = new BlockPos(x, y, z);
                if (solid.test(level, pos)) return y;
            }
        } else {
            for (int y = yStart; y >= 0; y--) {
                BlockPos pos = new BlockPos(x, y, z);
                if (solid.test(level, pos)) return y;
            }
            for (int y = yStart; y <= level.getMaxBuildHeight(); y++) {
                BlockPos pos = new BlockPos(x, y, z);
                if (solid.test(level, pos)) return y;
            }
        }

        return yStart;
    }

}
