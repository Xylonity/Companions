package dev.xylonity.companions.common.item.book;

import dev.xylonity.companions.common.item.TooltipItem;
import net.minecraft.sounds.SoundEvents;
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
    public InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player player, @NotNull InteractionHand pUsedHand) {
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, player.getSoundSource(), 1.0F, 1.0F);
        return super.use(pLevel, player, pUsedHand);
    }

}