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

package com.legoatoom.gameblocks.common.items;

import com.legoatoom.gameblocks.common.screen.slot.GridSlot;
import com.legoatoom.gameblocks.common.util.ActionType;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

public interface IPieceItem {

    boolean isDefaultLocation(int x, int y);

    void calculateLegalActions(GridSlot slot);

    void handleAction(ScreenHandler handler, GridSlot slot, ItemStack cursorStack, ActionType actionType);

    boolean isBlack();
}
