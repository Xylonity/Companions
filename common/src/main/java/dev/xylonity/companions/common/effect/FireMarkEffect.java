package dev.xylonity.companions.common.effect;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.FireMarkProjectile;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class FireMarkEffect extends MobEffect {

    public FireMarkEffect() {
        super(MobEffectCategory.HARMFUL, 0x303030);
    }

    public void addAttributeModifiers(@NotNull LivingEntity entity, @NotNull AttributeMap pAttributeMap, int pAmplifier) {
        super.addAttributeModifiers(entity, pAttributeMap, pAmplifier);
        final FireMarkProjectile fireMark = CompanionsEntities.FIRE_MARK_PROJECTILE.get().create(entity.level());
        if (fireMark != null) {
            fireMark.moveTo(entity.getX(), entity.getY(), entity.getZ());
            fireMark.setOwner(entity);
            entity.level().addFreshEntity(fireMark);
        }

    }

    @Override
    public void applyEffectTick(LivingEntity entity, int i) {
        if (entity.isOnFire()) {
            entity.removeEffect(this);
            final Level.ExplosionInteraction interaction = CompanionsConfig.SPELLS_GRIEF_WORLD ? Level.ExplosionInteraction.MOB : Level.ExplosionInteraction.NONE;
            entity.level().explode(null, entity.getX(), entity.getY(0.0625) + entity.getBbHeight() * 0.5, entity.getZ(), (float) CompanionsConfig.FIRE_MARK_EFFECT_RADIUS * (CompanionsConfig.FIRE_MARK_EFFECT_RADIUS > 4 ? 0.45F : 0.75f), interaction);
        }

        super.applyEffectTick(entity, i);
    }

    @Override
    public boolean isDurationEffectTick(int i, int ii) {
        return true;
    }

}
