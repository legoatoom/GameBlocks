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

import com.legoatoom.gameblocks.screen.slot.ChessBoardSlot;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class BishopItem extends IChessPieceItem  {

    public BishopItem(boolean isBlack) {
        super(isBlack, 2, ChessPieceType.BISHOP);
    }

    @Override
    public boolean isDefaultLocation(int x, int y) {
        if (isBlack()){
            return (x == 2 || x == 5) && y == 0;
        } else {
            return (x == 2 || x == 5) && y == 7;
        }
    }

    @Override
    public @NotNull ArrayList<ChessBoardSlot> calculateLegalActions(@NotNull ChessBoardSlot slot) {
        return new ArrayList<>();
    }
}
