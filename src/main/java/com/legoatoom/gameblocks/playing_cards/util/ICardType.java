package com.legoatoom.gameblocks.playing_cards.util;

import net.minecraft.text.Text;

public interface ICardType {

    String getTranslationKey();

    Card asCard();
}
