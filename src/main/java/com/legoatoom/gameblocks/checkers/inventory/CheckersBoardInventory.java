package com.legoatoom.gameblocks.checkers.inventory;

import com.legoatoom.gameblocks.common.inventory.AbstractBoardInventory;
import net.minecraft.screen.ArrayPropertyDelegate;

import java.util.ArrayList;

public class CheckersBoardInventory extends AbstractBoardInventory {

    public CheckersBoardInventory() {
        this(true);
    }

    public CheckersBoardInventory(boolean isClient) {
        super(isClient, 10, 2);
    }

    @Override
    public ArrayList<ArrayPropertyDelegate> getSlotHintsPropertyDelegates() {
        return new ArrayList<>();
    }

    @Override
    public void markDirty() {

    }
}
