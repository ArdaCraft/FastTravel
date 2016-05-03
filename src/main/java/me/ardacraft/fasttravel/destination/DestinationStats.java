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

package me.ardacraft.fasttravel.destination;

import com.google.common.reflect.TypeToken;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.persistence.DataSerializer;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Optional;

/**
 * @author dags <dags@dags.me>
 */
public class DestinationStats {

    private int visits = 0;

    public int visits() {
        return visits;
    }

    public void incVisits() {
        visits++;
    }

    public DestinationStats copy() {
        DestinationStats stats = new DestinationStats();
        stats.visits = visits;
        return stats;
    }

    @Override
    public String toString() {
        return "visits=" + visits;
    }

    public static class Serializer implements DataSerializer<DestinationStats> {

        static final TypeToken<DestinationStats> token = TypeToken.of(DestinationStats.class);
        static final DataQuery VISITS = DataQuery.of("visits");

        @Override
        public TypeToken<DestinationStats> getToken() {
            return token;
        }

        @Override
        public Optional<DestinationStats> deserialize(DataView dataView) throws InvalidDataException {
            Optional<Object> visits = dataView.get(VISITS);
            if (visits.isPresent()) {
                DestinationStats stats = new DestinationStats();
                stats.visits = (Integer) visits.get();
                return Optional.of(stats);
            }
            return Optional.empty();
        }

        @Override
        public DataContainer serialize(DestinationStats destinationStats) throws InvalidDataException {
            return new MemoryDataContainer().set(VISITS, destinationStats.visits);
        }
    }
}
