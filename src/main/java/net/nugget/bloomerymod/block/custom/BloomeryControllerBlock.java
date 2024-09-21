package net.nugget.bloomerymod.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public class BloomeryControllerBlock extends Block
{
    public static final BooleanProperty LIT = BooleanProperty.create("lit");

    public BloomeryControllerBlock(Properties properties)
    {
        super(properties);

        this.registerDefaultState(this.defaultBlockState().setValue(LIT, false));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if (!level.isClientSide() && hand == InteractionHand.MAIN_HAND)
        {
            level.setBlock(blockPos, state.cycle(LIT), 3);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.SUCCESS;

        //return super.use(state, level, blockPos, player, hand, result);

    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(LIT);
    }
}
