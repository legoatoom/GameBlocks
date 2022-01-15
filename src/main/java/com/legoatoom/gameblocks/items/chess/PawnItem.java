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

package com.legoatoom.gameblocks.items.chess;

import com.legoatoom.gameblocks.GameBlocks;
import com.legoatoom.gameblocks.client.screen.ChessBoardScreen;
import com.legoatoom.gameblocks.screen.slot.ChessGridBoardSlot;
import com.legoatoom.gameblocks.util.chess.ChessActionType;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.legoatoom.gameblocks.GameBlocks.MOD_ID;
import static com.legoatoom.gameblocks.registry.ChessRegistry.CHESS_PIECES;

public class PawnItem extends IChessPieceItem {
    public PawnItem(boolean isBlack) {
        super(isBlack, 8, ChessPieceType.PAWN);
    }

    @SuppressWarnings("unused")
    public static void receivePromotionRequest(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        ItemStack stack = player.currentScreenHandler.getCursorStack();
        String id = buf.readString();
        NbtCompound nbt = stack.getOrCreateSubNbt(MOD_ID);
        nbt.putString("promotion", id);
    }


    @Override
    public boolean isDefaultLocation(int x, int y) {
        return isBlack() && y == 1 || !isBlack() && y == 6;
    }

    @Override
    public void calculateLegalActions(@NotNull ChessGridBoardSlot slot) {
        Optional<ChessGridBoardSlot> up = slot.up(isBlack());
        //Moving
        up.ifPresent(chessBoardSlot -> {
            if (!chessBoardSlot.hasStack()) {
                chessBoardSlot.setHoverHint(slot.getIndex(), isPromotion(chessBoardSlot) ? ChessActionType.PROMOTION : ChessActionType.MOVE);
                // First move can be 2 steps.
                if (isDefaultLocation(slot.getBoardXLoc(), slot.getBoardYLoc())) {
                    Optional<ChessGridBoardSlot> up2 = slot.up(isBlack(), 2);
                    up2.ifPresent(chessBoardSlot1 -> {
                        if (!chessBoardSlot1.hasStack()) {
                            chessBoardSlot1.setHoverHint(slot.getIndex(), ChessActionType.INITIAL_MOVE);
                        }
                    });
                }
            }
        });


        // Capture
        slot.upLeft(isBlack()).ifPresent(chessBoardSlot -> testCapture(chessBoardSlot, slot.getIndex()));
        slot.upRight(isBlack()).ifPresent(chessBoardSlot -> testCapture(chessBoardSlot, slot.getIndex()));

        // En Passant
        slot.left(isBlack()).ifPresent(chessBoardSlot -> testEnPassant(chessBoardSlot, slot.getIndex()));
        slot.right(isBlack()).ifPresent(chessBoardSlot -> testEnPassant(chessBoardSlot, slot.getIndex()));
    }

    @Override
    public void handleAction(ScreenHandler handler, ChessGridBoardSlot slot, ItemStack cursorStack, ChessActionType actionType) {
        if (actionType == ChessActionType.CAPTURE || actionType == ChessActionType.PROMOTION_CAPTURE) {
            slot.capturePiece(handler, cursorStack);
        }

        if (actionType == ChessActionType.EN_PASSANT) {
            slot.down(isBlack()).ifPresent(ChessGridBoardSlot::capturePiece);
        }

        for (int i = 0; i < slot.getInventory().size(); i++) {
            ItemStack itemStack = slot.getInventory().getStack(i);
            if (itemStack.getItem() instanceof PawnItem item && item.isBlack() == this.isBlack()) {
                continue;
            }
            if (itemStack.equals(cursorStack)) {
                continue;
            }
            if (itemStack.hasNbt()) {
                NbtCompound compound = itemStack.getOrCreateSubNbt(GameBlocks.MOD_ID);
                if (compound.contains("mayEnPassant")) {
                    compound.remove("mayEnPassant");
                }
            }
        }
        ItemStack slotStack = slot.getStack();
        NbtCompound nbtCompound = slotStack.getOrCreateSubNbt(GameBlocks.MOD_ID);
        if (actionType == ChessActionType.INITIAL_MOVE) {
            nbtCompound.putBoolean("mayEnPassant", true);
        }

        if (actionType == ChessActionType.PROMOTION || actionType == ChessActionType.PROMOTION_CAPTURE) {
            Item item = Registry.ITEM.get(GameBlocks.id(nbtCompound.getString("promotion")));
            if (item instanceof IChessPieceItem item1) {
                ItemStack newStack = new ItemStack(item1);
                NbtCompound nbtCompound1 = newStack.getOrCreateSubNbt(GameBlocks.MOD_ID);
                nbtCompound1.putBoolean("promoted", true);
                slot.setStack(newStack);
            }
        }
    }

