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
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.legoatoom.gameblocks.GameBlocks.GAME_BLOCKS;

public abstract class IChessPieceItem extends Item {
    private final boolean isBlack;

    public ChessPieceType getType() {
        return type;
    }

    private final ChessPieceType type;


    public IChessPieceItem(boolean isBlack, int maxCount, ChessPieceType type) {
        super(new FabricItemSettings().group(GAME_BLOCKS).maxCount(maxCount));
        GameBlocks.CHESS_PIECES.add(this);
        this.isBlack = isBlack;
        this.type = type;
    }

    public abstract boolean isDefaultLocation(int x, int y);

    @NotNull
    public abstract ArrayList<ChessBoardSlot> calculateLegalActions(@NotNull ChessBoardSlot slot);

    public void cleanHoverActions(ChessBoardSlot[] slots){
        for (ChessBoardSlot slot : slots) {
            slot.setCurrentHoverAction(null);
        }
    }

    public boolean isBlack(){
        return isBlack;
    }
    public void handleAction(ScreenHandler handler, ChessBoardSlot slot, ItemStack cursorStack, ChessActionType actionType){
        if (actionType == ChessActionType.CAPTURE){
            slot.capturePiece(handler, cursorStack);
        }
    }

    public enum ChessPieceType{
        PAWN, KING, KNIGHT, ROOK, QUEEN, BISHOP;
    }
}
