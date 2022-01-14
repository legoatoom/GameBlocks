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

package com.legoatoom.gameblocks.registry;

import com.legoatoom.gameblocks.blocks.ChessBoardBlock;
import com.legoatoom.gameblocks.blocks.entity.ChessBoardBlockEntity;
import com.legoatoom.gameblocks.client.gui.PawnPromotionWidget;
import com.legoatoom.gameblocks.items.chess.*;
import com.legoatoom.gameblocks.screen.ChessBoardScreenHandler;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.ArrayList;

import static com.legoatoom.gameblocks.GameBlocks.*;

public class ChessRegistry {

    public static final ArrayList<IChessPieceItem> CHESS_PIECES = new ArrayList<>();
    // --- CHESS ---
    public static Block CHESS_BOARD_BLOCK = new ChessBoardBlock(FabricBlockSettings.of(Material.WOOD).strength(2.f).resistance(2.f).sounds(BlockSoundGroup.WOOD));
    public static Item CHESS_BOARD_ITEM = new BlockItem(CHESS_BOARD_BLOCK, new FabricItemSettings().group(GAME_BLOCKS));
    public static Item WHITE_PAWN;
    public static Item BLACK_PAWN;
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

    public static void register(){
        registerPieces();
        registerBoard();
        registerNetworking();
    }

    private static void registerNetworking() {
        ServerPlayNetworking.registerGlobalReceiver(
                PawnPromotionWidget.PawnPromotionChooseButtonWidget.PAWN_PROMOTION_C2S_UPDATE_KEY,
                ChessRegistry::receivePromotionRequest);
    }

    private static void registerBoard() {
        Registry.register(Registry.BLOCK, id("chess_board"), CHESS_BOARD_BLOCK);
        Registry.register(Registry.ITEM, id("chess_board"), CHESS_BOARD_ITEM);
        CHESS_BOARD_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(ChessBoardBlockEntity::new, CHESS_BOARD_BLOCK).build();
        CHESS_BOARD_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(id("chess_board_screen_handler"), ChessBoardScreenHandler::new);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, id("chess_board_entity"), CHESS_BOARD_BLOCK_ENTITY);
    }

    private static void registerPieces() {
        Registry.register(Registry.ITEM, id("white_pawn"), WHITE_PAWN);
        Registry.register(Registry.ITEM, id("black_pawn"), BLACK_PAWN);
        Registry.register(Registry.ITEM, id("white_rook"), WHITE_ROOK);
        Registry.register(Registry.ITEM, id("black_rook"), BLACK_ROOK);
        Registry.register(Registry.ITEM, id("white_king"), WHITE_KING);
        Registry.register(Registry.ITEM, id("black_king"), BLACK_KING);
        Registry.register(Registry.ITEM, id("white_queen"), WHITE_QUEEN);
        Registry.register(Registry.ITEM, id("black_queen"), BLACK_QUEEN);
        Registry.register(Registry.ITEM, id("white_bishop"), WHITE_BISHOP);
        Registry.register(Registry.ITEM, id("black_bishop"), BLACK_BISHOP);
        Registry.register(Registry.ITEM, id("white_knight"), WHITE_KNIGHT);
        Registry.register(Registry.ITEM, id("black_knight"), BLACK_KNIGHT);
    }

    private static void receivePromotionRequest(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        ItemStack stack = player.currentScreenHandler.getCursorStack();
        String id = buf.readString();
        NbtCompound nbt = stack.getOrCreateSubNbt(MOD_ID);
        nbt.putString("promotion", id);
    }
}
