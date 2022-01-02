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

import com.legoatoom.gameblocks.GameBlocks;
import com.legoatoom.gameblocks.screen.slot.ChessBoardSlot;
import com.legoatoom.gameblocks.util.chess.ChessActionType;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PawnItem extends IChessPieceItem{
    public PawnItem(boolean isBlack) {
        super(isBlack, 8);
        GameBlocks.CHESS_PIECES.add(this);
    }


    @Override
    public boolean isDefaultLocation(int x, int y) {
        if (isBlack()){
            return y == 1;
        } else {
            return y == 6;
        }
    }

    @Override
    public ArrayList<Pair<ChessBoardSlot, ChessActionType>> calculateLegalActions(@NotNull ChessBoardSlot slot) {
        ArrayList<Pair<ChessBoardSlot, ChessActionType>> result = new ArrayList<>();
        ChessBoardSlot up = slot.up(isBlack());

        if (up != null) {
            if (!up.hasStack()) {
                up.setCurrentHoverAction(ChessActionType.MOVE);
                result.add(new Pair<>(up, ChessActionType.MOVE));

                // First move can be 2 steps.
                int x = slot.getBoardXLoc();
                int y = slot.getBoardYLoc();
                if (isDefaultLocation(x, y)) {
                    ChessBoardSlot up2 = up.up(isBlack());
                    if (up2 != null && !up2.hasStack()) {
                        up2.setCurrentHoverAction(ChessActionType.INITIAL_MOVE);
                        result.add(new Pair<>(up2, ChessActionType.INITIAL_MOVE));
                    }
                }
            }

            // Capture
            ChessBoardSlot upLeft = up.left(isBlack());
            ChessBoardSlot upRight = up.right(isBlack());
            if (upLeft != null && upLeft.hasStack() && upLeft.getCurrentPiece().isBlack() != this.isBlack()){
                upLeft.setCurrentHoverAction(ChessActionType.CAPTURE);
                result.add(new Pair<>(upLeft, ChessActionType.CAPTURE));
            }
            if (upRight != null && upRight.hasStack() && upRight.getCurrentPiece().isBlack() != this.isBlack()){
                upRight.setCurrentHoverAction(ChessActionType.CAPTURE);
                result.add(new Pair<>(upRight, ChessActionType.CAPTURE));
            }
        }
        return result;
    }
}
