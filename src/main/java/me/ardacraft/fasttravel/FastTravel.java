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

package me.ardacraft.fasttravel;

import com.google.inject.Inject;
import me.ardacraft.fasttravel.commands.DestinationCommands;
import me.ardacraft.fasttravel.commands.WarpCommands;
import me.ardacraft.fasttravel.data.traveldata.ImmutableTravelData;
import me.ardacraft.fasttravel.data.traveldata.MutableTravelData;
import me.ardacraft.fasttravel.data.traveldata.TravelDataBuilder;
import me.ardacraft.fasttravel.destination.DestinationManager;
import me.ardacraft.fasttravel.destination.DestinationStats;
import me.ardacraft.fasttravel.traveller.TravellerManager;
import me.ardacraft.fasttravel.warp.WarpManager;
import me.dags.commandbus.CommandBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;

import java.nio.file.Path;

/**
 * @author dags <dags@dags.me>
 */
@Plugin(name = "FastTravel", id = "fasttravel", version = "1.0")
public class FastTravel {

    private static FastTravel instance;
    private static final Logger logger = LoggerFactory.getLogger("FastTravel");

    private final WarpManager warpManager = new WarpManager();
    private final TravellerManager travellerManager = new TravellerManager();
    private final DestinationManager destinationManager = new DestinationManager();

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;

    public FastTravel() {
        instance = this;
    }

    @Listener
    public void preInit(GamePreInitializationEvent event) {
        Sponge.getDataManager().registerSerializer(DestinationStats.class, new DestinationStats.Serializer());
        Sponge.getDataManager().register(MutableTravelData.class, ImmutableTravelData.class, new TravelDataBuilder());
        Sponge.getEventManager().registerListeners(this, warpManager);
        Sponge.getEventManager().registerListeners(this, travellerManager);
        Sponge.getEventManager().registerListeners(this, destinationManager);
        CommandBus.newInstance(logger).register(WarpCommands.class).register(DestinationCommands.class).submit(this);
    }

    @Listener
    public void init(GameStartedServerEvent event) {
        Sponge.getScheduler().createTaskBuilder()
                .delayTicks(10L)
                .intervalTicks(1L)
                .execute(travellerManager.getProcess())
                .submit(this);
    }

    public static void info(String message, Object... args) {
        logger.info(message, args);
    }

    public static FastTravel instance() {
        return instance;
    }

    public DestinationManager destinationManager() {
        return destinationManager;
    }

    public TravellerManager travellerManager() {
        return travellerManager;
    }

    public WarpManager warpManager() {
        return warpManager;
    }

    public Path configPath(String... path) {
        Path result = configDir;
        for (String s : path) {
            result = result.resolve(s);
        }
        return result;
    }
}
