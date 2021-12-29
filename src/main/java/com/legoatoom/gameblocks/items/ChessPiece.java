/*
 * Copyright (C) 2021 legoatoom
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

package com.legoatoom.gameblocks.items;

import com.legoatoom.gameblocks.GameBlocks;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

import static com.legoatoom.gameblocks.GameBlocks.GAME_BLOCKS;

public enum ChessPiece {
    BLACK_PAWN("pawn", 8),
    ROOK("rook", 2),
    BISHOP("bishop", 2),
    KNIGHT("knight", 2),
    QUEEN("queen",1),
    KING("king",1);

    private final String id;
    private final int maxCount;

    ChessPiece(String id, int maxCount) {
        this.id = id;
        this.maxCount = maxCount;
    }

    public static void registerAll(){
        for (ChessPiece chessPiece: ChessPiece.values()) {
            registerPiece("white", chessPiece);
            registerPiece("black", chessPiece);
        }
    }

    private static void registerPiece(String id, ChessPiece chessPiece) {
        Registry.register(Registry.ITEM, GameBlocks.id.apply("%s_%s".formatted(id, chessPiece.id)),
                new Item(new FabricItemSettings().group(GAME_BLOCKS).maxCount(chessPiece.maxCount)));
    }
}
