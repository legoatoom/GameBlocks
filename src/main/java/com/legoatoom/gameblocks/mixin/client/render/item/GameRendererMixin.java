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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemRenderer.class)
@Environment(EnvType.CLIENT)
public class GameRendererMixin {


//    @Shadow @Final private ItemModels models;
//
//    @Shadow @Final private ItemColors colors;
//
//
//    @ModifyVariable(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
//            at = @At(value = "HEAD"), print = true, argsOnly = true)
//    private BakedModel editModelForCard(BakedModel model, ItemStack stack, ModelTransformation.Mode renderMode){
//        if (renderMode.isFirstPerson() && stack.getItem() instanceof CardItem item) {
//            return this.models.getModelManager().getModel(new ModelIdentifier("gameblocks:playing_cards/card_in_hand#inventory"));
//        }
//        return model;
//    }

}
