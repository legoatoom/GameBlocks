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

import com.legoatoom.gameblocks.checkers.client.screen.CheckersBoardScreen;
import com.legoatoom.gameblocks.checkers.items.CheckersStoneItem;
import com.legoatoom.gameblocks.chess.client.screen.ChessBoardScreen;
import com.legoatoom.gameblocks.registry.CheckersRegistry;
import com.legoatoom.gameblocks.registry.ChessRegistry;
import io.wispforest.owo.ui.event.MouseScroll;
import io.wispforest.owo.ui.event.UIEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public final class GameBlocksClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HandledScreens.register(ChessRegistry.CHESS_BOARD_SCREEN_HANDLER, ChessBoardScreen::new);
        HandledScreens.register(CheckersRegistry.CHECKERS_BOARD_SCREEN_HANDLER, CheckersBoardScreen::new);

        registerKingedModelPredicate(CheckersRegistry.BLACK_STONE);
        registerKingedModelPredicate(CheckersRegistry.WHITE_STONE);
    }

    private void registerKingedModelPredicate(CheckersStoneItem whiteStone) {
        ModelPredicateProviderRegistry.register(whiteStone, GameBlocks.id("kinged"),
                (ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity, int i) ->
                        CheckersStoneItem.isKinged(itemStack) ? 1.F : 0.F);
    }
}
