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

package com.legoatoom.gameblocks.inventory.chess;

import com.google.common.collect.Lists;
import com.legoatoom.gameblocks.items.chess.*;
import com.legoatoom.gameblocks.screen.slot.ChessGridBoardSlot;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;

import java.util.ArrayList;
import java.util.Arrays;

import static com.legoatoom.gameblocks.registry.ChessRegistry.*;

public class ServerChessBoardInventory extends ChessBoardInventory {


    public final ArrayList<ArrayPropertyDelegate> slotHintPropertyDelegate = new ArrayList<>();
    final ChessGridBoardSlot[] slots;
    private final BlockEntity entity;


    public ServerChessBoardInventory(BlockEntity entity) {
        super(false);
        for (int i = 0; i < BOARD_SIZE; i++) {
            this.slotHintPropertyDelegate.add(new ArrayPropertyDelegate(BOARD_SIZE));
        }
        slots = new ChessGridBoardSlot[BOARD_SIZE];
        this.entity = entity;
    }

    public ArrayPropertyDelegate getSlotHintPropertyDelegate(int origin) {
        return slotHintPropertyDelegate.get(origin);
    }

    public void updateHints() {
        IChessPieceItem.cleanHoverActions(this.slots);
        for (ChessGridBoardSlot slot : this.slots) {
            slot.calculateHints();
        }
    }

    public void addSlot(ChessGridBoardSlot chessGridBoardSlot) {
        this.slots[chessGridBoardSlot.getIndex()] = chessGridBoardSlot;
    }

    public ChessGridBoardSlot getSlot(int index) {
        return slots[index];
    }

    public ChessGridBoardSlot getSlot(int x, int y) {
        return getSlot(y * BOARD_WIDTH + x);
    }

    /**
     * Very important, it makes sure that the inventory is stored.
     */
    @Override
    public void markDirty() {
        if (entity.getWorld() != null) {
            entity.markDirty();
        }
    }

    public void resetBoard() {
        ArrayList<IChessPieceItem> availableItems = Lists.newArrayList();
        for (ItemStack stack : getItems()) {
            if (stack.isEmpty()) continue;
            for (int i = 0; i < stack.getCount(); i++) {
                if (stack.getItem() instanceof IChessPieceItem item){
                    availableItems.add(item);
                }
            }
        }
        this.clear();
        label:
        for (IChessPieceItem chessPiece : availableItems) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                for (int y = 0; y < BOARD_WIDTH; y++) {
                    if (chessPiece.isDefaultLocation(x, y) && !getSlot(x,y).hasStack()) {
                        setStack(y * BOARD_WIDTH + x, new ItemStack(chessPiece));
                        continue label;
                    }
                }
            }
        }

    }

    @SuppressWarnings("PointlessArithmeticExpression")
    @Override
    public void fillWithDefaultPieces() {
        this.setStack(0 + 0 * 2 + BOARD_SIZE, new ItemStack(WHITE_PAWN,WHITE_PAWN.getMaxCount()));
        this.setStack(0 + 1 * 2 + BOARD_SIZE, new ItemStack(WHITE_ROOK,WHITE_ROOK.getMaxCount()));
        this.setStack(0 + 2 * 2 + BOARD_SIZE, new ItemStack(WHITE_KNIGHT,WHITE_KNIGHT.getMaxCount()));
        this.setStack(0 + 3 * 2 + BOARD_SIZE, new ItemStack(WHITE_BISHOP,WHITE_BISHOP.getMaxCount()));
        this.setStack(0 + 4 * 2 + BOARD_SIZE, new ItemStack(WHITE_QUEEN,WHITE_QUEEN.getMaxCount()));
        this.setStack(0 + 5 * 2 + BOARD_SIZE, new ItemStack(WHITE_KING,WHITE_KING.getMaxCount()));

        this.setStack(1 + 0 * 2 + BOARD_SIZE, new ItemStack(BLACK_PAWN,BLACK_PAWN.getMaxCount()));
        this.setStack(1 + 1 * 2 + BOARD_SIZE, new ItemStack(BLACK_ROOK,BLACK_ROOK.getMaxCount()));
        this.setStack(1 + 2 * 2 + BOARD_SIZE, new ItemStack(BLACK_KNIGHT,BLACK_KNIGHT.getMaxCount()));
        this.setStack(1 + 3 * 2 + BOARD_SIZE, new ItemStack(BLACK_BISHOP,BLACK_BISHOP.getMaxCount()));
        this.setStack(1 + 4 * 2 + BOARD_SIZE, new ItemStack(BLACK_QUEEN,BLACK_QUEEN.getMaxCount()));
        this.setStack(1 + 5 * 2 + BOARD_SIZE, new ItemStack(BLACK_KING,BLACK_KING.getMaxCount()));
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
            if (stack.getItem() instanceof IChessPieceItem item){
                switch (item.getType()){
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
