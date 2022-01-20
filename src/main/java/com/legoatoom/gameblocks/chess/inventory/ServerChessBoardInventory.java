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

package com.legoatoom.gameblocks.chess.inventory;

import com.legoatoom.gameblocks.chess.items.IChessPieceItem;
import com.legoatoom.gameblocks.chess.screen.slot.ChessGridSlot;
import com.legoatoom.gameblocks.chess.util.ChessActionType;
import com.legoatoom.gameblocks.common.inventory.ServerBoardInventory;
import com.legoatoom.gameblocks.common.util.ActionType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.legoatoom.gameblocks.registry.ChessRegistry.CHESS_PIECES;

public class ServerChessBoardInventory extends ChessBoardInventory implements ServerBoardInventory<ChessGridSlot> {

    public final ArrayList<ArrayPropertyDelegate> slotHintPropertyDelegate = new ArrayList<>();
    final ChessGridSlot[] slots;
    private final BlockEntity entity;


    public ServerChessBoardInventory(BlockEntity entity) {
        super(false);
        for (int i = 0; i < boardSize; i++) {
            this.slotHintPropertyDelegate.add(new ArrayPropertyDelegate(boardSize));
        }
        slots = new ChessGridSlot[boardSize];
        this.entity = entity;
    }

    @Override
    public ChessGridSlot[] getSlots() {
        return slots;
    }

    @Override
    public ActionType getDefaultHint() {
        return ChessActionType.NONE;
    }

    @Override
    public void setSlot(int index, ChessGridSlot slot) {
        this.slots[index] = slot;
    }

    @Override
    public ChessGridSlot getSlot(int index) {
        return slots[index];
    }

    public BlockEntity getEntity() {
        return entity;
    }

    @Override
    @NotNull
    public ArrayList<ArrayPropertyDelegate> getSlotHintsPropertyDelgates() {
        return slotHintPropertyDelegate;
    }

    @Override
    public void fillWithDefaultPieces() {
        for (IChessPieceItem chessPiece : CHESS_PIECES) {
            this.setStack(boardSize + chessPiece.getStorageIndex(),  new ItemStack(chessPiece, chessPiece.getMaxCount()));
        }
    }

    @Override
    public void markDirty() {
        var entity = getEntity();
        if (getEntity().getWorld() != null) {
            entity.markDirty();
        }
    }

    @Override
    public boolean canDropPackage() {
        int wpawns = 8, bpawns = 8;
        int wrooks = 2, brooks = 2;
        int wknights = 2, bknights = 2;
        int wbishops = 2, bbishops = 2;
        int wqueen = 1, bqueen = 1;
        int wkings = 1, bkings = 1;
        for (ItemStack stack : getItems()) {
            if (stack.getItem() instanceof IChessPieceItem item) {
                boolean isBlack = item.isBlack();
                switch (item.getType()) {
                    case ROOK -> {
                        if (isBlack) brooks -= stack.getCount();
                        else wrooks -= stack.getCount();
                    }
                    case PAWN -> {
                        if (isBlack) bpawns -= stack.getCount();
                        else wpawns -= stack.getCount();
                    }
                    case KNIGHT -> {
                        if (isBlack) bknights -= stack.getCount();
                        else wknights -= stack.getCount();
                    }case BISHOP -> {
                        if (isBlack) bbishops -= stack.getCount();
                        else wbishops -= stack.getCount();
                    }
                    case QUEEN -> {
                        if (isBlack) bqueen -= stack.getCount();
                        else wqueen -= stack.getCount();
                    }
                    case KING -> {
                        if (isBlack) bkings -= stack.getCount();
                        else wkings -= stack.getCount();
                    }
                }
            }
        }
        if (wpawns != 0) return false;
        if (wrooks != 0) return false;
        if (wknights != 0) return false;
        if (wbishops != 0) return false;
        if (wqueen != 0) return false;
        if (wkings != 0) return false;
        if (bpawns != 0) return false;
        if (brooks != 0) return false;
        if (bknights != 0) return false;
        if (bbishops != 0) return false;
        if (bqueen != 0) return false;
        if (bkings != 0) return false;
        return true;
    }


}
