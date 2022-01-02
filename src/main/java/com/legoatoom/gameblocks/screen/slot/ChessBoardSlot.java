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
import com.legoatoom.gameblocks.items.chess.IChessPieceItem;
import com.legoatoom.gameblocks.util.chess.ChessActionType;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChessBoardSlot extends Slot {


    private final int xLoc, yLoc;
    public final int clientX, clientY;

    private ChessActionType currentHoverAction;

    public ChessBoardInventory getInventory() {
        return (ChessBoardInventory) this.inventory;
    }

    public int getBoardXLoc() {
        return xLoc;
    }

    public int getBoardYLoc() {
        return yLoc;
    }

    public ChessBoardSlot(ChessBoardInventory inventory, int boardXLoc, int boardYLoc, int screenXLoc, int screenYLoc, int clientX, int clientY) {
        super(inventory, xyToIndex(boardXLoc, boardYLoc), screenXLoc, screenYLoc);
        this.xLoc = boardXLoc;
        this.yLoc = boardYLoc;
        this.clientX = clientX;
        this.clientY = clientY;
        inventory.addSlot(this);
        System.out.println("creating slot = " + this);
    }


    @Override
    public String toString() {
        return "ChessBoardSlot{" +
                "xLoc=" + xLoc +
                ", yLoc=" + yLoc +
                ", clientX=" + clientX +
                ", clientY=" + clientY +
                ", index=" + getIndex() +
                ", x=" + x +
                ", y=" + y +
                '}';
    }

    public static int xyToIndex(int x, int y) {
        return y * ChessBoardInventory.BOARD_WIDTH + x;
    }

    public List<Pair<ChessBoardSlot, ChessActionType>> calculateLegalActions(){
        if (hasStack()) {
            IChessPieceItem current = getCurrentPiece();
            Objects.requireNonNull(current);
            return current.calculateLegalActions(this);
        } else return new ArrayList<>();
    }

    public List<Pair<ChessBoardSlot, ChessActionType>> calculateLegalActions(@NotNull IChessPieceItem item){
        return item.calculateLegalActions(this);
    }

    @Override
    public int getMaxItemCount(ItemStack stack) {
        return 1;
    }

    public IChessPieceItem getCurrentPiece() {
        return hasStack() ? (IChessPieceItem) this.getStack().getItem() : null;
    }

    @Deprecated
    @Override
    public void setStack(ItemStack stack) {
        super.setStack(stack);
    }

    @Override
    public int getMaxItemCount() {
        if (!hasStack()) return super.getMaxItemCount();
        assert this.getCurrentPiece() != null;
        return this.getCurrentPiece().getMaxCount();
    }

    @Nullable
    public ChessBoardSlot up(boolean isBlack){
        return isBlack ? down(false) : getSlotFromInventory(this.xLoc, this.yLoc - 1);
    }

    @Nullable
    public ChessBoardSlot down(boolean isBlack){
        return isBlack ? up(false) : getSlotFromInventory(this.xLoc, this.yLoc + 1);
    }

    @Nullable
    public ChessBoardSlot left(boolean isBlack){
        return isBlack ? right(false) : getSlotFromInventory(this.xLoc + 1, this.yLoc);
    }

    @Nullable
    public ChessBoardSlot right(boolean isBlack){
        return isBlack ? left(false) : getSlotFromInventory(this.xLoc - 1, this.yLoc);
    }

    @Nullable
    private ChessBoardSlot getSlotFromInventory(int xLoc, int yLoc) {
        try{
            return getInventory().getSlot(xLoc, yLoc);
        } catch (IndexOutOfBoundsException e){
            return null;
        }
    }

    public ChessActionType getCurrentHoverAction() {
        return currentHoverAction;
    }

    public void setCurrentHoverAction(ChessActionType currentHoverAction) {
        this.currentHoverAction = currentHoverAction;
    }
}
