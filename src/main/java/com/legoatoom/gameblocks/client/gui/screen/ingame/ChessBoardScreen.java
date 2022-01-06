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
import com.legoatoom.gameblocks.client.gui.PawnPromotionWidget;
import com.legoatoom.gameblocks.items.chess.IChessPieceItem;
import com.legoatoom.gameblocks.screen.ChessBoardScreenHandler;
import com.legoatoom.gameblocks.screen.slot.ChessBoardSlot;
import com.legoatoom.gameblocks.util.chess.ChessActionType;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ChessBoardScreen extends HandledScreen<ChessBoardScreenHandler> {
    private static final Identifier TEXTURE = GameBlocks.id("textures/gui/chess_board_fancy.png");
    private boolean isSelectingPromotion = false;

    public ChessBoardScreen(ChessBoardScreenHandler handler, PlayerInventory inventory, Text title) {
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
                && handler.getCursorStack().getItem() instanceof IChessPieceItem
                && lastClickedSlotPost != null && lastClickedSlotPost instanceof ChessBoardSlot chessBoardSlot) {
            //When holding a Piece
            List<ChessBoardSlot> actions = this.handler.getCurrentSlotActions(chessBoardSlot.getIndex());
            if (this.focusedSlot != null){
                RenderSystem.disableDepthTest();
                for (ChessBoardSlot action : actions) {
                    if (action == this.focusedSlot){
                        ChessActionType type = ChessActionType.fromId(this.handler.slotHintPropertyDelegate.get(this.lastClickedSlotPost.getIndex()).get(action.getIndex()));
                        List<Text> info = type.getInfo(this.textRenderer);
                        if (!info.isEmpty()) {
                            renderTooltip(matrices, info, this.focusedSlot.x + this.x + 12, this.focusedSlot.y + this.y);
                        }
                    }
                }
                RenderSystem.enableDepthTest();

            }
            drawChessGuide(matrices, actions, this.lastClickedSlotPost);
        } else if (this.focusedSlot != null && this.focusedSlot instanceof ChessBoardSlot chessBoardSlot) {
            // When hovering a Piece
            if (chessBoardSlot.hasStack()){
                List<ChessBoardSlot> actions = this.handler.getCurrentSlotActions(chessBoardSlot.getIndex());
                drawChessGuide(matrices, actions, this.focusedSlot);
            }
        }
    }

    private Slot lastClickedSlotPre, lastClickedSlotPost;

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        lastClickedSlotPre = (this.lastClickedSlot == null || this.lastClickedSlot.getIndex() >= 64) ? lastClickedSlotPre : this.lastClickedSlot;
        if (isSelectingPromotion) {
            return checkForPromotionButton(mouseX, mouseY, button);
        }
        boolean result = super.mouseClicked(mouseX, mouseY, button);
        lastClickedSlotPost = (this.lastClickedSlot == null || this.lastClickedSlot.getIndex() >= 64) ? lastClickedSlotPost : this.lastClickedSlot;
        return result;
    }

    private boolean checkForPromotionButton(double mouseX, double mouseY, int button){
        for (Element element : this.children()) {
            if (!element.mouseClicked(mouseX, mouseY, button)) continue;
            this.setFocused(element);
            if (button == 0) {
                this.setDragging(true);
            }
            return true;
        }
        return false;
    }



    @Override
    protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
        // Special Case for Promotion of Pawn
        if (isSelectingPromotion) return;
        if (checkPromotion(slot, slotId, button, actionType)){
            super.onMouseClick(slot, slotId, button, actionType);
        }
    }

    private boolean checkPromotion(Slot slot, int slotId, int button, SlotActionType actionType) {
        if (lastClickedSlotPre != null && slot instanceof ChessBoardSlot && !handler.getCursorStack().isEmpty()){
            boolean isBlack = ((IChessPieceItem) handler.getCursorStack().getItem()).isBlack();
            ChessActionType type = this.handler.getActionTypeFromSlot(lastClickedSlotPre.getIndex(), slotId);
            if (type == ChessActionType.PROMOTION || type == ChessActionType.PROMOTION_CAPTURE){
                this.isSelectingPromotion = true;
                this.addDrawableChild(new PawnPromotionWidget(this, slot.x + this.x, slot.y+ this.y, this.client, isBlack, slot, button, actionType));
                return false;
            }
        }
        return true;
    }


    private void drawChessGuide(MatrixStack matrices, List<ChessBoardSlot> legalAction, Slot focusPoint) {
        // TODO: 12022-01-04 Remake the whole highlighting moves so it requests syncs a lists with the server.
        //  It is well designed that the rules of chess are client sided.
        if (!legalAction.isEmpty()){
            for (ChessBoardSlot action : legalAction) {
                RenderSystem.colorMask(true, true, true, false);
                ChessActionType type = ChessActionType.fromId(this.handler.slotHintPropertyDelegate.get(focusPoint.getIndex()).get(action.getIndex()));
                int color = type.getColor();
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
        this.handler.onContentChanged(this.handler.inventory);
        titleX = (176 - textRenderer.getWidth(title)) / 2;
    }

    public void setPromotionSelectionOff(PawnPromotionWidget pawnPromotionWidget) {
        isSelectingPromotion = false;
        this.remove(pawnPromotionWidget);
    }

    public boolean isSelectingPromotion() {
        return isSelectingPromotion;
    }
}
