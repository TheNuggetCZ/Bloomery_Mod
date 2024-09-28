package net.nugget.bloomerymod.integration;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.nugget.bloomerymod.BloomeryMod;
import net.nugget.bloomerymod.block.ModBlocks;
import net.nugget.bloomerymod.recipe.BloomeryControllerRecipe;

import java.util.List;
import java.util.Objects;

@JeiPlugin
public class JEIBloomeryModPlugin implements IModPlugin
{
    public static RecipeType<BloomeryControllerRecipe> BLOOM_TYPE =
            new RecipeType<>(BloomeryControllerRecipeCategory.UID, BloomeryControllerRecipe.class);

    @Override
    public ResourceLocation getPluginUid()
    {
        return new ResourceLocation(BloomeryMod.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new
                BloomeryControllerRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager rm = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();

        List<BloomeryControllerRecipe> recipesBlooming = rm.getAllRecipesFor(BloomeryControllerRecipe.Type.INSTANCE);
        registration.addRecipes(BLOOM_TYPE, recipesBlooming);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration)
    {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.BLOOMERY_CONTROLLER_BRICKS.get()), BLOOM_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.BLOOMERY_CONTROLLER_MUD_BRICKS.get()), BLOOM_TYPE);
    }
}
