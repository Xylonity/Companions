package dev.xylonity.companions.common.effect;

import dev.xylonity.companions.common.accessor.TestEntityAccessor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import org.jetbrains.annotations.NotNull;

public class DarkAbsorption extends MobEffect {

    private float prevAbsAmount;

    public DarkAbsorption() {
        super(MobEffectCategory.BENEFICIAL, 0x303030);
    }

    public void addAttributeModifiers(@NotNull LivingEntity pLivingEntity, @NotNull AttributeMap pAttributeMap, int pAmplifier) {
        float darkAbsorptionToAdd = 4 * (pAmplifier + 1);

        TestEntityAccessor entity = (TestEntityAccessor) pLivingEntity;

        float newDarkAbsorption = entity.getDarkAbsorptionAmount() + darkAbsorptionToAdd;
        entity.setDarkAbsorptionAmount(newDarkAbsorption);

        float totalAbsorption = pLivingEntity.getAbsorptionAmount() + darkAbsorptionToAdd;
        pLivingEntity.setAbsorptionAmount(totalAbsorption);

        super.addAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
    }

    public void removeAttributeModifiers(@NotNull LivingEntity pLivingEntity, @NotNull AttributeMap pAttributeMap, int pAmplifier) {
        float darkAbsorptionToRemove = 4 * (pAmplifier + 1);

        TestEntityAccessor entity = (TestEntityAccessor) pLivingEntity;

        float newDarkAbsorption = entity.getDarkAbsorptionAmount() - darkAbsorptionToRemove;
        entity.setDarkAbsorptionAmount(newDarkAbsorption);

        float totalAbsorption = pLivingEntity.getAbsorptionAmount() - darkAbsorptionToRemove;
        pLivingEntity.setAbsorptionAmount(Math.max(totalAbsorption, 0));

        super.removeAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
    }

}
