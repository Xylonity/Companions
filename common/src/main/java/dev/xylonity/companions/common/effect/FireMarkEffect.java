package dev.xylonity.companions.common.effect;

import dev.xylonity.companions.common.entity.projectile.FireMarkProjectile;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsEffects;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class FireMarkEffect extends MobEffect {

    public FireMarkEffect() {
        super(MobEffectCategory.HARMFUL, 0x303030);
    }

    @Override
    public void onEffectAdded(@NotNull LivingEntity entity, int amplifier) {
        super.onEffectAdded(entity, amplifier);
        final FireMarkProjectile fireMark = CompanionsEntities.FIRE_MARK_PROJECTILE.get().create(entity.level());
        if (fireMark != null) {
            fireMark.moveTo(entity.getX(), entity.getY(), entity.getZ());
            fireMark.setOwner(entity);
            entity.level().addFreshEntity(fireMark);
        }

    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity entity, int i) {
        if (entity.isOnFire()) {
            entity.removeEffect(CompanionsEffects.holder(CompanionsEffects.FIRE_MARK));
            final Level.ExplosionInteraction interaction = CompanionsConfig.SPELLS_GRIEF_WORLD ? Level.ExplosionInteraction.MOB : Level.ExplosionInteraction.NONE;
            entity.level().explode(null, entity.getX(), entity.getY(0.0625) + entity.getBbHeight() * 0.5, entity.getZ(), (float) CompanionsConfig.FIRE_MARK_EFFECT_RADIUS * (CompanionsConfig.FIRE_MARK_EFFECT_RADIUS > 4 ? 0.45F : 0.75f), interaction);
        }

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

}
