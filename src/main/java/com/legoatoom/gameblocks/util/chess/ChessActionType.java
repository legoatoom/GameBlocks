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
import java.util.List;
import java.util.Locale;

public enum ChessActionType {
    CAPTURE(0x80cc0000), CASTLE(0x803d85c6), EN_PASSANT(0x803d85c6), MOVE(0x803d85c6), PROMOTION(0x803d85c6), INITIAL_MOVE(0x80f1c232);

    private final int color;

    ChessActionType(int color) {
        this.color = color;
    }

    public List<Text> getInfo(TextRenderer renderer){
        ArrayList<Text> list = Lists.newArrayListWithExpectedSize(2);
        if (this == MOVE) return list;
        String name = this.name().toLowerCase(Locale.ROOT);
        Text title = new TranslatableText("game.chess.action.tooltip.title.%s".formatted(name)).setStyle(Style.EMPTY.withColor(this.color));
        var detail = renderer.getTextHandler().wrapLines(
                new TranslatableText("game.chess.action.tooltip.detail.%s".formatted(name)),
                150, Style.EMPTY);
        list.add(title);
        for (StringVisitable stringVisitable : detail) {
            list.add(new LiteralText(stringVisitable.getString()).setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
        }
        return list;
    }

    public int getColor() {
        return this.color;
    }
}
