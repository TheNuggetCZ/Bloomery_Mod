package net.nugget.bloomerymod.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.nugget.bloomerymod.BloomeryMod;
import net.nugget.bloomerymod.block.entity.AbstractBloomeryControllerBlockEntity;
import org.jetbrains.annotations.Nullable;

public class BloomeryControllerRecipe implements Recipe<SimpleContainer>
{
    private final ResourceLocation id;
    private final NonNullList<ItemStack> output;
    private final NonNullList<Ingredient> recipeItems;

    public BloomeryControllerRecipe(ResourceLocation id, NonNullList<ItemStack> output, NonNullList<Ingredient> recipeItems)
    {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
    }

    @Override
    public boolean matches(SimpleContainer container, Level level)
    {
        if (level.isClientSide())
            return false;

        return recipeItems.get(0).test(container.getItem(0));
    }

    @Override
    public NonNullList<Ingredient> getIngredients()
    {
        return recipeItems;
    }

    @Override
    public ItemStack assemble(SimpleContainer simpleContainer)
    {
        //return output;
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height)
    {
        return true;
    }

    @Override
    public ItemStack getResultItem()
    {
        return null;
    }

    public NonNullList<ItemStack> getResultItems()
    {
        return output;
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType()
    {
        return Type.INSTANCE;
    }

    public int getCookingTime()
    {
        return AbstractBloomeryControllerBlockEntity.getCookTime();
    }

    public static class Type implements RecipeType<BloomeryControllerRecipe>
    {
        private Type(){}

        public static final Type INSTANCE = new Type();
        public static final String ID = "blooming";
    }

    public static class Serializer implements RecipeSerializer<BloomeryControllerRecipe>
    {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID =
                new ResourceLocation(BloomeryMod.MOD_ID, "blooming");

        @Override
        public BloomeryControllerRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            JsonArray outputArray = GsonHelper.getAsJsonArray(pSerializedRecipe, "output");
            NonNullList<ItemStack> outputs = NonNullList.create();

            for (int i = 0; i < outputArray.size(); i++) {
                outputs.add(ShapedRecipe.itemStackFromJson(outputArray.get(i).getAsJsonObject()));
            }

            JsonArray ingredients = GsonHelper.getAsJsonArray(pSerializedRecipe, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(1, Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            return new BloomeryControllerRecipe(pRecipeId, outputs, inputs);
        }

        @Override
        public @Nullable BloomeryControllerRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(buf.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(buf));
            }

            NonNullList<ItemStack> outputs = NonNullList.withSize(buf.readInt(), ItemStack.EMPTY);

            for (int i = 0; i < outputs.size(); i++) {
                outputs.set(i, buf.readItem());
            }

            return new BloomeryControllerRecipe(id, outputs, inputs);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, BloomeryControllerRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());

            for (Ingredient ing : recipe.getIngredients()) {
                ing.toNetwork(buf);
            }

            buf.writeInt(recipe.getResultItems().size());

            for (ItemStack output : recipe.getResultItems())
            {
                buf.writeItem(output);
            }
        }
    }
}
