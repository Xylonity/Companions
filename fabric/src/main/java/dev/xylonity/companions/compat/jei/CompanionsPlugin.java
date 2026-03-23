package dev.xylonity.companions.compat.jei;

import dev.xylonity.companions.CompanionsFabric;
import dev.xylonity.companions.common.recipe.*;
import dev.xylonity.companions.compat.jei.category.*;
import dev.xylonity.companions.registry.CompanionsBlocks;
import dev.xylonity.companions.registry.CompanionsItems;
import dev.xylonity.companions.registry.CompanionsRecipeTypes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public final class CompanionsPlugin implements IModPlugin {

    private static final ResourceLocation UID = new ResourceLocation(CompanionsFabric.MOD_ID, "jei_plugin");

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
        reg.addRecipes(ShadeMawAltarRecipeCategory.TYPE, List.of(new ShadeMawAltarRecipe()));
        reg.addRecipes(ShadeSwordAltarRecipeCategory.TYPE, List.of(new ShadeSwordAltarRecipe()));
        reg.addRecipes(CroissantEggRecipeCategory.TYPE, List.of(new CroissantEggRecipe()));
        reg.addRecipes(ShadeAltarRecipeCategory.TYPE, List.of(new ShadeAltarRecipe()));
        reg.addRecipes(RespawnTotemRecipeCategory.TYPE, List.of(new HourglassRecipe()));
        reg.addRecipes(AntlionRecipeCategory.TYPE, List.of(new HourglassRecipe()));
        reg.addRecipes(PuppetRecipeCategory.TYPE, List.of(new EmptyPuppetRecipe()));

        Level lvl = Minecraft.getInstance().level;
        if (lvl != null) {
            List<SoulFurnaceItemRecipe> itemRecipes = new ArrayList<>();
            List<SoulFurnaceEntityRecipe> entityRecipes = new ArrayList<>();
            for (Recipe<?> base : lvl.getRecipeManager().getAllRecipesFor(CompanionsRecipeTypes.SOUL_FURNACE_TYPE)) {
                if (!(base instanceof SoulFurnaceRecipe r)) continue;

                ItemStack[] inputs = r.input().getItems();

                if (inputs.length == 0) continue;

                ItemStack in = inputs[0];
                if (r.resultItem() != null) {
                    itemRecipes.add(new SoulFurnaceItemRecipe(in, new ItemStack(r.resultItem(), Math.max(1, r.resultCount()))));
                }
                else if (r.resultBlock() != null) {
                    itemRecipes.add(new SoulFurnaceItemRecipe(in, new ItemStack(r.resultBlock())));
                }
                else if (r.resultEntity() != null) {
                    entityRecipes.add(new SoulFurnaceEntityRecipe(in, r.resultEntity()));
                }

            }
            reg.addRecipes(SoulFurnaceItemRecipeCategory.TYPE, itemRecipes);
            reg.addRecipes(SoulFurnaceEntityRecipeCategory.TYPE, entityRecipes);
        }

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
