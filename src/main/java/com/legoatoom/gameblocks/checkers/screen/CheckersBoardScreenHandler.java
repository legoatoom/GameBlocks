package com.legoatoom.gameblocks.checkers.screen;

import com.legoatoom.gameblocks.checkers.inventory.CheckersBoardInventory;
import com.legoatoom.gameblocks.checkers.items.CheckersStoneItem;
import com.legoatoom.gameblocks.checkers.screen.slot.CheckersGridSlot;
import com.legoatoom.gameblocks.checkers.screen.slot.CheckersStorageBoardSlot;
import com.legoatoom.gameblocks.checkers.util.CheckersActionType;
import com.legoatoom.gameblocks.common.screen.AbstractBoardScreenHandler;
import com.legoatoom.gameblocks.common.screen.slot.AbstractGridSlot;
import com.legoatoom.gameblocks.common.util.ActionType;
import com.legoatoom.gameblocks.registry.CheckersRegistry;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;

import static com.legoatoom.gameblocks.registry.CheckersRegistry.BLACK_STONE;
import static com.legoatoom.gameblocks.registry.CheckersRegistry.WHITE_STONE;

public class CheckersBoardScreenHandler extends AbstractBoardScreenHandler<CheckersBoardInventory> {


    public CheckersBoardScreenHandler(int syncId, PlayerInventory inv, PacketByteBuf buf) {
        super(CheckersRegistry.CHECKERS_BOARD_SCREEN_HANDLER,syncId, inv, buf, CheckersBoardInventory::new);
    }

    public CheckersBoardScreenHandler(int syncId, PlayerInventory playerInventory, CheckersBoardInventory inventory, Direction facing) {
        super(CheckersRegistry.CHECKERS_BOARD_SCREEN_HANDLER, syncId, playerInventory, inventory, facing);
    }

    @Override
    protected void initializeSlots() {
        int y, x;
        int startY = 8;
        int startX = 17;
        for (y = 0; y < BOARD_WIDTH; y++) {
            for (x = 0; x < BOARD_WIDTH; x++) {
                Pair<Integer, Integer> pair = rotationTransformer(x, y);
                int boardX = pair.getLeft();
                int boardY = pair.getRight();
                Slot slot = new CheckersGridSlot(getBoardInventory(), boardX, boardY, startX + x * 14, startY + y * 14);
                this.addSlot(slot);
            }
        }

        // Chess Pieces Storage
        startY = 64;
        startX = 164;
        CheckersBoardInventory inv = getBoardInventory();
        for (CheckersStoneItem checkersPieceItem : new CheckersStoneItem[]{WHITE_STONE, BLACK_STONE}) {
            int a = checkersPieceItem.isBlack() ? 1 : 0;
            this.addSlot(new CheckersStorageBoardSlot(inv, checkersPieceItem.getStorageIndex() + BOARD_SIZE,
                    startX, startY + (a * 14), checkersPieceItem));
        }
        //The player inventory
        startY = 174;
        startX = 8;
        for (y = 0; y < 3; ++y) {
            for (x = 0; x < 9; ++x) {
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, startX + x * 18, startY + y * 18));
            }
        }
        //The player Hotbar
        startY = 232;
        for (y = 0; y < 9; ++y) {
            this.addSlot(new Slot(playerInventory, y, startX + y * 18, startY));
        }
    }

    @Override
    public ArrayList<AbstractGridSlot> getCurrentSlotActions(int origin) {
        ArrayList<AbstractGridSlot> result = new ArrayList<>();
        for (Slot slot : this.slots) {
            if (slot instanceof CheckersGridSlot s) {
                CheckersActionType type = CheckersActionType.fromId(this.slotHintPropertyDelegate.get(origin).get(slot.getIndex()));
                if (!type.shouldIgnore()) {
                    result.add(s);
                }
            }
        }
        return result;
    }

    @Override
    public CheckersBoardInventory getBoardInventory() {
        return this.boardInventory;
    }

    @Override
    public ActionType getActionTypeFromSlot(int origin, int slotId) {
        return CheckersActionType.fromId(getSlotHintPropertyDelegate().get(origin).get(slotId));
    }
}
