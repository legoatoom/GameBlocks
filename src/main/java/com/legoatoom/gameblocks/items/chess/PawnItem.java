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
import com.legoatoom.gameblocks.screen.slot.ChessGridBoardSlot;
import com.legoatoom.gameblocks.util.chess.ChessActionType;
import com.legoatoom.gameblocks.util.chess.ChessPieceType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PawnItem extends IChessPieceItem {
    public PawnItem(boolean isBlack) {
        super(isBlack, 8, ChessPieceType.PAWN);
    }


    @Override
    public boolean isDefaultLocation(int x, int y) {
        return isBlack() && y == 1 || !isBlack() && y == 6;
    }

    @Override
    public void calculateLegalActions(@NotNull ChessGridBoardSlot slot) {
        Optional<ChessGridBoardSlot> up = slot.up(isBlack());
        //Moving
        up.ifPresent(chessBoardSlot -> {
            if (!chessBoardSlot.hasStack()) {
                chessBoardSlot.setHoverHint(slot.getIndex(), isPromotion(chessBoardSlot) ? ChessActionType.PROMOTION : ChessActionType.MOVE);
                // First move can be 2 steps.
                if (isDefaultLocation(slot.getBoardXLoc(), slot.getBoardYLoc())) {
                    Optional<ChessGridBoardSlot> up2 = slot.up(isBlack(), 2);
                    up2.ifPresent(chessBoardSlot1 -> {
                        if (!chessBoardSlot1.hasStack()) {
                            chessBoardSlot1.setHoverHint(slot.getIndex(), ChessActionType.INITIAL_MOVE);
                        }
                    });
                }
            }
        });


        // Capture
        slot.upLeft(isBlack()).ifPresent(chessBoardSlot -> testCapture(chessBoardSlot, slot.getIndex()));
        slot.upRight(isBlack()).ifPresent(chessBoardSlot -> testCapture(chessBoardSlot, slot.getIndex()));

        // En Passant
        slot.left(isBlack()).ifPresent(chessBoardSlot -> testEnPassant(chessBoardSlot, slot.getIndex()));
        slot.right(isBlack()).ifPresent(chessBoardSlot -> testEnPassant(chessBoardSlot, slot.getIndex()));
    }

    @Override
    public void handleAction(ScreenHandler handler, ChessGridBoardSlot slot, ItemStack cursorStack, ChessActionType actionType) {
        if (actionType == ChessActionType.CAPTURE || actionType == ChessActionType.PROMOTION_CAPTURE) {
            slot.capturePiece(handler, cursorStack);
        }

        if (actionType == ChessActionType.EN_PASSANT) {
            slot.down(isBlack()).ifPresent(ChessGridBoardSlot::capturePiece);
        }

        for (int i = 0; i < slot.getInventory().size(); i++) {
            ItemStack itemStack = slot.getInventory().getStack(i);
            if (itemStack.getItem() instanceof PawnItem item && item.isBlack() == this.isBlack()) {
                continue;
            }
            if (itemStack.equals(cursorStack)) {
                continue;
            }
            if (itemStack.hasNbt()) {
                NbtCompound compound = itemStack.getOrCreateSubNbt(GameBlocks.MOD_ID);
                if (compound.contains("mayEnPassant")) {
                    compound.remove("mayEnPassant");
                }
            }
        }
        ItemStack slotStack = slot.getStack();
        NbtCompound nbtCompound = slotStack.getOrCreateSubNbt(GameBlocks.MOD_ID);
        if (actionType == ChessActionType.INITIAL_MOVE) {
            nbtCompound.putBoolean("mayEnPassant", true);
        }

        if (actionType == ChessActionType.PROMOTION || actionType == ChessActionType.PROMOTION_CAPTURE) {
            Item item = Registry.ITEM.get(GameBlocks.id(nbtCompound.getString("promotion")));
            if (item instanceof IChessPieceItem item1) {
                ItemStack newStack = new ItemStack(item1);
                NbtCompound nbtCompound1 = newStack.getOrCreateSubNbt(GameBlocks.MOD_ID);
                nbtCompound1.putBoolean("promoted", true);
                slot.setStack(newStack);
            }
        }
    }

    private void testCapture(@NotNull ChessGridBoardSlot current, int origin) {
        current.getItem().ifPresentOrElse(chessPieceItem -> {
            if (chessPieceItem.isBlack() != this.isBlack()) {
                current.setHoverHint(origin, isPromotion(current) ? ChessActionType.PROMOTION_CAPTURE : ChessActionType.CAPTURE);
            }
        }, () -> current.setHoverHint(origin, ChessActionType.PAWN_POTENTIAL));
    }

    private boolean isPromotion(@NotNull ChessGridBoardSlot current) {
        return current.getBoardYLoc() == 0 || current.getBoardYLoc() == 7;
    }

    private void testEnPassant(@NotNull ChessGridBoardSlot current, int origin) {
        current.getItem().ifPresent(chessPieceItem -> {
            NbtCompound compound = current.getStack().getSubNbt(GameBlocks.MOD_ID);
            if (compound != null && compound.contains("mayEnPassant") && chessPieceItem.isBlack() != this.isBlack()) {
                current.up(isBlack()).ifPresent(chessBoardSlot -> chessBoardSlot.setHoverHint(origin, ChessActionType.EN_PASSANT));
            }
        });
    }
}
