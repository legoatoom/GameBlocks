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

package com.legoatoom.gameblocks.playing_cards.items;

import com.legoatoom.gameblocks.playing_cards.util.ICardType;
import net.minecraft.item.Item;

public class CardItem extends Item {
    private final ICardType type;

    public CardItem(Settings settings, ICardType type) {
        super(settings);
        this.type =type;
    }





    public ICardType getType() {
        return type;
    }
}
