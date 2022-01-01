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
import com.legoatoom.gameblocks.util.chess.ChessActionType;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.legoatoom.gameblocks.GameBlocks.GAME_BLOCKS;

public abstract class IChessPieceItem extends Item {
    private final boolean isBlack;

    public IChessPieceItem(boolean isBlack, int maxCount) {
        super(new FabricItemSettings().group(GAME_BLOCKS).maxCount(maxCount));
        this.isBlack = isBlack;
    }

    public abstract boolean isDefaultLocation(int x, int y);

    public abstract ArrayList<Pair<ChessBoardSlot, ChessActionType>> calculateLegalActions(@NotNull ChessBoardSlot slot);

    public boolean isBlack(){
        return isBlack;
    }
}
