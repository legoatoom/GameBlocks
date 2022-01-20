package com.legoatoom.gameblocks.checkers.blocks.entity;

import com.legoatoom.gameblocks.checkers.blocks.CheckersBoardBlock;
import com.legoatoom.gameblocks.checkers.inventory.ServerCheckersBoardInventory;
import com.legoatoom.gameblocks.checkers.screen.CheckersBoardScreenHandler;
import com.legoatoom.gameblocks.common.blocks.entity.AbstractBoardBlockEntity;
import com.legoatoom.gameblocks.registry.CheckersRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class CheckersBoardBlockEntity extends AbstractBoardBlockEntity {

    protected ServerCheckersBoardInventory board = new ServerCheckersBoardInventory(this);

    public CheckersBoardBlockEntity(BlockPos pos, BlockState state) {
        super(CheckersRegistry.CHECKERS_BOARD_BLOCK_ENTITY, pos, state);
    }

    @Override
    public ServerCheckersBoardInventory getBoard() {
        return board;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new CheckersBoardScreenHandler(syncId, inv, getBoard(), this.getCachedState().get(CheckersBoardBlock.FACING));
    }
}
