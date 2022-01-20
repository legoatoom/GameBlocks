package com.legoatoom.gameblocks.checkers.blocks;

import com.legoatoom.gameblocks.checkers.blocks.entity.CheckersBoardBlockEntity;
import com.legoatoom.gameblocks.common.blocks.AbstractBoardBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class CheckersBoardBlock extends AbstractBoardBlock {

    protected static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16.f, 3, 16.f);

    public CheckersBoardBlock(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CheckersBoardBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
}
