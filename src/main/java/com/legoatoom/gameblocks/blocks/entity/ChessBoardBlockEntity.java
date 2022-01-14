/*
 * Copyright (C) 2021 legoatoom
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

package com.legoatoom.gameblocks.blocks.entity;

import com.legoatoom.gameblocks.blocks.ChessBoardBlock;
import com.legoatoom.gameblocks.inventory.ServerChessBoardInventory;
import com.legoatoom.gameblocks.registry.ChessRegistry;
import com.legoatoom.gameblocks.screen.ChessBoardScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class ChessBoardBlockEntity extends AbstractBoardBlockEntity {

    protected ServerChessBoardInventory board = new ServerChessBoardInventory(this);

    public ChessBoardBlockEntity(BlockPos pos, BlockState state) {
        super(ChessRegistry.CHESS_BOARD_BLOCK_ENTITY, pos, state);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new ChessBoardScreenHandler(syncId, inv, getBoard(), this.getCachedState().get(ChessBoardBlock.FACING));
    }

    @Override
    public ServerChessBoardInventory getBoard() {
        return board;
    }
}
