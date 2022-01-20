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

package com.legoatoom.gameblocks.chess.items;

import com.legoatoom.gameblocks.common.screen.slot.GridSlot;
import com.legoatoom.gameblocks.chess.util.ChessPieceType;
import org.jetbrains.annotations.NotNull;

public class BishopItem extends IChessPieceItem {

    public BishopItem(boolean isBlack) {
        super(isBlack, 2, ChessPieceType.BISHOP);
    }

    @Override
    public int getStorageIndex() {
        return 6 + (isBlack() ? 1 : 0);
    }

    @Override
    public boolean isDefaultLocation(int x, int y) {
        return (x == 2 || x == 5) && y == (isBlack() ? 0 : 7);
    }

    @Override
    public void calculateLegalActions(@NotNull GridSlot slot) {
        checkDiagonals(slot);
    }
}
