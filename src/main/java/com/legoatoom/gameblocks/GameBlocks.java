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

package com.legoatoom.gameblocks;

import com.legoatoom.gameblocks.blocks.ChessBoardBlock;
import com.legoatoom.gameblocks.blocks.entity.ChessBoardBlockEntity;
import com.legoatoom.gameblocks.client.gui.PawnPromotionWidget;
import com.legoatoom.gameblocks.items.chess.*;
import com.legoatoom.gameblocks.registry.ChessRegistry;
import com.legoatoom.gameblocks.registry.CommonRegistry;
import com.legoatoom.gameblocks.screen.ChessBoardScreenHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.legoatoom.gameblocks.registry.ChessRegistry.BLACK_PAWN;

/**
 * @author legoatoom
 */
public final class GameBlocks implements ModInitializer {
    /**
     * The ID of this mod.
     */
    public static final String MOD_ID = "gameblocks";
    public static ItemGroup GAME_BLOCKS = FabricItemGroupBuilder.build(id("main"), () -> new ItemStack(BLACK_PAWN));


    @Contract("_ -> new")
    public static @NotNull Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        CommonRegistry.register();
        ChessRegistry.register();
    }
}
