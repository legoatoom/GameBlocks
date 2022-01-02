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

package com.legoatoom.gameblocks.client.gui.screen.ingame;

import com.legoatoom.gameblocks.GameBlocks;
import com.legoatoom.gameblocks.items.chess.IChessPieceItem;
import com.legoatoom.gameblocks.screen.slot.ChessBoardSlot;
import com.legoatoom.gameblocks.util.chess.ChessActionType;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ChessBoardScreen extends HandledScreen<ScreenHandler> {
    private static final Identifier TEXTURE = GameBlocks.id("textures/gui/chess_board_fancy.png");

    public ChessBoardScreen(ScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        backgroundWidth = 204;
        backgroundHeight = 252;
        this.playerInventoryTitleY = this.backgroundHeight - 94;

    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        this.textRenderer.drawWithShadow(matrices, this.title, (float)this.titleX, (float)this.titleY, 0xAAAAAA);
        this.textRenderer.draw(matrices, this.playerInventoryTitle, (float)this.playerInventoryTitleX, (float)this.playerInventoryTitleY, 0x404040);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawChessMoveHints(matrices);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    private void drawChessMoveHints(MatrixStack matrices) {
        if (!handler.getCursorStack().isEmpty()
                && handler.getCursorStack().getItem() instanceof IChessPieceItem chessPieceItem
                && lastClickedSlot != null && lastClickedSlot instanceof ChessBoardSlot chessBoardSlot) {
            //When holding a Piece
            List<ChessBoardSlot> actions = chessBoardSlot.calculateLegalActions(chessPieceItem);
            if (this.focusedSlot != null){
                RenderSystem.disableDepthTest();
                for (ChessBoardSlot action : actions) {
                    if (action == this.focusedSlot){
                        List<Text> info = action.getCurrentHoverAction().getInfo(this.textRenderer);
                        if (!info.isEmpty()) {
                            renderTooltip(matrices, info, this.focusedSlot.x + this.x + 12, this.focusedSlot.y + this.y);
                        }
                    }
                }
                RenderSystem.enableDepthTest();

            }
            drawChessGuide(matrices, actions);
        } else if (this.focusedSlot != null && this.focusedSlot instanceof ChessBoardSlot chessBoardSlot) {
            // When hovering a Piece
            if (chessBoardSlot.hasStack()){
                List<ChessBoardSlot> actions = chessBoardSlot.calculateLegalActions();
                drawChessGuide(matrices, actions);
            }
        }
    }

    @Override
    protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
        super.onMouseClick(slot, slotId, button, actionType);
        if (slot instanceof ChessBoardSlot chessBoardSlot && chessBoardSlot.getCurrentHoverAction() != null) {
            ChessActionType action = chessBoardSlot.getCurrentHoverAction();
            action.sendNbtUpdate(slotId);
        }
    }

    private void drawChessGuide(MatrixStack matrices, List<ChessBoardSlot> legalAction) {
        if (!legalAction.isEmpty()){
            for (ChessBoardSlot action : legalAction) {
                RenderSystem.colorMask(true, true, true, false);

                int color = action.getCurrentHoverAction().getColor();
                // Vanilla code uses gradient, therefor I also do.
                HandledScreen.fillGradient(matrices, action.x + 1 + this.x, action.y + 1 + this.y, action.x + 15 + this.x, action.y + 15 + this.y, color, color, getZOffset());
                RenderSystem.colorMask(true, true, true, true);
            }
        }
    }



    @Override
    protected void init() {
        super.init();
        // Center the title
        titleX = (176 - textRenderer.getWidth(title)) / 2;
    }
}
