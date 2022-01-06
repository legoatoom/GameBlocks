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

package com.legoatoom.gameblocks.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public abstract class ChessBoardInventory implements ImplementedInventory {

    private final DefaultedList<ItemStack> board;

    public static final int BOARD_WIDTH = 8;
    public static final int BOARD_SIZE = 64;
    private final boolean isClient;



    public ChessBoardInventory(boolean isClient) {
        this.isClient = isClient;
        board = DefaultedList.ofSize(BOARD_SIZE + 12, ItemStack.EMPTY);

    }

    public boolean isClient() {
        return isClient;
    }

    /**
     * Retrieves the item list of this inventory.
     * Must return the same instance every time it's called.
     */
    @Override
    public DefaultedList<ItemStack> getItems() {
        return board;
    }


}
