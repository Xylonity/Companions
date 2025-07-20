package dev.xylonity.companions.common.effect;

import dev.xylonity.companions.CompanionsCommon;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.projectile.Projectile;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FireMarkEffect extends MobEffect {
    private static final Map<UUID, Projectile> FIRE_MARK_PROJECTILES = new ConcurrentHashMap<>();

    public FireMarkEffect() {
        super(MobEffectCategory.HARMFUL, 0x303030);
    }

    @Override
    public void onEffectAdded(LivingEntity entity, int amplifier) {
        Projectile fireMark = (Projectile) CompanionsCommon.COMMON_PLATFORM.getFireMarkProjectile().create(entity.level());
        if (fireMark != null) {
            fireMark.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());
            fireMark.setOwner(entity);
            entity.level().addFreshEntity(fireMark);
            FIRE_MARK_PROJECTILES.put(entity.getUUID(), fireMark);
        }

        super.onEffectAdded(entity, amplifier);
    }

    @Override
    public void onMobRemoved(LivingEntity entity, int amplifier, @Nullable Entity.RemovalReason reason) {
        Projectile fireMark = FIRE_MARK_PROJECTILES.remove(entity.getUUID());
        if (fireMark != null && !fireMark.isRemoved()) {
            fireMark.remove(Entity.RemovalReason.DISCARDED);
        }

        super.onMobRemoved(entity, amplifier, reason);
    }
}
