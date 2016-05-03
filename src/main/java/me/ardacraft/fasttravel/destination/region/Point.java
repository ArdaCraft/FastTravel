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

import com.flowpowered.math.vector.Vector3i;

/**
 * @author dags <dags@dags.me>
 */
public class Point {

    public static final Point DUMMY = new Point();

    private int x = 0;
    private int y = 0;
    private int z = 0;

    private Point() {}

    public Point(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point(Vector3i pos) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Point min(Point other) {
        int x = Math.min(this.x, other.x);
        int y = Math.min(this.y, other.y);
        int z = Math.min(this.z, other.z);
        return new Point(x, y, z);
    }

    public Point max(Point other) {
        int x = Math.max(this.x, other.x);
        int y = Math.max(this.y, other.y);
        int z = Math.max(this.z, other.z);
        return new Point(x, y, z);
    }

    public Point mid(Point other) {
        Point min = min(other);
        Point max = max(other);
        int x = max.x - min.x;
        int y = max.y - min.y;
        int z = max.z - min.z;
        return new Point(x, y, z);
    }

    public boolean lesserThan(int x, int y, int z) {
        return this.x < x && this.y < y && this.z < z;
    }

    public boolean greaterThan(int x, int y, int z) {
        return this.x > x && this.y > y && this.z > z;
    }

    public boolean lesserThanHor(int x, int z) {
        return this.x < x && this.z < z;
    }

    public boolean greaterThanHor(int x, int z) {
        return this.x > x && this.z > z;
    }

    @Override
    public String toString() {
        return x + "," + y + "," + z;
    }

    public static Point min(int x1, int y1, int z1, int x2, int y2, int z2) {
        int x = Math.min(x1, z2);
        int y = Math.min(y1, y2);
        int z = Math.min(z1, z2);
        return new Point(x, y, z);
    }

    public static Point max(int x1, int y1, int z1, int x2, int y2, int z2) {
        int x = Math.max(x1, z2);
        int y = Math.max(y1, y2);
        int z = Math.max(z1, z2);
        return new Point(x, y, z);
    }
}
