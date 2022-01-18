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

package com.legoatoom.gameblocks.screen.slot;

import com.legoatoom.gameblocks.inventory.chess.ChessBoardInventory;
import com.legoatoom.gameblocks.items.chess.IChessPieceItem;
import com.legoatoom.gameblocks.util.chess.ChessPieceType;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ChessGridSlot extends GridSlot {

    public ChessGridSlot(ChessBoardInventory inventory, int boardXLoc, int boardYLoc, int screenXLoc, int screenYLoc) {
        super(inventory, boardXLoc, boardYLoc, screenXLoc, screenYLoc);
    }

    public Optional<IChessPieceItem> getItem() {
        //noinspection unchecked
        return (Optional<IChessPieceItem>) super.getItem();
    }

    public ChessBoardInventory getInventory() {
        return (ChessBoardInventory) this.inventory;
    }

    public void capturePiece() {
        ItemStack stack = getStack();
        IChessPieceItem chessPieceItem = (IChessPieceItem) stack.getItem();
        if (chessPieceItem != null) {
            int slotId = getStorageSlotIndexFromType(chessPieceItem.isBlack(), chessPieceItem.getType());
            getInventory().setStack(getIndex(), ItemStack.EMPTY);
            int i = getInventory().getStack(slotId).getCount();
            stack.setCount(i + 1);
            getInventory().setStack(slotId, stack);
        }
    }

    public void capturePiece(ScreenHandler handler, ItemStack itemStack) {
        if (itemStack.getItem() instanceof IChessPieceItem item) {
            int slotId = getStorageSlotIndexFromType(item.isBlack(), item.getType());
            handler.setCursorStack(ItemStack.EMPTY);
            int i = getInventory().getStack(slotId).getCount();
            itemStack.setCount(i + 1);
            itemStack.removeSubNbt("gameblocks");
            getInventory().setStack(slotId, itemStack);
        }

    }

    @Override
    public boolean canInsert(@NotNull ItemStack stack) {
        return stack.isEmpty() || stack.getItem().asItem() instanceof IChessPieceItem || stack.getCount() == getMaxItemCount();
    }

    @Override
    public int getMaxItemCount() {
        return 1;
    }

    @Override
    public int getMaxItemCount(ItemStack stack) {
        return getMaxItemCount();
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    private int getStorageSlotIndexFromType(boolean isBlack, ChessPieceType type) {
        return switch (type) {
            case PAWN -> 0 + (isBlack ? 1 : 0) + 64;
            case ROOK -> 2 + (isBlack ? 1 : 0) + 64;
            case KNIGHT -> 4 + (isBlack ? 1 : 0) + 64;
            case BISHOP -> 6 + (isBlack ? 1 : 0) + 64;
            case QUEEN -> 8 + (isBlack ? 1 : 0) + 64;
            case KING -> 10 + (isBlack ? 1 : 0) + 64;
        };
    }

    // --- HELPER FUNCTIONS ---
    @Override
    public int getSlotHighLighterSize() {
        return 14;
    }
}
