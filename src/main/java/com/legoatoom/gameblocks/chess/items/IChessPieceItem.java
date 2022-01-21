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
import com.legoatoom.gameblocks.chess.screen.slot.ChessGridSlot;
import com.legoatoom.gameblocks.chess.util.ChessActionType;
import com.legoatoom.gameblocks.chess.util.ChessPieceType;
import com.legoatoom.gameblocks.common.items.IPieceItem;
import com.legoatoom.gameblocks.common.screen.slot.AbstractGridSlot;
import com.legoatoom.gameblocks.common.util.ActionType;
import com.legoatoom.gameblocks.registry.ChessRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
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

import static com.legoatoom.gameblocks.GameBlocks.GAME_BLOCKS;
import static com.legoatoom.gameblocks.registry.ChessRegistry.BLACK_PAWN;
import static com.legoatoom.gameblocks.registry.ChessRegistry.WHITE_PAWN;

public abstract class IChessPieceItem extends IPieceItem {
    private final boolean isBlack;
    private final ChessPieceType type;

    public IChessPieceItem(boolean isBlack, int maxCount, ChessPieceType type) {
        super(new FabricItemSettings().group(GAME_BLOCKS).maxCount(maxCount));
        ChessRegistry.CHESS_PIECES.add(this);
        this.isBlack = isBlack;
        this.type = type;
    }

    @NotNull
    public ChessPieceType getType() {
        return type;
    }

    @Override
    public abstract boolean isDefaultLocation(int x, int y);

    @Override
    public abstract void calculateLegalActions(@NotNull AbstractGridSlot slot);

    public void handleAction(ScreenHandler handler, AbstractGridSlot slot, ItemStack cursorStack, ActionType actionType) {
        if (actionType == ChessActionType.CAPTURE) slot.capturePiece(handler, (defaultState(cursorStack)));
    }

    public boolean isBlack() {
        return isBlack;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (isPromoted(stack)) tooltip.add(new TranslatableText("game.chess.tooltip.promotion").fillStyle(Style.EMPTY.withColor(Formatting.GRAY)));
        super.appendTooltip(stack, world, tooltip, context); // Just in case another mod mixins into the super.
    }

    protected void checkDiagonals(@NotNull AbstractGridSlot slot) {
        int origin = slot.getIndex();
        AbstractGridSlot current = slot;
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

    protected void checkHorizontals(@NotNull AbstractGridSlot slot) {
        int origin = slot.getIndex();
        AbstractGridSlot current = slot;
        while (true) {
            Optional<AbstractGridSlot> x = current.up(isBlack);
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
            Optional<AbstractGridSlot> x = current.down(isBlack);
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
            Optional<AbstractGridSlot> x = current.left(isBlack);
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
            Optional<AbstractGridSlot> x = current.right(isBlack);
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

    protected boolean moveOrCaptureCheck(@NotNull AbstractGridSlot current, int origin) {
        if (current instanceof ChessGridSlot chessGridSlot) {
            Optional<IChessPieceItem> item = chessGridSlot.getItem();
            if (item.isPresent()) {
                if (item.get().isBlack() != this.isBlack()) {
                    chessGridSlot.setHoverHintForOriginIndex(origin, ChessActionType.CAPTURE);
                }
                return false;
            }
            chessGridSlot.setHoverHintForOriginIndex(origin, ChessActionType.MOVE);
            return true;
        }
        return false;
    }

    protected boolean isPromoted(ItemStack stack) {
        if (stack.hasNbt()) {
            NbtCompound nbtCompound = stack.getNbt();
            if (nbtCompound != null && nbtCompound.contains(GameBlocks.MOD_ID)) {
                NbtCompound nbtCompound1 = nbtCompound.getCompound(GameBlocks.MOD_ID);
                return nbtCompound1.contains("promoted");
            }
        }
        return false;
    }

    @Override
    public ItemStack defaultState(ItemStack stack) {
        if (isPromoted(stack)){
            return new ItemStack(isBlack() ? BLACK_PAWN : WHITE_PAWN, stack.getCount());
        }
        return stack;
    }
}
