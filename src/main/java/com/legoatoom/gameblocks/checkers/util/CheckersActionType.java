package com.legoatoom.gameblocks.checkers.util;

import com.google.common.collect.Lists;
import com.legoatoom.gameblocks.common.util.ActionType;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum CheckersActionType implements ActionType {
    JUMP_UP_LEFT(1, 0x80cc0000, true, "jump"),
    JUMP_UP_RIGHT(2, 0x80cc0000, true, "jump"),
    JUMP_DOWN_LEFT(3, 0x80cc0000, true, "jump"),
    JUMP_DOWN_RIGHT(4, 0x80cc0000, true, "jump"),
    JUMP_UP_LEFT_KINGS_ROW(5, 0x80B9273D, true){
        @Override
        public List<Text> getInfo(TextRenderer renderer) {
            return comboInfo(renderer);
        }
    },
    JUMP_UP_RIGHT_KINGS_ROW(6, 0x80B9273D, true){
        @Override
        public List<Text> getInfo(TextRenderer renderer) {
            return comboInfo(renderer);
        }
    },
    JUMP_DOWN_LEFT_KINGS_ROW(7, 0x80B9273D, true){
        @Override
        public List<Text> getInfo(TextRenderer renderer) {
            return comboInfo(renderer);
        }
    },
    JUMP_DOWN_RIGHT_KINGS_ROW(8, 0x80B9273D, true){
        @Override
        public List<Text> getInfo(TextRenderer renderer) {
            return comboInfo(renderer);
        }
    },
    MOVE_KINGS_ROW(9, 0x80a64d79, true, "king"),
    MOVE(10, 0x803d85c6),
    NONE,
    STONE_POTENTIAL(11);

    CheckersActionType(int i, int color, boolean hasToolTip) {
        this(i, color, hasToolTip, "");
    }

    public boolean isKinged() {
        return this == MOVE_KINGS_ROW || this == JUMP_UP_LEFT_KINGS_ROW || this == JUMP_UP_RIGHT_KINGS_ROW || this == JUMP_DOWN_LEFT_KINGS_ROW || this == JUMP_DOWN_RIGHT_KINGS_ROW;
    }

    @NotNull
    protected List<Text> comboInfo(TextRenderer renderer) {
        return Stream.of(JUMP_UP_LEFT.getInfo(renderer), MOVE_KINGS_ROW.getInfo(renderer))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private final int color;
    private final int id;
    private final boolean hasToolTip;
    private final String hintName;

    CheckersActionType() {
        this(-1, -1, false, "");
    }

    CheckersActionType(int id) {
        this(id, -1, false,"");
    }


    CheckersActionType(int id, int color, boolean hasTooltip, String hintName) {
        this.id = id;
        this.color = color;
        this.hasToolTip = hasTooltip;
        this.hintName = hintName;
    }

    CheckersActionType(int i, int color) {
        this(i, color, false, "");
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
        Text title = new TranslatableText("game.checkers.action.tooltip.title.%s".formatted(hintName))
                .setStyle(Style.EMPTY.withColor(color));
        var detail = renderer.getTextHandler().wrapLines(
                new TranslatableText("game.checkers.action.tooltip.detail.%s".formatted(hintName)),
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
