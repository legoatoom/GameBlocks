/*
 * Copyright (C) 2022 legoatoom
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.legoatoom.gameblocks.util.chess;

import com.google.common.collect.Lists;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ChessActionType {
    CAPTURE(1, 0x80cc0000),
    CASTLE(2, 0x8045818e),
    EN_PASSANT(3, 0x806aa84f),
    INITIAL_MOVE(4, 0x80f1c232),
    MOVE(5, 0x803d85c6),
    NONE,
    PAWN_POTENTIAL(8),
    PROMOTION(6, 0x80a64d79),
    PROMOTION_CAPTURE(7, 0x80B9273D) {
        @Override
        public List<Text> getInfo(TextRenderer renderer) {
            return Stream.of(CAPTURE.getInfo(renderer), PROMOTION.getInfo(renderer))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        }
    };

    private final int color;
    private final int id;

    ChessActionType() {
        this(-1, -1);
    }

    ChessActionType(int id) {
        this(id, -1);
    }


    ChessActionType(int id, int color) {
        this.id = id;
        this.color = color;
    }

    public static ChessActionType fromId(int id) {
        for (ChessActionType value : ChessActionType.values()) {
            if (value.id == id) {
                return value;
            }
        }
        return NONE;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean shouldIgnore() {
        return this == NONE || this == PAWN_POTENTIAL;
    }

    public int getId() {
        return id;
    }

    public List<Text> getInfo(TextRenderer renderer) {
        ArrayList<Text> list = Lists.newArrayListWithExpectedSize(2);
        if (this == MOVE) return list;
        Text title = new TranslatableText("game.chess.action.tooltip.title.%s".formatted(toString())).setStyle(Style.EMPTY.withColor(this.color));
        var detail = renderer.getTextHandler().wrapLines(
                new TranslatableText("game.chess.action.tooltip.detail.%s".formatted(toString())),
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
