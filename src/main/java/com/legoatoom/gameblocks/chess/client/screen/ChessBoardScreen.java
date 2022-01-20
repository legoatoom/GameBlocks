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

package com.legoatoom.gameblocks.chess.client.screen;

import com.legoatoom.gameblocks.GameBlocks;
import com.legoatoom.gameblocks.chess.client.gui.PawnPromotionWidget;
import com.legoatoom.gameblocks.common.client.screen.AbstractBoardScreen;
import com.legoatoom.gameblocks.chess.items.IChessPieceItem;
import com.legoatoom.gameblocks.chess.screen.ChessBoardScreenHandler;
import com.legoatoom.gameblocks.chess.screen.slot.ChessGridSlot;
import com.legoatoom.gameblocks.common.util.ActionType;
import com.legoatoom.gameblocks.chess.util.ChessActionType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Element;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ChessBoardScreen extends AbstractBoardScreen<ChessBoardScreenHandler> {
    private static final Identifier TEXTURE = GameBlocks.id("textures/gui/chess_board_fancy.png");
    private boolean isSelectingPromotion = false;

    public ChessBoardScreen(ChessBoardScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title, 64, 204, 244);
    }

    @Override
    protected Identifier getTexture() {
        return TEXTURE;
    }

    private boolean checkForPromotionButton(double mouseX, double mouseY, int button) {
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

    private boolean checkPromotion(Slot slot, int button, SlotActionType actionType) {
        ItemStack cursor = handler.getCursorStack();
        if (lastClickedSlotPre == null
                || !(slot instanceof ChessGridSlot s)
                || cursor.isEmpty()
                || (!(cursor.getItem() instanceof IChessPieceItem c))
                || !(lastClickedSlotPre instanceof ChessGridSlot s2))
            return true;
        int newPreSlotId = s2.getIndex();
        int newSlotId = s.getIndex();
        ActionType type = this.handler.getActionTypeFromSlot(newPreSlotId, newSlotId);
        if (type instanceof ChessActionType type1){
            if (type1 == ChessActionType.PROMOTION || type1 == ChessActionType.PROMOTION_CAPTURE) {
                this.isSelectingPromotion = true;
                this.addDrawableChild(new PawnPromotionWidget(this, slot.x + this.x, slot.y + this.y, this.client, c.isBlack(), s, button, actionType));
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
        // Special Case for Promotion of Pawn
        if (isSelectingPromotion) return;
        if (checkPromotion(slot, button, actionType)) {
            super.onMouseClick(slot, slotId, button, actionType);
        }
    }

    public void setPromotionSelectionOff(PawnPromotionWidget pawnPromotionWidget) {
        isSelectingPromotion = false;
        this.remove(pawnPromotionWidget);
    }

    public boolean isSelectingPromotion() {
        return isSelectingPromotion;
    }

    @Override
    protected boolean mouseClickedPre(double mouseX, double mouseY, int button) {
        if (isSelectingPromotion) {
            return checkForPromotionButton(mouseX, mouseY, button);
        }
        return false;
    }
}
