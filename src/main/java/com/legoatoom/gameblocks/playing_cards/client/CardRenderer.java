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

package com.legoatoom.gameblocks.playing_cards.client;

import com.legoatoom.gameblocks.playing_cards.items.CardDeckItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class CardRenderer{


    public static int currentSelected = 0;
    private boolean selected = false;

    public static void render(ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0f));
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0f));

//        matrices.scale(0.1f, 0.1f, 0.1f);
        matrices.scale(0.012f, 0.012f, 0.012f);

        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        Identifier texture = MinecraftClient.getInstance().getItemRenderer().getModels().getModel(stack).getParticleSprite().getId();
        texture = new Identifier(texture.getNamespace(), "textures/%s.png".formatted(texture.getPath()));
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getText(texture));
        vertexConsumer.vertex(matrix4f, -8f, -64.0f, 0f).color(255, 255, 255, 255).texture(0.0f, 1.0f).light(light).next();
        vertexConsumer.vertex(matrix4f, 8f, -64.0f, 0f).color(255, 255, 255, 255).texture(1.0f, 1.0f).light(light).next();
        vertexConsumer.vertex(matrix4f, 8f, -80.0f, 0f).color(255, 255, 255, 255).texture(1.0f, 0.0f).light(light).next();
        vertexConsumer.vertex(matrix4f, -8f, -80.0f, 0f).color(255, 255, 255, 255).texture(0.0f, 0.0f).light(light).next();
//        matrices.scale(0.9f, 0.9f, 0.9f);
//        MinecraftClient.getInstance().textRenderer.draw(matrices, "♦", 0f, 3f, 0xfff);
        matrices.pop();
    }

    public static void renderCardInBothHand(HeldItemRenderer cardItemHoldingRenderer, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
                                            float pitch, float equipProgress, float swingProgress, ItemStack stack) {

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        final int count = 13;
        float i = getCardAngle(pitch);
        if (player == null) return;
        float f = MathHelper.sqrt(swingProgress);
        float g = -0.2f * MathHelper.sin(swingProgress * (float)Math.PI);
        float h = -0.4f * MathHelper.sin(f * (float)Math.PI);
        matrices.translate(0.0, -g / 2.0f, h);
        if (pitch < -10f) matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(pitch+10f));
        matrices.translate(0.0, 0.04f + equipProgress * -1.2f + i * -0.5f, -0.72f);
        matrices.push();
        {
            if (!player.isInvisible()) {
                matrices.push();
                {
                    matrices.translate(-0.20f, 0f, 0f);
                    matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90.0f));
                    cardItemHoldingRenderer.renderArm(matrices, vertexConsumers, light, Arm.RIGHT);
                }
                matrices.pop();
                matrices.push();
                {
                    matrices.translate(+0.20f, 0f, 0f);
                    matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90.0f));
                    cardItemHoldingRenderer.renderArm(matrices, vertexConsumers, light, Arm.LEFT);
                }
                matrices.pop();
            }
        }
        matrices.pop();
//        matrices.scale(0.0625f, 0.0625f, 0.0625f);
////        matrices.translate(-1f, 0f, 0f);
////        matrices.scale(16f, 16f, 16f);
//        CardRenderer.render(stack, matrices, vertexConsumers, light);

        float angle = Math.min(15f/count, 4f);
        matrices.translate(0, -1.15f, 0f);
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(angle * (count-1f)));
        int current = getCurrentSelected(count);
        for (int j = 0; j < count; j++) {
            if (j == current){
                matrices.push();
                matrices.translate(0f, .1f, .002f);
                CardRenderer.render(stack, matrices, vertexConsumers, light);
                matrices.pop();
            } else {
                CardRenderer.render(stack, matrices, vertexConsumers, light);
            }
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-angle*2f));
            matrices.translate(0, 0f, .00001f);

        }



    }

    private static int getCurrentSelected(int count) {
        currentSelected = (currentSelected + count) % count;
        return currentSelected;
    }

    private static float getCardAngle(float tickDelta) {
        float f = 1.0f - tickDelta / 45.0f + 0.1f;
        f = MathHelper.clamp(f, 0.0f, 0.5f);
        f = -MathHelper.cos(f * (float)Math.PI) * 0.5f + 0.1f;
        return f;
    }



    public static void addCurrentSelected(int i) {
        currentSelected += i;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            ItemStack stack = player.getMainHandStack();
            if (stack.getItem() instanceof CardDeckItem item) {
                MinecraftClient.getInstance().player.sendMessage(Text.translatable(item.getTranslationKey()), true);
            }
        }
    }


//    @Environment(EnvType.CLIENT)
//    static class CardTexture
//    implements AutoCloseable{
//        private final ResourceTexture texture;
//        private final RenderLayer renderLayer;
//
//        CardTexture(Identifier identifier){
//            this.texture = new ResourceTexture(identifier);
//            try {
//                this.texture.load(MinecraftClient.getInstance().getResourceManager());
//            } catch (IOException e) {
//                GameBlocksState.warn(e.getMessage());
//            }
//            this.renderLayer = RenderLayer.getText(identifier);
//        }
//
//        @Override
//        public void close() {
//            this.texture.close();
//        }
//
//        public void draw(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
//            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
//            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.renderLayer);
//            vertexConsumer.vertex(matrix4f, 0.0f, 128.0f, -0.01f).color(255, 255, 255, 255).texture(0.0f, 1.0f).light(light).next();
//            vertexConsumer.vertex(matrix4f, 128.0f, 128.0f, -0.01f).color(255, 255, 255, 255).texture(1.0f, 1.0f).light(light).next();
//            vertexConsumer.vertex(matrix4f, 128.0f, 0.0f, -0.01f).color(255, 255, 255, 255).texture(1.0f, 0.0f).light(light).next();
//            vertexConsumer.vertex(matrix4f, 0.0f, 0.0f, -0.01f).color(255, 255, 255, 255).texture(0.0f, 0.0f).light(light).next();
//        }
//    }
}
