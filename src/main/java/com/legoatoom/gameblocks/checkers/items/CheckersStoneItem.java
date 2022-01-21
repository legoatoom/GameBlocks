package com.legoatoom.gameblocks.checkers.items;

import com.legoatoom.gameblocks.GameBlocks;
import com.legoatoom.gameblocks.checkers.util.CheckersActionType;
import com.legoatoom.gameblocks.common.items.IPieceItem;
import com.legoatoom.gameblocks.common.screen.slot.AbstractGridSlot;
import com.legoatoom.gameblocks.common.util.ActionType;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import org.jetbrains.annotations.NotNull;

import static com.legoatoom.gameblocks.GameBlocks.GAME_BLOCKS;

public class CheckersStoneItem extends IPieceItem {

    private final boolean isBlack;

    public CheckersStoneItem(boolean isBlack) {
        super(new FabricItemSettings().group(GAME_BLOCKS).maxCount(20));
        this.isBlack = isBlack;
    }

    @Override
    public int getStorageIndex() {
        return isBlack() ? 1 : 0;
    }

    @Override
    public boolean isDefaultLocation(int x, int y) {
        return (isBlack() ? y <= 3 : y >= 6) && (x + y) % 2 == 1;
    }

    @Override
    public void calculateLegalActions(@NotNull AbstractGridSlot slot) {
        ItemStack current = slot.getStack();
        if (isKinged(current)){
            calculateLegalActionsKing(slot, current);
        } else {
            calculateLegalActionsStone(slot, current);
        }
    }

    private void calculateLegalActionsStone(AbstractGridSlot slot, ItemStack current) {
        // TODO Settings for American Checkers (Cannot move backwards)
        slot.upLeft(isBlack()).ifPresent(slot1 -> {
            if (slot1.getStack().getItem() instanceof CheckersStoneItem checkersStoneItem) {
                if (checkersStoneItem.isBlack() != isBlack()) {
                    // Check for jump
                    slot1.upLeft(isBlack()).ifPresent(slot2 -> {
                        if (slot2.getStack().getItem() instanceof CheckersStoneItem) return;
                        slot2.setHoverHintForOriginIndex(slot.getIndex(), CheckersActionType.JUMP_UP_LEFT);
                    });
                }
            } else {
                // Otherwise move
                slot1.setHoverHintForOriginIndex(slot.getIndex(), CheckersActionType.MOVE);
            }
        });
        slot.upRight(isBlack()).ifPresent(slot1 -> {
            if (slot1.getStack().getItem() instanceof CheckersStoneItem checkersStoneItem) {
                if (checkersStoneItem.isBlack() != isBlack()) {
                    // Check for jump
                    slot1.upRight(isBlack()).ifPresent(slot2 -> {
                        if (slot2.getStack().getItem() instanceof CheckersStoneItem) return;
                        slot2.setHoverHintForOriginIndex(slot.getIndex(), CheckersActionType.JUMP_UP_RIGHT);
                    });
                }
            } else {
                // Otherwise move
                slot1.setHoverHintForOriginIndex(slot.getIndex(), CheckersActionType.MOVE);
            }
        });
        slot.downLeft(isBlack()).ifPresent(slot1 -> {
            if (slot1.getStack().getItem() instanceof CheckersStoneItem checkersStoneItem) {
                if (checkersStoneItem.isBlack() != isBlack()) {
                    // Check for jump
                    slot1.downLeft(isBlack()).ifPresent(slot2 -> {
                        if (slot2.getStack().getItem() instanceof CheckersStoneItem) return;
                        slot2.setHoverHintForOriginIndex(slot.getIndex(), CheckersActionType.JUMP_DOWN_LEFT);
                    });
                }
            } else {
                // Otherwise move
                slot1.setHoverHintForOriginIndex(slot.getIndex(), CheckersActionType.MOVE);
            }
        });
        slot.downRight(isBlack()).ifPresent(slot1 -> {
            if (slot1.getStack().getItem() instanceof CheckersStoneItem checkersStoneItem) {
                if (checkersStoneItem.isBlack() != isBlack()) {
                    // Check for jump
                    slot1.downRight(isBlack()).ifPresent(slot2 -> {
                        if (slot2.getStack().getItem() instanceof CheckersStoneItem) return;
                        slot2.setHoverHintForOriginIndex(slot.getIndex(), CheckersActionType.JUMP_DOWN_RIGHT);
                    });
                }
            } else {
                // Otherwise move
                slot1.setHoverHintForOriginIndex(slot.getIndex(), CheckersActionType.MOVE);
            }
        });
    }

    private void calculateLegalActionsKing(AbstractGridSlot slot, ItemStack current) {
        // TODO Settings for American Checkers (Kings same as stone but also backwards)
    }


    @Override
    public void handleAction(ScreenHandler handler, AbstractGridSlot slot, ItemStack cursorStack, ActionType actionType) {
        ItemStack current = slot.getStack();
        if (isKinged(current)){
            handleKingAction(handler, slot, current, cursorStack, actionType);
        } else {
            handleStoneAction(handler, slot, current, cursorStack, actionType);
        }
    }

    public static boolean isKinged(ItemStack current) {
        var s = current.getSubNbt(GameBlocks.MOD_ID);
        return (s != null && s.contains("kinged"));
    }

    private void handleKingAction(ScreenHandler handler, AbstractGridSlot slot, ItemStack current, ItemStack cursorStack, ActionType actionType) {
    }

    private void handleStoneAction(ScreenHandler handler, AbstractGridSlot slot, ItemStack current, ItemStack cursorStack, ActionType actionType) {
    }

    @Override
    public boolean isBlack() {
        return isBlack;
    }
}
