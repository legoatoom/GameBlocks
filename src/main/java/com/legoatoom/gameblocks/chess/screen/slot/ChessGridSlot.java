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

package com.legoatoom.gameblocks.chess.screen.slot;

import com.legoatoom.gameblocks.chess.inventory.ChessBoardInventory;
import com.legoatoom.gameblocks.chess.items.IChessPieceItem;
import com.legoatoom.gameblocks.common.screen.slot.AbstractGridSlot;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ChessGridSlot extends AbstractGridSlot {

    public ChessGridSlot(ChessBoardInventory inventory, int boardXLoc, int boardYLoc, int screenXLoc, int screenYLoc) {
        super(inventory, boardXLoc, boardYLoc, screenXLoc, screenYLoc);
    }

    public ChessBoardInventory getInventory() {
        return (ChessBoardInventory) this.inventory;
    }


    @SuppressWarnings("unchecked")
    @Override
    public Optional<IChessPieceItem> getItem() {
        return (Optional<IChessPieceItem>) super.getItem();
    }

    @Override
    public boolean canInsert(@NotNull ItemStack stack) {
        return stack.isEmpty() || (stack.getItem().asItem() instanceof IChessPieceItem && stack.getCount() == getMaxItemCount());
    }
    @Override
    public int getSlotHighLighterSize() {
        return 14;
    }
}
