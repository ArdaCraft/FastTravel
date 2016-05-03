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

package me.ardacraft.fasttravel.warp;

import com.flowpowered.math.vector.Vector3d;
import me.ardacraft.fasttravel.destination.Destination;
import me.ardacraft.fasttravel.destination.region.Region;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

/**
 * @author dags <dags@dags.me>
 */
public class Warp {

    private String name = "";
    private String world = "";
    private double x = 0;
    private double y = 0;
    private double z = 0;
    private Double pitch = null;
    private Double yaw = null;

    public Warp() {}

    public Warp(String name, Location<World> location, Vector3d rotation) {
        this.name = name;
        this.world = location.getExtent().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.pitch = rotation.getX();
        this.yaw = rotation.getY();
    }

    public String getName() {
        return name;
    }

    public String getWorld() {
        return world;
    }

    public int blockX() {
        return (int) x;
    }

    public int blockZ() {
        return (int) z;
    }

    public boolean matches(Destination destination) {
        String world = destination.getWorld();
        Region region = destination.getRegion();
        return getWorld().equals(world) && region.contains(blockX(), 0, blockZ());
    }

    public void teleport(Living living) {
        Optional<World> optional = Sponge.getServer().getWorld(world);
        if (optional.isPresent()) {
            Location<World> location = optional.get().getLocation(x, y, z);
            Vector3d rotation = getRotation(living.getRotation());
            living.setLocationAndRotation(location, rotation);
        }
    }

    private Vector3d getRotation(Vector3d orElse) {
        return pitch == null || yaw == null ? orElse : new Vector3d(pitch, yaw, 0);
    }
}
