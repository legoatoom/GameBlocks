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

package com.legoatoom.gameblocks.playing_cards.util;

import com.legoatoom.gameblocks.GameBlocks;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static com.legoatoom.gameblocks.playing_cards.util.StandardDeck.Suit.*;

public enum StandardDeck implements ICardType{
    H_ACE(H, 1),
    C_ACE(C, 1),
    S_ACE(S, 1),
    D_ACE(D, 1),
    H_2(H, 2),
    C_2(C, 2),
    S_2(S, 2),
    D_2(D, 2),
    H_3(H, 3),
    C_3(C, 3),
    S_3(S, 3),
    D_3(D, 3),
    H_4(H, 4),
    C_4(C, 4),
    S_4(S, 4),
    D_4(D, 4),
    H_5(H, 5),
    C_5(C, 5),
    S_5(S, 5),
    D_5(D, 5),
    H_6(H, 6),
    C_6(C, 6),
    S_6(S, 6),
    D_6(D, 6),
    H_7(H, 7),
    C_7(C, 7),
    S_7(S, 7),
    D_7(D, 7),
    H_8(H, 8),
    C_8(C, 8),
    S_8(S, 8),
    D_8(D, 8),
    H_9(H, 9),
    C_9(C, 9),
    S_9(S, 9),
    D_9(D, 9),
    H_10(H, 10),
    C_10(C, 10),
    S_10(S, 10),
    D_10(D, 10),
    H_11(H, 11),
    C_11(C, 11),
    S_11(S, 11),
    D_11(D, 11),
    H_12(H, 12),
    C_12(C, 12),
    S_12(S, 12),
    D_12(D, 12),
    H_13(H, 13),
    C_13(C, 13),
    S_13(S, 13),
    D_13(D, 13);

    private final int rank;
    private final Suit suit;

    StandardDeck(Suit suit, int rank){
        this.suit = suit;
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public Identifier asID() {
        String rankName = switch (rank){
            case 1 -> "ace";
            case 11 -> "jack";
            case 12 -> "queen";
            case 13 -> "king";
            default -> String.valueOf(rank);
        };
        return GameBlocks.id("item/playing_cards/%s_%s".formatted(suit.asString(), rankName));
    }

    @Override
    public Text toText() {
        return new LiteralText("Temporary Text");
    }

    enum Suit{
        H("heart"), C("club"), S("spade"), D("diamond");

        private final String name;

        Suit(String heart) {
            this.name = heart;
        }

        public String asString(){
            return name;
        }
    }
}
