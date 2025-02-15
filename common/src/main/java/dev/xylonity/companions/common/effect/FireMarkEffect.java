package dev.xylonity.companions.common.effect;

import dev.xylonity.companions.CompanionsCommon;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.projectile.Projectile;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FireMarkEffect extends MobEffect {
    private static final Map<UUID, Projectile> FIRE_MARK_PROJECTILES = new HashMap<>();

    public FireMarkEffect() {
        super(MobEffectCategory.HARMFUL, 0x303030);
    }

    public void addAttributeModifiers(@NotNull LivingEntity entity, @NotNull AttributeMap pAttributeMap, int pAmplifier) {
        super.addAttributeModifiers(entity, pAttributeMap, pAmplifier);
        Projectile fireMark = (Projectile) CompanionsCommon.COMMON_PLATFORM.getFireMarkProjectile().create(entity.level());
        if (fireMark != null) {
            fireMark.moveTo(entity.getX(), entity.getY(), entity.getZ());
            fireMark.setOwner(entity);
            entity.level().addFreshEntity(fireMark);

            FIRE_MARK_PROJECTILES.put(entity.getUUID(), fireMark);
        }
    }

    public void removeAttributeModifiers(@NotNull LivingEntity entity, @NotNull AttributeMap pAttributeMap, int pAmplifier) {
        super.removeAttributeModifiers(entity, pAttributeMap, pAmplifier);

        Projectile fireMark = FIRE_MARK_PROJECTILES.remove(entity.getUUID());
        if (fireMark != null) {
            fireMark.remove(Entity.RemovalReason.DISCARDED);
        }
    }

}
