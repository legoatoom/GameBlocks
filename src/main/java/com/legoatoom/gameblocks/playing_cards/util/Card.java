package com.legoatoom.gameblocks.playing_cards.util;

import com.legoatoom.gameblocks.GameBlocksState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public record Card(String key, Identifier frontTexture, Identifier backTexture) {

    public static final Card EMPTY = new Card("missing", null, null);

    public Card(NbtCompound nbt) {
        this(nbt.getString("Name"), new Identifier(nbt.getString("FaceTexture")), new Identifier(nbt.getString("BackTexture")));
    }

    public Identifier getFaceTexture(){
        return frontTexture;
    }

    public Identifier getBackTexture(){
//        return type.getTexture();
        return backTexture;
    }

    public static Card fromNbt(NbtCompound nbtCompound) {
        try {
            return new Card(nbtCompound);
        } catch (RuntimeException runtimeException) {
            GameBlocksState.LOGGER.debug("Tried to load invalid card: {}" + nbtCompound.toString());
            return EMPTY;
        }
    }

    public void writeNbt(NbtCompound compound) {
        compound.putString("Name", key);
        compound.putString("FaceTexture", getFaceTexture().toString());
        compound.putString("BackTexture", getBackTexture().toString());
    }
}
