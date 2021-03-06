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

package com.legoatoom.gameblocks.chess.screen.slot;

import com.legoatoom.gameblocks.GameBlocks;
import com.legoatoom.gameblocks.chess.inventory.ChessBoardInventory;
import com.legoatoom.gameblocks.chess.items.IChessPieceItem;
import com.legoatoom.gameblocks.common.screen.slot.AbstractBoardSlot;
import net.minecraft.item.ItemStack;

public class ChessStorageBoardSlot extends AbstractBoardSlot {

    public final Class<? extends IChessPieceItem> storeType;
    private final boolean isBlack;

    public ChessStorageBoardSlot(ChessBoardInventory inventory, int index, int x, int y, IChessPieceItem item) {
        super(inventory, index, x, y);
        this.storeType = item.getClass();
        this.isBlack = item.isBlack();
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        if (stack.isEmpty() || !this.storeType.isInstance(stack.getItem())) return false;

        IChessPieceItem item = (IChessPieceItem) stack.getItem().asItem();
        return this.isBlack == item.isBlack() && super.canInsert(stack);
    }

    @Override
    public ItemStack insertStack(ItemStack stack, int count) {
        stack.removeSubNbt(GameBlocks.MOD_ID); // Clear NBT_DATA when storing
        return super.insertStack(stack, count);
    }

    @Override
    public int getSlotHighLighterSize() {
        return 14;
    }

    @Override
    public ChessBoardInventory getInventory() {
        return ((ChessBoardInventory) this.inventory);
    }
}
