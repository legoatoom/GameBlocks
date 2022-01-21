package com.legoatoom.gameblocks.checkers.util;

import com.google.common.collect.Lists;
import com.legoatoom.gameblocks.common.util.ActionType;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public enum CheckersActionType implements ActionType {
    JUMP_UP_LEFT(1, 0x80cc0000, true),
    JUMP_UP_RIGHT(2, 0x80cc0000, true),
    JUMP_DOWN_LEFT(3, 0x80cc0000, true),
    JUMP_DOWN_RIGHT(4, 0x80cc0000, true),
    KINGS_ROW(5, 0x80a64d79, true),
    MOVE(6, 0x803d85c6, false),
    NONE,
    STONE_POTENTIAL(3);

    private final int color;
    private final int id;
    private final boolean hasToolTip;

    CheckersActionType() {
        this(-1, -1, false);
    }

    CheckersActionType(int id) {
        this(id, -1, false);
    }


    CheckersActionType(int id, int color, boolean hasTooltip) {
        this.id = id;
        this.color = color;
        this.hasToolTip = hasTooltip;
    }

    public static CheckersActionType fromId(int id) {
        for (CheckersActionType value : CheckersActionType.values()) {
            if (value.id == id) {
                return value;
            }
        }
        return NONE;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean shouldIgnore() {
        return this == NONE || this == STONE_POTENTIAL;
    }

    public int getId() {
        return id;
    }

    // Does this assume left -> right reading?
    public List<Text> getInfo(TextRenderer renderer) {
        ArrayList<Text> list = Lists.newArrayListWithExpectedSize(2);
        if (!hasToolTip) return list;
        Text title = new TranslatableText("game.checkers.action.tooltip.title.%s".formatted(toString()))
                .setStyle(Style.EMPTY.withColor(color));
        var detail = renderer.getTextHandler().wrapLines(
                new TranslatableText("game.checkers.action.tooltip.detail.%s".formatted(toString())),
                150, Style.EMPTY);
        list.add(title);
        for (StringVisitable stringVisitable : detail) {
            list.add(new LiteralText(stringVisitable.getString()).setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
        }
        return list;
    }

    @Override
    public String toString() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public int getColor() {
        return this.color;
    }
}
