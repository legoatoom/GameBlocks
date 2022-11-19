package com.legoatoom.gameblocks.playing_cards.util;

import com.legoatoom.gameblocks.GameBlocks;
import com.legoatoom.gameblocks.GameBlocksState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public record Card(Identifier name) {

    public static final Card EMPTY = new Card(GameBlocks.id("missing-card"));

    public Card(NbtCompound nbt) {
        this(new Identifier(nbt.getString("Name")));
    }
    public Card(String identifier) {
        this(GameBlocks.id(identifier));
    }

    public static Card fromNbt(NbtCompound nbtCompound){
        try{
            return new Card(nbtCompound);
        } catch (RuntimeException runtimeException) {
            GameBlocksState.LOGGER.debug("Tried to load invalid card: {}" + nbtCompound.toString());
            return EMPTY;
        }
    }
}
