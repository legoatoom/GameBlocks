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

package com.legoatoom.gameblocks.screen.slot;

import com.legoatoom.gameblocks.inventory.AbstractBoardInventory;
import com.legoatoom.gameblocks.inventory.chess.ServerBoardInventory;
import com.legoatoom.gameblocks.inventory.chess.ServerChessBoardInventory;
import com.legoatoom.gameblocks.items.IPieceItem;
import com.legoatoom.gameblocks.items.chess.IChessPieceItem;
import com.legoatoom.gameblocks.util.chess.ChessActionType;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class GridSlot extends AbstractBoardSlot {

    private final int BOARD_WIDTH;
    private final int xLoc, yLoc;

    public GridSlot(AbstractBoardInventory inventory, int boardXLoc, int boardYLoc, int screenXLoc, int screenYLoc) {
        super(inventory, boardYLoc * inventory.BOARD_WIDTH + boardXLoc, screenXLoc, screenYLoc);
        this.xLoc = boardXLoc;
        this.yLoc = boardYLoc;
        this.BOARD_WIDTH = inventory.BOARD_WIDTH;
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
        if (!hasStack() || !(this.getStack().getItem() instanceof IChessPieceItem)) return null;
        return (IPieceItem) this.getStack().getItem();
    }

    /**
     * Set the current hint type for this slot from a specified origin.
     *
     * @param originIndex
     * @param currentHoverAction
     */
    public void setHoverHintForOriginIndex(int originIndex, @NotNull ChessActionType currentHoverAction) {
        // Just making sure the inventory isn't empty and this is not the client.
        if (this.getInventory().isEmpty() || this.getInventory().isClient()) return;
        ((ServerBoardInventory) getInventory()).getSlotHintsPropertyDelgates().get(originIndex).set(getIndex(), currentHoverAction.getId());
    }

    public abstract void capturePiece();

    public abstract void capturePiece(ScreenHandler handler, ItemStack stack);

    @NotNull
    public Optional<GridSlot> upLeft(boolean isBlack) {
        return upLeft(isBlack, 1);
    }

    @NotNull
    public Optional<GridSlot> downLeft(boolean isBlack) {
        return downLeft(isBlack, 1);
    }

    @NotNull
    public Optional<GridSlot> upRight(boolean isBlack) {
        return upRight(isBlack, 1);
    }

    @NotNull
    public Optional<GridSlot> downRight(boolean isBlack) {
        return downRight(isBlack, 1);
    }

    @NotNull
    public Optional<GridSlot> up(boolean isBlack) {
        return up(isBlack, 1);
    }

    @NotNull
    public Optional<GridSlot> down(boolean isBlack) {
        return down(isBlack, 1);
    }

    @NotNull
    public Optional<GridSlot> left(boolean isBlack) {
        return left(isBlack, 1);
    }

    @NotNull
    public Optional<GridSlot> right(boolean isBlack) {
        return right(isBlack, 1);
    }

    @NotNull
    public Optional<GridSlot> up(boolean isBlack, int amount) {
        return isBlack ? down(false, amount) : getSlotFromInventory(this.xLoc, this.yLoc - amount);
    }

    @NotNull
    public Optional<GridSlot> down(boolean isBlack, int amount) {
        return isBlack ? up(false, amount) : getSlotFromInventory(this.xLoc, this.yLoc + amount);
    }

    @NotNull
    public Optional<GridSlot> left(boolean isBlack, int amount) {
        return isBlack ? right(false, amount) : getSlotFromInventory(this.xLoc + amount, this.yLoc);
    }

    @NotNull
    public Optional<GridSlot> right(boolean isBlack, int amount) {
        return isBlack ? left(false, amount) : getSlotFromInventory(this.xLoc - amount, this.yLoc);
    }

    @NotNull
    public Optional<GridSlot> upLeft(boolean isBlack, int amount) {
        return isBlack ? downRight(false, amount) : getSlotFromInventory(this.xLoc + amount, this.yLoc - amount);
    }

    @NotNull
    public Optional<GridSlot> upRight(boolean isBlack, int amount) {
        return isBlack ? downLeft(false, amount) : getSlotFromInventory(this.xLoc - amount, this.yLoc - amount);
    }

    @NotNull
    public Optional<GridSlot> downLeft(boolean isBlack, int amount) {
        return isBlack ? upRight(false, amount) : getSlotFromInventory(this.xLoc + amount, this.yLoc + amount);
    }

    @NotNull
    public Optional<GridSlot> downRight(boolean isBlack, int amount) {
        return isBlack ? upLeft(false, amount) : getSlotFromInventory(this.xLoc - amount, this.yLoc + amount);
    }

    @NotNull
    private Optional<GridSlot> getSlotFromInventory(int xLoc, int yLoc) {
        if (this.getInventory().isClient() || 0 > xLoc || xLoc >= BOARD_WIDTH || 0 > yLoc || yLoc >= BOARD_WIDTH) {
            return Optional.empty();
        }
        return Optional.of(((ServerChessBoardInventory) this.getInventory()).getSlot(xLoc, yLoc));
    }

}
