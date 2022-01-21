package com.legoatoom.gameblocks.checkers.screen.slot;

import com.legoatoom.gameblocks.GameBlocks;
import com.legoatoom.gameblocks.checkers.inventory.CheckersBoardInventory;
import com.legoatoom.gameblocks.checkers.items.CheckersStoneItem;
import com.legoatoom.gameblocks.common.screen.slot.AbstractBoardSlot;
import net.minecraft.item.ItemStack;

public class CheckersStorageBoardSlot extends AbstractBoardSlot {

    public final Class<? extends CheckersStoneItem> storeType;
    private final boolean isBlack;

    public CheckersStorageBoardSlot(CheckersBoardInventory inventory, int index, int x, int y, CheckersStoneItem item) {
        super(inventory, index, x, y);
        this.storeType = item.getClass();
        this.isBlack = item.isBlack();
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        if (stack.isEmpty() || !this.storeType.isInstance(stack.getItem())) return false;

        CheckersStoneItem item = (CheckersStoneItem) stack.getItem().asItem();
        return this.isBlack == item.isBlack() && super.canInsert(stack);
    }

    @Override
    public ItemStack insertStack(ItemStack stack, int count) {
        stack.removeSubNbt(GameBlocks.MOD_ID); // Clear NBT_DATA when storing
        return super.insertStack(stack, count);
    }

    @Override
    public int getSlotHighLighterSize() {
        return 12;
    }

    @Override
    public CheckersBoardInventory getInventory() {
        return ((CheckersBoardInventory) this.inventory);
    }
}
