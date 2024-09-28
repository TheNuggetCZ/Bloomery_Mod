package net.nugget.bloomerymod.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BloomeryControllerMudBricksBlockEntity extends AbstractBloomeryControllerBlockEntity
{
    public BloomeryControllerMudBricksBlockEntity(BlockPos blockPos, BlockState state)
    {
        super(blockPos, state, ModBlockEntities.BLOOMERY_CONTROLLER_MUD_BRICKS.get());
    }
}
