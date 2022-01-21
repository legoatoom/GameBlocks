package com.legoatoom.gameblocks.registry;

import com.legoatoom.gameblocks.GameBlocksState;
import com.legoatoom.gameblocks.checkers.blocks.CheckersBoardBlock;
import com.legoatoom.gameblocks.checkers.blocks.entity.CheckersBoardBlockEntity;
import com.legoatoom.gameblocks.checkers.items.CheckersStoneItem;
import com.legoatoom.gameblocks.checkers.screen.CheckersBoardScreenHandler;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.registry.Registry;

import static com.legoatoom.gameblocks.GameBlocks.GAME_BLOCKS;
import static com.legoatoom.gameblocks.GameBlocks.id;

public class CheckersRegistry {



    // I ain't calling the pieces 'MEN' here, you can probably figure out why.
    public static CheckersStoneItem BLACK_STONE;
    public static CheckersStoneItem WHITE_STONE;

    public static Block CHECKERS_BOARD_BLOCK = new CheckersBoardBlock(FabricBlockSettings.of(Material.WOOD)
            .strength(2.f, 2.f).sounds(BlockSoundGroup.WOOD));
    public static Item CHECKERS_BOARD_ITEM = new BlockItem(CHECKERS_BOARD_BLOCK, new FabricItemSettings().group(GAME_BLOCKS));
    public static BlockEntityType<CheckersBoardBlockEntity> CHECKERS_BOARD_BLOCK_ENTITY;
    public static ScreenHandlerType<CheckersBoardScreenHandler> CHECKERS_BOARD_SCREEN_HANDLER;

    static {
        BLACK_STONE = new CheckersStoneItem(true);
        WHITE_STONE = new CheckersStoneItem(false);
    }

    public static void register() {
        GameBlocksState.info("Registering Game: Checkers ⛀");
        registerPieces();
        registerBoard();
        registerNetworking();
    }

    private static void registerNetworking() {

    }

    private static void registerBoard() {
        Registry.register(Registry.BLOCK, id("checkers_board"), CHECKERS_BOARD_BLOCK);
        Registry.register(Registry.ITEM, id("checkers_board"), CHECKERS_BOARD_ITEM);
        CHECKERS_BOARD_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(CheckersBoardBlockEntity::new, CHECKERS_BOARD_BLOCK).build();
        CHECKERS_BOARD_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(id("checkers_board_screen_handler"), CheckersBoardScreenHandler::new);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, id("checkers_board_entity"), CHECKERS_BOARD_BLOCK_ENTITY);
    }

    private static void registerPieces() {
        Registry.register(Registry.ITEM, id("checkers/black_stone"), BLACK_STONE);
        Registry.register(Registry.ITEM, id("checkers/white_stone"), WHITE_STONE);
    }
}
