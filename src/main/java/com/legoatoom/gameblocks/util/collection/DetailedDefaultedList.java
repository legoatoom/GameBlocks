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

package com.legoatoom.gameblocks.util.collection;

import net.minecraft.util.collection.DefaultedList;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class DetailedDefaultedList<E> extends DefaultedList<E> {

    @Nullable
    protected final Function<Integer, E> initialElementFunction;

    protected DetailedDefaultedList(List<E> delegate, @Nullable Function<Integer, E> initialElementFunction, E initialElement) {
        super(delegate, initialElement);
        this.initialElementFunction = initialElementFunction;
    }

    public static <E> DetailedDefaultedList<E> ofSizeAndFunction(int size, E initialElement, Function<Integer, E> initialElementFunction) {
        Validate.notNull(initialElementFunction);
        ArrayList<E> values = new ArrayList<>(size);
        for (int i = 0; i < size; ++i) {
            values.add(i, initialElementFunction.apply(i));
        }
        return new DetailedDefaultedList<>(values, initialElementFunction, initialElement);
    }
}
