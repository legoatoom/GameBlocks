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

package com.legoatoom.gameblocks.inventory;

import com.google.common.collect.Lists;
import com.legoatoom.gameblocks.GameBlocks;
import com.legoatoom.gameblocks.items.chess.IChessPieceItem;
import com.legoatoom.gameblocks.screen.slot.ChessBoardSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChessBoardInventory implements Inventory {

    public static final int BOARD_WIDTH = 8;
    public static final int BOARD_SIZE = 64;

    protected final DefaultedList<ItemStack> board;
    public List<ChessBoardSlot> movables;
    private final ChessBoardSlot[] slots;

//    private CheckmateDetector checkmateDetector;


    public ChessBoardInventory() {
        board = DefaultedList.ofSize(BOARD_SIZE, ItemStack.EMPTY);
        slots = new ChessBoardSlot[BOARD_SIZE];
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
                        board.set(ChessBoardSlot.xyToIndex(x,y), new ItemStack(chessPiece));
                    }
                }
            }
        }
    }

    @Override
    public int size() {
        return BOARD_SIZE;
    }

    @Override
    public boolean isEmpty() {
        return getBoard().stream().allMatch(ItemStack::isEmpty);
    }

    /**
     * Fetches the stack currently stored at the given slot. If the slot is empty,
     * or is outside the bounds of this inventory, returns see {@link ItemStack#EMPTY}.
     *
     * @param index
     */
    @Override
    public ItemStack getStack(int index) {
        try {
            return getBoard().get(index);
        } catch (IndexOutOfBoundsException e) {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return stack.getItem() instanceof IChessPieceItem;
    }

    /**
     * Removes a specific number of items from the given slot.
     *
     * @param slot
     * @param amount
     * @return the removed items as a stack
     */
    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack result = Inventories.splitStack(getBoard(), slot, amount);
        if (!result.isEmpty()) {
            markDirty();
        }
        return result;
    }

    /**
     * Removes the stack currently stored at the indicated slot.
     *
     * @param slot
     * @return the stack previously stored at the indicated slot.
     */
    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(getBoard(), slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        getBoard().set(slot, stack);
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        getBoard().clear();
    }

    public void readNbt(NbtCompound nbt) {
        Inventories.readNbt(nbt, board);
    }

    public void writeNBt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, board);
    }

    public DefaultedList<ItemStack> getBoard() {
        return board;
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
}
