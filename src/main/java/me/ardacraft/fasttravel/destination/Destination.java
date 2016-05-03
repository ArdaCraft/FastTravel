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

import com.flowpowered.math.vector.Vector3d;
import me.ardacraft.fasttravel.destination.region.Point;
import me.ardacraft.fasttravel.destination.region.Rectangle2D;
import me.ardacraft.fasttravel.destination.region.Region;

/**
 * @author dags <dags@dags.me>
 */
public class Destination {

    private String name = "";
    private String world = "";
    private Rectangle2D region = Rectangle2D.EMPTY;

    public Destination() {}

    private Destination(Builder builder) {
        this.name = builder.name;
        this.world = builder.world;
        this.region = new Rectangle2D(builder.point1, builder.point2);
    }

    public String getName() {
        return name;
    }

    public String getWorld() {
        return world;
    }

    public Region getRegion() {
        return region;
    }

    public Vector3d target() {
        Point center = region.getMax().mid(region.getMin());
        return new Vector3d(center.getX(), center.getY(), center.getZ());
    }

    public String toString() {
        return "name=" + name + ",region=" + region;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String name = "";
        private String world = "";
        private Point point1 = Point.DUMMY;
        private Point point2 = Point.DUMMY;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder world(String world) {
            this.world = world;
            return this;
        }

        public Builder point(Point point) {
            if (this.point1 == Point.DUMMY) {
                this.point1 = point;
            }  else if (this.point2 == Point.DUMMY) {
                this.point2 = point;
            } else {
                this.point1 = point;
                this.point2 = Point.DUMMY;
            }
            return this;
        }

        public Destination build() {
            if (name.isEmpty() || world.isEmpty() || point1 == Point.DUMMY || point2 == Point.DUMMY) {
                throw new UnsupportedOperationException();
            }
            return new Destination(this);
        }
    }
}
