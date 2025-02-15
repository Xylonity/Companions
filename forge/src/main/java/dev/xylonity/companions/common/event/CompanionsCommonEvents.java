package dev.xylonity.companions.common.event;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsEffects;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Companions.MOD_ID)
public class CompanionsCommonEvents {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.hasEffect(CompanionsEffects.FIRE_MARK.get()) && (event.getSource().is(DamageTypes.ON_FIRE) || event.getSource().is(DamageTypes.IN_FIRE))) {
            entity.level().explode(entity, entity.getX(), entity.getY(), entity.getZ(), CompanionsConfig.FIRE_MARK_EFFECT_RADIUS.get().floatValue(), Level.ExplosionInteraction.MOB);
            entity.removeEffect(CompanionsEffects.FIRE_MARK.get());
        }
    }

}
