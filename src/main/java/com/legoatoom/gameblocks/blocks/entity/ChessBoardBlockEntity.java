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

package com.legoatoom.gameblocks.blocks.entity;

import com.legoatoom.gameblocks.GameBlocks;
import com.legoatoom.gameblocks.blocks.ChessBoardBlock;
import com.legoatoom.gameblocks.inventory.ServerChessBoardInventory;
import com.legoatoom.gameblocks.screen.ChessBoardScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class ChessBoardBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory {

    protected ServerChessBoardInventory board = new ServerChessBoardInventory(this);

    public ChessBoardBlockEntity(BlockPos pos, BlockState state) {
        super(GameBlocks.CHESS_BOARD_BLOCK_ENTITY, pos, state);
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new ChessBoardScreenHandler(syncId, inv, this.board, this.getCachedState().get(ChessBoardBlock.FACING));
    }

    /**
     * Writes additional server -&gt; client screen opening data to the buffer.
     *
     * @param player the player that is opening the screen
     * @param buf    the packet buffer
     */
    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        Direction direction = this.getCachedState().get(ChessBoardBlock.FACING);
        buf.writeInt(direction.getHorizontal());
    }


    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, board.getItems());
    }
    @Override
    protected void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, board.getItems());
        super.writeNbt(nbt);
    }


//    @Override
//    public void onOpen(PlayerEntity player) {
//        board.onOpen(player);
//    }
//
//    @Override
//    public void onClose(PlayerEntity player) {
//        board.onClose(player);
//    }
//
//    /**
//     * Returns whether the given stack is a valid for the indicated slot position.
//     *
//     * @param slot
//     * @param stack
//     */
//    @Override
//    public boolean isValid(int slot, ItemStack stack) {
//        return board.isValid(slot, stack);
//    }
//
//    /**
//     * Returns the number of times the specified item occurs in this inventory across all stored stacks.
//     *
//     * @param item
//     */
//    @Override
//    public int count(Item item) {
//        return board.count(item);
//    }
//
//    /**
//     * Determines whether this inventory contains any of the given candidate items.
//     *
//     * @param items
//     */
//    @Override
//    public boolean containsAny(Set<Item> items) {
//        return board.containsAny(items);
//    }
//
//    @Override
//    public int size() {
//        return board.size();
//    }
//
//    @Override
//    public boolean isEmpty() {
//        return board.isEmpty();
//    }
//
//    /**
//     * Fetches the stack currently stored at the given slot. If the slot is empty,
//     * or is outside the bounds of this inventory, returns see {@link ItemStack#EMPTY}.
//     *
//     * @param slot
//     */
//    @Override
//    public ItemStack getStack(int slot) {
//        return board.getStack(slot);
//    }
//
//    /**
//     * Removes a specific number of items from the given slot.
//     *
//     * @param slot
//     * @param amount
//     * @return the removed items as a stack
//     */
//    @Override
//    public ItemStack removeStack(int slot, int amount) {
//        return board.removeStack(slot, amount);
//    }
//
//    /**
//     * Removes the stack currently stored at the indicated slot.
//     *
//     * @param slot
//     * @return the stack previously stored at the indicated slot.
//     */
//    @Override
//    public ItemStack removeStack(int slot) {
//        return board.removeStack(slot);
//    }
//
//    @Override
//    public void setStack(int slot, ItemStack stack) {
//
//    }
//
//    /**
//     * Returns the maximum number of items a stack can contain when placed inside this inventory.
//     * No slots may have more than this number of items. It is effectively the
//     * stacking limit for this inventory's slots.
//     *
//     * @return the max {@link ItemStack#getCount() count} of item stacks in this inventory
//     */
//    @Override
//    public int getMaxCountPerStack() {
//        return board.getMaxCountPerStack();
//    }
//
//    @Override
//    public boolean canPlayerUse(PlayerEntity player) {
//        return board.canPlayerUse(player);
//    }
//
//    @Override
//    public void clear() {
//        board.clear();
//    }


}
