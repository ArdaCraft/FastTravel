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

package me.ardacraft.fasttravel.utils;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.stream.Stream;

/**
 * @author dags <dags@dags.me>
 */
public class ChunkMap<T> {

    private final Map<Integer, T> map = Maps.newHashMap();
    private final int resolution;

    private ChunkMap(int resolution) {
        this.resolution = resolution;
    }

    public int getResolution() {
        return resolution;
    }

    public T getActive(int x, int z) {
        return map.get(hash(x, z));
    }

    public Stream<T> stream() {
        return map.values().stream();
    }

    public void put(int x, int z, T t) {
        map.put(hash(x, z), t);
    }

    private int hash(int x, int z) {
        x = x < 0 ? x / resolution - 1 : x / resolution;
        z = z < 0 ? z / resolution - 1 : z / resolution;
        return 31 * x + z;
    }

    public static <T> ChunkMap<T> simple(int size) {
        return new ChunkMap<>(size);
    }
}
