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

package com.legoatoom.gameblocks.screen.slot;

import com.legoatoom.gameblocks.items.chess.IChessPieceItem;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class ChessStorageSlot extends Slot {

    public final Class<? extends IChessPieceItem> storeType;
    private final boolean isBlack;

    public ChessStorageSlot(Inventory inventory, int index, int x, int y, Class<? extends IChessPieceItem> storeType, boolean isBlack) {
        super(inventory, index, x, y);
        this.storeType = storeType;
        this.isBlack = isBlack;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        if (!stack.isEmpty() && this.storeType.isInstance(stack.getItem())) {
            IChessPieceItem item = (IChessPieceItem) stack.getItem().asItem();
            if (this.isBlack != item.isBlack()){
                return false;
            }
            return super.canInsert(stack);
        }
        return false;
    }
}
