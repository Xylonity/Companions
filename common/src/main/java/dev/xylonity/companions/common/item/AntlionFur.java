package dev.xylonity.companions.common.item;

import dev.xylonity.companions.config.CompanionsConfig;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class AntlionFur extends TooltipItem {

    public AntlionFur(Properties properties, String tooltipName) {
        super(properties, tooltipName);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        ItemStack ret = super.finishUsingItem(stack, level, entity);

        entity.setSecondsOnFire(level.random.nextInt(2, 12));

        if (level.random.nextFloat() < 0.25 && CompanionsConfig.ANTLION_FUR_SHOULD_FIRE) {
            if (!entity.hasEffect(MobEffects.DAMAGE_BOOST)) {
                entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, level.random.nextInt(80, 160), 0, true, true, true));
            }
        }

        return ret;
    }

}
