package net.nugget.bloomerymod.block.entity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.nugget.bloomerymod.BloomeryMod;
import net.nugget.bloomerymod.block.ModBlocks;

public class ModBlockEntities
{
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, BloomeryMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<BloomeryControllerBricksBlockEntity>> BLOOMERY_CONTROLLER_BRICKS =
            BLOCK_ENTITIES.register("bloomery_controller_bricks", () -> BlockEntityType.Builder.of(BloomeryControllerBricksBlockEntity::new,
                    ModBlocks.BLOOMERY_CONTROLLER_BRICKS.get()).build(null));

    public static final RegistryObject<BlockEntityType<BloomeryControllerMudBricksBlockEntity>> BLOOMERY_CONTROLLER_MUD_BRICKS =
            BLOCK_ENTITIES.register("bloomery_controller_mud_bricks", () -> BlockEntityType.Builder.of(BloomeryControllerMudBricksBlockEntity::new,
                    ModBlocks.BLOOMERY_CONTROLLER_MUD_BRICKS.get()).build(null));

    public static void register(IEventBus eventBus)
    {
        BLOCK_ENTITIES.register(eventBus);
    }
}
