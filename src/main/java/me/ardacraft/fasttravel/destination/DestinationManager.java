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

import me.ardacraft.fasttravel.FastTravel;
import me.ardacraft.fasttravel.destination.region.Point;
import me.ardacraft.fasttravel.utils.ChunkMap;
import me.ardacraft.fasttravel.utils.FileUtil;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.event.world.UnloadWorldEvent;
import org.spongepowered.api.world.World;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class DestinationManager {

    private final Map<String, ChunkMap<Destination>> worldDestinations = new ConcurrentHashMap<>();
    private int size = 32;

    @Listener
    public void onWorldLoad(LoadWorldEvent event) {
        String world = event.getTargetWorld().getName();
        load(world);
    }

    @Listener
    public void onWorldUnload(UnloadWorldEvent event) {
        String world = event.getTargetWorld().getName();
        worldDestinations.remove(world);
    }

    public Stream<Destination> find(Collection<String> matches) {
        return worldDestinations.values().stream()
                .flatMap(d -> d.stream())
                .filter(d -> matches.contains(d.getName()));
    }

    public Stream<Destination> stream(String world) {
        ChunkMap<Destination> map = worldDestinations.get(world);
        if (map != null) {
            return map.stream();
        }
        return Stream.empty();
    }

    public void add(Destination destination) {
        ChunkMap<Destination> map = worldDestinations.get(destination.getWorld());
        if (map == null) {
            worldDestinations.put(destination.getWorld(), map = ChunkMap.simple(size));
        }
        Point min = destination.getRegion().getMin();
        Point max = destination.getRegion().getMax();
        for (int x = min.getX(); x <= max.getX(); x += size) {
            for (int z = min.getZ(); z <= max.getZ(); z += size) {
                map.put(x, z, destination);
            }
        }
    }

    public Destination get(World world, int x, int z) {
        return get(world.getName(), x, z);
    }

    public Destination get(String world, int x, int z) {
        ChunkMap<Destination> map = worldDestinations.get(world);
        if (map != null) {
            System.out.print(".");
            return map.getActive(x, z);
        }
        return null;
    }

    public void save(Destination destination) {
        Path path = FastTravel.instance().configPath(destination.getWorld(), "destinations");
        Path file = path.resolve(destination.getName().toLowerCase() + ".conf");
        FileUtil.toJson(destination, file);
    }

    private void load(String world) {
        worldDestinations.put(world, ChunkMap.simple(size));
        Path dir = FastTravel.instance().configPath(world, "destinations");
        List<Destination> list = FileUtil.loadAll(dir, Destination.class);
        FastTravel.info("Loaded {} Destinations for world {}!", list.size(), world);
        list.forEach(this::add);
    }
}
