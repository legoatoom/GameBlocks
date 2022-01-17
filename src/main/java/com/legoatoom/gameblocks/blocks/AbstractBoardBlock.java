/*
 * Copyright (C) 2022 legoatoom
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.legoatoom.gameblocks.blocks;

import com.legoatoom.gameblocks.blocks.entity.AbstractBoardBlockEntity;
import com.legoatoom.gameblocks.items.PiecesPackageItem;
import com.legoatoom.gameblocks.registry.CommonRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public abstract class AbstractBoardBlock extends BlockWithEntity
        implements Waterloggable {

    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    protected AbstractBoardBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(WATERLOGGED, false));
    }

    @Nullable
    @Override
    public abstract BlockEntity createBlockEntity(BlockPos pos, BlockState state);

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        return getDefaultState().with(FACING, ctx.getPlayerFacing()).with(WATERLOGGED,
                fluidState.getFluid().isIn(FluidTags.WATER));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            if (player.getStackInHand(hand).getItem() instanceof PiecesPackageItem) {
                // Import pieces
                if (extractPackageInto(player.getStackInHand(hand), world, pos, player.isCreative())) {
                    player.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, SoundCategory.PLAYERS, 0.8f, 0.8f + world.getRandom().nextFloat() * 0.4f);
                    return ActionResult.CONSUME;
                }
                return ActionResult.PASS;
            } else {
                if (player.isSneaking()){
                    // Reset Board
                    BlockEntity entity = world.getBlockEntity(pos);
                    if (entity instanceof AbstractBoardBlockEntity e){
                        var board = e.getBoard();
                        player.playSound(SoundEvents.ITEM_BOOK_PUT, SoundCategory.PLAYERS, 0.8f, 0.8f + world.getRandom().nextFloat() * 0.4f);
                        board.resetBoard();
                    }
                } else {
                    // Open Screen
                    NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
                    if (screenHandlerFactory != null) {
                        player.openHandledScreen(screenHandlerFactory);
                    }
                }
            }

        }
        return ActionResult.SUCCESS;
    }

    private boolean extractPackageInto(ItemStack stack, World world, BlockPos pos, boolean isCreative) {
        BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof AbstractBoardBlockEntity boardEntity) {
            var board = boardEntity.getBoard();
            if (board.isEmpty()){
                if (!world.isClient()) {
                    board.fillWithDefaultPieces();
                    if (!isCreative){
                        stack.decrement(1);
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    @Override
    public abstract VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context);

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    //This method will drop all items onto the ground when the block is broken
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof AbstractBoardBlockEntity b) {
                if (b.canDropPackage()){
                    ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(CommonRegistry.PIECES_PACKAGE_ITEM));
                } else {
                    ItemScatterer.spawn(world, pos, b.getBoard());
                }

                // update comparators
                world.updateComparators(pos,this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
}