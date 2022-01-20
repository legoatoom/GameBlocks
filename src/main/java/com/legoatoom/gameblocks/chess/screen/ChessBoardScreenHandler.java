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

package com.legoatoom.gameblocks.chess.screen;

import com.legoatoom.gameblocks.chess.inventory.ChessBoardInventory;
import com.legoatoom.gameblocks.chess.inventory.ServerChessBoardInventory;
import com.legoatoom.gameblocks.chess.screen.slot.ChessGridSlot;
import com.legoatoom.gameblocks.chess.screen.slot.ChessStorageBoardSlot;
import com.legoatoom.gameblocks.common.screen.AbstractBoardScreenHandler;
import com.legoatoom.gameblocks.registry.ChessRegistry;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;

import static com.legoatoom.gameblocks.registry.ChessRegistry.*;

public class ChessBoardScreenHandler extends AbstractBoardScreenHandler<ChessBoardInventory> {

    public ChessBoardScreenHandler(int syncId, PlayerInventory inv, PacketByteBuf buf) {
        super(ChessRegistry.CHESS_BOARD_SCREEN_HANDLER, syncId, inv, buf, ChessBoardInventory::new);
    }

    public ChessBoardScreenHandler(int syncId, PlayerInventory inv, ServerChessBoardInventory board, Direction direction) {
        super(ChessRegistry.CHESS_BOARD_SCREEN_HANDLER, syncId, inv, board, direction);
    }

    @Override
    public void initializeSlots() {
        int y, x;
        int startY = 9;
        int startX = 24;
        for (y = 0; y < BOARD_WIDTH; y++) {
            for (x = 0; x < BOARD_WIDTH; x++) {
                Pair<Integer, Integer> pair = rotationTransformer(x, y);
                int boardX = pair.getLeft();
                int boardY = pair.getRight();
                Slot slot = new ChessGridSlot(getBoardInventory(), boardX, boardY, startX + x * 16, startY + y * 16);
                this.addSlot(slot);
            }
        }

        // Chess Pieces Storage
        var inv = getBoardInventory();
        startY = 25;
        startX = 159;
        this.addSlot(new ChessStorageBoardSlot(inv, BOARD_SIZE, startX, startY, WHITE_PAWN));
        this.addSlot(new ChessStorageBoardSlot(inv, 2 + BOARD_SIZE, startX, startY + 16, WHITE_ROOK));
        this.addSlot(new ChessStorageBoardSlot(inv, 4 + BOARD_SIZE, startX, startY + 32, WHITE_KNIGHT));
        this.addSlot(new ChessStorageBoardSlot(inv, 6 + BOARD_SIZE, startX, startY + 48, WHITE_BISHOP));
        this.addSlot(new ChessStorageBoardSlot(inv, 8 + BOARD_SIZE, startX, startY + 64, WHITE_QUEEN));
        this.addSlot(new ChessStorageBoardSlot(inv, 10 + BOARD_SIZE, startX, startY + 80, WHITE_KNIGHT));

        this.addSlot(new ChessStorageBoardSlot(inv, 1 + BOARD_SIZE, startX + 16, startY, BLACK_PAWN));
        this.addSlot(new ChessStorageBoardSlot(inv, 3 + BOARD_SIZE, startX + 16, startY + 16, BLACK_ROOK));
        this.addSlot(new ChessStorageBoardSlot(inv, 5 + BOARD_SIZE, startX + 16, startY + 32, BLACK_KNIGHT));
        this.addSlot(new ChessStorageBoardSlot(inv, 7 + BOARD_SIZE, startX + 16, startY + 48, BLACK_BISHOP));
        this.addSlot(new ChessStorageBoardSlot(inv, 9 + BOARD_SIZE, startX + 16, startY + 64, BLACK_QUEEN));
        this.addSlot(new ChessStorageBoardSlot(inv, 11 + BOARD_SIZE, startX + 16, startY + 80, BLACK_KNIGHT));
        //The player inventory
        startY = 162;
        startX = 8;
        for (y = 0; y < 3; ++y) {
            for (x = 0; x < 9; ++x) {
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, startX + x * 18, startY + y * 18));
            }
        }
        //The player Hotbar
        startY = 220;
        for (y = 0; y < 9; ++y) {
            this.addSlot(new Slot(playerInventory, y, startX + y * 18, startY));
        }
    }


}
