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

package com.legoatoom.gameblocks.registry;

import com.legoatoom.gameblocks.GameBlocks;
import com.legoatoom.gameblocks.GameBlocksState;
import com.legoatoom.gameblocks.playing_cards.items.CardItem;
import com.legoatoom.gameblocks.playing_cards.util.CardType;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;

import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public class CardRegistry {

    public static Item DIAMOND_ACE = new CardItem(new FabricItemSettings().group(GameBlocks.GAME_BLOCKS).maxCount(1), CardType.D_ACE);

    public static void register() {
        GameBlocksState.info("Registering Cards");
        Registry.register(Registry.ITEM, GameBlocks.id("playing_cards/diamond_ace"), DIAMOND_ACE);

    }
}
