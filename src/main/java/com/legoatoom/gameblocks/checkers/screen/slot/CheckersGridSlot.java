package com.legoatoom.gameblocks.checkers.screen.slot;

import com.legoatoom.gameblocks.checkers.inventory.CheckersBoardInventory;
import com.legoatoom.gameblocks.checkers.items.CheckersStoneItem;
import com.legoatoom.gameblocks.common.inventory.AbstractBoardInventory;
import com.legoatoom.gameblocks.common.screen.slot.AbstractGridSlot;
import net.minecraft.item.ItemStack;

import java.util.Optional;

public class CheckersGridSlot extends AbstractGridSlot {
    public CheckersGridSlot(AbstractBoardInventory inventory, int boardXLoc, int boardYLoc, int screenXLoc, int screenYLoc) {
        super(inventory, boardXLoc, boardYLoc, screenXLoc, screenYLoc);
    }

    @Override
    public int getSlotHighLighterSize() {
        return 12;
    }


    @Override
    public boolean canInsert(ItemStack stack) {
        return stack.isEmpty() || (stack.getItem().asItem() instanceof CheckersStoneItem && stack.getCount() == getMaxItemCount());
    }


    @SuppressWarnings("unchecked")
    @Override
    public Optional<CheckersStoneItem> getItem() {
        return (Optional<CheckersStoneItem>) super.getItem();
    }

    @Override
    public CheckersBoardInventory getInventory() {
        return ((CheckersBoardInventory) super.getInventory());
    }
}
