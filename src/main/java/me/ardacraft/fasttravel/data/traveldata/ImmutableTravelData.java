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

import me.ardacraft.fasttravel.destination.DestinationStats;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dags <dags@dags.me>
 */
public class ImmutableTravelData extends AbstractImmutableData<ImmutableTravelData, MutableTravelData> {

    private boolean exploring = false;
    private String lastVisited = "";
    private Map<String, DestinationStats> visited = new HashMap<>();

    ImmutableTravelData(MutableTravelData data) {
        exploring = data.exploring;
        lastVisited = data.lastVisited;
        data.visited.entrySet().forEach(e -> visited.put(e.getKey(), e.getValue().copy()));
    }

    @Override
    public MutableTravelData asMutable() {
        MutableTravelData data = new MutableTravelData();
        data.exploring = exploring;
        data.lastVisited = lastVisited;
        visited.entrySet().forEach(e -> data.visited.put(e.getKey(), e.getValue().copy()));
        return data;
    }

    @Override
    protected void registerGetters() {

    }

    @Override
    public int compareTo(ImmutableTravelData o) {
        return 0;
    }

    @Override
    public int getContentVersion() {
        return TravelDataBuilder.VERSION;
    }

    @Override
    public String toString() {
        return "exploring=" + exploring + ",lastVisited=" + lastVisited + ",visit=[" + visited + "]";
    }
}
