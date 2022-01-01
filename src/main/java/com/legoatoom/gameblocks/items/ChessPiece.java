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

package com.legoatoom.gameblocks.items;

import com.legoatoom.gameblocks.GameBlocks;
import com.legoatoom.gameblocks.screen.slot.ChessBoardSlot;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.function.TriFunction;

import java.util.*;
import java.util.function.BiFunction;

import static com.legoatoom.gameblocks.GameBlocks.GAME_BLOCKS;

@Deprecated
public enum ChessPiece {
//    BLACK_BISHOP(true, 2, (chessBoardSlots, chessBoardSlot) -> new ArrayList<>(), 2, 5),
//    BLACK_KING(true, 1, ChessPiece::hoverKing, 4),
//    BLACK_KNIGHT(true, 2, ChessPiece::hoverKnight, 1, 6),
//    BLACK_PAWN(true, 8, ChessPiece::hoverPawn, 8, 9, 10, 11, 12, 13, 14, 15),
//    BLACK_QUEEN(true, 1, (chessBoardSlots, chessBoardSlot) -> new ArrayList<>(), 3),
//    BLACK_ROOK(true, 2, ChessPiece::hoverPawn, 0, 7),
//    WHITE_BISHOP(false, 2, (chessBoardSlots, chessBoardSlot) -> new ArrayList<>(), 58, 61),
//    WHITE_KING(false, 1, ChessPiece::hoverKing, 60),
//    WHITE_KNIGHT(false, 2, ChessPiece::hoverKnight, 57, 62),
//    WHITE_PAWN(false, 8, ChessPiece::hoverPawn, 48, 49, 50, 51, 52, 53, 54, 55),
//    WHITE_QUEEN(false, 1, (chessBoardSlots, chessBoardSlot) -> new ArrayList<>(), 59),
//    WHITE_ROOK(false, 2, (chessBoardSlots, chessBoardSlot) -> new ArrayList<>(), 56, 63);
//
//    public final static Map<ChessPiece, ChessPieceItem> CHESS_PIECE_ITEMS = new HashMap<>();
//    private final static Map<Integer, ChessPiece> DEFAULT_LOCATIONS;
//
//    static {
//        DEFAULT_LOCATIONS = new HashMap<>();
//        for (ChessPiece value : values()) {
//            Arrays.stream(value.locations).forEach(loc -> DEFAULT_LOCATIONS.put(loc, value));
//        }
//    }
//
//    private final int maxCount;
//    private final boolean isBlack;
//    private final int[] locations;
//    private final BiFunction<ChessPiece[][], ChessBoardSlot, ChessPiece[][]> hoverFunction;
//
//    ChessPiece(boolean isBlack, int maxCount, BiFunction<ChessPiece[][], ChessBoardSlot, ChessPiece[][]> hoverFunction, int... locations) {
//        this.isBlack = isBlack;
//        this.maxCount = maxCount;
//        this.hoverFunction = hoverFunction;
//        this.locations = locations;
//    }
//
//    /**
//     * Returns the default piece for a given location.
//     *
//     * @param location A value between 0 and 63.
//     * @return a {@link Optional} with a ChessPiece whose location is the default position,
//     * or null if no ChessPiece has that default location.
//     * @throws IndexOutOfBoundsException if location is higher than 64 or negative.
//     */
//    public static Optional<ChessPiece> getDefaultPiece(int location) {
//        if (location < 0 || location >= 64) {
//            throw new IndexOutOfBoundsException(location);
//        }
//        return Optional.ofNullable(DEFAULT_LOCATIONS.get(location));
//    }
//
//    public static void registerAll() {
//        for (ChessPiece chessPiece : ChessPiece.values()) {
//            ChessPieceItem item = Registry.register(Registry.ITEM, GameBlocks.id.apply(chessPiece.name().toLowerCase(Locale.ROOT)),
//                    new ChessPieceItem(new FabricItemSettings().group(GAME_BLOCKS).maxCount(chessPiece.maxCount),
//                            chessPiece));
//            CHESS_PIECE_ITEMS.put(chessPiece, item);
//        }
//    }
//
//    private static List<ChessBoardSlot> hoverKnight(ChessPiece[][] allSlots, ChessBoardSlot hSlot) {
//        return allSlots.stream()
//                .filter(s -> {
//                    int dX = Math.abs(s.getBoardX() - hSlot.getBoardX());
//                    int dY = Math.abs(s.getBoardY() - hSlot.getBoardY());
//                    return 0 < dX && dX <= 2 && 0 < dY && dY <= 2 && dX + dY == 3;
//                }).toList();
//    }
//
//    private static List<ChessBoardSlot> hoverPawn(ChessPiece[][] allSlots, ChessBoardSlot hSlot) {
//        boolean startRow = (hSlot.isBlack() ? 6 : 1) == hSlot.getBoardY();
//        return allSlots.stream()
//                .filter(s -> {
//                    int x = s.getBoardX() - hSlot.getBoardX();
//                    int y = s.getBoardY() - hSlot.getBoardY();
//                    int dX = Math.abs(x);
//                    int dY = Math.abs(y);
//                    boolean canWalkThere = s.getType() == null && dX == 0 &&
//                            (y == (hSlot.isBlack() ? -1 : 1) || startRow && y == (hSlot.isBlack() ? -2 : 2));
//                    boolean canAttackTo = (s.getType() != null && s.isBlack() != hSlot.isBlack()
//                            && y == (hSlot.isBlack() ? -1 : 1) && dX == 1);
//                    return canWalkThere || canAttackTo;
//                }).toList();
//    }
//
//    private static List<ChessBoardSlot> hoverKing(List<ChessBoardSlot> allSlots, ChessBoardSlot hSlot) {
//        return allSlots.stream()
//                .filter(s -> {
//                    int dX = Math.abs(s.getBoardX() - hSlot.getBoardX());
//                    int dY = Math.abs(s.getBoardY() - hSlot.getBoardY());
//                    return (0 <= dX && dX < 2 && 0 <= dY && dY < 2);
//                }).toList();
//    }
//
//    public boolean isBlack() {
//        return isBlack;
//    }
//
//    public static class ChessPieceItem extends Item {
//
//        private final ChessPiece type;
//
//        public ChessPieceItem(Settings settings, ChessPiece type) {
//            super(settings);
//            this.type = type;
//        }
//        public List<ChessBoardSlot> hoverIndicationFunction(ChessPiece[][] slots, ChessBoardSlot hSlot) {
//            return this.type.hoverFunction.apply(slots, hSlot);
//        }
//
//        public ChessPiece getType() {
//            return type;
//        }
//    }
}
