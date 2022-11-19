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

package com.legoatoom.gameblocks.chess.client.gui;

import com.legoatoom.gameblocks.GameBlocks;
import com.legoatoom.gameblocks.chess.client.screen.ChessBoardScreen;
import com.legoatoom.gameblocks.chess.items.IChessPieceItem;
import com.legoatoom.gameblocks.chess.items.KingItem;
import com.legoatoom.gameblocks.chess.items.PawnItem;
import com.legoatoom.gameblocks.chess.screen.slot.ChessGridSlot;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

import static com.legoatoom.gameblocks.registry.ChessRegistry.CHESS_PIECES;

@Environment(EnvType.CLIENT)
public class PawnPromotionWidget extends DrawableHelper implements Drawable, Element, Selectable {
    public static final Identifier TEXTURE = GameBlocks.id("textures/gui/pawn_promotion_widget.png");
    private final int x, y;
    private final List<TexturedButtonWidget> options = new ArrayList<>();

    public PawnPromotionWidget(ChessBoardScreen chessBoardScreen, int x, int y, MinecraftClient client, boolean isBlack, ChessGridSlot slot, int button, SlotActionType actionType) {
        this.x = x;
        this.y = y;
        int i = 0;
        for (IChessPieceItem chessPiece : CHESS_PIECES) {
            if (chessPiece instanceof PawnItem || chessPiece.isBlack() != isBlack || chessPiece instanceof KingItem)
                continue;
            Identifier id = GameBlocks.id("textures/item/%s.png".formatted(chessPiece.toString()));


            ButtonWidget.PressAction action = (ButtonWidget button2) -> {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeString(chessPiece.toString());
                ClientPlayNetworking.send(PawnPromotionChooseButtonWidget.PAWN_PROMOTION_C2S_UPDATE_KEY, buf);

                if (client.interactionManager != null) {
                    client.interactionManager.clickSlot(chessBoardScreen.getScreenHandler().syncId, slot.id, button, actionType, client.player);
                }
                chessBoardScreen.setPromotionSelectionOff(PawnPromotionWidget.this);
            };


            options.add(new PawnPromotionChooseButtonWidget(chessBoardScreen, chessPiece, this.x + 7 + 16 * i, this.y + 7, 14, 14, 0, 0, 0, id, 16, 16, action));
            i++;
        }
    }

    @Override
    public SelectionType getType() {
        return SelectionType.FOCUSED;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (TexturedButtonWidget option : options) {
            if (option.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return isMouseOver(mouseX, mouseY);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.enableDepthTest();
        drawTexture(matrices, x, y, 300, 0, 0, 78, 30, 78, 30);
        for (ButtonWidget button : options) {
            button.render(matrices, mouseX, mouseY, delta);
        }
    }

    @Environment(EnvType.CLIENT)
    public static class PawnPromotionChooseButtonWidget extends TexturedButtonWidget {
        // TODO: 12022-01-06 Look at AnimatedResultButton
        public static final Identifier PAWN_PROMOTION_C2S_UPDATE_KEY = GameBlocks.id("pawn_promotion_c2s_update_key");

        private final Identifier texture;
        private final int textureWidth;
        private final int textureHeight;
        private final ChessBoardScreen screen;
        private final Item item;

        public PawnPromotionChooseButtonWidget(ChessBoardScreen abstractBoardScreen, IChessPieceItem chessPiece, int x, int y, int width, int height, int u, int v, int hoveredVOffset, Identifier texture, int textureWidth, int textureHeight, PressAction pressAction) {
            super(x, y, width, height, u, v, hoveredVOffset, texture, textureWidth, textureHeight, pressAction);
            this.texture = texture;
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
            this.screen = abstractBoardScreen;
            this.item = chessPiece;
        }

        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            RenderSystem.setShaderTexture(0, texture);
            drawTexture(matrices, x, y, 300, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);
            if (this.isHovered()) {
                this.renderTooltip(matrices, mouseX, mouseY);
                RenderSystem.disableDepthTest();
                RenderSystem.colorMask(true, true, true, false);
                // Vanilla code uses gradient, therefor I also do.
                HandledScreen.fillGradient(matrices, x + 1, y + 1, x + 15, y + 15, -2130706433, -2130706433, 0);
                RenderSystem.colorMask(true, true, true, true);
                RenderSystem.enableDepthTest();
            }
        }

        @Override
        public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {
            screen.renderTooltip(matrices, Text.translatable(item.getTranslationKey()), x, y);
        }
    }
}
