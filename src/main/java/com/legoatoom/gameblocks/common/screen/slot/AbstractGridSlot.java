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

package com.legoatoom.gameblocks.common.screen.slot;

import com.legoatoom.gameblocks.common.inventory.AbstractBoardInventory;
import com.legoatoom.gameblocks.common.inventory.ServerBoardInventory;
import com.legoatoom.gameblocks.common.items.IPieceItem;
import com.legoatoom.gameblocks.common.util.ActionType;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class AbstractGridSlot extends AbstractBoardSlot {

    private final int BOARD_WIDTH;
    private final int xLoc, yLoc;

    public AbstractGridSlot(AbstractBoardInventory inventory, int boardXLoc, int boardYLoc, int screenXLoc, int screenYLoc) {
        super(inventory, boardYLoc * inventory.getBoardWidth() + boardXLoc, screenXLoc, screenYLoc);
        this.xLoc = boardXLoc;
        this.yLoc = boardYLoc;
        this.BOARD_WIDTH = inventory.getBoardWidth();
        if (!inventory.isClient() && inventory instanceof ServerBoardInventory serverBoardInventory) {
            //noinspection unchecked
            serverBoardInventory.addSlot(this);
        }
    }

    public Optional<? extends IPieceItem> getItem() {
        if (this.hasStack()) {
            return Optional.ofNullable((IPieceItem) this.getStack().getItem());
        } else {
            return Optional.empty();
        }
    }

    public int getBoardXLoc() {
        return xLoc;
    }

    public int getBoardYLoc() {
        return yLoc;
    }

    public AbstractBoardInventory getInventory() {
        return (AbstractBoardInventory) this.inventory;
    }

    public void calculateHints() {
        if (!hasStack() || getInventory().isClient()) return;

        getCurrentPiece().calculateLegalActions(this);
    }

    public IPieceItem getCurrentPiece() {
        if (!hasStack() || !(this.getStack().getItem() instanceof IPieceItem)) return null;
        return (IPieceItem) this.getStack().getItem();
    }

    /**
     * Set the current hint type for this slot from a specified origin.
     *
     */
    public void setHoverHintForOriginIndex(int originIndex, @NotNull ActionType currentHoverAction) {
        // Just making sure the inventory isn't empty and this is not the client.
        if (this.getInventory().isEmpty() || this.getInventory().isClient()) return;
        ((ServerBoardInventory<?>) getInventory()).getSlotHintsPropertyDelegates().get(originIndex).set(getIndex(), currentHoverAction.getId());
    }

    /**
     * Capture the piece that this slot holds.
     */
    public void captureMe(){
        ItemStack stack = getStack();
        IPieceItem pieceItem = (IPieceItem) stack.getItem();
        if (pieceItem != null) {
            int slotId = pieceItem.getStorageIndex() + (BOARD_WIDTH * BOARD_WIDTH);
            getInventory().setStack(getIndex(), ItemStack.EMPTY);
            int i = getInventory().getStack(slotId).getCount();
            stack.setCount(i + 1);
            getInventory().setStack(slotId, stack);
        }
    }

    /**
     * Capturing from a specified stack.
     */
    public void capturePiece(ScreenHandler handler, ItemStack stack){
        if (stack.getItem() instanceof IPieceItem item) {
            int slotId = item.getStorageIndex() + (BOARD_WIDTH * BOARD_WIDTH);
            handler.setCursorStack(ItemStack.EMPTY);
            int i = getInventory().getStack(slotId).getCount();
            stack.setCount(i + 1);
            getInventory().setStack(slotId, stack);
        }
    }

    @Override
    public int getMaxItemCount() {
        return 1;
    }

    @Override
    public int getMaxItemCount(ItemStack stack) {
        return getMaxItemCount();
    }

    @NotNull
    public Optional<AbstractGridSlot> upLeft(boolean isBlack) {
        return upLeft(isBlack, 1);
    }

    @NotNull
    public Optional<AbstractGridSlot> downLeft(boolean isBlack) {
        return downLeft(isBlack, 1);
    }

    @NotNull
    public Optional<AbstractGridSlot> upRight(boolean isBlack) {
        return upRight(isBlack, 1);
    }

    @NotNull
    public Optional<AbstractGridSlot> downRight(boolean isBlack) {
        return downRight(isBlack, 1);
    }

    @NotNull
    public Optional<AbstractGridSlot> up(boolean isBlack) {
        return up(isBlack, 1);
    }

    @NotNull
    public Optional<AbstractGridSlot> down(boolean isBlack) {
        return down(isBlack, 1);
    }

    @NotNull
    public Optional<AbstractGridSlot> left(boolean isBlack) {
        return left(isBlack, 1);
    }

    @NotNull
    public Optional<AbstractGridSlot> right(boolean isBlack) {
        return right(isBlack, 1);
    }

    @NotNull
    public Optional<AbstractGridSlot> up(boolean isBlack, int amount) {
        return isBlack ? down(false, amount) : getSlotFromInventory(this.xLoc, this.yLoc - amount);
    }

    @NotNull
    public Optional<AbstractGridSlot> down(boolean isBlack, int amount) {
        return isBlack ? up(false, amount) : getSlotFromInventory(this.xLoc, this.yLoc + amount);
    }

    @NotNull
    public Optional<AbstractGridSlot> left(boolean isBlack, int amount) {
        return isBlack ? right(false, amount) : getSlotFromInventory(this.xLoc + amount, this.yLoc);
    }

    @NotNull
    public Optional<AbstractGridSlot> right(boolean isBlack, int amount) {
        return isBlack ? left(false, amount) : getSlotFromInventory(this.xLoc - amount, this.yLoc);
    }

    @NotNull
    public Optional<AbstractGridSlot> upLeft(boolean isBlack, int amount) {
        return isBlack ? downRight(false, amount) : getSlotFromInventory(this.xLoc + amount, this.yLoc - amount);
    }

    @NotNull
    public Optional<AbstractGridSlot> upRight(boolean isBlack, int amount) {
        return isBlack ? downLeft(false, amount) : getSlotFromInventory(this.xLoc - amount, this.yLoc - amount);
    }

    @NotNull
    public Optional<AbstractGridSlot> downLeft(boolean isBlack, int amount) {
        return isBlack ? upRight(false, amount) : getSlotFromInventory(this.xLoc + amount, this.yLoc + amount);
    }

    @NotNull
    public Optional<AbstractGridSlot> downRight(boolean isBlack, int amount) {
        return isBlack ? upLeft(false, amount) : getSlotFromInventory(this.xLoc - amount, this.yLoc + amount);
    }

    @NotNull
    private Optional<AbstractGridSlot> getSlotFromInventory(int xLoc, int yLoc) {
        if (this.getInventory().isClient() || 0 > xLoc || xLoc >= BOARD_WIDTH || 0 > yLoc || yLoc >= BOARD_WIDTH) {
            return Optional.empty();
        }
        return Optional.of(((ServerBoardInventory<?>) this.getInventory()).getSlot(xLoc, yLoc));
    }

}
