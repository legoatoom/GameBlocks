package com.legoatoom.gameblocks.checkers.client.screen;

import com.legoatoom.gameblocks.GameBlocks;
import com.legoatoom.gameblocks.checkers.screen.CheckersBoardScreenHandler;
import com.legoatoom.gameblocks.common.client.screen.AbstractBoardScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CheckersBoardScreen extends AbstractBoardScreen<CheckersBoardScreenHandler> {
    private static final Identifier TEXTURE = GameBlocks.id("textures/gui/checkers_board_fancy.png");

    public CheckersBoardScreen(CheckersBoardScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title, 100, 192, 256);
    }

    @Override
    protected Identifier getTexture() {
        return TEXTURE;
    }
}
