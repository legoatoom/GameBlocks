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

package com.legoatoom.gameblocks.mixin.client.render.item;

import com.legoatoom.gameblocks.playing_cards.client.CardRenderer;
import com.legoatoom.gameblocks.playing_cards.items.CardItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class CardItemHoldingRenderer {

    @Shadow @Final private MinecraftClient client;

    @Shadow protected abstract void renderArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Arm arm);

    @Shadow protected abstract float getMapAngle(float tickDelta);

    @Shadow private ItemStack offHand;



    @Inject(method = "renderFirstPersonItem", at = @At("HEAD"), cancellable = true)
    private void renderFirstPersonItemInject(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci){
        if (item.getItem() instanceof CardItem){
            boolean bl = hand == Hand.MAIN_HAND;
            Arm arm = bl ? player.getMainArm() : player.getMainArm().getOpposite();
            if (bl && this.offHand.isEmpty()) {
                matrices.push();
                CardRenderer.renderCardInBothHand((HeldItemRenderer)((Object) this), matrices, vertexConsumers, light, pitch, equipProgress, swingProgress, item);
                ci.cancel();
                matrices.pop();
            }
        }
    }



}
