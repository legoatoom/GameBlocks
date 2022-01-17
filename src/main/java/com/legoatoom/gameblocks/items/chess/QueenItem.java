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

package com.legoatoom.gameblocks.items.chess;

import com.legoatoom.gameblocks.screen.slot.ChessGridBoardSlot;
import com.legoatoom.gameblocks.util.chess.ChessPieceType;
import org.jetbrains.annotations.NotNull;

public class QueenItem extends IChessPieceItem {
    public QueenItem(boolean isBlack) {
        super(isBlack, 1, ChessPieceType.QUEEN);
    }

    @Override
    public boolean isDefaultLocation(int x, int y) {
        return x == 3 && y == (isBlack() ? 0 : 7);
    }

    @Override
    public void calculateLegalActions(@NotNull ChessGridBoardSlot slot) {
        checkDiagonals(slot);
        checkHorizontals(slot);
    }
}
