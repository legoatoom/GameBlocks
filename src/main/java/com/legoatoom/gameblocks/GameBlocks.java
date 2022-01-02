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
import com.legoatoom.gameblocks.items.ChessPiece;
import com.legoatoom.gameblocks.items.chess.IChessPieceItem;
import com.legoatoom.gameblocks.items.chess.PawnItem;
import com.legoatoom.gameblocks.screen.ChessBoardScreenHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.function.Function;

public class GameBlocks implements ModInitializer {


    public static final String MOD_ID = "gameblocks";
    public static final Function<String, Identifier> id = (String path) -> new Identifier(MOD_ID, path);

    public static final ItemGroup GAME_BLOCKS = FabricItemGroupBuilder.build(id.apply("game_blocks"),
            () -> new ItemStack(Blocks.BARRIER));


    public static Block CHESS_BOARD_BLOCK;
    public static Item CHESS_BOARD_ITEM;

    //Chess Pieces
    public static final ArrayList<IChessPieceItem> CHESS_PIECES = new ArrayList<>();
    public static Item BLACK_PAWN;
    public static Item WHITE_PAWN;



    public static BlockEntityType<ChessBoardBlockEntity> CHESS_BOARD_BLOCK_ENTITY;
    public static ScreenHandlerType<ChessBoardScreenHandler> CHESS_BOARD_SCREEN_HANDLER;

    static {
        CHESS_BOARD_BLOCK = new ChessBoardBlock();
        CHESS_BOARD_ITEM = new BlockItem(CHESS_BOARD_BLOCK, new FabricItemSettings().group(GAME_BLOCKS));
        CHESS_BOARD_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(ChessBoardBlockEntity::new, CHESS_BOARD_BLOCK).build();

        CHESS_BOARD_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(id.apply("chess_board"), ChessBoardScreenHandler::new);

        BLACK_PAWN = new PawnItem(true);
        WHITE_PAWN = new PawnItem(false);
    }

    @Override
    public void onInitialize() {
        registerBlocks();
        registerBlocksEntities();
        registerItems();
    }

    private void registerItems() {
        // ChessPieces
        Registry.register(Registry.ITEM, id.apply("black_pawn"), BLACK_PAWN);
        Registry.register(Registry.ITEM, id.apply("white_pawn"), WHITE_PAWN);


    }

    private void registerBlocksEntities() {
        Registry.register(Registry.BLOCK_ENTITY_TYPE, id.apply("chess_board_entity"), CHESS_BOARD_BLOCK_ENTITY);
    }

    private void registerBlocks() {
        Registry.register(Registry.BLOCK, id.apply("chess_board"), CHESS_BOARD_BLOCK);
        Registry.register(Registry.ITEM, id.apply("chess_board"), CHESS_BOARD_ITEM);
    }
}
