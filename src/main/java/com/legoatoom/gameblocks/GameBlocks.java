/*
 * Copyright (C) 2021 legoatoom
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

package com.legoatoom.gameblocks;

import com.legoatoom.gameblocks.registry.CheckersRegistry;
import com.legoatoom.gameblocks.registry.ChessRegistry;
import com.legoatoom.gameblocks.registry.CommonRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import static com.legoatoom.gameblocks.registry.ChessRegistry.BLACK_PAWN;

/**
 * @author legoatoom
 */
public final class GameBlocks implements ModInitializer {
    /**
     * The ID of this mod.
     */

    public static final String MOD_ID = "gameblocks";
    public static final Version version = FabricLoader.getInstance().getModContainer(MOD_ID).get().getMetadata().getVersion();
    public static ItemGroup GAME_BLOCKS = FabricItemGroupBuilder.build(id("main"), () -> new ItemStack(BLACK_PAWN));

    public static @NotNull Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        GameBlocksState.env = FabricLoader.getInstance().getEnvironmentType();
        GameBlocksState.isDev = FabricLoader.getInstance().isDevelopmentEnvironment();
        CommonRegistry.register();
        ChessRegistry.register();
        CheckersRegistry.register();
    }
}
