package net.nugget.bloomerymod.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.nugget.bloomerymod.BloomeryMod;

public class ModItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BloomeryMod.MOD_ID);

    //public  static final RegistryObject<Item>

    public static void register(IEventBus eventBus)
    {
        ITEMS.register(eventBus);
    }
}
