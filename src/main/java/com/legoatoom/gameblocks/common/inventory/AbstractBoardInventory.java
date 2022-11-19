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

package com.legoatoom.gameblocks.common.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;

public abstract class AbstractBoardInventory implements Inventory {

    private final int storageSlotSize;

    public int getBoardWidth() {
        return boardWidth;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public final int boardWidth;
    public final int boardSize;
    private final DefaultedList<ItemStack> board;
    private final boolean isClient;

    public AbstractBoardInventory(boolean isClient, int boardWidth, int storageSlotSize) {
        this.boardWidth = boardWidth;
        this.boardSize = boardWidth * boardWidth;
        this.isClient = isClient;
        this.storageSlotSize = storageSlotSize;
        this.board = DefaultedList.ofSize(boardSize + storageSlotSize, ItemStack.EMPTY);
    }

    public boolean isClient() {
        return isClient;
    }

    /**
     * Retrieves the item list of this inventory.
     * Must return the same instance every time it's called.
     */
    public DefaultedList<ItemStack> getItems() {
        return board;
    }

    /**
     * Returns the inventory size.
     */
    @Override
    public int size() {
        return getItems().size();
    }

    /**
     * Checks if the inventory is empty.
     *
     * @return true if this inventory has only empty stacks, false otherwise.
     */
    @Override
    public boolean isEmpty() {
        for (int i = 0; i < size(); i++) {
            ItemStack stack = getStack(i);
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Retrieves the item in the slot.
     */
    @Override
    public ItemStack getStack(int slot) {
        return getItems().get(slot);
    }

    /**
     * Removes items from an inventory slot.
     *
     * @param slot  The slot to remove from.
     * @param count How many items to remove. If there are less items in the slot than what are requested,
     *              takes all items in that slot.
     */
    @Override
    public ItemStack removeStack(int slot, int count) {
        ItemStack result = Inventories.splitStack(getItems(), slot, count);
        if (!result.isEmpty()) {
            markDirty();
        }
        return result;
    }

    /**
     * Removes all items from an inventory slot.
     *
     * @param slot The slot to remove from.
     */
    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(getItems(), slot);
    }

    /**
     * Replaces the current stack in an inventory slot with the provided stack.
     *
     * @param slot  The inventory slot of which to replace the itemstack.
     * @param stack The replacing itemstack. If the stack is too big for
     *              this inventory ({@link Inventory#getMaxCountPerStack()}),
     *              it gets resized to this inventory's maximum amount.
     */
    @Override
    public void setStack(int slot, ItemStack stack) {
        getItems().set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
    }

    /**
     * @return true if the player can use the inventory, false otherwise.
     */
    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }


    /**
     * Clears the inventory.
     */
    @Override
    public void clear() {
        getItems().clear();
    }


    public abstract ArrayList<ArrayPropertyDelegate> getSlotHintsPropertyDelegates();

    public int getStorageSlotSize() {
        return storageSlotSize;
    }
}
