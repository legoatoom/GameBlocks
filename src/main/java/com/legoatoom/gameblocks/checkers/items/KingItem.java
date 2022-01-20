package com.legoatoom.gameblocks.checkers.items;

import com.legoatoom.gameblocks.GameBlocksState;
import net.minecraft.item.ItemStack;

import static com.legoatoom.gameblocks.checkers.util.CheckersPieceType.KING;
import static com.legoatoom.gameblocks.registry.CheckersRegistry.BLACK_STONE;
import static com.legoatoom.gameblocks.registry.CheckersRegistry.WHITE_STONE;

public class KingItem extends ICheckersPieceItem {

    public KingItem(boolean isBlack) {
        super(isBlack, KING);
    }

    @Override
    public int getStorageIndex() {
        GameBlocksState.error("Unexpected call to checkers KingItem storage index, it doesn't have one");
        return -1;
    }

    @Override
    public boolean isDefaultLocation(int x, int y) {
        return false;
    }

    @Override
    public ItemStack defaultState(ItemStack stack) {
        return new ItemStack(isBlack() ? BLACK_STONE : WHITE_STONE, stack.getCount());
    }
}
