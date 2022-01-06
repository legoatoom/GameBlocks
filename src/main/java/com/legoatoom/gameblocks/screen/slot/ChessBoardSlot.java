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

package com.legoatoom.gameblocks.screen.slot;

import com.legoatoom.gameblocks.inventory.ChessBoardInventory;
import com.legoatoom.gameblocks.inventory.ServerChessBoardInventory;
import com.legoatoom.gameblocks.items.chess.IChessPieceItem;
import com.legoatoom.gameblocks.util.chess.ChessActionType;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public class ChessBoardSlot extends Slot {


    private final int xLoc, yLoc;

    public ChessBoardSlot(ChessBoardInventory inventory, int boardXLoc, int boardYLoc, int screenXLoc, int screenYLoc) {
        super(inventory, xyToIndex(boardXLoc, boardYLoc), screenXLoc, screenYLoc);
        this.xLoc = boardXLoc;
        this.yLoc = boardYLoc;
        if (!inventory.isClient()) {
            ((ServerChessBoardInventory) inventory).addSlot(this);
        }
    }

    public Optional<IChessPieceItem> getItem(){
        if (this.hasStack()){
            return Optional.ofNullable((IChessPieceItem) this.getStack().getItem());
        } else {
            return Optional.empty();
        }
    }

    public static int xyToIndex(int x, int y) {
        return y * ChessBoardInventory.BOARD_WIDTH + x;
    }

    public ChessBoardInventory getInventory() {
        return (ChessBoardInventory) this.inventory;
    }

    public int getBoardXLoc() {
        return xLoc;
    }

    public int getBoardYLoc() {
        return yLoc;
    }

    public void calculateHints() {
        if (hasStack() && !getInventory().isClient()) {
            IChessPieceItem current = getCurrentPiece();
            Objects.requireNonNull(current);

            current.calculateLegalActions(this);
        }
    }

    @Override
    public int getMaxItemCount(ItemStack stack) {
        return 1;
    }

    public IChessPieceItem getCurrentPiece() {
        if (hasStack() && this.getStack().getItem() instanceof IChessPieceItem)
            return (IChessPieceItem) this.getStack().getItem();
        return null;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return stack.isEmpty() || stack.getItem().asItem() instanceof IChessPieceItem;
    }


    public void capturePiece() {
        ItemStack stack = getStack();
        IChessPieceItem chessPieceItem = (IChessPieceItem) stack.getItem();
        if (chessPieceItem != null) {
            int slotId = getSlotIdFromType(chessPieceItem.isBlack(), chessPieceItem.getType());
            getInventory().setStack(getIndex(), ItemStack.EMPTY);
            int i = getInventory().getStack(slotId).getCount();
            stack.setCount(i + 1);
            getInventory().setStack(slotId, stack);
        }
    }

    public void capturePiece(ScreenHandler handler, ItemStack itemStack) {
        if (itemStack.getItem() instanceof IChessPieceItem item) {
            int slotId = getSlotIdFromType(item.isBlack(), item.getType());
            handler.setCursorStack(ItemStack.EMPTY);
            int i = getInventory().getStack(slotId).getCount();
            itemStack.setCount(i + 1);
            itemStack.removeSubNbt("gameblocks");
            getInventory().setStack(slotId, itemStack);
        }

    }

    @SuppressWarnings("PointlessArithmeticExpression")
    private int getSlotIdFromType(boolean isBlack, IChessPieceItem.ChessPieceType type) {
        return switch (type) {
            case PAWN -> 0 + (isBlack ? 1 : 0) + 64;
            case ROOK -> 2 + (isBlack ? 1 : 0) + 64;
            case KNIGHT -> 4 + (isBlack ? 1 : 0) + 64;
            case BISHOP -> 6 + (isBlack ? 1 : 0) + 64;
            case QUEEN -> 8 + (isBlack ? 1 : 0) + 64;
            case KING -> 10 + (isBlack ? 1 : 0) + 64;
        };
    }

    @Override
    public int getMaxItemCount() {
        if (!hasStack()) return super.getMaxItemCount();
        assert this.getCurrentPiece() != null;
        return this.getCurrentPiece().getMaxCount();
    }

    @NotNull
    public Optional<ChessBoardSlot> upLeft(boolean isBlack) {
        return upLeft(isBlack, 1);
    }

    @NotNull
    public Optional<ChessBoardSlot> downLeft(boolean isBlack) {
        return downLeft(isBlack, 1);
    }

    @NotNull
    public Optional<ChessBoardSlot> upRight(boolean isBlack) {
        return upRight(isBlack, 1);
    }

    @NotNull
    public Optional<ChessBoardSlot> downRight(boolean isBlack) {
        return downRight(isBlack, 1);
    }

    @NotNull
    public Optional<ChessBoardSlot> up(boolean isBlack) {
        return up(isBlack, 1);
    }

    @NotNull
    public Optional<ChessBoardSlot> down(boolean isBlack) {
        return down(isBlack, 1);
    }

    @NotNull
    public Optional<ChessBoardSlot> left(boolean isBlack) {
        return left(isBlack, 1);
    }

    @NotNull
    public Optional<ChessBoardSlot> right(boolean isBlack) {
        return right(isBlack, 1);
    }

    @NotNull
    public Optional<ChessBoardSlot> up(boolean isBlack, int amount) {
        return isBlack ? down(false, amount) : getSlotFromInventory(this.xLoc, this.yLoc - amount);
    }

    @NotNull
    public Optional<ChessBoardSlot> down(boolean isBlack, int amount) {
        return isBlack ? up(false, amount) : getSlotFromInventory(this.xLoc, this.yLoc + amount);
    }

    @NotNull
    public Optional<ChessBoardSlot> left(boolean isBlack, int amount) {
        return isBlack ? right(false, amount) : getSlotFromInventory(this.xLoc + amount, this.yLoc);
    }

    @NotNull
    public Optional<ChessBoardSlot> right(boolean isBlack, int amount) {
        return isBlack ? left(false, amount) : getSlotFromInventory(this.xLoc - amount, this.yLoc);
    }

    @NotNull
    public Optional<ChessBoardSlot> upLeft(boolean isBlack, int amount) {
        return isBlack ? downRight(false, amount) : getSlotFromInventory(this.xLoc + amount, this.yLoc - amount);
    }

    @NotNull
    public Optional<ChessBoardSlot> upRight(boolean isBlack, int amount) {
        return isBlack ? downLeft(false, amount) : getSlotFromInventory(this.xLoc - amount, this.yLoc - amount);
    }

    @NotNull
    public Optional<ChessBoardSlot> downLeft(boolean isBlack, int amount) {
        return isBlack ? upRight(false, amount) : getSlotFromInventory(this.xLoc + amount, this.yLoc + amount);
    }

    @NotNull
    public Optional<ChessBoardSlot> downRight(boolean isBlack, int amount) {
        return isBlack ? upLeft(false, amount) : getSlotFromInventory(this.xLoc - amount, this.yLoc + amount);
    }

    @NotNull
    private Optional<ChessBoardSlot> getSlotFromInventory(int xLoc, int yLoc) {
        if (this.getInventory().isClient() || 0 > xLoc || xLoc >= ChessBoardInventory.BOARD_WIDTH || 0 > yLoc || yLoc >= ChessBoardInventory.BOARD_WIDTH) {
            return Optional.empty();
        }
        return Optional.of(((ServerChessBoardInventory) this.getInventory()).getSlot(xLoc, yLoc));
    }

    public void setHoverHint(int origin, @NotNull ChessActionType currentHoverAction) {
        if (!this.getInventory().isEmpty() && !this.getInventory().isClient()) {
            ((ServerChessBoardInventory) getInventory()).getSlotHintPropertyDelegate(origin).set(getIndex(), currentHoverAction.getId());
        }
    }
}
