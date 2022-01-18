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

package com.legoatoom.gameblocks.mixin.client.gui.screen.ingame;

import com.legoatoom.gameblocks.client.screen.AbstractBoardScreen;
import com.legoatoom.gameblocks.client.screen.ChessBoardScreen;
import com.legoatoom.gameblocks.screen.slot.AbstractBoardSlot;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(HandledScreen.class)
public abstract class CustomHighSlotHighLighter<T extends ScreenHandler> {


    @Shadow
    protected int x;
    @Shadow
    protected int y;
    @Shadow
    @Nullable
    protected Slot focusedSlot;

    @Redirect(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawSlotHighlight(Lnet/minecraft/client/util/math/MatrixStack;III)V")
    )
    private void render(MatrixStack matrices, int x, int y, int z) {
        if (this.focusedSlot instanceof AbstractBoardSlot s) {
            // Mixins are weird and they some reason allow me to do this.
            //noinspection unchecked
            if ((HandledScreen<T>) ((Object) this) instanceof ChessBoardScreen a) {
                if (a.isSelectingPromotion()) {
                    return;
                }
            }
            RenderSystem.disableDepthTest();
            RenderSystem.colorMask(true, true, true, false);
            // Vanilla code uses gradient, therefor I also do.
            int slotSize = s.getSlotHighLighterSize();
            int offset = (16 - slotSize) / 2;
            HandledScreen.fillGradient(matrices, x + offset, y + offset, x + offset + slotSize, y + offset + slotSize, -2130706433, -2130706433, z);
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.enableDepthTest();
        } else {
            HandledScreen.drawSlotHighlight(matrices, x, y, z);
        }
    }

    @Inject(method = "isPointOverSlot", at = @At("HEAD"), cancellable = true)
    private void isOverChessBoardSlot(Slot slot, double pointX, double pointY, CallbackInfoReturnable<Boolean> cir) {
        if (slot instanceof AbstractBoardSlot s) {
            cir.setReturnValue(this.isWithinBounds(slot.x, slot.y, pointX, pointY, s.getSlotHighLighterSize()));
        }
    }

    protected boolean isWithinBounds(int x, int y, double mouseX, double mouseY, int slotSize) {
        int screenX = this.x;
        int screenY = this.y;
        int offset = (16 - slotSize) / 2;
        mouseX -= screenX;
        mouseY -= screenY;
        return mouseX >= (double) (x - 1 + offset) && mouseX < (double) (x + offset + slotSize + 1)
                && mouseY >= (double) (y - 1 + offset) && mouseY < (double) (y + offset + slotSize + 1);
    }
}
