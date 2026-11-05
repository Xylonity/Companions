package dev.xylonity.companions.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.xylonity.companions.common.item.weapon.BloodScytheItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {

    @WrapOperation(method = "canEnchant", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentCategory;canEnchant(Lnet/minecraft/world/item/Item;)Z"))
    private boolean companions$scytheWeaponEnchantments(EnchantmentCategory category, Item item, Operation<Boolean> original) {
        return original.call(category, item) || (category == EnchantmentCategory.WEAPON && item instanceof BloodScytheItem);
    }

}
