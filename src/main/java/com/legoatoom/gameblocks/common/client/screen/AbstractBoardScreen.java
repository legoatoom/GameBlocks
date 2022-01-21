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

package com.legoatoom.gameblocks.common.client.screen;

import com.legoatoom.gameblocks.common.items.IPieceItem;
import com.legoatoom.gameblocks.common.screen.AbstractBoardScreenHandler;
import com.legoatoom.gameblocks.common.screen.slot.AbstractGridSlot;
import com.legoatoom.gameblocks.common.util.ActionType;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

@Environment(EnvType.CLIENT)
public abstract class AbstractBoardScreen<T extends AbstractBoardScreenHandler<?>> extends HandledScreen<T> {

    private final int boardSize;
    protected Slot lastClickedSlotPre, lastClickedSlotPost;



    public AbstractBoardScreen(T handler, PlayerInventory inventory, Text title, int boardSize, int backgroundWidth, int backgroundHeight) {
        super(handler, inventory, title);
        this.backgroundWidth = backgroundWidth;
        this.backgroundHeight = backgroundHeight;
        this.boardSize = boardSize;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
        this.titleY = 0;

    }

    protected abstract Identifier getTexture();

    private void drawChessMoveHints(MatrixStack matrices) {
        if (!handler.getCursorStack().isEmpty()
                && handler.getCursorStack().getItem() instanceof IPieceItem
                && lastClickedSlotPost != null && lastClickedSlotPost instanceof AbstractGridSlot abstractGridSlot) {
            //When holding a Piece
            List<AbstractGridSlot> actions = this.handler.getCurrentSlotActions(abstractGridSlot.getIndex());
            if (this.focusedSlot != null) {
                RenderSystem.disableDepthTest();
                for (AbstractGridSlot action : actions) {
                    if (action == this.focusedSlot) {
                        ActionType type = this.handler.getActionTypeFromSlot(abstractGridSlot.getIndex(), action.getIndex());
                        List<Text> info = type.getInfo(this.textRenderer);
                        if (!info.isEmpty()) {
                            renderTooltip(matrices, info, this.focusedSlot.x + this.x + 12, this.focusedSlot.y + this.y);
                        }
                    }
                }
                RenderSystem.enableDepthTest();

            }
            drawGuide(matrices, actions, abstractGridSlot);
        } else if (this.focusedSlot != null && this.focusedSlot instanceof AbstractGridSlot abstractGridSlot) {
            // When hovering a Piece
            if (abstractGridSlot.hasStack()) {
                List<AbstractGridSlot> actions = this.handler.getCurrentSlotActions(abstractGridSlot.getIndex());
                drawGuide(matrices, actions, abstractGridSlot);
            }
        }
    }



    private void drawGuide(MatrixStack matrices, List<AbstractGridSlot> legalAction, AbstractGridSlot focusPoint) {
        int slotSize = focusPoint.getSlotHighLighterSize();
        int offset = (16 - slotSize) / 2;

        if (!legalAction.isEmpty()) {
            for (AbstractGridSlot action : legalAction) {
                RenderSystem.colorMask(true, true, true, false);
                ActionType type = this.handler.getActionTypeFromSlot(focusPoint.getIndex(), action.getIndex());
                int color = type.getColor();
                // Vanilla code uses gradient, therefor I also do.
                HandledScreen.fillGradient(matrices, action.x + offset + this.x, action.y + offset + this.y, action.x + offset + slotSize + this.x, action.y + offset + slotSize + this.y, color, color, getZOffset());
                RenderSystem.colorMask(true, true, true, true);
            }
        }
    }

    @Override
    protected void init() {
        super.init();
        // Center the title
        titleX = getTitleX();
    }

    protected int getTitleX(){
        return (176 - textRenderer.getWidth(title)) / 2;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawChessMoveHints(matrices);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        this.textRenderer.drawWithShadow(matrices, this.title, (float) this.titleX, (float) this.titleY, 0xAAAAAA);
        this.textRenderer.draw(matrices, this.playerInventoryTitle, (float) this.playerInventoryTitleX, (float) this.playerInventoryTitleY, 0x404040);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, getTexture());
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        lastClickedSlotPre = (this.lastClickedSlot == null || this.lastClickedSlot.getIndex() >= boardSize) ? lastClickedSlotPre : this.lastClickedSlot;
        if (this.mouseClickedPre(mouseX, mouseY, button)){
            return true;
        }
        boolean result = super.mouseClicked(mouseX, mouseY, button);
        lastClickedSlotPost = (this.lastClickedSlot == null || this.lastClickedSlot.getIndex() >= boardSize) ? lastClickedSlotPost : this.lastClickedSlot;
        return result;
    }

    protected boolean mouseClickedPre(double mouseX, double mouseY, int button) {
        return false;
    }


}
