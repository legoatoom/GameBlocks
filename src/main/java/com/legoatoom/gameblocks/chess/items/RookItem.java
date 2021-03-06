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
import com.legoatoom.gameblocks.chess.util.ChessPieceType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import org.jetbrains.annotations.NotNull;

public class RookItem extends IChessPieceItem {

    public RookItem(boolean isBlack) {
        super(isBlack, 2, ChessPieceType.ROOK);
    }

    @Override
    public boolean isDefaultLocation(int x, int y) {
        return (x == 0 || x == 7) && y == (isBlack() ? 0 : 7);
    }

    @Override
    public void calculateLegalActions(@NotNull AbstractGridSlot slot) {
        this.checkHorizontals(slot);
    }

    @Override
    public void handleAction(ScreenHandler handler, AbstractGridSlot slot, ItemStack cursorStack, ActionType actionType) {
        super.handleAction(handler, slot, cursorStack, actionType);
        NbtCompound nbtCompound = slot.getStack().getOrCreateSubNbt(GameBlocks.MOD_ID);
        if (!nbtCompound.contains("hasMoved")) {
            nbtCompound.putBoolean("hasMoved", true);
        }
    }


    @Override
    public int getStorageIndex() {
        return 2 + (isBlack() ? 1 : 0);
    }
}
