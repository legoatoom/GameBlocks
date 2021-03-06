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

package com.legoatoom.gameblocks.chess.items;

import com.legoatoom.gameblocks.GameBlocks;
import com.legoatoom.gameblocks.common.screen.slot.AbstractGridSlot;
import com.legoatoom.gameblocks.common.util.ActionType;
import com.legoatoom.gameblocks.chess.util.ChessActionType;
import com.legoatoom.gameblocks.chess.util.ChessPieceType;
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
    public int getStorageIndex() {
        return isBlack() ? 1 : 0; // Fun fact, when compiled the bytecode won't contain a jump. So there isn't any if here technically.
    }

    @Override
    public boolean isDefaultLocation(int x, int y) {
        return isBlack() && y == 1 || !isBlack() && y == 6;
    }

    @Override
    public void calculateLegalActions(@NotNull AbstractGridSlot slot) {
        Optional<AbstractGridSlot> up = slot.up(isBlack());
        //Moving
        up.ifPresent(chessBoardSlot -> {
            if (!chessBoardSlot.hasStack()) {
                chessBoardSlot.setHoverHintForOriginIndex(slot.getIndex(), isPromotion(chessBoardSlot) ? ChessActionType.PROMOTION : ChessActionType.MOVE);
                // First move can be 2 steps.
                if (isDefaultLocation(slot.getBoardXLoc(), slot.getBoardYLoc())) {
                    Optional<AbstractGridSlot> up2 = slot.up(isBlack(), 2);
                    up2.ifPresent(chessBoardSlot1 -> {
                        if (!chessBoardSlot1.hasStack()) {
                            chessBoardSlot1.setHoverHintForOriginIndex(slot.getIndex(), ChessActionType.INITIAL_MOVE);
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
    public void handleAction(ScreenHandler handler, AbstractGridSlot slot, ItemStack cursorStack, ActionType actionType) {
        if (actionType == ChessActionType.CAPTURE || actionType == ChessActionType.PROMOTION_CAPTURE) {
            slot.capturePiece(handler, cursorStack);
        }

        if (actionType == ChessActionType.EN_PASSANT) {
            slot.down(isBlack()).ifPresent(AbstractGridSlot::captureMe);
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

    private void testCapture(@NotNull AbstractGridSlot current, int origin) {
        current.getItem().ifPresentOrElse(chessPieceItem -> {
            if (chessPieceItem.isBlack() != this.isBlack()) {
                current.setHoverHintForOriginIndex(origin, isPromotion(current) ? ChessActionType.PROMOTION_CAPTURE : ChessActionType.CAPTURE);
            }
        }, () -> current.setHoverHintForOriginIndex(origin, ChessActionType.PAWN_POTENTIAL));
    }

    private boolean isPromotion(@NotNull AbstractGridSlot current) {
        return current.getBoardYLoc() == 0 || current.getBoardYLoc() == 7;
    }

    private void testEnPassant(@NotNull AbstractGridSlot current, int origin) {
        current.getItem().ifPresent(chessPieceItem -> {
            NbtCompound compound = current.getStack().getSubNbt(GameBlocks.MOD_ID);
            if (compound != null && compound.contains("mayEnPassant") && chessPieceItem.isBlack() != this.isBlack()) {
                current.up(isBlack()).ifPresent(chessBoardSlot -> chessBoardSlot.setHoverHintForOriginIndex(origin, ChessActionType.EN_PASSANT));
            }
        });
    }
}
