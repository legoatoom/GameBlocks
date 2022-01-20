package com.legoatoom.gameblocks.checkers.items;

import com.legoatoom.gameblocks.common.screen.slot.AbstractGridSlot;
import org.jetbrains.annotations.NotNull;

import static com.legoatoom.gameblocks.checkers.util.CheckersPieceType.STONE;

// TODO: 12022-01-20 Change KingItem to just a model-predicate and just one Checkers Item
public class StoneItem extends ICheckersPieceItem {

    public StoneItem(boolean isBlack) {
        super(isBlack, STONE);
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
        // TODO: 12022-01-20 Fill in
    }
}
