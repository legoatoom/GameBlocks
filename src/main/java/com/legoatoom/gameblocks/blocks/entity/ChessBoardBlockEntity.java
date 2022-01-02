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
import com.legoatoom.gameblocks.inventory.ChessBoardInventory;
import com.legoatoom.gameblocks.screen.ChessBoardScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class ChessBoardBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory {



    private final ChessBoardInventory chessBoardInventory = new ChessBoardInventory();

    public ChessBoardBlockEntity(BlockPos pos, BlockState state) {
        super(GameBlocks.CHESS_BOARD_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        chessBoardInventory.readNbt(nbt);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        chessBoardInventory.writeNBt(nbt);
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new ChessBoardScreenHandler(syncId, inv, this.chessBoardInventory, this.getCachedState().get(ChessBoardBlock.FACING));
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
}
