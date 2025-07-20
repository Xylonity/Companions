package dev.xylonity.companions.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import org.jetbrains.annotations.NotNull;

public class PhantomEffect extends MobEffect {

    public PhantomEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x303030);
    }

}
