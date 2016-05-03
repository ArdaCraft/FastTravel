/*
 * The MIT License (MIT)
 *
 * Copyright (c) dags <https://dags.me>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package me.ardacraft.fasttravel.data.traveldata;

import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Optional;

/**
 * @author dags <dags@dags.me>
 */
public class TravelDataBuilder extends AbstractDataBuilder<MutableTravelData>  implements DataManipulatorBuilder<MutableTravelData, ImmutableTravelData> {

    static final int VERSION = 0;
    static final DataQuery EXPLORING = DataQuery.of("exploring");
    static final DataQuery LAST_VISITED = DataQuery.of("lastVisited");
    static final DataQuery VISITED = DataQuery.of("visited");

    public TravelDataBuilder() {
        super(MutableTravelData.class, VERSION);
    }

    @Override
    protected Optional<MutableTravelData> buildContent(DataView dataView) throws InvalidDataException {
        return MutableTravelData.fromView(dataView);
    }

    @Override
    public MutableTravelData create() {
        return new MutableTravelData();
    }

    @Override
    public Optional<MutableTravelData> createFrom(DataHolder dataHolder) {

        return create().fill(dataHolder);
    }
}
