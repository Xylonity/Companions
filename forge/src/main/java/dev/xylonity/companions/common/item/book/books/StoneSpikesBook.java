package dev.xylonity.companions.common.item.book.books;

import dev.xylonity.companions.common.entity.projectile.StoneSpikeProjectile;
import dev.xylonity.companions.common.item.book.AbstractMagicBook;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsSounds;
import dev.xylonity.knightlib.api.TickScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

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

        return super.use(level, player, usedHand);
    }

    @Override
    protected void playSound(Player player) {
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, player.getSoundSource(), 1.0F, 1.0F);
    }

    private void spawnSpikeRow(Vec3 direction, int count, Player player) {
        for (int i = 0; i < count; i++) {
            Vec3 pos = player.position().add(direction.scale(1.5 + i * 1.5));
            int y = player.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos((int) pos.x, 0, (int) pos.z)).getY();

            StoneSpikeProjectile spike = CompanionsEntities.STONE_SPIKE_PROJECTILE.get().create(player.level());
            if (spike != null) {
                spike.moveTo(pos.x, Util.findValidSpawnPos(new BlockPos((int) pos.x, y, (int) pos.z), player.level()).getY(), pos.z, player.getYRot(), 0.0F);
                spike.setOwner(player);

                if (i == 0) {
                    player.level().addFreshEntity(spike);
                } else {
                    TickScheduler.scheduleBoth(player.level(), () -> player.level().addFreshEntity(spike), i * 2);
                }
            }
        }

    }

}
