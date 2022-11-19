package com.legoatoom.gameblocks.checkers.items;

import com.legoatoom.gameblocks.GameBlocks;
import com.legoatoom.gameblocks.checkers.util.CheckersActionType;
import com.legoatoom.gameblocks.common.items.IPieceItem;
import com.legoatoom.gameblocks.common.screen.slot.AbstractGridSlot;
import com.legoatoom.gameblocks.common.util.ActionType;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

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
            calculateLegalActionsKing(slot);
        } else {
            calculateLegalActionsStone(slot);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (isKinged(stack)){
            tooltip.add(Text.translatable("game.checkers.tooltip.kinged").formatted(Formatting.GRAY));
        }
    }

    private void calculateLegalActionsStone(AbstractGridSlot slot) {
        // TODO Settings for American Checkers (Cannot jump backwards)
        checkerJumpCheck(slot, abstractGridSlot -> abstractGridSlot.upLeft(isBlack()), CheckersActionType.JUMP_UP_LEFT_KINGS_ROW, CheckersActionType.JUMP_UP_LEFT, true);
        checkerJumpCheck(slot, abstractGridSlot -> abstractGridSlot.upRight(isBlack()), CheckersActionType.JUMP_UP_RIGHT_KINGS_ROW, CheckersActionType.JUMP_UP_RIGHT, true);
        checkerJumpCheck(slot, abstractGridSlot -> abstractGridSlot.downLeft(isBlack()), CheckersActionType.JUMP_DOWN_LEFT_KINGS_ROW, CheckersActionType.JUMP_DOWN_LEFT, false);
        checkerJumpCheck(slot, abstractGridSlot -> abstractGridSlot.downRight(isBlack()), CheckersActionType.JUMP_DOWN_RIGHT_KINGS_ROW, CheckersActionType.JUMP_DOWN_RIGHT, false);
    }

    private void calculateLegalActionsKing(AbstractGridSlot slot) {
        // TODO Settings for American Checkers (Kings same as stone but also backwards)
        checkerFlyingKingJumpCheck(slot, abstractGridSlot -> abstractGridSlot.upLeft(isBlack()), CheckersActionType.JUMP_UP_LEFT);
        checkerFlyingKingJumpCheck(slot, abstractGridSlot -> abstractGridSlot.upRight(isBlack()), CheckersActionType.JUMP_UP_RIGHT);
        checkerFlyingKingJumpCheck(slot, abstractGridSlot -> abstractGridSlot.downLeft(isBlack()), CheckersActionType.JUMP_DOWN_LEFT);
        checkerFlyingKingJumpCheck(slot, abstractGridSlot -> abstractGridSlot.downRight(isBlack()), CheckersActionType.JUMP_DOWN_RIGHT);
    }

    private void checkerFlyingKingJumpCheck(AbstractGridSlot slot, Function<AbstractGridSlot, Optional<AbstractGridSlot>> function, CheckersActionType JUMP){
        var current = slot;
        boolean found = false;
        while(true) {
            Optional<AbstractGridSlot> x = function.apply(current);
            if (x.isPresent()) {
                current = x.get();
                Item item = current.getStack().getItem();
                if (item instanceof CheckersStoneItem checkersStoneItem) {
                    if (checkersStoneItem.isBlack() != isBlack()) {
                        if (found) {
                            // already found an enemy.
                            break;
                        }

                        found = true;
                        continue;
                    } else {
                        // found our own type;
                        break;
                    }
                }
                // kinged pieces cannot get kinged again.
                current.setHoverHintForOriginIndex(slot.getIndex(), found ? JUMP : CheckersActionType.MOVE);
                //continue
            } else {
                break;
            }
        }
    }

    private void checkerJumpCheck(AbstractGridSlot slot, Function<AbstractGridSlot, Optional<AbstractGridSlot>> function, CheckersActionType KING, CheckersActionType JUMP, boolean canMove){
        function.apply(slot).ifPresent(slot1 -> {
            if (slot1.getStack().getItem() instanceof CheckersStoneItem checkersStoneItem) {
                if (checkersStoneItem.isBlack() != isBlack()) {
                    // Check for jump
                    //TODO American checkers cannot do this backwards
                    function.apply(slot1).ifPresent(slot2 -> {
                        if (slot2.getStack().getItem() instanceof CheckersStoneItem) return;
                        slot2.setHoverHintForOriginIndex(slot.getIndex(),
                                isKingRow(slot2.getBoardYLoc()) ? KING : JUMP);
                    });
                }
            } else if (canMove){
                // Otherwise move
                slot1.setHoverHintForOriginIndex(slot.getIndex(),
                        isKingRow(slot1.getBoardYLoc()) ? CheckersActionType.MOVE_KINGS_ROW : CheckersActionType.MOVE);
            }
        });
    }


    @Override
    public void handleAction(ScreenHandler handler, AbstractGridSlot slot, ItemStack cursorStack, ActionType actionType) {
        ItemStack current = slot.getStack();
        if (isKinged(current)){
            handleKingAction(handler, slot, (CheckersActionType) actionType);
        } else {
            handleStoneAction(slot, current, (CheckersActionType) actionType);
        }
    }

    public static boolean isKinged(ItemStack current) {
        var s = current.getSubNbt(GameBlocks.MOD_ID);
        return (s != null && s.contains("kinged"));
    }

    private boolean isKingRow(int yLoc){
        return (isBlack()) ? yLoc == 9 : yLoc == 0;
    }

    private void handleKingAction(ScreenHandler handler, AbstractGridSlot slot, CheckersActionType actionType) {
        switch (actionType) {
            case JUMP_UP_LEFT -> {
                var current = slot;
                while (true){
                    var s = current.downRight(isBlack());
                    if (s.isEmpty()) break;

                    current = s.get();
                    if (current.getStack().getItem() instanceof CheckersStoneItem item && item.isBlack() != isBlack()) {
                        current.captureMe();
                        break;
                    }
                }
            }
            case JUMP_UP_RIGHT -> {
                var current = slot;
                while (true){
                    var s = current.downLeft(isBlack());
                    if (s.isEmpty()) break;

                    current = s.get();
                    if (current.getStack().getItem() instanceof CheckersStoneItem item && item.isBlack() != isBlack()) {
                        current.captureMe();
                        break;
                    }
                }
            }
            case JUMP_DOWN_LEFT -> {
                var current = slot;
                while (true){
                    var s = current.upRight(isBlack());
                    if (s.isEmpty()) break;

                    current = s.get();
                    if (current.getStack().getItem() instanceof CheckersStoneItem item && item.isBlack() != isBlack()) {
                        current.captureMe();
                        break;
                    }
                }
            }
            case JUMP_DOWN_RIGHT -> {
                var current = slot;
                while (true){
                    var s = current.upLeft(isBlack());
                    if (s.isEmpty()) break;

                    current = s.get();
                    if (current.getStack().getItem() instanceof CheckersStoneItem item && item.isBlack() != isBlack()) {
                        current.captureMe();
                        break;
                    }
                }
            }
        }
    }

    private void handleStoneAction(AbstractGridSlot slot, ItemStack current, CheckersActionType actionType) {
        switch (actionType) {
            case JUMP_UP_LEFT, JUMP_UP_LEFT_KINGS_ROW -> slot.downRight(isBlack()).ifPresent(abstractGridSlot -> {
                if (abstractGridSlot.getStack().getItem() instanceof CheckersStoneItem item && item.isBlack() != isBlack()) {
                    abstractGridSlot.captureMe();
                }
            });
            case JUMP_DOWN_LEFT, JUMP_DOWN_LEFT_KINGS_ROW -> slot.upRight(isBlack()).ifPresent(abstractGridSlot -> {
                if (abstractGridSlot.getStack().getItem() instanceof CheckersStoneItem item && item.isBlack() != isBlack()) {
                    abstractGridSlot.captureMe();
                }
            });
            case JUMP_UP_RIGHT, JUMP_UP_RIGHT_KINGS_ROW -> slot.downLeft(isBlack()).ifPresent(abstractGridSlot -> {
                if (abstractGridSlot.getStack().getItem() instanceof CheckersStoneItem item && item.isBlack() != isBlack()) {
                    abstractGridSlot.captureMe();
                }
            });
            case JUMP_DOWN_RIGHT, JUMP_DOWN_RIGHT_KINGS_ROW -> slot.upLeft(isBlack()).ifPresent(abstractGridSlot -> {
                if (abstractGridSlot.getStack().getItem() instanceof CheckersStoneItem item && item.isBlack() != isBlack()) {
                    abstractGridSlot.captureMe();
                }
            });
        }
        if (actionType.isKinged()){
            var s = current.getOrCreateSubNbt(GameBlocks.MOD_ID);
            s.putBoolean("kinged", true);
        }
    }

    @Override
    public boolean isBlack() {
        return isBlack;
    }
}
