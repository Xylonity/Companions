package dev.xylonity.companions.common.item;

import dev.xylonity.companions.registry.CompanionsEffects;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TestItem extends Item {

    public TestItem(Properties $$0) {
        super($$0);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!world.isClientSide) {
            int duration = 200;
            int amplifier = 0;
            player.addEffect(new MobEffectInstance(CompanionsEffects.BLACK_ABSORPTION.get(), duration, amplifier));
        }

        return InteractionResultHolder.success(stack);
    }

}
