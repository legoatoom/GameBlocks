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

package com.legoatoom.gameblocks.gameblocks.screen;

import com.legoatoom.gameblocks.gameblocks.GameBlocks;
import com.legoatoom.gameblocks.gameblocks.blocks.entity.ChessBoardBlockEntity;
import com.legoatoom.gameblocks.gameblocks.inventory.ChessBoardInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class ChessBoardScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PlayerInventory playerInventory;

    public ChessBoardScreenHandler(int syncId, PlayerInventory inv) {
        this(syncId, inv, new ChessBoardInventory(64));
    }

    public ChessBoardScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory){
        super(GameBlocks.CHESS_BOARD_SCREEN_HANDLER, syncId);
        this.inventory = inventory;
        this.playerInventory = playerInventory;

        inventory.onOpen(playerInventory.player);

        initializeSlots();
    }

    private void initializeSlots() {
        int m;
        int l;
        for (m = 0; m < 8; m++) {
            for (l = 0; l < 8; l++) {
                this.addSlot(new ChessBoardSlot(inventory, l + m * 8, 24 + l * 16, 17 + m * 16));
            }
        }
        //The player inventory
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 160 + m * 18));
            }
        }
        //The player Hotbar
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 218));
        }
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        //noinspection ConstantConditions
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    public static class ChessBoardSlot extends Slot{
        public ChessBoardSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public int getMaxItemCount() {
            return 1;
        }
    }
}
