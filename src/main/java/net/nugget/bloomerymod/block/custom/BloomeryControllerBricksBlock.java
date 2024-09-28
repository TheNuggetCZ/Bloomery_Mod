package net.nugget.bloomerymod.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.nugget.bloomerymod.block.entity.AbstractBloomeryControllerBlockEntity;
import net.nugget.bloomerymod.block.entity.BloomeryControllerBricksBlockEntity;
import net.nugget.bloomerymod.block.entity.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

public class BloomeryControllerBricksBlock extends AbstractBloomeryControllerBlock
{
    public BloomeryControllerBricksBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        return new BloomeryControllerBricksBlockEntity(blockPos, blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> entityType)
    {
        return createTickerHelper(entityType, ModBlockEntities.BLOOMERY_CONTROLLER_BRICKS.get(), AbstractBloomeryControllerBlockEntity::tick);
    }
}
