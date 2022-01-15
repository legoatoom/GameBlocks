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

package com.legoatoom.gameblocks.inventory.chess;

import com.legoatoom.gameblocks.inventory.AbstractBoardInventory;

public class ChessBoardInventory extends AbstractBoardInventory {
    public ChessBoardInventory(boolean isClient) {
        super(isClient, 8, 12);
    }

    @Override
    public void markDirty() {
    }

    @Override
    public void resetBoard() {
    }

    @Override
    public void fillWithDefaultPieces() {
    }

    @Override
    public boolean canDropPackage() {
        return false;
    }
}
