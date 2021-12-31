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

package com.legoatoom.gameblocks.screen;

import com.legoatoom.gameblocks.GameBlocks;
import com.legoatoom.gameblocks.inventory.ChessBoardInventory;
import com.legoatoom.gameblocks.screen.slot.ChessBoardSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.Direction;
import oshi.util.tuples.Triplet;

public class ChessBoardScreenHandler extends ScreenHandler {

    private static final int BOARD_WIDTH = 8;
    private final Inventory inventory;
    private final PlayerInventory playerInventory;
    private final Direction chessBoardDirection;

    public ChessBoardScreenHandler(int syncId, PlayerInventory inv) {
        this(syncId, inv, new ChessBoardInventory(BOARD_WIDTH * BOARD_WIDTH), Direction.NORTH /* Default, no influence on client */);
    }

    public ChessBoardScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, Direction facing) {
        super(GameBlocks.CHESS_BOARD_SCREEN_HANDLER, syncId);

        this.inventory = inventory;
        this.playerInventory = playerInventory;
        this.chessBoardDirection = facing;

        inventory.onOpen(playerInventory.player);

        initializeSlots();
    }

    private void initializeSlots() {
        int m, l;
        // 0 - 8  + 0 - 64
        for (m = 0; m < BOARD_WIDTH; m++) {
            for (l = 0; l < BOARD_WIDTH; l++) {
                Triplet<Integer, Integer, Integer> triplet = getIndexFromDirection(m, l);
                this.addSlot(new ChessBoardSlot(inventory, triplet.getA(), 24 + l * 16, 17 + m * 16, triplet.getB(), triplet.getC()));
            }
        }
        //The player inventory
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 170 + m * 18));
            }
        }
        //The player Hotbar
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 228));
        }
    }

    private Triplet<Integer, Integer, Integer> getIndexFromDirection(int row, int column) {

        Direction playerFacing = playerInventory.player.getHorizontalFacing();
        if (this.chessBoardDirection == playerFacing) {
            // default
            return new Triplet<>((row * BOARD_WIDTH) + column, column, row);
        }
        if (this.chessBoardDirection.getOpposite() == playerFacing) {
            // 180 degree
            return new Triplet<>((BOARD_WIDTH * BOARD_WIDTH)- 1 - (BOARD_WIDTH * row) - column, 7 - column ,7-row);
        }
        if (this.chessBoardDirection.rotateYClockwise() == playerFacing) {
            // 270 degree
            return new Triplet<>(BOARD_WIDTH - (row + 1) + (BOARD_WIDTH * column), row, 7-column);
        }
        // 90 degree
        return new Triplet<>((BOARD_WIDTH * BOARD_WIDTH) - (BOARD_WIDTH * (column + 1)) + row, 7-row ,column);
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




    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        this.inventory.onClose(player);
    }


}
