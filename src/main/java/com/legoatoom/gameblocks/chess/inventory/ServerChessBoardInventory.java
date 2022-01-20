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
import java.util.Arrays;

import static com.legoatoom.gameblocks.registry.ChessRegistry.*;

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
        int pawns = WHITE_PAWN.getMaxCount() + BLACK_PAWN.getMaxCount();
        int rooks = WHITE_ROOK.getMaxCount() + BLACK_ROOK.getMaxCount();
        int knights = WHITE_KNIGHT.getMaxCount() + BLACK_KNIGHT.getMaxCount();
        int bishops = WHITE_BISHOP.getMaxCount() + BLACK_BISHOP.getMaxCount();
        int queen = WHITE_QUEEN.getMaxCount() + BLACK_QUEEN.getMaxCount();
        int kings = WHITE_KING.getMaxCount() + BLACK_KING.getMaxCount();
        for (ItemStack stack : getItems()) {
            if (stack.getItem() instanceof IChessPieceItem item) {
                switch (item.getType()) {
                    case ROOK -> rooks -= stack.getCount();
                    case PAWN -> pawns -= stack.getCount();
                    case KNIGHT -> knights -= stack.getCount();
                    case BISHOP -> bishops -= stack.getCount();
                    case QUEEN -> queen -= stack.getCount();
                    case KING -> kings -= stack.getCount();
                }
            }
        }
        return Arrays.stream(new int[]{pawns, rooks, knights, bishops, queen, kings}).allMatch(value -> value == 0);
    }


}
