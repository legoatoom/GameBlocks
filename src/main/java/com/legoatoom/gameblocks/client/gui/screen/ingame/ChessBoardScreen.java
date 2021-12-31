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
import com.legoatoom.gameblocks.items.ChessPiece;
import com.legoatoom.gameblocks.screen.ChessBoardScreenHandler;
import com.legoatoom.gameblocks.screen.slot.ChessBoardSlot;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ChessBoardScreen extends HandledScreen<ScreenHandler> {
    private static final Identifier TEXTURE = GameBlocks.id.apply("textures/gui/chess_board_fancy.png");


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

        hoverAssistance:
        if (this.focusedSlot != null && this.focusedSlot instanceof ChessBoardSlot chessBoardSlot) {
            Item focusedSlotItem = chessBoardSlot.getStack().getItem();
            if (!(focusedSlotItem instanceof ChessPiece.ChessPieceItem chessPieceItem))
                break hoverAssistance;

            List<ChessBoardSlot> chessPieces = this.handler.slots.stream()
                    .filter(slot -> slot instanceof ChessBoardSlot)
                    .filter(slot -> !slot.equals(this.focusedSlot))
                    .map(slot -> ((ChessBoardSlot) slot))
                    .toList();

            List<ChessBoardSlot> indicateList = chessPieceItem.hoverIndicationFunction(chessPieces, chessBoardSlot);
            for (ChessBoardSlot slot: indicateList) {
                ChessPiece type = slot.getType();
                if (type != null && type.isBlack() == chessBoardSlot.isBlack()) continue;
                RenderSystem.disableDepthTest();
                RenderSystem.colorMask(true, true, true, false);
                int color = (type == null) ? 0x80ffffff : 0x80cc0000;
                // Vanilla code uses gradient, therefor I also do.
                HandledScreen.fillGradient(matrices, slot.x + 1 + this.x, slot.y + 1 + this.y, slot.x + 15 + this.x, slot.y + 15 + this.y, color, color, this.getZOffset());
                RenderSystem.colorMask(true, true, true, true);
                RenderSystem.enableDepthTest();
            }
        }


        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        // Center the title

        titleX = (176 - textRenderer.getWidth(title)) / 2;
    }


}
