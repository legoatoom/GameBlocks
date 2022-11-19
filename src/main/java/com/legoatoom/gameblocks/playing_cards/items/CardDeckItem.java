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

import com.legoatoom.gameblocks.GameBlocks;
import com.legoatoom.gameblocks.GameBlocksState;
import com.legoatoom.gameblocks.playing_cards.util.Card;
import com.legoatoom.gameblocks.registry.CardRegistry;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Pair;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Cards
 * First Card in List is Top
 * Last Card in list is Bottom
 */
public class CardDeckItem extends Item {

    private static final String CARDS_KEY = GameBlocks.MOD_ID + ":Cards";

    public CardDeckItem(Settings settings) {
        // TODO: 19/11/2022 Default deck somehow?
        super(settings);
    }

    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        if (clickType != ClickType.RIGHT) {
            // Placing it in a slot.
            return false;
        }
        ItemStack otherStack = slot.getStack();

        if (otherStack.isEmpty()) {
            List<Card> cards = CardDeckItem.getCards(stack);
            if (cards.size() == 1){
                // If you have only one card, and place it on a empty slot.
                // Then normal stuff should happen.
                return false;
            }
            CardDeckItem.removeBottom(stack).ifPresent(slot::insertStack);

        } else if (otherStack.getItem() instanceof CardDeckItem) {
            ItemStack result = CardDeckItem.putBottom(stack, otherStack);

            slot.setStack(result);
        }
        return true;
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (clickType == ClickType.LEFT) {
            if (!(otherStack.getItem() instanceof CardDeckItem)) {
                return false;
            }
            NbtCompound compound = stack.getOrCreateNbt();
            NbtCompound compound2 = otherStack.getOrCreateNbt();
            NbtList cards = compound.getList(CARDS_KEY, NbtElement.COMPOUND_TYPE);
            NbtList cards2 = compound2.getList(CARDS_KEY, NbtElement.COMPOUND_TYPE);
            cards2.addAll(cards);
            compound.put(CARDS_KEY, cards2);
            cursorStackReference.set(ItemStack.EMPTY);
            return true;
        }
        if (otherStack.isEmpty()) {
            CardDeckItem.removeTop(stack).ifPresent(cursorStackReference::set);
            if (CardDeckItem.isEmpty(stack)){
                slot.setStack(ItemStack.EMPTY);
            }
        }
        // No need to check other way round since it has.
        return true;
    }

    private static boolean isEmpty(ItemStack stack) {
        if (!stack.hasNbt()){
            return false;
        }
        NbtCompound nbtCompound = stack.getNbt();
        if (nbtCompound == null || !nbtCompound.contains(CARDS_KEY)) {
            return false;
        }
        NbtList cards = nbtCompound.getList(CARDS_KEY, NbtElement.COMPOUND_TYPE);
        return cards.isEmpty();
    }


    public static Pair<ItemStack, ItemStack> split() {
        // TODO: 19/11/2022 Dragging and evenly splitting a deck?
        return null;
    }
    public static ItemStack putBottom(ItemStack deck, ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof CardDeckItem)) {
            return stack;
        }
        NbtCompound nbtCompound = deck.getOrCreateNbt();
        if (!nbtCompound.contains(CARDS_KEY)) {
            GameBlocksState.warn("A deck without any card NBT was located.");
            nbtCompound.put(CARDS_KEY, new NbtList());
        }
        NbtList deckNbtList = nbtCompound.getList(CARDS_KEY, NbtElement.COMPOUND_TYPE);

        CardDeckItem.removeTop(stack).ifPresent(resultStack -> {
            NbtCompound cardCompound = resultStack.getOrCreateNbt();
            NbtList cards = cardCompound.getList(CARDS_KEY, NbtElement.COMPOUND_TYPE);
            NbtCompound card = cards.getCompound(0);
            deckNbtList.add(card);
        });
        if (CardDeckItem.isEmpty(stack)){
            return ItemStack.EMPTY;
        }
        return stack;
    }

    private static List<Card> getCards(ItemStack deck) {
        NbtCompound nbtCompound = deck.getNbt();
        if (nbtCompound == null) {
            return List.of();
        }
        NbtList nbtList = nbtCompound.getList(CARDS_KEY, NbtElement.COMPOUND_TYPE);
        return nbtList.stream().map(NbtCompound.class::cast).map(Card::fromNbt).toList();
    }

    public static Optional<ItemStack> removeTop(ItemStack deck) {
        return remove(deck, true);
    }

    private static Optional<ItemStack> remove(ItemStack deck, Boolean isTop){
        NbtCompound nbtCompound = deck.getOrCreateNbt();
        if (!nbtCompound.contains(CARDS_KEY)) {
            GameBlocksState.warn("A deck without any card NBT was located.");
            nbtCompound.put(CARDS_KEY, new NbtList());
        }
        NbtList nbtList = nbtCompound.getList(CARDS_KEY, NbtElement.COMPOUND_TYPE);
        if (nbtList.isEmpty()){
            return Optional.empty();
        }
        NbtCompound cardCompound;

        if (isTop){
            cardCompound= nbtList.getCompound(0);
            nbtList.remove(0);
        } else {
            cardCompound = nbtList.getCompound(nbtList.size() - 1);
            nbtList.remove(nbtList.size()-1);
        }



        ItemStack result_deck = new ItemStack(CardRegistry.CARD_DECK);
        NbtList newList = new NbtList();
        newList.add(cardCompound);
        result_deck.setSubNbt(CARDS_KEY, newList);
        return Optional.of(result_deck);
    }
    public static Optional<ItemStack> removeBottom(ItemStack deck) {
        return remove(deck, false);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        // TODO: 19/11/2022 REMOVE THIS CODE
        if (!stack.hasNbt()){
            NbtList newList = new NbtList();
            NbtCompound card = new NbtCompound();

            card.putString("Name", "gameblocks:temp_card_"+Random.create().nextInt(200));
            newList.add(card);
            stack.setSubNbt(CARDS_KEY, newList);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        List<Card> cards = CardDeckItem.getCards(stack);
        for (Card card : cards) {
            tooltip.add(Text.literal(card.name().toString()));
        }
    }
}
