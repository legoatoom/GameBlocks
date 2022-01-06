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
import com.legoatoom.gameblocks.screen.ChessBoardScreenHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;

public final class GameBlocks implements ModInitializer {


    public static final String MOD_ID = "gameblocks";

    public static final ItemGroup GAME_BLOCKS = FabricItemGroupBuilder.build(id("game_blocks"),
            () -> new ItemStack(Blocks.BARRIER));


    public static Block CHESS_BOARD_BLOCK;
    public static Item CHESS_BOARD_ITEM;

    //Chess Pieces
    public static final ArrayList<IChessPieceItem> CHESS_PIECES = new ArrayList<>();
    public static Item BLACK_PAWN;
    public static Item WHITE_PAWN;
    public static Item WHITE_ROOK;
    public static Item BLACK_ROOK;
    public static Item WHITE_KING;
    public static Item BLACK_KING;
    public static Item WHITE_QUEEN;
    public static Item BLACK_QUEEN;
    public static Item WHITE_BISHOP;
    public static Item BLACK_BISHOP;
    public static Item WHITE_KNIGHT;
    public static Item BLACK_KNIGHT;



    public static BlockEntityType<ChessBoardBlockEntity> CHESS_BOARD_BLOCK_ENTITY;
    public static ScreenHandlerType<ChessBoardScreenHandler> CHESS_BOARD_SCREEN_HANDLER;

    static {
        CHESS_BOARD_BLOCK = new ChessBoardBlock();
        CHESS_BOARD_ITEM = new BlockItem(CHESS_BOARD_BLOCK, new FabricItemSettings().group(GAME_BLOCKS));
        CHESS_BOARD_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(ChessBoardBlockEntity::new, CHESS_BOARD_BLOCK).build();

        CHESS_BOARD_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(id("chess_board"), ChessBoardScreenHandler::new);

        BLACK_PAWN = new PawnItem(true);
        WHITE_PAWN = new PawnItem(false);
        BLACK_ROOK = new RookItem(true);
        WHITE_ROOK = new RookItem(false);
        BLACK_KING = new KingItem(true);
        WHITE_KING = new KingItem(false);
        BLACK_QUEEN = new QueenItem(true);
        WHITE_QUEEN = new QueenItem(false);
        BLACK_BISHOP = new BishopItem(true);
        WHITE_BISHOP = new BishopItem(false);
        BLACK_KNIGHT = new KnightItem(true);
        WHITE_KNIGHT = new KnightItem(false);

    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        registerBlocks();
        registerBlocksEntities();
        registerItems();
        registerNetworking();
    }

    private void registerNetworking() {
        ServerPlayNetworking.registerGlobalReceiver(PawnPromotionWidget.PawnPromotionChooseButtonWidget.PAWN_PROMOTION_C2S_UPDATE_KEY,
                (server, player, handler, buf, responseSender) -> {
            ItemStack stack = player.currentScreenHandler.getCursorStack();
            String id = buf.readString();
            NbtCompound nbt = stack.getOrCreateSubNbt(MOD_ID);
            nbt.putString("promotion", id);
        });
    }


    private void registerItems() {
        // ChessPieces
        Registry.register(Registry.ITEM, id("white_pawn"),WHITE_PAWN);
        Registry.register(Registry.ITEM, id("black_pawn"),BLACK_PAWN);
        Registry.register(Registry.ITEM, id("white_rook"),WHITE_ROOK);
        Registry.register(Registry.ITEM, id("black_rook"),BLACK_ROOK);
        Registry.register(Registry.ITEM, id("white_king"),WHITE_KING);
        Registry.register(Registry.ITEM, id("black_king"),BLACK_KING);
        Registry.register(Registry.ITEM, id("white_queen"),WHITE_QUEEN);
        Registry.register(Registry.ITEM, id("black_queen"),BLACK_QUEEN);
        Registry.register(Registry.ITEM, id("white_bishop"),WHITE_BISHOP);
        Registry.register(Registry.ITEM, id("black_bishop"),BLACK_BISHOP);
        Registry.register(Registry.ITEM, id("white_knight"),WHITE_KNIGHT);
        Registry.register(Registry.ITEM, id("black_knight"),BLACK_KNIGHT);


    }

    private void registerBlocksEntities() {
        Registry.register(Registry.BLOCK_ENTITY_TYPE, id("chess_board_entity"), CHESS_BOARD_BLOCK_ENTITY);
    }

    private void registerBlocks() {
        Registry.register(Registry.BLOCK, id("chess_board"), CHESS_BOARD_BLOCK);
        Registry.register(Registry.ITEM, id("chess_board"), CHESS_BOARD_ITEM);
    }
}
