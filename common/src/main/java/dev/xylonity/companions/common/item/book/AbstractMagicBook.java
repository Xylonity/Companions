package dev.xylonity.companions.common.item.book;

import dev.xylonity.companions.common.item.TooltipItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractMagicBook extends TooltipItem {

    public AbstractMagicBook(Properties properties) {
        super(properties);
    }

    public AbstractMagicBook(Properties properties, String tooltipName) {
        super(properties, tooltipName);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player player, @NotNull InteractionHand pUsedHand) {
        player.swing(pUsedHand, true);

        playSound(player);

        return super.use(pLevel, player, pUsedHand);
    }

    protected abstract void playSound(Player player);

}