package dev.xylonity.companions.common.event;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsEffects;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = Companions.MOD_ID)
public class CompanionsCommonEvents {

    @SubscribeEvent
    public static void onLivingHurt(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.hasEffect(CompanionsEffects.FIRE_MARK) && (event.getSource().is(DamageTypes.ON_FIRE) || event.getSource().is(DamageTypes.IN_FIRE))) {
            entity.level().explode(entity, entity.getX(), entity.getY(), entity.getZ(), (float) CompanionsConfig.FIRE_MARK_EFFECT_RADIUS, Level.ExplosionInteraction.MOB);
            entity.removeEffect(CompanionsEffects.FIRE_MARK);
        }
    }

}
