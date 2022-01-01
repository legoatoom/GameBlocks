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
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;

public class ChessBoardScreenHandler extends ScreenHandler {

    private static final int BOARD_WIDTH = 8;
    private final ChessBoardInventory inventory;
    private final PlayerInventory playerInventory;
    private final Direction chessBoardDirection;

    public ChessBoardScreenHandler(int syncId, PlayerInventory inv) {
        this(syncId, inv, new ChessBoardInventory(), Direction.NORTH /* Default, no influence on client */);
    }

    public ChessBoardScreenHandler(int syncId, PlayerInventory playerInventory, ChessBoardInventory inventory, Direction facing) {
        super(GameBlocks.CHESS_BOARD_SCREEN_HANDLER, syncId);

        this.inventory = inventory;
        this.playerInventory = playerInventory;
        this.chessBoardDirection = facing;

        inventory.onOpen(playerInventory.player);

        initializeSlots();
    }

    private void initializeSlots() {
        int y, x;
        // 0 - 8  + 0 - 64
        for (y = 0; y < BOARD_WIDTH; y++) {
            for (x = 0; x < BOARD_WIDTH; x++) {
                Pair<Integer, Integer> pair = rotationTransformer(x, y);
                int boardX = pair.getLeft();
                int boardY = pair.getRight();
                this.addSlot(new ChessBoardSlot(inventory, boardX, boardY, 24 + x * 16, 17 + y * 16));
            }
        }
        //The player inventory
        for (y = 0; y < 3; ++y) {
            for (x = 0; x < 9; ++x) {
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 170 + y * 18));
            }
        }
        //The player Hotbar
        for (y = 0; y < 9; ++y) {
            this.addSlot(new Slot(playerInventory, y, 8 + y * 18, 228));
        }
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private Pair<Integer, Integer> rotationTransformer(int x, int y) {
        Direction playerFacing = playerInventory.player.getHorizontalFacing();
        if (this.chessBoardDirection == playerFacing) {
            // default
            return new Pair<>(x, y);
        }
        if (this.chessBoardDirection.getOpposite() == playerFacing) {
            // 180 degree
            return new Pair<>(7 - x, 7 - y);
        }
        if (this.chessBoardDirection.rotateYClockwise() == playerFacing) {
            // 270 degree
            return new Pair<>(7 - y, x);
        }
        // 90 degree
        return new Pair<>(y, 7 - x);
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
