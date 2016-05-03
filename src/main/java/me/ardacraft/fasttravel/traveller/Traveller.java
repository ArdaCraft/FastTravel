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

package me.ardacraft.fasttravel.traveller;

import me.ardacraft.fasttravel.FastTravel;
import me.ardacraft.fasttravel.data.traveldata.MutableTravelData;
import me.ardacraft.fasttravel.destination.Destination;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.lang.ref.WeakReference;

public class Traveller {

    private final WeakReference<Player> player;
    private final MutableTravelData data;

    private int lastPosX = 0;
    private int lastPosZ = 0;
    private Destination current = null;

    Traveller(Player player) {
        this.player = new WeakReference<>(player);
        this.data = player.get(MutableTravelData.class).orElse(new MutableTravelData());
    }

    private void updateData() {
        Player player = this.player.get();
        if (player != null) {
            player.offer(data);
        }
    }

    void update() {
        if (!data.isExploring()) {
            return;
        }

        Player player = this.player.get();

        if (player == null) {
            FastTravel.instance().travellerManager().remove(this);
            return;
        }

        Location<World> location = player.getLocation();
        int x = location.getBlockX();
        int z = location.getBlockZ();

        if (x == lastPosX && z == lastPosZ) {
            return;
        }

        lastPosX = x;
        lastPosZ = z;

        String world = location.getExtent().getName();
        Destination destination = FastTravel.instance().destinationManager().get(world, x, z);
        if (destination != null && destination.getRegion().contains(x, 0, z)) {
            if (current == null || current != destination) {
                enter(destination);
            }
        } else if (current != null) {
            leave(current);
        }
    }

    private void enter(Destination destination) {
        if (data.getLastVisited().equals(destination.getName())) {
            actionBar(destination);
        } else if (data.hasVisited(destination.getName())) {
            actionBar(destination);
            data.getVisitStats(destination.getName()).incVisits();
        } else {
            title(destination);
            data.discover(destination.getName());
        }
        data.setLastVisited(destination.getName());
        current = destination;
        updateData();
    }

    private void leave(Destination destination) {
        data.setLastVisited(destination.getName());
        current = null;
        updateData();
    }

    public void setExplorer(boolean value) {
        data.setExploring(value);
        updateData();
    }

    private void actionBar(Destination destination) {
        Player player = this.player.get();
        if (player != null) {
            player.sendMessage(ChatTypes.ACTION_BAR, Text.of("Entering " + destination.getName()));
        }
    }

    private void title(Destination destination) {
        Player player = this.player.get();
        if (player != null) {
            Title title = Title.builder()
                    .title(Text.of(destination.getName()))
                    .subtitle(Text.of("DISCOVERED"))
                    .fadeIn(10)
                    .fadeOut(10)
                    .stay(10)
                    .build();
            player.sendTitle(title);
        }
    }
}