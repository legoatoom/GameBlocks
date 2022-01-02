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
import com.legoatoom.gameblocks.GameBlocks;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public enum ChessActionType {
    CAPTURE(0, 0x80cc0000),
    CASTLE(1, 0x803d85c6),
    EN_PASSANT(2, 0x806aa84f),
    INITIAL_MOVE(3, 0x80f1c232),
    MOVE(4, 0x803d85c6),
    PROMOTION(5, 0x803d85c6),
    PROMOTION_CAPTURE(6, 0x803d85c6);

    private final int color;

    public final static Identifier SYNC_TYPE_PACKET_ID = GameBlocks.id("sync_type_packet");
    public final static String ACTION_NBT_KEY = GameBlocks.id("action_type").toString();
    private final int id;

    ChessActionType(int id, int color) {
        this.id = id;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public static ChessActionType fromId(int id){
        for (ChessActionType value : ChessActionType.values()) {
            if (value.id == id){
                return value;
            }
        }
        return null;
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

    public void sendNbtUpdate(int slotId) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(slotId);
        buf.writeInt(this.getId());
        ClientPlayNetworking.send(SYNC_TYPE_PACKET_ID, buf);
    }
}
