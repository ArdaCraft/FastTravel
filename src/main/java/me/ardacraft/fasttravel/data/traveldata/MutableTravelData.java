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

import me.ardacraft.fasttravel.FastTravel;
import me.ardacraft.fasttravel.destination.Destination;
import me.ardacraft.fasttravel.destination.DestinationStats;
import org.spongepowered.api.data.*;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.text.BookView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author dags <dags@dags.me>
 */
public class MutableTravelData extends AbstractData<MutableTravelData, ImmutableTravelData> {

    boolean exploring = true;
    String lastVisited = "";
    Map<String, DestinationStats> visited = new HashMap<>();

    public boolean isExploring() {
        return exploring;
    }

    public boolean hasVisited(String destination) {
        return visited.containsKey(destination);
    }

    public int getVisitedCount() {
        return visited.size();
    }

    public String getLastVisited() {
        return lastVisited;
    }

    public DestinationStats getVisitStats(String name) {
        DestinationStats stats = visited.get(name);
        if (stats == null) {
            visited.put(name, stats = new DestinationStats());
        }
        return stats;
    }

    public BookView listWarps() {
        List<Destination> destinations = FastTravel.instance().destinationManager()
                .find(visited.keySet())
                .collect(Collectors.toList());
        return FastTravel.instance().warpManager().listWarps(w -> destinations.stream().anyMatch(w::matches));
    }

    public void setExploring(boolean value) {
        this.exploring = value;
    }

    public void setLastVisited(String destination) {
        this.lastVisited = destination;
    }

    public void discover(String destination) {
        if (exploring) {
            DestinationStats stats = visited.get(destination);
            if (stats == null) {
                visited.put(destination, stats = new DestinationStats());
            }
            stats.incVisits();
        }
    }

    @Override
    protected void registerGettersAndSetters() {

    }

    @Override
    public Optional<MutableTravelData> fill(DataHolder dataHolder, MergeFunction mergeFunction) {
        return from(dataHolder.toContainer());
    }

    @Override
    public Optional<MutableTravelData> from(DataContainer dataContainer) {
        return fromView(dataContainer);
    }

    @Override
    public MutableTravelData copy() {
        MutableTravelData data = new MutableTravelData();
        data.exploring = exploring;
        data.lastVisited = lastVisited;
        visited.entrySet().forEach(e -> data.visited.put(e.getKey(), e.getValue().copy()));
        return data;
    }

    @Override
    public ImmutableTravelData asImmutable() {
        return new ImmutableTravelData(this);
    }

    @Override
    public int compareTo(MutableTravelData o) {
        return 0;
    }

    @Override
    public int getContentVersion() {
        return TravelDataBuilder.VERSION;
    }

    @Override
    public DataContainer toContainer() {
        return new MemoryDataContainer()
                .set(Queries.CONTENT_VERSION, getContentVersion())
                .set(TravelDataBuilder.EXPLORING, exploring)
                .set(TravelDataBuilder.LAST_VISITED, lastVisited)
                .set(TravelDataBuilder.VISITED, visited);
    }

    @Override
    public String toString() {
        return "exploring=" + exploring + ",lastVisited=" + lastVisited + ",visit=[" + visited + "]";
    }

    static Optional<MutableTravelData> fromView(DataView dataView) {
        Optional<?> exploring = dataView.get(TravelDataBuilder.EXPLORING);
        Optional<?> lastVisited = dataView.get(TravelDataBuilder.LAST_VISITED);
        if (exploring.isPresent() && lastVisited.isPresent() && dataView.contains(TravelDataBuilder.VISITED)) {
            MutableTravelData data = new MutableTravelData();
            data.exploring = (Boolean) exploring.get();
            data.lastVisited = (String) lastVisited.get();
            fillStats(data, dataView);
            return Optional.of(data);
        }
        return Optional.empty();
    }

    private static void fillStats(MutableTravelData data, DataView dataView) {
        Optional<? extends Map<?,?>> map = dataView.getMap(TravelDataBuilder.VISITED);
        if (map.isPresent()) {
            for (Object o : map.get().keySet()) {
                String key = o.toString();
                Optional<DestinationStats> stats = dataView.getObject(TravelDataBuilder.VISITED.then(DataQuery.of(key)), DestinationStats.class);
                if (stats.isPresent()) {
                    data.visited.put(key, stats.get());
                }
            }
        }
    }
}
