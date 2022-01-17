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
import com.legoatoom.gameblocks.inventory.chess.ChessBoardInventory;
import com.legoatoom.gameblocks.inventory.chess.ServerChessBoardInventory;
import com.legoatoom.gameblocks.screen.slot.ChessGridBoardSlot;
import com.legoatoom.gameblocks.util.chess.ChessActionType;
import com.legoatoom.gameblocks.util.chess.ChessPieceType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class KingItem extends IChessPieceItem {

    public KingItem(boolean isBlack) {
        super(isBlack, 1, ChessPieceType.KING);
    }


    @Override
    public boolean isDefaultLocation(int x, int y) {
        return x == 4 && y == (isBlack() ? 0 : 7);
    }

    @Override
    public void calculateLegalActions(@NotNull ChessGridBoardSlot slot) {
        int origin = slot.getIndex();
        slot.up(isBlack()).ifPresent(chessBoardSlot -> moveOrCaptureCheck(chessBoardSlot, origin));
        slot.upRight(isBlack()).ifPresent(chessBoardSlot -> moveOrCaptureCheck(chessBoardSlot, origin));
        slot.upLeft(isBlack()).ifPresent(chessBoardSlot -> moveOrCaptureCheck(chessBoardSlot, origin));
        slot.left(isBlack()).ifPresent(chessBoardSlot -> moveOrCaptureCheck(chessBoardSlot, origin));
        slot.right(isBlack()).ifPresent(chessBoardSlot -> moveOrCaptureCheck(chessBoardSlot, origin));
        slot.down(isBlack()).ifPresent(chessBoardSlot -> moveOrCaptureCheck(chessBoardSlot, origin));
        slot.downRight(isBlack()).ifPresent(chessBoardSlot -> moveOrCaptureCheck(chessBoardSlot, origin));
        slot.downLeft(isBlack()).ifPresent(chessBoardSlot -> moveOrCaptureCheck(chessBoardSlot, origin));

        ItemStack stack = slot.getStack();
        if (checkNotMoved(stack) && !slotUnderAttack(slot)) {
            // black and white king are on the same location so isBlack is not necessary here, and castling is only horizontal
            slot.left(false).ifPresent(chessBoardSlot -> {
                if (chessBoardSlot.hasStack()) return;
                chessBoardSlot.left(false).ifPresent(chessBoardSlot1 -> {
                    if (chessBoardSlot1.hasStack()) return;
                    chessBoardSlot1.left(false).ifPresent(chessBoardSlot2 -> {
                        // Rook
                        chessBoardSlot2.getItem().ifPresent(chessPieceItem -> {
                            if (chessPieceItem.getType() == ChessPieceType.ROOK && checkNotMoved(chessBoardSlot2.getStack()) && !slotUnderAttack(chessBoardSlot1)) {
                                chessBoardSlot1.setHoverHint(origin, ChessActionType.CASTLE);
                            }
                        });
                    });
                });
            });
            // black and white king are on the same location so isBlack is not necessary here, and castling is only horizontal
            slot.right(false).ifPresent(chessBoardSlot -> {
                if (chessBoardSlot.hasStack()) return;
                chessBoardSlot.right(false).ifPresent(chessBoardSlot1 -> {
                    if (chessBoardSlot1.hasStack()) return;
                    chessBoardSlot1.right(false).ifPresent(chessBoardSlot2 -> {

                        if (chessBoardSlot2.hasStack()) return;
                        chessBoardSlot2.right(false).ifPresent(chessBoardSlot3 -> {
                            // Rook
                            chessBoardSlot3.getItem().ifPresent(chessPieceItem -> {
                                if (chessPieceItem.getType() == ChessPieceType.ROOK && checkNotMoved(chessBoardSlot3.getStack()) && !slotUnderAttack(chessBoardSlot1)) {
                                    chessBoardSlot1.setHoverHint(origin, ChessActionType.CASTLE);
                                }
                            });
                        });
                    });
                });
            });
        }

    }

    @Override
    public void handleAction(ScreenHandler handler, ChessGridBoardSlot slot, ItemStack cursorStack, ChessActionType actionType) {
        super.handleAction(handler, slot, cursorStack, actionType);
        NbtCompound nbtCompound = slot.getStack().getOrCreateSubNbt(GameBlocks.MOD_ID);
        if (!nbtCompound.contains("hasMoved")) {
            nbtCompound.putBoolean("hasMoved", true);
        }

        // black and white king are on the same location so isBlack is not necessary here, and castling is only horizontal
        if (actionType == ChessActionType.CASTLE) {
            if (slot.getBoardXLoc() == 6) {
                slot.left(false).ifPresent(leftSlot -> {
                    var stack = leftSlot.getStack();
                    if (checkNotMoved(stack) || ((IChessPieceItem) stack.getItem()).getType() == ChessPieceType.ROOK) {
                        slot.right(false).ifPresent(rightSlot -> {
                            rightSlot.setStack(stack);
                            leftSlot.setStack(ItemStack.EMPTY);
                        });
                    }
                });
            } else if (slot.getBoardXLoc() == 2) {
                slot.right(false, 2).ifPresent(rightSlot -> {
                    var stack = rightSlot.getStack();
                    if (checkNotMoved(stack) || ((IChessPieceItem) stack.getItem()).getType() == ChessPieceType.ROOK) {
                        slot.left(false).ifPresent(leftSlot -> {
                            leftSlot.setStack(stack);
                            rightSlot.setStack(ItemStack.EMPTY);
                        });
                    }
                });
            }
        }
    }

    @Override
    protected boolean moveOrCaptureCheck(@NotNull ChessGridBoardSlot current, int origin) {
        Optional<IChessPieceItem> item = current.getItem();
        if (item.isPresent()) {
            if (item.get().isBlack() != this.isBlack()) {
                current.setHoverHint(origin, ChessActionType.CAPTURE);
            }
            return false;
        }
        if (slotUnderAttack(current)) return false;
        current.setHoverHint(origin, ChessActionType.MOVE);
        return true;
    }

    /**
     * Is not perfect, as moving the king will allow other pieces to move to spaces that we cannot expect to know.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean slotUnderAttack(@NotNull ChessGridBoardSlot slot) {
        ChessBoardInventory x = slot.getInventory();
        if (x.isClient()) return false;
        ServerChessBoardInventory serverInv = (ServerChessBoardInventory) x;
        ArrayList<ArrayPropertyDelegate> slotHintPropertyDelegate = serverInv.slotHintPropertyDelegate;
        for (int i = 0; i < slotHintPropertyDelegate.size(); i++) {
            //Check if the slot exists
            if (serverInv.getSlot(i).getItem().isPresent()) {
                var chessPieceItem = serverInv.getSlot(i).getItem().get();
                //Check if it is a chessItem (Should always be) and it is of the other team.
                if (this.isBlack() != chessPieceItem.isBlack()) {
                    ArrayPropertyDelegate arrayPropertyDelegate = slotHintPropertyDelegate.get(i);
                    int actionId = arrayPropertyDelegate.get(slot.getIndex());
                    // These action types can result into an attack.
                    List<Integer> checks;
                    if (chessPieceItem.getType() == ChessPieceType.PAWN) {
                        //Pawn Moves do not capture
                        checks = Arrays.asList(ChessActionType.CAPTURE.getId(), ChessActionType.PROMOTION_CAPTURE.getId(),
                                ChessActionType.EN_PASSANT.getId(), ChessActionType.PAWN_POTENTIAL.getId());
                    } else {
                        checks = Arrays.asList(ChessActionType.CAPTURE.getId(), ChessActionType.MOVE.getId());
                    }
                    if (checks.contains(actionId)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean checkNotMoved(ItemStack stack) {
        NbtCompound nbtCompound = stack.getSubNbt(GameBlocks.MOD_ID);
        if (nbtCompound == null) return true;
        return !nbtCompound.contains("hasMoved");
    }


}