    private void testCapture(@NotNull ChessGridBoardSlot current, int origin) {
        current.getItem().ifPresentOrElse(chessPieceItem -> {
            if (chessPieceItem.isBlack() != this.isBlack()) {
                current.setHoverHint(origin, isPromotion(current) ? ChessActionType.PROMOTION_CAPTURE : ChessActionType.CAPTURE);
            }
        }, () -> current.setHoverHint(origin, ChessActionType.PAWN_POTENTIAL));
    }

    private boolean isPromotion(@NotNull ChessGridBoardSlot current) {
        return current.getBoardYLoc() == 0 || current.getBoardYLoc() == 7;
    }

    private void testEnPassant(@NotNull ChessGridBoardSlot current, int origin) {
        current.getItem().ifPresent(chessPieceItem -> {
            NbtCompound compound = current.getStack().getSubNbt(GameBlocks.MOD_ID);
            if (compound != null && compound.contains("mayEnPassant") && chessPieceItem.isBlack() != this.isBlack()) {
                current.up(isBlack()).ifPresent(chessBoardSlot -> chessBoardSlot.setHoverHint(origin, ChessActionType.EN_PASSANT));
            }
        });
    }

    @Environment(EnvType.CLIENT)
    public static class PawnPromotionWidget extends DrawableHelper implements Drawable, Element, Selectable {
        public static final Identifier TEXTURE = GameBlocks.id("textures/gui/pawn_promotion_widget.png");
        private final MinecraftClient client;
        private final int x, y;
        private final List<TexturedButtonWidget> options = new ArrayList<>();

        public PawnPromotionWidget(ChessBoardScreen chessBoardScreen, int x, int y, MinecraftClient client, boolean isBlack, ChessGridBoardSlot slot, int button, SlotActionType actionType) {
            this.x = x;
            this.y = y;
            this.client = client;
            int i = 0;
            for (IChessPieceItem chessPiece : CHESS_PIECES) {
                if (chessPiece instanceof PawnItem || chessPiece.isBlack() != isBlack || chessPiece instanceof KingItem)
                    continue;
                Identifier id = GameBlocks.id("textures/item/%s.png".formatted(chessPiece.toString()));


                ButtonWidget.PressAction action = (ButtonWidget button2) -> {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeString(chessPiece.toString());
                    ClientPlayNetworking.send(PawnPromotionChooseButtonWidget.PAWN_PROMOTION_C2S_UPDATE_KEY, buf);

                    if (PawnPromotionWidget.this.client.interactionManager != null) {
                        PawnPromotionWidget.this.client.interactionManager.clickSlot(chessBoardScreen.getScreenHandler().syncId, slot.id, button, actionType, PawnPromotionWidget.this.client.player);
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

            public PawnPromotionChooseButtonWidget(ChessBoardScreen chessBoardScreen, IChessPieceItem chessPiece, int x, int y, int width, int height, int u, int v, int hoveredVOffset, Identifier texture, int textureWidth, int textureHeight, PressAction pressAction) {
                super(x, y, width, height, u, v, hoveredVOffset, texture, textureWidth, textureHeight, pressAction);
                this.texture = texture;
                this.textureWidth = textureWidth;
                this.textureHeight = textureHeight;
                this.screen = chessBoardScreen;
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
                screen.renderTooltip(matrices, new TranslatableText(item.getTranslationKey()), x, y);
            }
        }
    }
}
