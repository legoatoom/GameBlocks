package com.legoatoom.gameblocks.checkers.util;

import com.legoatoom.gameblocks.common.util.ActionType;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;

import java.util.List;

public enum CheckersActionType implements ActionType {
    NONE;

    @Override
    public boolean shouldIgnore() {
        return true;
    }

    @Override
    public int getColor() {
        return 0;
    }

    @Override
    public List<Text> getInfo(TextRenderer textRenderer) {
        return null;
    }

    @Override
    public int getId() {
        return 0;
    }
}
