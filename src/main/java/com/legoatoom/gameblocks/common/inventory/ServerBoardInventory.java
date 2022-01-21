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

import com.google.common.collect.Lists;
import com.legoatoom.gameblocks.common.items.IPieceItem;
import com.legoatoom.gameblocks.common.screen.slot.AbstractGridSlot;
import com.legoatoom.gameblocks.common.util.ActionType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;

public interface ServerBoardInventory<T extends AbstractGridSlot> extends Inventory {

    default void addSlot(T slot) {
        this.setSlot(slot.getIndex(), slot);
    }

    default void updateHints(){
        cleanHoverActions();
        for (T slot : getSlots()) {
            slot.calculateHints();
        }
    }

    default void cleanHoverActions(){
        for (T origin : getSlots()) {
            for (T focus : getSlots()) {
                origin.setHoverHintForOriginIndex(focus.getIndex(), getDefaultHint());
            }
        }
    }

    T[] getSlots();

    ActionType getDefaultHint();


    void setSlot(int index, T slot);

    T getSlot(int index);

    default T getSlot(int x, int y) {
        return getSlot(y * getBoardWidth() + x);
    }

    int getBoardWidth();

    int getBoardSize();

    ArrayList<ArrayPropertyDelegate> getSlotHintsPropertyDelgates();

    void fillWithDefaultPieces();

    // Possibly replace with function that will handle the dropping of Packages and leftovers.
    boolean canDropPackage();

    default void resetBoard() {
        ArrayList<IPieceItem> availableItems = Lists.newArrayList();
        for (ItemStack stack : getItems()) {
            if (stack.isEmpty()) continue;
            for (int i = 0; i < stack.getCount(); i++) {
                if (stack.getItem() instanceof IPieceItem item) {
                    availableItems.add((IPieceItem) item.defaultState(stack).getItem());
                }
            }
        }
        this.clear();
        forAllAvailable:
        for (IPieceItem pieceItem : availableItems) {
            for (int y = 0; y < getBoardWidth(); y++) {
                for (int x = 0; x < getBoardWidth(); x++) {
                    if (pieceItem.isDefaultLocation(x, y) && !getSlot(x, y).hasStack()) {
                        setStack(y * getBoardWidth() + x, new ItemStack(pieceItem));
                        continue forAllAvailable;
                    }
                }
            }
            for (int x = 0; x < getStorageSlotSize(); x++){
                if (x == pieceItem.getStorageIndex()){
                    ItemStack orig = getStack(x + getBoardSize());
                    if (orig.isEmpty()){
                        setStack(x + getBoardSize(), new ItemStack(pieceItem));
                    } else {
                        orig.increment(1);
                    }
                    continue forAllAvailable;
                }
            }
        }
        markDirty();
    }

    int getStorageSlotSize();

    /**
     * Retrieves the item list of this inventory.
     * Must return the same instance every time it's called.
     *
     * @see AbstractBoardInventory#getItems()
     */
    DefaultedList<ItemStack> getItems();
}
