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
import com.legoatoom.gameblocks.items.PiecesPackageItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public class CommonRegistry {

    public static Item PIECES_PACKAGE_ITEM = new PiecesPackageItem(new FabricItemSettings().group(GameBlocks.GAME_BLOCKS).maxCount(16));

    public static void register(){
        Registry.register(Registry.ITEM, GameBlocks.id("pieces_package"), PIECES_PACKAGE_ITEM);
    }
}
