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

import com.legoatoom.gameblocks.GameBlocks;
import com.legoatoom.gameblocks.items.chess.IChessPieceItem;
import com.legoatoom.gameblocks.screen.slot.ChessBoardSlot;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;

import java.util.ArrayList;

public class ServerChessBoardInventory extends ChessBoardInventory{


    public final ArrayList<ArrayPropertyDelegate> slotHintPropertyDelegate = new ArrayList<>();
    private final BlockEntity entity;
    final ChessBoardSlot[] slots;


    public ServerChessBoardInventory(BlockEntity entity){
        super(false);
        for (int i = 0; i < BOARD_SIZE; i++) {
            this.slotHintPropertyDelegate.add(new ArrayPropertyDelegate(BOARD_SIZE));
        }
        slots = new ChessBoardSlot[BOARD_SIZE];
        this.entity = entity;

    }

    @Override
    public void onOpen(PlayerEntity player) {
        if (isEmpty()) initializePieces();
    }

    private void initializePieces() {
        for (int x = 0; x < BOARD_WIDTH; x++) {
            for (int y = 0; y < BOARD_WIDTH; y++) {
                for (IChessPieceItem chessPiece : GameBlocks.CHESS_PIECES) {
                    if (chessPiece.isDefaultLocation(x, y)){
                        setStack(ChessBoardSlot.xyToIndex(x,y), new ItemStack(chessPiece));
                    }
                }
            }
        }
    }



    public ArrayPropertyDelegate getSlotHintPropertyDelegate(int origin) {
        return slotHintPropertyDelegate.get(origin);
    }

    public void updateHints() {
        IChessPieceItem.cleanHoverActions(this.slots);
        for (ChessBoardSlot slot : this.slots) {
            slot.calculateHints();
        }
    }





    public void addSlot(ChessBoardSlot chessBoardSlot) {
        this.slots[chessBoardSlot.getIndex()] = chessBoardSlot;
    }

    public ChessBoardSlot getSlot(int index){
        return slots[index];
    }

    public ChessBoardSlot getSlot(int x, int y){
        return getSlot(ChessBoardSlot.xyToIndex(x, y));
    }

    @Override
    public void markDirty() {
        if (entity.getWorld() != null) {
            entity.markDirty();
        }
    }


}
