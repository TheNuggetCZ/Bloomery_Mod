package net.nugget.bloomerymod.integration;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.event.ContainerScreenEvent;
import net.minecraftforge.items.SlotItemHandler;
import net.nugget.bloomerymod.BloomeryMod;
import net.nugget.bloomerymod.block.ModBlocks;
import net.nugget.bloomerymod.recipe.BloomeryControllerRecipe;

import java.util.List;
import java.util.Locale;

public class BloomeryControllerRecipeCategory implements IRecipeCategory<BloomeryControllerRecipe> {
    public final static ResourceLocation UID = new ResourceLocation(BloomeryMod.MOD_ID, "blooming");
    public final static ResourceLocation TEXTURE =
            new ResourceLocation(BloomeryMod.MOD_ID, "textures/gui/bloomery_controller_gui.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final LoadingCache<Integer, IDrawableAnimated> cachedArrows;
    private final IDrawable staticFlame;

    public BloomeryControllerRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 40, 10, 96, 52);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.BLOOMERY_CONTROLLER_BRICKS.get()));
        this.staticFlame = helper.createDrawable(TEXTURE, 176, 0, 14, 14);

        this.cachedArrows = CacheBuilder.newBuilder().maximumSize(25L).build(new CacheLoader<Integer, IDrawableAnimated>() {
            public IDrawableAnimated load(Integer cookTime) {
                return helper.drawableBuilder(TEXTURE, 177, 14, 22, 16).buildAnimated(cookTime, IDrawableAnimated.StartDirection.LEFT, false);}});
    }

    @Override
    public RecipeType<BloomeryControllerRecipe> getRecipeType() {
        return JEIBloomeryModPlugin.BLOOM_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal("Blooming");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    protected IDrawableAnimated getArrow(BloomeryControllerRecipe recipe) {
        int cookTime = recipe.getCookingTime();
        /*if (cookTime <= 0) {
            cookTime = this.regularCookTime;
        }*/

        return (IDrawableAnimated)this.cachedArrows.getUnchecked(cookTime);
    }

    public void draw(BloomeryControllerRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack poseStack, double mouseX, double mouseY) {
        this.staticFlame.draw(poseStack, 5, 31);
        IDrawableAnimated arrow = this.getArrow(recipe);
        arrow.draw(poseStack, 29, 20);
        //this.drawExperience(recipe, poseStack, 0);
        this.drawCookTime(recipe, poseStack, 45);
    }

    protected void drawCookTime(BloomeryControllerRecipe recipe, PoseStack poseStack, int y) {
        int cookTime = recipe.getCookingTime();
        if (cookTime > 0) {
            int cookTimeSeconds = cookTime / 20;
            Component timeString = Component.translatable("gui.jei.category.smelting.time.seconds", new Object[]{cookTimeSeconds});
            Minecraft minecraft = Minecraft.getInstance();
            Font fontRenderer = minecraft.font;
            int stringWidth = fontRenderer.width(timeString);
            fontRenderer.draw(poseStack, timeString, (float)(this.background.getWidth() - stringWidth), (float)y, -8355712);
        }

    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BloomeryControllerRecipe recipe, IFocusGroup iFocusGroup) {
        builder.addSlot(RecipeIngredientRole.INPUT, 44 - 40, 19 - 10).addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 98 - 40, 30 - 10).addItemStack(recipe.getResultItems().get(0));

        if (recipe.getResultItems().size() >= 2)
            builder.addSlot(RecipeIngredientRole.OUTPUT, 116 - 40, 30 - 10).addItemStack(recipe.getResultItems().get(1));
    }

    public boolean isHandled(BloomeryControllerRecipe recipe) {
        return !recipe.isSpecial();
    }
}
