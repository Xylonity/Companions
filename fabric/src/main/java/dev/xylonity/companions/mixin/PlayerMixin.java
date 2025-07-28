package dev.xylonity.companions.mixin;

import dev.xylonity.companions.common.entity.projectile.PontiffFireRingProjectile;
import dev.xylonity.companions.common.material.ArmorMaterials;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@SuppressWarnings("all")
@Mixin(Player.class)
public class PlayerMixin {

    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true, index = 2)
    private float companions$hurt(float dmg, DamageSource pSource, float pAmount) {
        Player self = (Player) (Object) this;

        dmg = pAmount;

        int amountHoly = Util.hasFullSetOn(self, ArmorMaterials.HOLY_ROBE);
        int amountMage = Util.hasFullSetOn(self, ArmorMaterials.MAGE);
        int amountBlood = Util.hasFullSetOn(self, ArmorMaterials.CRYSTALLIZED_BLOOD);

        // Crystallized blood set reduction
        if (amountBlood != 0 && self.getHealth() <= self.getMaxHealth() * CompanionsConfig.CRYSTALLIZED_BLOOD_SET_MIN_HEALTH) {
            float reduction = (float) CompanionsConfig.CRYSTALLIZED_BLOOD_SET_REDUCTION * amountBlood;
            dmg = companions$applyRedution(dmg, reduction);
        }

        // Magic set reduction
        if (amountMage != 0 && pSource.is(DamageTypes.MAGIC)) {
            float reduction = (float) CompanionsConfig.MAGE_SET_DAMAGE_REDUCTION * amountMage;
            dmg = companions$applyRedution(dmg, reduction);
        }

        // Holy robe set reduction
        if (amountHoly != 0) {
            float reduction = (float) CompanionsConfig.HOLY_ROBE_DAMAGE_REDUCTION * amountHoly;
            dmg = companions$applyRedution(dmg, reduction);

            // Ocassionally summon fire ring
            if (self.getRandom().nextFloat() <= CompanionsConfig.HOLY_ROBE_FIRE_RING_SPAWN_CHANCE * amountHoly) {
                if (!self.level().isClientSide && !pSource.is(DamageTypes.IN_FIRE) && !pSource.is(DamageTypes.ON_FIRE)) {
                    PontiffFireRingProjectile ring = CompanionsEntities.PONTIFF_FIRE_RING.create(self.level());
                    if (ring != null) {
                        ring.moveTo(self.position());
                        ring.setOwner(self);
                        self.level().addFreshEntity(ring);
                    }
                }
            }

        }

        return dmg;
    }

    private float companions$applyRedution(float amount, float reduction) {
        return amount * (1f - reduction);
    }

}
