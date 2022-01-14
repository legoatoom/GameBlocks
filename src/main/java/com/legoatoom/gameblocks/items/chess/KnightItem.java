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
import org.jetbrains.annotations.NotNull;


public class KnightItem extends IChessPieceItem {
    public KnightItem(boolean isBlack) {
        super(isBlack, 2, ChessPieceType.KNIGHT);
    }

    @Override
    public boolean isDefaultLocation(int x, int y) {
        return (x == 1 || x == 6) && y == (isBlack() ? 0 : 7);
    }

    @Override
    public void calculateLegalActions(@NotNull ChessGridBoardSlot slot) {
        int origin = slot.getIndex();
        slot.up(isBlack(), 2).ifPresent(chessBoardSlot -> {
            chessBoardSlot.left(isBlack()).ifPresent(chessBoardSlot1 -> moveOrCaptureCheck(chessBoardSlot1, origin));
            chessBoardSlot.right(isBlack()).ifPresent(chessBoardSlot1 -> moveOrCaptureCheck(chessBoardSlot1, origin));
        });
        slot.down(isBlack(), 2).ifPresent(chessBoardSlot -> {
            chessBoardSlot.left(isBlack()).ifPresent(chessBoardSlot1 -> moveOrCaptureCheck(chessBoardSlot1, origin));
            chessBoardSlot.right(isBlack()).ifPresent(chessBoardSlot1 -> moveOrCaptureCheck(chessBoardSlot1, origin));
        });

        slot.left(isBlack(), 2).ifPresent(chessBoardSlot -> {
            chessBoardSlot.down(isBlack()).ifPresent(chessBoardSlot1 -> moveOrCaptureCheck(chessBoardSlot1, origin));
            chessBoardSlot.up(isBlack()).ifPresent(chessBoardSlot1 -> moveOrCaptureCheck(chessBoardSlot1, origin));
        });
        slot.right(isBlack(), 2).ifPresent(chessBoardSlot -> {
            chessBoardSlot.down(isBlack()).ifPresent(chessBoardSlot1 -> moveOrCaptureCheck(chessBoardSlot1, origin));
            chessBoardSlot.up(isBlack()).ifPresent(chessBoardSlot1 -> moveOrCaptureCheck(chessBoardSlot1, origin));
        });

    }
}
