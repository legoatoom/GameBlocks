package com.legoatoom.gameblocks.mixin.entity.player;

import com.legoatoom.gameblocks.playing_cards.client.CardRenderer;
import com.legoatoom.gameblocks.playing_cards.items.CardDeckItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {

    @Shadow @Final public PlayerEntity player;

    @Inject(method = "scrollInHotbar", at = @At("HEAD"), cancellable = true)
    public void scrollCard(double scrollAmount, CallbackInfo ci){
        if (player.isHolding(stack -> stack.getItem() instanceof CardDeckItem) && player.getPitch() >= 45.0f){
            CardRenderer.addCurrentSelected(scrollAmount >= 0 ? -1 : 1);
            ci.cancel();
        }
    }
}
