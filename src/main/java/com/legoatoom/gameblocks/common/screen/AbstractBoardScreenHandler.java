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

package com.legoatoom.gameblocks.common.screen;

import com.legoatoom.gameblocks.common.inventory.AbstractBoardInventory;
import com.legoatoom.gameblocks.common.inventory.ServerBoardInventory;
import com.legoatoom.gameblocks.common.items.IPieceItem;
import com.legoatoom.gameblocks.common.screen.slot.AbstractBoardSlot;
import com.legoatoom.gameblocks.common.screen.slot.AbstractGridSlot;
import com.legoatoom.gameblocks.common.util.ActionType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.function.Supplier;

public abstract class AbstractBoardScreenHandler<T extends AbstractBoardInventory> extends ScreenHandler implements ScreenHandlerListener {
    protected final PlayerInventory playerInventory;
    protected final T boardInventory;
    protected final int BOARD_WIDTH, BOARD_SIZE;
    protected final Direction FACING;
    protected final ArrayList<ArrayPropertyDelegate> slotHintPropertyDelegate;

    public AbstractBoardScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory inv, PacketByteBuf buf, Supplier<T> boardSupplier) {
        this(type, syncId, inv, boardSupplier.get(), Direction.fromHorizontal(buf.readInt()));
        //CLIENT
    }

    public AbstractBoardScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, T boardInventory, Direction facing) {
        super(type, syncId);
        this.playerInventory = playerInventory;
        this.boardInventory = boardInventory;
        this.BOARD_WIDTH = boardInventory.boardWidth;
        this.BOARD_SIZE = boardInventory.boardSize;
        this.FACING = facing;
        this.slotHintPropertyDelegate = boardInventory.getSlotHintsPropertyDelgates();
        boardInventory.onOpen(playerInventory.player);
        initializeSlots();
        if (boardInventory.isClient()) {
            initializeClient();
        } else {
            initializeServer();
        }
    }

    public ArrayList<ArrayPropertyDelegate> getSlotHintPropertyDelegate() {
        return slotHintPropertyDelegate;
    }

    protected abstract void initializeSlots();

    protected void initializeClient() {
        // Client needs to create them.
        // I am not 100% sure this code is only run in client.
        for (int i = 0; i < BOARD_SIZE; i++) {
            var x = new ArrayPropertyDelegate(BOARD_SIZE);
            this.slotHintPropertyDelegate.add(x);
            this.addProperties(x);
        }
    }

    protected void initializeServer() {
        // Server already has them.
        // I am not 100% sure this code is only run in server.
        for (ArrayPropertyDelegate pd : this.slotHintPropertyDelegate) {
            this.addProperties(pd);
        }
        this.addListener(this);
        this.sendContentUpdates();
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public Pair<Integer, Integer> rotationTransformer(int x, int y) {
        Direction playerFacing = playerInventory.player.getHorizontalFacing();
        if (this.FACING == playerFacing) {
            // default
            return new Pair<>(x, y);
        }
        if (this.FACING.getOpposite() == playerFacing) {
            // 180 degree
            return new Pair<>(BOARD_WIDTH - 1 - x, BOARD_WIDTH - 1 - y);
        }
        if (this.FACING.rotateYClockwise() == playerFacing) {
            // 270 degree
            return new Pair<>(BOARD_WIDTH - 1 - y, x);
        }
        // 90 degree
        return new Pair<>(y, BOARD_WIDTH - 1 - x);
    }

    public abstract ArrayList<AbstractGridSlot> getCurrentSlotActions(int origin);

    @Override
    public ItemStack transferSlot(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.boardInventory.size()) {
                if (!this.insertItem(originalStack, this.boardInventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.insertItem(originalStack, boardInventory.boardSize, this.boardInventory.size(), false)) {
                    return ItemStack.EMPTY;
                }
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
    public void close(PlayerEntity player) {
        super.close(player);
        boardInventory.onClose(player);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return boardInventory.canPlayerUse(player);
    }

    public T getBoardInventory() {
        return boardInventory;
    }

    //Keep Track of where we last clicked.
    private int originIndex = -1;

    @Override
    public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
        // SlotID != SlotIndex, just so you know.
        assert handler == this : "OnSlotUpdate received handler that isn't itself";
        if (this.getBoardInventory().isClient()) return;

        Slot slot = this.getSlot(slotId);
        // We check if the updated slot is a AbstractBoardSlot
        if (slot instanceof AbstractGridSlot s) {
            int slotIndex = s.getIndex();
            // If we grabbed something and the result is now empty.
            if (!this.getCursorStack().isEmpty() && stack.isEmpty()) {
                originIndex = slotIndex;
                return;
            }
            // If we get here it means we have placed something on an empty grid slot.
            ItemStack slotStack = slot.getStack();
            if (slotStack.getItem() instanceof IPieceItem chessPieceItem) {
                // This check should probably never fail but just to make sure.
                if (originIndex != -1) {
                    // Origin means the location came from. So we can check what was the action
                    // for the slot that was updated.
                    ActionType type = this.getActionTypeFromSlot(originIndex, slotIndex);
                    if (!type.shouldIgnore()) {
                        chessPieceItem.handleAction(this, s, getCursorStack(), type);
                    }
                }
                // We checked above that it is indeed not client.
                // Now that an action has happened we have to update hints.
                ((ServerBoardInventory<?>) s.getInventory()).updateHints();
            }
        } else {
            originIndex = -1;
        }
    }

    public abstract ActionType getActionTypeFromSlot(int origin, int slotId);

    @Override
    public void onPropertyUpdate(ScreenHandler handler, int property, int value) {

    }

    @Override
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        if (slot instanceof AbstractBoardSlot) {
            return slot.canInsert(stack);
        }
        return false;
    }
}
