package net.nugget.bloomerymod.recipe;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.nugget.bloomerymod.BloomeryMod;

public class ModRecipes
{
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, BloomeryMod.MOD_ID);

    public static final RegistryObject<RecipeSerializer<BloomeryControllerRecipe>> BLOOMERY_CONTROLLER_SERIALIZER =
            SERIALIZERS.register("blooming", () -> BloomeryControllerRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus)
    {
        SERIALIZERS.register(eventBus);
    }
}
