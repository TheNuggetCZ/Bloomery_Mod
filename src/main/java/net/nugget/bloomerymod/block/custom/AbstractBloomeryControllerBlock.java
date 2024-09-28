package net.nugget.bloomerymod.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.nugget.bloomerymod.block.entity.AbstractBloomeryControllerBlockEntity;

public abstract class AbstractBloomeryControllerBlock extends BaseEntityBlock
{
    public static final BooleanProperty LIT = BooleanProperty.create("lit");

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public AbstractBloomeryControllerBlock(Properties properties)
    {
        super(properties);

        this.registerDefaultState(this.defaultBlockState().setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(LIT, FACING);
    }

    /* DIRECTION STUFF */

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    /* BLOCK ENTITY */

    @Override
    public RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos blockPos, BlockState newState, boolean isMoving)
    {
        if (state.getBlock() != newState.getBlock())
        {
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            if (blockEntity instanceof AbstractBloomeryControllerBlockEntity)
            {
                ((AbstractBloomeryControllerBlockEntity) blockEntity).drops();
            }
        }

        super.onRemove(state, level, blockPos, newState, isMoving);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        if (!level.isClientSide())
        {
            BlockEntity entity = level.getBlockEntity(blockPos);
            if (entity instanceof AbstractBloomeryControllerBlockEntity)
            {
                NetworkHooks.openScreen(((ServerPlayer) player), (AbstractBloomeryControllerBlockEntity)entity, blockPos);
            }
            else
            {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
