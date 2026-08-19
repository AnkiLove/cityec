package com.cityec.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class TallMachineBlock extends Block implements net.minecraft.block.BlockEntityProvider {
    public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;
    public static final net.minecraft.state.property.DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final net.minecraft.state.property.BooleanProperty ACTIVE = Properties.POWERED;

    protected TallMachineBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(HALF, DoubleBlockHalf.LOWER).with(FACING, net.minecraft.util.math.Direction.NORTH).with(ACTIVE, true));
    }

    @Override protected void appendProperties(StateManager.Builder<Block, BlockState> builder) { builder.add(HALF, FACING, ACTIVE); }

    @Override public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos pos = ctx.getBlockPos();
        if (pos.getY() >= ctx.getWorld().getTopY() - 1 || !ctx.getWorld().getBlockState(pos.up()).canReplace(ctx)) return null;
        return getDefaultState().with(HALF, DoubleBlockHalf.LOWER).with(FACING, ctx.getHorizontalPlayerFacing().getOpposite()).with(ACTIVE, true);
    }

    @Override public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (!world.isClient) world.setBlockState(pos.up(), state.with(HALF, DoubleBlockHalf.UPPER), Block.NOTIFY_ALL);
    }

    @Override public BlockState getStateForNeighborUpdate(BlockState state, net.minecraft.util.math.Direction direction,
                                                            BlockState neighborState, net.minecraft.world.WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        DoubleBlockHalf half = state.get(HALF);
        if (direction == net.minecraft.util.math.Direction.DOWN && half == DoubleBlockHalf.UPPER && neighborState.getBlock() != this) {
            return net.minecraft.block.Blocks.AIR.getDefaultState();
        }
        if (direction == net.minecraft.util.math.Direction.UP && half == DoubleBlockHalf.LOWER && neighborState.getBlock() != this) {
            return net.minecraft.block.Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            BlockPos other = state.get(HALF) == DoubleBlockHalf.UPPER ? pos.down() : pos.up();
            if (world.getBlockState(other).getBlock() == this) world.setBlockState(other, net.minecraft.block.Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
        }
        super.onBreak(world, pos, state, player);
    }

    protected BlockPos lowerPos(BlockPos pos, BlockState state) {
        return state.get(HALF) == DoubleBlockHalf.UPPER ? pos.down() : pos;
    }
}
