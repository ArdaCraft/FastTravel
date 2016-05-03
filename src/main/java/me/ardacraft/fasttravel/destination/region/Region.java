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

package me.ardacraft.fasttravel.destination.region;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import me.ardacraft.fasttravel.utils.ChunkMap;

/**
 * @author dags <dags@dags.me>
 */
public interface Region {

    Point getMin();

    Point getMax();

    default boolean contains(int x, int y, int z) {
        return getMin().lesserThan(x, y, z) && getMax().greaterThan(x, y, z);
    }

    default boolean contains(Vector3d pos) {
        return contains(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ());
    }

    default boolean contains(Vector3i pos) {
        return contains(pos.getX(), pos.getY(), pos.getZ());
    }

    default void addToChunkMap(ChunkMap<Region> map) {
        int res = map.getResolution();
        for (int x = getMin().getX(); x < getMax().getX(); x += res) {
            for (int z = getMin().getZ(); z < getMax().getZ(); z += res) {
                map.put(x, z, this);
            }
        }
    }
}
