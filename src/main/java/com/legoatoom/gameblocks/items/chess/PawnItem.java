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

package com.legoatoom.gameblocks.items.chess;

import com.legoatoom.gameblocks.GameBlocks;
import com.legoatoom.gameblocks.screen.slot.ChessBoardSlot;
import com.legoatoom.gameblocks.util.chess.ChessActionType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PawnItem extends IChessPieceItem{
    public PawnItem(boolean isBlack) {
        super(isBlack, 8, ChessPieceType.PAWN);
    }


    @Override
    public boolean isDefaultLocation(int x, int y) {
        return isBlack() ? y == 1 : y == 6;
    }

    @Override
    public @NotNull ArrayList<ChessBoardSlot> calculateLegalActions(@NotNull ChessBoardSlot slot) {
        ArrayList<ChessBoardSlot> result = new ArrayList<>();
        ChessBoardSlot up = slot.up(isBlack());

        if (up != null) {
            if (!up.hasStack()) {
                up.setCurrentHoverAction(ChessActionType.MOVE);
                result.add(up);

                // First move can be 2 steps.
                int x = slot.getBoardXLoc();
                int y = slot.getBoardYLoc();
                if (isDefaultLocation(x, y)) {
                    ChessBoardSlot up2 = up.up(isBlack());
                    if (up2 != null && !up2.hasStack()) {
                        up2.setCurrentHoverAction(ChessActionType.INITIAL_MOVE);
                        result.add(up2);
                    }
                }
            }

            // Capture
            ChessBoardSlot upLeft = up.left(isBlack());
            testCapture(upLeft, result);
            ChessBoardSlot upRight = up.right(isBlack());
            testCapture(upRight, result);
        }

        //En Passant
        ChessBoardSlot left = slot.left(isBlack());
        testEnPassant(result, left);
        ChessBoardSlot right = slot.right(isBlack());
        testEnPassant(result, right);

        return result;
    }

    private void testCapture(ChessBoardSlot upRight, ArrayList<ChessBoardSlot> result) {
        if (upRight != null && upRight.hasStack() && upRight.getCurrentPiece().isBlack() != this.isBlack()){
            upRight.setCurrentHoverAction(ChessActionType.CAPTURE);
            result.add(upRight);
        }
    }

    private void testEnPassant(ArrayList<ChessBoardSlot> result, ChessBoardSlot slot) {
        if (slot != null && slot.hasStack() && slot.getCurrentPiece().isBlack() != this.isBlack() && slot.getStack().hasNbt()){
            NbtCompound compound = slot.getStack().getNbt();
            assert compound != null;
            if (compound.contains("gameblocks:mayEnPassant")){
                ChessBoardSlot up = slot.up(isBlack());
                assert up != null;
                up.setCurrentHoverAction(ChessActionType.EN_PASSANT);
                result.add(up);
            }
        }
    }

    @Override
    public void handleAction(ScreenHandler handler, ChessBoardSlot slot, ItemStack cursorStack, ChessActionType actionType) {
        super.handleAction(handler, slot, cursorStack,actionType);
        if (actionType == ChessActionType.EN_PASSANT){
            slot.down(isBlack()).capturePiece();
        }
        for (ItemStack itemStack : slot.getInventory().getBoard()) {
            if (itemStack.getItem() instanceof PawnItem item && item.isBlack() == this.isBlack()){
                continue;
            }
            if (itemStack.equals(cursorStack)){
                continue;
            }
            if (itemStack.hasNbt()) {
                NbtCompound compound = itemStack.getOrCreateNbt();
                if (compound.contains("gameblocks:mayEnPassant")) {
                    compound.remove("gameblocks:mayEnPassant");
                }
            }
        }
        ItemStack slotStack = slot.getStack();
        NbtCompound nbtCompound = slotStack.getOrCreateNbt();
        if (actionType == ChessActionType.INITIAL_MOVE){
            nbtCompound.putBoolean("gameblocks:mayEnPassant", true);
        }
    }
}
