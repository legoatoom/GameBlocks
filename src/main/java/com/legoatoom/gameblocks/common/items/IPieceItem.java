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

import com.legoatoom.gameblocks.GameBlocksState;
import com.legoatoom.gameblocks.common.screen.slot.AbstractGridSlot;
import com.legoatoom.gameblocks.common.util.ActionType;
import com.legoatoom.gameblocks.common.util.IPieceType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class IPieceItem extends Item {

    public IPieceItem(Settings settings) {
        super(settings);
    }

    public abstract int getStorageIndex();

    public abstract boolean isDefaultLocation(int x, int y);

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) return super.use(world, user, hand);
        ItemStack stack = user.getStackInHand(hand);

        // Clearing data when using.
        return TypedActionResult.pass(defaultState(stack));
    }

    public abstract void calculateLegalActions(AbstractGridSlot slot);

    public abstract void handleAction(ScreenHandler handler, AbstractGridSlot slot, ItemStack cursorStack, ActionType actionType);

    public abstract boolean isBlack();

    public ItemStack defaultState(ItemStack stack) {
        return stack;
    }

    @Nullable
    public IPieceType getType(){
        GameBlocksState.warn("Tried to access %s Piece Type even though there isn't one.".formatted(Text.translatable(this.getTranslationKey())));
        return null;
    };
}
