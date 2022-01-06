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
import com.legoatoom.gameblocks.inventory.ClientChessBoardInventory;
import com.legoatoom.gameblocks.inventory.ChessBoardInventory;
import com.legoatoom.gameblocks.inventory.ServerChessBoardInventory;
import com.legoatoom.gameblocks.items.chess.*;
import com.legoatoom.gameblocks.screen.slot.ChessBoardSlot;
import com.legoatoom.gameblocks.screen.slot.ChessStorageSlot;
import com.legoatoom.gameblocks.util.chess.ChessActionType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

public class ChessBoardScreenHandler extends ScreenHandler {

    private static final int BOARD_WIDTH = 8;
    public final ChessBoardInventory inventory;
    public final PlayerInventory playerInventory;
    private final Direction chessBoardDirection;
    public final ArrayList<ArrayPropertyDelegate> slotHintPropertyDelegate;

    public ChessBoardScreenHandler(int syncId, PlayerInventory inv, PacketByteBuf buf) {
        super(GameBlocks.CHESS_BOARD_SCREEN_HANDLER, syncId);
        //CLIENT
        this.inventory = new ClientChessBoardInventory();
        this.playerInventory = inv;
        this.chessBoardDirection = Direction.fromHorizontal(buf.readInt());
        this.slotHintPropertyDelegate = new ArrayList<>();
        for (int i = 0; i < 64; i++) {
            this.slotHintPropertyDelegate.add(new ArrayPropertyDelegate(64));
        }
        for (ArrayPropertyDelegate pd : this.slotHintPropertyDelegate) {
            this.addProperties(pd);
        }


        inventory.onOpen(playerInventory.player);

        initializeSlots();

    }

    public ChessBoardScreenHandler(int syncId, PlayerInventory playerInventory, ServerChessBoardInventory inventory, Direction facing) {
        super(GameBlocks.CHESS_BOARD_SCREEN_HANDLER, syncId);
        //SERVER
        this.inventory = inventory;
        this.playerInventory = playerInventory;
        this.chessBoardDirection = facing;
        this.slotHintPropertyDelegate = inventory.slotHintPropertyDelegate;
        inventory.onOpen(playerInventory.player);
        for (ArrayPropertyDelegate pd : this.slotHintPropertyDelegate) {
            this.addProperties(pd);
        }
        this.addListener(new ScreenHandlerListener() {

            private int originId = -1;

            @Override
            public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
                Slot slot = handler.getSlot(slotId);
                if (slotId > 63) {
                    return;
                }
                if (!handler.getCursorStack().isEmpty() && stack.isEmpty()) {
                    originId = slotId;
                    return;
                }
                if (slot instanceof ChessBoardSlot s){
                    if (!s.getInventory().isClient()){
                        ItemStack slotStack = slot.getStack();
                        if (slotStack.getItem() instanceof IChessPieceItem chessPieceItem) {
                            if (handler instanceof ChessBoardScreenHandler c && originId != -1){
                                ChessActionType type = c.getActionTypeFromSlot(originId, slotId);

                                if (!type.shouldIgnore()) {
                                    chessPieceItem.handleAction(handler, s, getCursorStack(), type);
                                }
                            }
                            originId = -1;

                        }
                        ((ServerChessBoardInventory) s.getInventory()).updateHints();

                    }
                }
                originId = slotId;
            }

            @Override
            public void onPropertyUpdate(ScreenHandler handler, int property, int value) {

            }
        });
        initializeSlots();
        this.sendContentUpdates();
    }


    @SuppressWarnings("PointlessArithmeticExpression")
    private void initializeSlots() {
        int y, x;
        for (y = 0; y < BOARD_WIDTH; y++) {
            for (x = 0; x < BOARD_WIDTH; x++) {
                Pair<Integer, Integer> pair = rotationTransformer(x, y);
                int boardX = pair.getLeft();
                int boardY = pair.getRight();
                Slot slot = new ChessBoardSlot(inventory, boardX, boardY, 24 + x * 16, 17 + y * 16);
                this.addSlot(slot);
            }
        }

        // Chess Pieces Storage
        this.addSlot(new ChessStorageSlot(inventory, 0 + 0 * 2 + 64, 159 + 0 * 16, 33 + 0 * 16, PawnItem.class, false));
        this.addSlot(new ChessStorageSlot(inventory, 0 + 1 * 2 + 64, 159 + 0 * 16, 33 + 1 * 16, RookItem.class, false));
        this.addSlot(new ChessStorageSlot(inventory, 0 + 2 * 2 + 64, 159 + 0 * 16, 33 + 2 * 16, KnightItem.class, false));
        this.addSlot(new ChessStorageSlot(inventory, 0 + 3 * 2 + 64, 159 + 0 * 16, 33 + 3 * 16, BishopItem.class, false));
        this.addSlot(new ChessStorageSlot(inventory, 0 + 4 * 2 + 64, 159 + 0 * 16, 33 + 4 * 16, QueenItem.class, false));
        this.addSlot(new ChessStorageSlot(inventory, 0 + 5 * 2 + 64, 159 + 0 * 16, 33 + 5 * 16, KingItem.class, false));

        this.addSlot(new ChessStorageSlot(inventory, 1 + 0 * 2 + 64, 159 + 1 * 16, 33 + 0 * 16, PawnItem.class, true));
        this.addSlot(new ChessStorageSlot(inventory, 1 + 1 * 2 + 64, 159 + 1 * 16, 33 + 1 * 16, RookItem.class, true));
        this.addSlot(new ChessStorageSlot(inventory, 1 + 2 * 2 + 64, 159 + 1 * 16, 33 + 2 * 16, KnightItem.class, true));
        this.addSlot(new ChessStorageSlot(inventory, 1 + 3 * 2 + 64, 159 + 1 * 16, 33 + 3 * 16, BishopItem.class, true));
        this.addSlot(new ChessStorageSlot(inventory, 1 + 4 * 2 + 64, 159 + 1 * 16, 33 + 4 * 16, QueenItem.class, true));
        this.addSlot(new ChessStorageSlot(inventory, 1 + 5 * 2 + 64, 159 + 1 * 16, 33 + 5 * 16, KingItem.class, true));


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

    public ChessActionType getActionTypeFromSlot(int origin, int slotId){
        return ChessActionType.fromId(this.slotHintPropertyDelegate.get(origin).get(slotId));
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;

        return newStack;
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        if (slot instanceof ChessBoardSlot || slot instanceof ChessStorageSlot){
            return slot.canInsert(stack);
        }
        return stack.isEmpty() || !(stack.getItem().asItem() instanceof IChessPieceItem);
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

    public List<ChessBoardSlot> getCurrentSlotActions(int origin) {

        ArrayList<ChessBoardSlot> result = new ArrayList<>();

        for (Slot slot : this.slots) {
            if (slot instanceof ChessBoardSlot s){
                ChessActionType type = ChessActionType.fromId(this.slotHintPropertyDelegate.get(origin).get(slot.getIndex()));
                if (!type.shouldIgnore()){
                    result.add(s);
                }
            }
        }
        return result;
    }
}
