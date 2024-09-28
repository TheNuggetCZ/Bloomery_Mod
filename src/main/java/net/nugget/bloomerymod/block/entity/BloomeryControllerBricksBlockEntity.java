package net.nugget.bloomerymod.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.state.BlockState;

public class BloomeryControllerBricksBlockEntity extends AbstractBloomeryControllerBlockEntity
{
    public BloomeryControllerBricksBlockEntity(BlockPos blockPos, BlockState state)
    {
        super(blockPos, state, ModBlockEntities.BLOOMERY_CONTROLLER_BRICKS.get());
    }
}
