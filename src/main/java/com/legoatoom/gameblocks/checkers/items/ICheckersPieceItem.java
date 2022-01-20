package com.legoatoom.gameblocks.checkers.items;

import com.legoatoom.gameblocks.checkers.util.CheckersPieceType;
import com.legoatoom.gameblocks.common.items.IPieceItem;
import com.legoatoom.gameblocks.common.screen.slot.AbstractGridSlot;
import com.legoatoom.gameblocks.common.util.ActionType;
import com.legoatoom.gameblocks.common.util.IPieceType;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import static com.legoatoom.gameblocks.GameBlocks.GAME_BLOCKS;

public abstract class ICheckersPieceItem extends Item implements IPieceItem {

    private final boolean isBlack;
    private final CheckersPieceType type;

    public ICheckersPieceItem(boolean isBlack, CheckersPieceType type) {
        super(new FabricItemSettings().group(GAME_BLOCKS).maxCount(20));
        this.isBlack = isBlack;
        this.type = type;
    }

    @Override
    public void calculateLegalActions(AbstractGridSlot slot) {
        // TODO: 12022-01-20
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) return super.use(world, user, hand);
        ItemStack stack = user.getStackInHand(hand);

        // Clearing data when using.
        return TypedActionResult.pass(defaultState(stack));
    }

    @Override
    public void handleAction(ScreenHandler handler, AbstractGridSlot slot, ItemStack cursorStack, ActionType actionType) {
        // TODO: 12022-01-20
    }

    @Override
    public boolean isBlack() {
        return isBlack;
    }

    @Override
    public IPieceType getType() {
        return type;
    }
}
