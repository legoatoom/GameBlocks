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

import com.legoatoom.gameblocks.items.ChessPiece;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;

public class ChessBoardSlot extends Slot {

    public int getBoardX() {
        return boardX;
    }

    public int getBoardY() {
        return boardY;
    }

    private final int boardX, boardY;
    public ChessBoardSlot(Inventory inventory, int index, int x, int y, int boardX, int boardY) {
        super(inventory, index, x, y);
        this.boardX = boardX;
        this.boardY = boardY;
    }

    @Override
    public int getMaxItemCount() {
        return 1;
    }

    @Nullable
    public ChessPiece getType() {
        // TODO: 12021-12-31 Remove the check and make this slot only allow chesspieces
        Item item = this.getStack().getItem();
        if (item instanceof ChessPiece.ChessPieceItem piece) {
            return piece.getType();
        } else {
            return null;
        }
    }

    public ChessPiece.ChessPieceItem getItem(){
        if (getStack().getItem() instanceof ChessPiece.ChessPieceItem chessPieceItem){
            return chessPieceItem;
        } else {
            return null;
        }
    }

    public boolean isBlack(){
        if (getType() == null) return false;
        return getType().isBlack();
    }
}
