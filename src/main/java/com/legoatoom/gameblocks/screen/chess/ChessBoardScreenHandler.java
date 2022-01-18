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

package com.legoatoom.gameblocks.screen.chess;

import com.legoatoom.gameblocks.inventory.chess.ChessBoardInventory;
import com.legoatoom.gameblocks.inventory.chess.ServerChessBoardInventory;
import com.legoatoom.gameblocks.items.chess.*;
import com.legoatoom.gameblocks.registry.ChessRegistry;
import com.legoatoom.gameblocks.screen.slot.ChessGridSlot;
import com.legoatoom.gameblocks.screen.slot.ChessStorageBoardSlot;
import com.legoatoom.gameblocks.util.chess.ChessActionType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;

public class ChessBoardScreenHandler extends AbstractBoardScreenHandler {



    public ChessBoardScreenHandler(int syncId, PlayerInventory inv, PacketByteBuf buf) {
        super(ChessRegistry.CHESS_BOARD_SCREEN_HANDLER, syncId, inv, new ChessBoardInventory(true), Direction.fromHorizontal(buf.readInt()));
        //CLIENT
        initializeSlots();
    }


    public ChessBoardScreenHandler(int syncId, PlayerInventory playerInventory, ServerChessBoardInventory inventory, Direction facing) {
        super(ChessRegistry.CHESS_BOARD_SCREEN_HANDLER, syncId, playerInventory, inventory, facing);
        //SERVER
        initializeSlots();
        this.sendContentUpdates();
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    @Override
    public void initializeSlots() {
        int y, x;
        for (y = 0; y < BOARD_WIDTH; y++) {
            for (x = 0; x < BOARD_WIDTH; x++) {
                Pair<Integer, Integer> pair = rotationTransformer(x, y);
                int boardX = pair.getLeft();
                int boardY = pair.getRight();
                Slot slot = new ChessGridSlot(getBoardInventory(), boardX, boardY, 24 + x * 16, 17 + y * 16);
                this.addSlot(slot);
            }
        }

        // Chess Pieces Storage
        var inv = getBoardInventory();
        this.addSlot(new ChessStorageBoardSlot(inv, 0 + 0 * 2 + BOARD_SIZE, 159 + 0 * 16, 33 + 0 * 16, PawnItem.class, false));
        this.addSlot(new ChessStorageBoardSlot(inv, 0 + 1 * 2 + BOARD_SIZE, 159 + 0 * 16, 33 + 1 * 16, RookItem.class, false));
        this.addSlot(new ChessStorageBoardSlot(inv, 0 + 2 * 2 + BOARD_SIZE, 159 + 0 * 16, 33 + 2 * 16, KnightItem.class, false));
        this.addSlot(new ChessStorageBoardSlot(inv, 0 + 3 * 2 + BOARD_SIZE, 159 + 0 * 16, 33 + 3 * 16, BishopItem.class, false));
        this.addSlot(new ChessStorageBoardSlot(inv, 0 + 4 * 2 + BOARD_SIZE, 159 + 0 * 16, 33 + 4 * 16, QueenItem.class, false));
        this.addSlot(new ChessStorageBoardSlot(inv, 0 + 5 * 2 + BOARD_SIZE, 159 + 0 * 16, 33 + 5 * 16, KingItem.class, false));

        this.addSlot(new ChessStorageBoardSlot(inv, 1 + 0 * 2 + BOARD_SIZE, 159 + 1 * 16, 33 + 0 * 16, PawnItem.class, true));
        this.addSlot(new ChessStorageBoardSlot(inv, 1 + 1 * 2 + BOARD_SIZE, 159 + 1 * 16, 33 + 1 * 16, RookItem.class, true));
        this.addSlot(new ChessStorageBoardSlot(inv, 1 + 2 * 2 + BOARD_SIZE, 159 + 1 * 16, 33 + 2 * 16, KnightItem.class, true));
        this.addSlot(new ChessStorageBoardSlot(inv, 1 + 3 * 2 + BOARD_SIZE, 159 + 1 * 16, 33 + 3 * 16, BishopItem.class, true));
        this.addSlot(new ChessStorageBoardSlot(inv, 1 + 4 * 2 + BOARD_SIZE, 159 + 1 * 16, 33 + 4 * 16, QueenItem.class, true));
        this.addSlot(new ChessStorageBoardSlot(inv, 1 + 5 * 2 + BOARD_SIZE, 159 + 1 * 16, 33 + 5 * 16, KingItem.class, true));
        //The player inventory
        for (y = 0; y < 3; ++y) {
            for (x = 0; x < 9; ++x) {
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 170 + y * 18));
            }
        }
        //The player Hotbar
        for (y = 0; y < 9; ++y) {
            this.addSlot(new Slot(playerInventory, y, 8 + y * 18, 228));
        }
    }

    @Override
    public ChessBoardInventory getBoardInventory() {
        return (ChessBoardInventory) this.boardInventory;
    }

    @Override
    public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {

    }


}
