package dev.xylonity.companions.common.item;

import dev.xylonity.companions.common.blockentity.AbstractShadeAltarBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class CrystallizedBloodItem extends TooltipItem {

    public CrystallizedBloodItem(Properties properties) {
        super(properties);
    }

    @Override
    protected String tooltipName() {
        return "crystallized_blood";
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();

        if (!(level.getBlockEntity(ctx.getClickedPos()) instanceof AbstractShadeAltarBlockEntity altar))
            return InteractionResult.PASS;

        if (altar.addCharge() && ctx.getPlayer() != null) {
            if (!ctx.getPlayer().getAbilities().instabuild) {
                ctx.getItemInHand().shrink(1);
            }

            if (!level.isClientSide) {
                ctx.getPlayer().displayClientMessage(Component.translatable("crystallized_blood.companions.client_message.added_charge",altar.getCharges(), altar.getMaxCharges()), true);
            }
        } else if (!level.isClientSide && ctx.getPlayer() != null) {
            ctx.getPlayer().displayClientMessage(Component.translatable("crystallized_blood.companions.client_message.max_charges"), true);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

}