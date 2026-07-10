package dev.xylonity.companions.mixin;

import dev.xylonity.companions.common.item.gecko.GeckoArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SmithingTrimRecipe.class)
public abstract class SmithingTrimRecipeMixin {

    @Inject(method = "matches(Lnet/minecraft/world/item/crafting/SmithingRecipeInput;Lnet/minecraft/world/level/Level;)Z", at = @At("HEAD"), cancellable = true)
    private void companions$blockGeckoArmorTrim(SmithingRecipeInput input, Level level, CallbackInfoReturnable<Boolean> cir) {
        final ItemStack base = input.base();
        if (base.getItem() instanceof GeckoArmorItem) {
            cir.setReturnValue(false);
        }

    }

}
