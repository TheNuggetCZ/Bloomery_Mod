package net.nugget.bloomerymod.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.nugget.bloomerymod.block.custom.AbstractBloomeryControllerBlock;
import net.nugget.bloomerymod.recipe.BloomeryControllerRecipe;
import net.nugget.bloomerymod.screen.BloomeryControllerMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public class AbstractBloomeryControllerBlockEntity extends BlockEntity implements MenuProvider
{
    private final ItemStackHandler itemHandler = new ItemStackHandler(3)
    {
        @Override
        protected void onContentsChanged(int slot)
        {
            setChanged();
            if (getLevel() != null && !getLevel().isClientSide())
                getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack)
        {
            return switch (slot)
            {
                case 1, 2 -> false;
                default -> super.isItemValid(slot, stack);
            };
        }
    };


    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private final Map<Direction, LazyOptional<WrappedHandler>> directionWrappedHandlerMap =
            Map.of(Direction.DOWN, LazyOptional.of(() -> new WrappedHandler(itemHandler, (i) -> i > 0, (i, s) -> itemHandler.isItemValid(i, s))),
                    Direction.NORTH, LazyOptional.of(() -> new WrappedHandler(itemHandler, (i) -> i > 0, (i, s) -> itemHandler.isItemValid(i, s))),
                    Direction.SOUTH, LazyOptional.of(() -> new WrappedHandler(itemHandler, (i) -> i > 0, (i, s) -> itemHandler.isItemValid(i, s))),
                    Direction.EAST, LazyOptional.of(() -> new WrappedHandler(itemHandler, (i) -> i > 0, (i, s) -> itemHandler.isItemValid(i, s))),
                    Direction.WEST, LazyOptional.of(() -> new WrappedHandler(itemHandler, (i) -> i > 0, (i, s) -> itemHandler.isItemValid(i, s))));

    protected final ContainerData data;
    private int progress = 0;
    private static int maxProgress = 300;
    private BlockEntityType entityType;

    public AbstractBloomeryControllerBlockEntity(BlockPos blockPos, BlockState state, BlockEntityType entityType)
    {
        super(entityType, blockPos, state);

        this.entityType = entityType;

        this.data = new ContainerData()
        {
            @Override
            public int get(int index)
            {
                return switch (index)
                {
                    case 0 -> AbstractBloomeryControllerBlockEntity.this.progress;
                    case 1 -> AbstractBloomeryControllerBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value)
            {
                switch (index)
                {
                    case 0 -> AbstractBloomeryControllerBlockEntity.this.progress = value;
                    case 1 -> AbstractBloomeryControllerBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount()
            {
                return 2;
            }
        };
    }

    public static int getCookTime()
    {
        return maxProgress;
    }

    @Override
    public Component getDisplayName()
    {
        return Component.literal("Bloomery Controller");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, Inventory inventory, Player player)
    {
        return new BloomeryControllerMenu(id, inventory, this, this.data);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
    {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if(side == null) {
                return lazyItemHandler.cast();
            }

            if(directionWrappedHandlerMap.containsKey(side)) {
                Direction localDir = this.getBlockState().getValue(AbstractBloomeryControllerBlock.FACING);

                if(side == Direction.UP || side == Direction.DOWN) {
                    return directionWrappedHandlerMap.get(side).cast();
                }

                return switch (localDir) {
                    case EAST -> directionWrappedHandlerMap.get(side.getClockWise()).cast();
                    case SOUTH -> directionWrappedHandlerMap.get(side).cast();
                    case WEST -> directionWrappedHandlerMap.get(side.getCounterClockWise()).cast();
                    default -> directionWrappedHandlerMap.get(side.getOpposite()).cast();
                };
            }
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps()
    {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt)
    {
        nbt.put("inventory", itemHandler.serializeNBT());
        nbt.putInt("bloomery_controller.progress", this.progress);
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        progress = nbt.getInt("bloomery_controller.progress");
    }

    public void drops()
    {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++)
        {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, AbstractBloomeryControllerBlockEntity entity)
    {
        if (level.isClientSide())
            return;

        Direction direction = state.getValue(AbstractBloomeryControllerBlock.FACING);

        Block bricks;

        if (entity.entityType == ModBlockEntities.BLOOMERY_CONTROLLER_BRICKS.get()) {
            bricks = Blocks.BRICKS;
        }
        else {
            bricks = Blocks.MUD_BRICKS;
        }

        if (level.getBlockState(blockPos.relative(direction, -1)).getBlock() == Blocks.COAL_BLOCK &&
                level.getBlockState(blockPos.relative(direction, -2)).getBlock() == bricks &&
                level.getBlockState(blockPos.above().relative(direction, -2)).getBlock() == bricks &&
                level.getBlockState(blockPos.above()).getBlock() == bricks &&
                level.getBlockState(blockPos.below().relative(direction, -1)).getBlock() == bricks &&
                level.getBlockState(blockPos.relative(direction.getClockWise()).relative(direction, -1)).getBlock() == bricks &&
                level.getBlockState(blockPos.above().relative(direction.getClockWise()).relative(direction, -1)).getBlock() == bricks &&
                level.getBlockState(blockPos.relative(direction.getCounterClockWise()).relative(direction, -1)).getBlock() == bricks &&
                level.getBlockState(blockPos.above().relative(direction.getCounterClockWise()).relative(direction, -1)).getBlock() == bricks)
            level.setBlock(blockPos, state.setValue(AbstractBloomeryControllerBlock.LIT, true), 3);
        else
            level.setBlock(blockPos, state.setValue(AbstractBloomeryControllerBlock.LIT, false), 3);

        if (hasRecipe(entity) && state.getValue(AbstractBloomeryControllerBlock.LIT) && !entity.itemChanged(entity))
        {
            entity.progress++;
            setChanged(level, blockPos, state);

            if (entity.progress >= entity.maxProgress)
            {
                craftItem(entity);
            }
        }
        else
        {
            entity.resetProgress();
            setChanged(level, blockPos, state);
        }


    }

    private Item lastItem;
    private boolean itemChanged(AbstractBloomeryControllerBlockEntity entity)
    {
        Item newItem = getInventory(entity).getItem(0).getItem();
        Item item = lastItem;
        lastItem = newItem;
        return item != newItem;
    }

    private void resetProgress()
    {
        this.progress = 0;
    }

    private static void craftItem(AbstractBloomeryControllerBlockEntity entity)
    {
        Optional<BloomeryControllerRecipe> recipe = getRecipe(entity);
        SimpleContainer inventory = getInventory(entity);

        entity.itemHandler.extractItem(0, 1, false);
        entity.itemHandler.setStackInSlot(1, new ItemStack(recipe.get().getResultItems().get(0).getItem(), entity.itemHandler.getStackInSlot(1).getCount() + recipe.get().getResultItems().get(0).getCount()));
        if (recipe.get().getResultItems().size() >= 2)
            entity.itemHandler.setStackInSlot(2, new ItemStack(recipe.get().getResultItems().get(1).getItem(), entity.itemHandler.getStackInSlot(2).getCount() + recipe.get().getResultItems().get(1).getCount()));

        entity.resetProgress();
    }

    private static boolean hasRecipe(AbstractBloomeryControllerBlockEntity entity)
    {
        Optional<BloomeryControllerRecipe> recipe = getRecipe(entity);
        SimpleContainer inventory = getInventory(entity);

        return recipe.isPresent() && canInsertIntoOutputSlots(inventory, recipe) && canInsertIntoOutputSlots(inventory, recipe.get().getResultItems());
    }

    private static boolean canInsertIntoOutputSlots(SimpleContainer inventory, NonNullList<ItemStack> resultItems)
    {
        boolean firstOutput = inventory.getItem(1).getItem() == resultItems.get(0).getItem() || inventory.getItem(1).isEmpty();
        boolean secondOutput = resultItems.size() >= 2 ? (inventory.getItem(2).getItem() == resultItems.get(1).getItem() || inventory.getItem(2).isEmpty()) : true;
        return firstOutput && secondOutput;
    }

    private static boolean canInsertIntoOutputSlots(SimpleContainer inventory, Optional<BloomeryControllerRecipe> recipe)
    {
        if (recipe.get().getResultItems().size() >= 2)
            return inventory.getItem(1).getMaxStackSize() >= inventory.getItem(1).getCount() + recipe.get().getResultItems().get(0).getCount() && inventory.getItem(2).getMaxStackSize() >= inventory.getItem(2).getCount() + recipe.get().getResultItems().get(1).getCount();
        else
            return  inventory.getItem(1).getMaxStackSize() >= inventory.getItem(1).getCount() + recipe.get().getResultItems().get(0).getCount();
    }

    private static Optional<BloomeryControllerRecipe> getRecipe(AbstractBloomeryControllerBlockEntity entity)
    {
        Level level = entity.level;
        SimpleContainer inventory = getInventory(entity);

        return level.getRecipeManager().getRecipeFor(BloomeryControllerRecipe.Type.INSTANCE, inventory, level);
    }

    private static SimpleContainer getInventory(AbstractBloomeryControllerBlockEntity entity)
    {
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++)
        {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        return inventory;
    }
}
