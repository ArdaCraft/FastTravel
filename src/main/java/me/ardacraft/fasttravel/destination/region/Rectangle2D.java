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

/**
 * @author dags <dags@dags.me>
 */
public class Rectangle2D implements Region {

    public static final Rectangle2D EMPTY = new Rectangle2D();

    private Point min = Point.DUMMY;
    private Point max = Point.DUMMY;

    private Rectangle2D() {
        min = new Point(1, 1, 1);
        max = new Point(0, 0, 0);
    }

    public Rectangle2D(Point p1, Point p2) {
        this.min = p1.min(p2);
        this.max = p1.max(p2);
    }

    @Override
    public Point getMin() {
        return min;
    }

    @Override
    public Point getMax() {
        return max;
    }

    @Override
    public boolean contains(int x, int y, int z) {
        return min.lesserThanHor(x, z) && max.greaterThanHor(x, z);
    }

    @Override
    public String toString() {
        return "[(" + min + "),(" + max + ")]";
    }
}
