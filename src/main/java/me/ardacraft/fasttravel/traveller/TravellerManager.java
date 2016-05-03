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

import me.ardacraft.fasttravel.data.traveldata.MutableTravelData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.living.humanoid.ChangeGameModeEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TravellerManager {

    private final Map<Player, Traveller> travellers = new ConcurrentHashMap<>();
    private int processPerTick = 5;

    public void setProcessesPerTick(int value) {
        processPerTick = value;
    }

    public void remove(Traveller traveller) {
        Iterator<Map.Entry<Player, Traveller>> iterator = travellers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Player, Traveller> e = iterator.next();
            if (e.getValue().equals(traveller)) {
                iterator.remove();
                return;
            }
        }
    }

    @Listener
    public void onJoin(ClientConnectionEvent.Join event) {
        if (event.getTargetEntity().get(MutableTravelData.class).map(MutableTravelData::isExploring).orElse(false)) {
            travellers.put(event.getTargetEntity(), new Traveller(event.getTargetEntity()));
        }
    }

    @Listener
    public void onQuit(ClientConnectionEvent.Disconnect event) {
        travellers.remove(event.getTargetEntity());
    }

    @Listener
    public void onModeChange(ChangeGameModeEvent event, @Root Player player) {
        if (event.getGameMode() != GameModes.ADVENTURE) {
            travellers.get(player).setExplorer(false);
            travellers.remove(player);
        }
    }

    public Traveller get(Player player) {
        Traveller traveller = travellers.get(player);
        if (traveller == null) {
            travellers.put(player, traveller = new Traveller(player));
        }
        return traveller;
    }

    public Updater getProcess() {
        return new Updater();
    }

    private class Updater implements Runnable {

        private Iterator<Traveller> iterator = travellers.values().iterator();

        @Override
        public void run() {
            int count = 0;
            while (count++ < processPerTick && iterator.hasNext()) {
                Traveller traveller = iterator.next();
                traveller.update();
            }
            if (!iterator.hasNext()) {
                iterator = travellers.values().iterator();
            }
        }
    }
}
