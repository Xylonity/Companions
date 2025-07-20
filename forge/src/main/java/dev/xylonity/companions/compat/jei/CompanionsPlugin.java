package dev.xylonity.companions.compat.jei;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.recipe.*;
import dev.xylonity.companions.compat.jei.category.*;
import dev.xylonity.companions.registry.CompanionsBlocks;
import dev.xylonity.companions.registry.CompanionsItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@JeiPlugin
public final class CompanionsPlugin implements IModPlugin {

    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "jei_plugin");

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration reg) {
        reg.addRecipeCategories(new ShadeMawAltarRecipeCategory(reg.getJeiHelpers().getGuiHelper()));
        reg.addRecipeCategories(new ShadeSwordAltarRecipeCategory(reg.getJeiHelpers().getGuiHelper()));
        reg.addRecipeCategories(new ShadeAltarRecipeCategory(reg.getJeiHelpers().getGuiHelper()));
        reg.addRecipeCategories(new CroissantEggRecipeCategory(reg.getJeiHelpers().getGuiHelper()));
        reg.addRecipeCategories(new CroissantDragonArmorRecipeCategory(reg.getJeiHelpers().getGuiHelper()));
        reg.addRecipeCategories(new RespawnTotemRecipeCategory(reg.getJeiHelpers().getGuiHelper()));
        reg.addRecipeCategories(new AntlionRecipeCategory(reg.getJeiHelpers().getGuiHelper()));
        reg.addRecipeCategories(new PuppetRecipeCategory(reg.getJeiHelpers().getGuiHelper()));
        reg.addRecipeCategories(new SoulFurnaceItemRecipeCategory(reg.getJeiHelpers().getGuiHelper()));
        reg.addRecipeCategories(new SoulFurnaceEntityRecipeCategory(reg.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration reg) {
        reg.addRecipes(ShadeMawAltarRecipeCategory.TYPE, List.of(new ShadeMawAltarRecipe(
                new ItemStack(CompanionsItems.SHADOW_BELL.get())
        )));
        reg.addRecipes(ShadeSwordAltarRecipeCategory.TYPE, List.of(new ShadeSwordAltarRecipe(
                new ItemStack(CompanionsItems.SHADOW_BELL.get())
        )));
        reg.addRecipes(CroissantEggRecipeCategory.TYPE, List.of(new CroissantEggRecipe(
                new ItemStack(CompanionsBlocks.CROISSANT_EGG.get())
        )));
        reg.addRecipes(ShadeAltarRecipeCategory.TYPE, List.of(new ShadeAltarRecipe(
                new ItemStack(CompanionsItems.CRYSTALLIZED_BLOOD.get())
        )));
        reg.addRecipes(RespawnTotemRecipeCategory.TYPE, List.of(new HourglassRecipe(
                new ItemStack(CompanionsItems.HOURGLASS.get())
        )));
        reg.addRecipes(AntlionRecipeCategory.TYPE, List.of(new HourglassRecipe(
                new ItemStack(CompanionsItems.HOURGLASS.get())
        )));
        reg.addRecipes(PuppetRecipeCategory.TYPE, List.of(new EmptyPuppetRecipe(
                new ItemStack(CompanionsBlocks.EMPTY_PUPPET.get())
        )));
        reg.addRecipes(SoulFurnaceEntityRecipeCategory.TYPE, List.of(new SoulFurnaceEntityRecipe(
                new ItemStack(Items.CANDLE)
        )));
        reg.addRecipes(SoulFurnaceItemRecipeCategory.TYPE, List.of(
                new SoulFurnaceItemRecipe(new ItemStack(Items.ROTTEN_FLESH), new ItemStack(CompanionsItems.CRYSTALLIZED_BLOOD.get())),
                new SoulFurnaceItemRecipe(new ItemStack(Items.DIAMOND), new ItemStack(CompanionsItems.SOUL_GEM.get())),
                new SoulFurnaceItemRecipe(new ItemStack(CompanionsItems.BIG_BREAD.get()), new ItemStack(CompanionsBlocks.CROISSANT_EGG.get()))
        ));
        reg.addRecipes(CroissantDragonArmorRecipeCategory.TYPE, List.of(
                new CroissantDragonArmorRecipe(new ItemStack(CompanionsItems.CROISSANT_DRAGON_ARMOR_VANILLA.get())),
                new CroissantDragonArmorRecipe(new ItemStack(CompanionsItems.CROISSANT_DRAGON_ARMOR_CHOCOLATE.get())),
                new CroissantDragonArmorRecipe(new ItemStack(CompanionsItems.CROISSANT_DRAGON_ARMOR_STRAWBERRY.get()))
        ));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration reg) {
        reg.addRecipeCatalyst(new ItemStack(CompanionsBlocks.SHADE_MAW_ALTAR.get()), ShadeMawAltarRecipeCategory.TYPE);
        reg.addRecipeCatalyst(new ItemStack(CompanionsBlocks.SHADE_SWORD_ALTAR.get()), ShadeSwordAltarRecipeCategory.TYPE);
        reg.addRecipeCatalyst(new ItemStack(CompanionsBlocks.CROISSANT_EGG.get()), CroissantEggRecipeCategory.TYPE);
        reg.addRecipeCatalyst(new ItemStack(CompanionsBlocks.RESPAWN_TOTEM.get()), RespawnTotemRecipeCategory.TYPE);
        reg.addRecipeCatalyst(new ItemStack(CompanionsBlocks.EMPTY_PUPPET.get()), PuppetRecipeCategory.TYPE);
        reg.addRecipeCatalyst(new ItemStack(CompanionsBlocks.SOUL_FURNACE.get()), SoulFurnaceItemRecipeCategory.TYPE);
        reg.addRecipeCatalyst(new ItemStack(CompanionsBlocks.SOUL_FURNACE.get()), SoulFurnaceEntityRecipeCategory.TYPE);
        reg.addRecipeCatalyst(new ItemStack(CompanionsItems.CRYSTALLIZED_BLOOD.get()), ShadeAltarRecipeCategory.TYPE);
    }

}
