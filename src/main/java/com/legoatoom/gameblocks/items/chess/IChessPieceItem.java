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
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.legoatoom.gameblocks.GameBlocks.GAME_BLOCKS;

public abstract class IChessPieceItem extends Item {
    private final boolean isBlack;

    public ChessPieceType getType() {
        return type;
    }

    private final ChessPieceType type;


    public IChessPieceItem(boolean isBlack, int maxCount, ChessPieceType type) {
        super(new FabricItemSettings().group(GAME_BLOCKS).maxCount(maxCount));
        GameBlocks.CHESS_PIECES.add(this);
        this.isBlack = isBlack;
        this.type = type;
    }

    public abstract boolean isDefaultLocation(int x, int y);

    public abstract void calculateLegalActions(@NotNull ChessBoardSlot slot);

    public static void cleanHoverActions(ChessBoardSlot[] slots){
        for (ChessBoardSlot slot : slots) {
            for (ChessBoardSlot slot2 : slots) {
                slot.setHoverHint(slot2.getIndex(), ChessActionType.NONE);
            }
        }
    }

    public boolean isBlack(){
        return isBlack;
    }

    public void handleAction(ScreenHandler handler, ChessBoardSlot slot, ItemStack cursorStack, ChessActionType actionType){
        if (actionType == ChessActionType.CAPTURE){
            if (isPromoted(cursorStack)){
                slot.capturePiece(handler, new ItemStack(isBlack() ? GameBlocks.WHITE_PAWN : GameBlocks.BLACK_PAWN));
            } else {
                slot.capturePiece(handler, cursorStack);
            }
        }
    }



    protected void checkDiagonals(@NotNull ChessBoardSlot slot) {
        int origin = slot.getIndex();
        AtomicBoolean b = new AtomicBoolean(false);

        ChessBoardSlot current = slot;
        while (true) {
            var x = current.upLeft(isBlack);
            if (x.isPresent()) {
                if (!moveOrCaptureCheck(x.get(), origin)) {
                    break;
                } else {
                    current = x.get();
                    continue;
                }
            }
            break;
        }
        current = slot;
        while (true) {
            var x = current.upRight(isBlack);
            if (x.isPresent()) {
                if (!moveOrCaptureCheck(x.get(), origin)) {
                    break;
                } else {
                    current = x.get();
                    continue;
                }
            }
            break;
        }
        current = slot;
        while (true) {
            var x = current.downLeft(isBlack);
            if (x.isPresent()) {
                if (!moveOrCaptureCheck(x.get(), origin)) {
                    break;
                } else {
                    current = x.get();
                    continue;
                }
            }
            break;
        }
        current = slot;
        while (true) {
            var x = current.downRight(isBlack);
            if (x.isPresent()) {
                if (!moveOrCaptureCheck(x.get(), origin)) {
                    break;
                } else {
                    current = x.get();
                    continue;
                }
            }
            break;
        }


    }

    protected void checkHorizontals(@NotNull ChessBoardSlot slot) {
        int origin = slot.getIndex();
        ChessBoardSlot current = slot;
        while (true) {
            var x = current.up(isBlack);
            if (x.isPresent()) {
                if (!moveOrCaptureCheck(x.get(), origin)) {
                    break;
                } else {
                    current = x.get();
                    continue;
                }
            }
            break;
        }
        current = slot;
        while (true) {
            var x = current.down(isBlack);
            if (x.isPresent()) {
                if (!moveOrCaptureCheck(x.get(), origin)) {
                    break;
                } else {
                    current = x.get();
                    continue;
                }
            }
            break;
        }
        current = slot;
        while (true) {
            var x = current.left(isBlack);
            if (x.isPresent()) {
                if (!moveOrCaptureCheck(x.get(), origin)) {
                    break;
                } else {
                    current = x.get();
                    continue;
                }
            }
            break;
        }
        current = slot;
        while (true) {
            var x = current.right(isBlack);
            if (x.isPresent()) {
                if (!moveOrCaptureCheck(x.get(), origin)) {
                    break;
                } else {
                    current = x.get();
                    continue;
                }
            }
            break;
        }
    }

    protected boolean moveOrCaptureCheck(@NotNull ChessBoardSlot current, int origin) {
        Optional<IChessPieceItem> item = current.getItem();
        if (item.isPresent()){
            if (item.get().isBlack() != this.isBlack()){
                current.setHoverHint(origin, ChessActionType.CAPTURE);
            }
            return false;
        }
        current.setHoverHint(origin, ChessActionType.MOVE);
        return true;
    }

    public enum ChessPieceType{
        PAWN, KING, KNIGHT, ROOK, QUEEN, BISHOP;
    }

    protected boolean isPromoted(ItemStack stack){
        if (stack.hasNbt()){
            NbtCompound nbtCompound = stack.getNbt();
            if (nbtCompound != null && nbtCompound.contains(GameBlocks.MOD_ID)) {
                NbtCompound nbtCompound1 = nbtCompound.getCompound(GameBlocks.MOD_ID);
                return nbtCompound1.contains("promoted");
            }
        }
        return false;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        if (isPromoted(stack)){
            tooltip.add(new TranslatableText("game.chess.action.tooltip.promotion").fillStyle(Style.EMPTY.withColor(Formatting.GRAY)));
        }
    }
}
