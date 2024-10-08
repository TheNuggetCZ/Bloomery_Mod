package net.nugget.bloomerymod;

import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.nugget.bloomerymod.block.ModBlocks;
import net.nugget.bloomerymod.block.entity.ModBlockEntities;
import net.nugget.bloomerymod.item.ModItems;
import net.nugget.bloomerymod.recipe.ModRecipes;
import net.nugget.bloomerymod.screen.BloomeryControllerMenu;
import net.nugget.bloomerymod.screen.BloomeryControllerScreen;
import net.nugget.bloomerymod.screen.ModMenuTypes;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(BloomeryMod.MOD_ID)
public class BloomeryMod
{
    public static final String MOD_ID = "bloomerymod";
    private static final Logger LOGGER = LogUtils.getLogger();

    public BloomeryMod()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModRecipes.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            MenuScreens.register(ModMenuTypes.BLOOMERY_CONTROLLER_MENU.get(), BloomeryControllerScreen::new);
        }
    }
}
